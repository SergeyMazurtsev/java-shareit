package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.CommonGetItemAndUser;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exception.IdViolationException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidatorException;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final CommonGetItemAndUser commonGetItemAndUser;

    @Override
    public BookingDtoOut createBooking(BookingDtoIn bookingDtoIn, Long userId) {
        Booking booking = BookingMapper.toBooking(bookingDtoIn);
        booking.setBooker(commonGetItemAndUser.getInDBUser(userId));
        booking.setItem(commonGetItemAndUser.getInDbItem(bookingDtoIn.getItem()));
        booking.setStatus(BookingStatus.WAITING);
        if (Stream.of(booking.getBooker(), booking.getStart(), booking.getEnd())
                .anyMatch(Objects::isNull)) {
            throw new ValidatorException("Bad request.");
        }
        if (!booking.getItem().getAvailable()) {
            throw new ValidatorException("Bad request.");
        }
        if (booking.getStart().isAfter(booking.getEnd()) || booking.getStart().isBefore(LocalDateTime.now())
                || booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidatorException("Bad request.");
        }
        if (booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Bad request.");
        }
        try {
            return BookingMapper.toBookingDtoOut(bookingRepository.save(booking));
        } catch (DataIntegrityViolationException e) {
            throw new IdViolationException("This booking is already in base.");
        }
    }

    @Override
    public BookingDtoOut patchBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Not found."));
        Long owner = booking.getItem().getOwner().getId();
        if (!commonGetItemAndUser.getInDBUser(userId).getId().equals(owner)) {
            throw new NotFoundException("Not found.");
        }
        if (!booking.getItem().getAvailable()) {
            throw new ValidatorException("Bad request.");
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED) && approved.equals(true)) {
            throw new ValidatorException("Bad request.");
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
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFoundException("Not found."));
        Long owner = booking.getItem().getOwner().getId();
        if (!commonGetItemAndUser.getInDBUser(userId).getId().equals(owner)) {
            if (!booking.getBooker().getId().equals(userId)) {
                throw new NotFoundException("Not found.");
            }
        }
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Override
    public List<BookingDtoOut> getBookingOwnerByStatus(Long userId, BookingState status) {
        User user = commonGetItemAndUser.getInDBUser(userId);
        List<Booking> searchBooking = new ArrayList<>();
        switch (status) {
            case ALL:
                searchBooking = bookingRepository
                        .findAllByBookerOrderByStartDesc(user);
                break;
            case CURRENT:
                searchBooking = bookingRepository
                        .findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(user, LocalDateTime.now(), LocalDateTime.now());
                break;
            case FUTURE:
                searchBooking = bookingRepository
                        .findAllByBookerAndStartAfterOrderByStartDesc(user, LocalDateTime.now());
                break;
            case WAITING:
                searchBooking = bookingRepository
                        .findAllByBookerAndStatusLikeOrderByStartDesc(user, BookingStatus.WAITING);
                break;
            case REJECTED:
                searchBooking = bookingRepository
                        .findAllByBookerAndStatusLikeOrderByStartDesc(user, BookingStatus.REJECTED);
                break;
            case PAST:
                searchBooking = bookingRepository
                        .findAllByBookerAndEndBeforeAndStatusNot(user, LocalDateTime.now(), BookingStatus.REJECTED);
                break;
            default:
                throw new NotFoundException("Not found.");
        }
        return searchBooking.stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoOut> getBookingItemsByStatus(Long userId, BookingState approved) {
        User user = commonGetItemAndUser.getInDBUser(userId);
        List<BookingDtoOut> searchBookings = bookingRepository.findAllBookingOfUserInItem(userId).stream()
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
                return searchBookings.stream().filter(b -> b.getStatus().equals(BookingStatus.WAITING))
                        .collect(Collectors.toList());
            case REJECTED:
                return searchBookings.stream().filter(b -> b.getStatus().equals(BookingStatus.REJECTED))
                        .collect(Collectors.toList());
            case PAST:
                return searchBookings.stream().filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                        .collect(Collectors.toList());
            default:
                throw new NotFoundException("Not found.");
        }
    }
}
