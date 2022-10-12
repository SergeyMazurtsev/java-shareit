package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.CommonService;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exception.IdViolationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidatorException;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final CommonService commonService;

    @Override
    public BookingDtoOut createBooking(BookingDtoIn bookingDtoIn, Long userId) {
        Booking booking = BookingMapper.toBooking(bookingDtoIn);
        booking.setBooker(commonService.getInDBUser(userId));
        booking.setItem(commonService.getInDbItem(bookingDtoIn.getItem()));
        booking.setStatus(BookingStatus.WAITING);
        if (Stream.of(booking.getBooker(), booking.getStart(), booking.getEnd())
                .anyMatch(Objects::isNull)) {
            throw new ValidatorException("Bad request. Booker or start or end is null.");
        }
        if (!booking.getItem().getAvailable()) {
            throw new ValidatorException("Bad request. Item is not available.");
        }
        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().isBefore(LocalDateTime.now())
                || booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidatorException("Bad request. Start is after end or start is before now or end is before now.");
        }
        Long checkOwnerId = booking.getItem().getOwner().getId();
        if (checkOwnerId.equals(userId)) {
            throw new NotFoundException("Bad request. User id with id of owner is not equal.");
        }
        try {
            return BookingMapper.toBookingDtoOut(bookingRepository.save(booking));
        } catch (DataIntegrityViolationException e) {
            throw new IdViolationException("This booking is already in base.");
        }
    }

    @Override
    public BookingDtoOut patchBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Not found. Booking not found in base."));
        Long owner = booking.getItem().getOwner().getId();
        Long checkUserId = commonService.getInDBUser(userId).getId();
        if (!checkUserId.equals(owner)) {
            throw new NotFoundException("Not found. User id with id of owner is not equal.");
        }
        if (!booking.getItem().getAvailable()) {
            throw new ValidatorException("Bad request. Item is not available.");
        }
        if ((booking.getStatus() == BookingStatus.APPROVED) && approved) {
            throw new ValidatorException("Bad request. Booking is already approved.");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        try {
            return BookingMapper.toBookingDtoOut(bookingRepository.save(booking));
        } catch (DataIntegrityViolationException e) {
            throw new IdViolationException("Booking already is there.");
        }
    }

    @Override
    public BookingDtoOut getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Not found. Booking not found in base."));
        Long owner = booking.getItem().getOwner().getId();
        Long checkUserId = commonService.getInDBUser(userId).getId();
        if (!checkUserId.equals(owner)) {
            Long checkBookerId = booking.getBooker().getId();
            if (!checkBookerId.equals(userId)) {
                throw new NotFoundException("Not found. User id is not equal with owner item or booker id.");
            }
        }
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Override
    public List<BookingDtoOut> getBookingOwnerByStatus(Long userId, BookingState status, Integer from, Integer size) {
        User user = commonService.getInDBUser(userId);
        switch (status) {
            case ALL:
                return bookingRepository
                        .findAllByBookerOrderByStartDesc(user, commonService.getPagination(from, size, null))
                        .stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
            case CURRENT:
                return bookingRepository
                        .findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                                user, LocalDateTime.now(), LocalDateTime.now(),
                                commonService.getPagination(from, size, null))
                        .stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
            case FUTURE:
                return bookingRepository
                        .findAllByBookerAndStartAfterOrderByStartDesc(
                                user, LocalDateTime.now(), commonService.getPagination(from, size, null))
                        .stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
            case WAITING:
                return bookingRepository
                        .findAllByBookerAndStatusLikeOrderByStartDesc(
                                user, BookingStatus.WAITING, commonService.getPagination(from, size, null))
                        .stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
            case REJECTED:
                return bookingRepository
                        .findAllByBookerAndStatusLikeOrderByStartDesc(
                                user, BookingStatus.REJECTED, commonService.getPagination(from, size, null))
                        .stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
            case PAST:
                return bookingRepository
                        .findAllByBookerAndEndBeforeAndStatusNot(
                                user, LocalDateTime.now(), BookingStatus.REJECTED,
                                commonService.getPagination(from, size, null))
                        .stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
            default:
                throw new NotFoundException("Not found. No such booking state.");
        }
    }

    @Override
    public List<BookingDtoOut> getBookingItemsByStatus(Long userId, BookingState approved, Integer from, Integer size) {
        commonService.getInDBUser(userId);
        List<BookingDtoOut> searchBookings = bookingRepository.findAllBookingOfUserInItem(
                        userId, commonService.getPagination(from, size, null)).stream()
                .map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
        switch (approved) {
            case ALL:
                return searchBookings;
            case CURRENT:
                return searchBookings.stream().filter(b -> b.getStart().isBefore(LocalDateTime.now()))
                        .filter(b -> b.getEnd().isAfter(LocalDateTime.now())).collect(Collectors.toList());
            case FUTURE:
                return searchBookings.stream().filter(b -> b.getStart().isAfter(LocalDateTime.now()))
                        .collect(Collectors.toList());
            case WAITING:
                return searchBookings.stream().filter(b -> b.getStatus() == BookingStatus.WAITING)
                        .collect(Collectors.toList());
            case REJECTED:
                return searchBookings.stream().filter(b -> b.getStatus() == BookingStatus.REJECTED)
                        .collect(Collectors.toList());
            case PAST:
                return searchBookings.stream().filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            default:
                throw new NotFoundException("Not found. No such booking state.");
        }
    }
}
