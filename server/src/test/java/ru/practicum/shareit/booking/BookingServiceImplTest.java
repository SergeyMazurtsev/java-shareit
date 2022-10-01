package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.CommonService;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidatorException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {
    private final BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
    private final CommonService commonService = Mockito.mock(CommonService.class);
    private BookingService bookingService = new BookingServiceImpl(bookingRepository, commonService);

    private Booking booking;
    private BookingDtoOut bookingDtoOut;
    private BookingDtoIn bookingDtoIn;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private ItemDto itemDto;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().plusMinutes(1);
        end = LocalDateTime.now().plusMinutes(10);
        user = User.builder()
                .id(1L)
                .name("Test name")
                .build();
        userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
        item = Item.builder()
                .id(1L)
                .name("Test item")
                .owner(user)
                .available(true)
                .build();
        itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.getAvailable())
                .build();
        booking = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
        bookingDtoOut = BookingDtoOut.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(itemDto)
                .booker(userDto)
                .status(booking.getStatus())
                .build();
        bookingDtoIn = BookingDtoIn.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem().getId())
                .build();
    }

    @Test
    void createBooking() {
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user);
        when(commonService.getInDbItem(anyLong()))
                .thenReturn(item);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        BookingDtoOut testBookingDtoOut = bookingService.createBooking(bookingDtoIn, 10L);
        assertThat(testBookingDtoOut.getId(), equalTo(bookingDtoOut.getId()));
        assertThat(testBookingDtoOut.getStart(), equalTo(bookingDtoOut.getStart()));
        assertThat(testBookingDtoOut.getEnd(), equalTo(bookingDtoOut.getEnd()));
        assertThat(testBookingDtoOut.getItem(), equalTo(bookingDtoOut.getItem()));
        assertThat(testBookingDtoOut.getBooker(), equalTo(bookingDtoOut.getBooker()));
        assertThat(testBookingDtoOut.getStatus(), equalTo(bookingDtoOut.getStatus()));

        BookingDtoIn bookingDtoIn1 = BookingDtoIn.builder()
                .start(null)
                .end(bookingDtoIn.getEnd())
                .item(bookingDtoIn.getItem())
                .build();
        try {
            BookingDtoOut testBookingDtoOut1 = bookingService.createBooking(bookingDtoIn1, 10L);
        } catch (ValidatorException e) {
            assertThat(e.getMessage(), equalTo("Bad request. Booker or start or end is null."));
        }

        Item item1 = Item.builder()
                .id(1L)
                .name("Test item")
                .owner(user)
                .available(false)
                .build();
        when(commonService.getInDbItem(anyLong()))
                .thenReturn(item1);
        try {
            BookingDtoOut testBookingDtoOut1 = bookingService.createBooking(bookingDtoIn, 10L);
        } catch (ValidatorException e) {
            assertThat(e.getMessage(), equalTo("Bad request. Item is not available."));
        }

        BookingDtoIn bookingDtoIn2 = BookingDtoIn.builder()
                .start(start.minusMinutes(10))
                .end(end.minusMinutes(50))
                .item(bookingDtoIn.getItem())
                .build();
        when(commonService.getInDbItem(anyLong()))
                .thenReturn(item);
        try {
            BookingDtoOut testBookingDtoOut1 = bookingService.createBooking(bookingDtoIn2, 10L);
        } catch (ValidatorException e) {
            assertThat(e.getMessage(), equalTo("Bad request. Start is after end or start is before now or end is before now."));
        }

        try {
            BookingDtoOut testBookingDtoOut1 = bookingService.createBooking(bookingDtoIn, user.getId());
        } catch (NotFoundException e) {
            assertThat(e.getMessage(), equalTo("Bad request. User id with id of owner is not equal."));
        }

        verify(commonService, times(5))
                .getInDBUser(anyLong());
        verify(commonService, times(5))
                .getInDbItem(anyLong());
        verify(bookingRepository, times(1))
                .save(any(Booking.class));
    }

    @Test
    void patchBooking() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user);
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking);

        bookingDtoOut.setStatus(BookingStatus.APPROVED);
        BookingDtoOut testBookingDtoOut = bookingService.patchBooking(user.getId(), booking.getId(), true);
        assertThat(testBookingDtoOut.getId(), equalTo(bookingDtoOut.getId()));
        assertThat(testBookingDtoOut.getStart(), equalTo(bookingDtoOut.getStart()));
        assertThat(testBookingDtoOut.getEnd(), equalTo(bookingDtoOut.getEnd()));
        assertThat(testBookingDtoOut.getItem(), equalTo(bookingDtoOut.getItem()));
        assertThat(testBookingDtoOut.getBooker(), equalTo(bookingDtoOut.getBooker()));
        assertThat(testBookingDtoOut.getStatus(), equalTo(bookingDtoOut.getStatus()));

        User user1 = User.builder()
                .id(10L)
                .name("Test name")
                .build();
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user1);
        try {
            BookingDtoOut testBookingDtoOut1 = bookingService.patchBooking(user1.getId(), booking.getId(), true);
        } catch (NotFoundException e) {
            assertThat(e.getMessage(), equalTo("Not found. User id with id of owner is not equal."));
        }
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user);

        Item item1 = Item.builder()
                .id(1L)
                .name("Test item")
                .owner(user)
                .available(false)
                .build();
        Booking booking1 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item1)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking1));
        try {
            BookingDtoOut testBookingDtoOut1 = bookingService.patchBooking(user.getId(), booking.getId(), true);
        } catch (ValidatorException e) {
            assertThat(e.getMessage(), equalTo("Bad request. Item is not available."));
        }

        Booking booking2 = Booking.builder()
                .id(1L)
                .start(start)
                .end(end)
                .item(item)
                .booker(user)
                .status(BookingStatus.APPROVED)
                .build();
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking2));
        try {
            BookingDtoOut testBookingDtoOut1 = bookingService.patchBooking(user.getId(), booking.getId(), true);
        } catch (ValidatorException e) {
            assertThat(e.getMessage(), equalTo("Bad request. Booking is already approved."));
        }

        verify(bookingRepository, times(4))
                .findById(anyLong());
        verify(commonService, times(4))
                .getInDBUser(anyLong());
        verify(bookingRepository, times(1))
                .save(any(Booking.class));
    }

    @Test
    void getBooking() {
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(booking));
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user);

        BookingDtoOut testBookingDtoOut = bookingService.getBooking(user.getId(), booking.getId());
        assertThat(testBookingDtoOut.getId(), equalTo(bookingDtoOut.getId()));
        assertThat(testBookingDtoOut.getStart(), equalTo(bookingDtoOut.getStart()));
        assertThat(testBookingDtoOut.getEnd(), equalTo(bookingDtoOut.getEnd()));
        assertThat(testBookingDtoOut.getItem(), equalTo(bookingDtoOut.getItem()));
        assertThat(testBookingDtoOut.getBooker(), equalTo(bookingDtoOut.getBooker()));
        assertThat(testBookingDtoOut.getStatus(), equalTo(bookingDtoOut.getStatus()));

        User user1 = User.builder()
                .id(50L)
                .name("Test name")
                .build();
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user1);
        try {
            BookingDtoOut testBookingDtoOut1 = bookingService.getBooking(user1.getId(), booking.getId());
        } catch (NotFoundException e) {
            assertThat(e.getMessage(), equalTo("Not found. User id is not equal with owner item or booker id."));
        }
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user);

        verify(bookingRepository, times(2))
                .findById(anyLong());
        verify(commonService, times(2))
                .getInDBUser(anyLong());
    }

    @Test
    void getBookingOwnerByStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        List<BookingDtoOut> bookingDtoOuts = List.of(bookingDtoOut);
        List<Booking> bookings = List.of(booking);
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user);
        when(commonService.getPagination(anyInt(), anyInt(), any()))
                .thenReturn(pageable);
        when(bookingRepository.findAllByBookerOrderByStartDesc(any(User.class), any(Pageable.class)))
                .thenReturn(bookings);
        when(bookingRepository.findAllByBookerAndStartBeforeAndEndAfterOrderByStartDesc(
                any(User.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookings);
        when(bookingRepository.findAllByBookerAndStartAfterOrderByStartDesc(
                any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookings);
        when(bookingRepository.findAllByBookerAndStatusLikeOrderByStartDesc(
                any(User.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(bookings);
        when(bookingRepository.findAllByBookerAndEndBeforeAndStatusNot(
                any(User.class), any(LocalDateTime.class), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingDtoOut> testBookingDtoOut = bookingService.getBookingOwnerByStatus(
                user.getId(), BookingState.ALL, 0, 10);
        assertThat(testBookingDtoOut, hasSize(bookingDtoOuts.size()));
        assertThat(testBookingDtoOut.get(0).getId(), equalTo(bookingDtoOuts.get(0).getId()));
        assertThat(testBookingDtoOut.get(0).getStart(), equalTo(bookingDtoOuts.get(0).getStart()));
        assertThat(testBookingDtoOut.get(0).getEnd(), equalTo(bookingDtoOuts.get(0).getEnd()));
        assertThat(testBookingDtoOut.get(0).getItem(), equalTo(bookingDtoOuts.get(0).getItem()));
        assertThat(testBookingDtoOut.get(0).getBooker(), equalTo(bookingDtoOuts.get(0).getBooker()));
        assertThat(testBookingDtoOut.get(0).getStatus(), equalTo(bookingDtoOuts.get(0).getStatus()));

        List<BookingDtoOut> testBookingDtoOut1 = bookingService.getBookingOwnerByStatus(
                user.getId(), BookingState.CURRENT, 0, 10);
        assertThat(testBookingDtoOut1, hasSize(bookingDtoOuts.size()));

        List<BookingDtoOut> testBookingDtoOut2 = bookingService.getBookingOwnerByStatus(
                user.getId(), BookingState.FUTURE, 0, 10);
        assertThat(testBookingDtoOut2, hasSize(bookingDtoOuts.size()));

        List<BookingDtoOut> testBookingDtoOut3 = bookingService.getBookingOwnerByStatus(
                user.getId(), BookingState.WAITING, 0, 10);
        assertThat(testBookingDtoOut3, hasSize(bookingDtoOuts.size()));

        List<BookingDtoOut> testBookingDtoOut4 = bookingService.getBookingOwnerByStatus(
                user.getId(), BookingState.REJECTED, 0, 10);
        assertThat(testBookingDtoOut4, hasSize(bookingDtoOuts.size()));

        List<BookingDtoOut> testBookingDtoOut5 = bookingService.getBookingOwnerByStatus(
                user.getId(), BookingState.PAST, 0, 10);
        assertThat(testBookingDtoOut5, hasSize(bookingDtoOuts.size()));

        verify(commonService, times(6))
                .getInDBUser(anyLong());
        verify(commonService, times(6))
                .getPagination(anyInt(), anyInt(), any());
    }

    @Test
    void getBookingItemsByStatus() {
        Pageable pageable = PageRequest.of(0, 10);
        List<BookingDtoOut> bookingDtoOuts = List.of(bookingDtoOut);
        List<Booking> bookings = List.of(booking);
        when(commonService.getInDBUser(anyLong()))
                .thenReturn(user);
        when(commonService.getPagination(anyInt(), anyInt(), any()))
                .thenReturn(pageable);
        when(bookingRepository.findAllBookingOfUserInItem(anyLong(), any(Pageable.class)))
                .thenReturn(bookings);

        List<BookingDtoOut> testBookingDtoOut = bookingService.getBookingItemsByStatus(
                user.getId(), BookingState.ALL, 0, 10);
        assertThat(testBookingDtoOut, hasSize(bookingDtoOuts.size()));
        assertThat(testBookingDtoOut.get(0).getId(), equalTo(bookingDtoOuts.get(0).getId()));
        assertThat(testBookingDtoOut.get(0).getStart(), equalTo(bookingDtoOuts.get(0).getStart()));
        assertThat(testBookingDtoOut.get(0).getEnd(), equalTo(bookingDtoOuts.get(0).getEnd()));
        assertThat(testBookingDtoOut.get(0).getItem(), equalTo(bookingDtoOuts.get(0).getItem()));
        assertThat(testBookingDtoOut.get(0).getBooker(), equalTo(bookingDtoOuts.get(0).getBooker()));
        assertThat(testBookingDtoOut.get(0).getStatus(), equalTo(bookingDtoOuts.get(0).getStatus()));

        bookings.get(0).setStart(start.minusMinutes(10));
        List<BookingDtoOut> testBookingDtoOut1 = bookingService.getBookingItemsByStatus(
                user.getId(), BookingState.CURRENT, 0, 10);
        assertThat(testBookingDtoOut1, hasSize(bookingDtoOuts.size()));

        bookings.get(0).setStart(start.plusMinutes(10));
        List<BookingDtoOut> testBookingDtoOut2 = bookingService.getBookingItemsByStatus(
                user.getId(), BookingState.FUTURE, 0, 10);
        assertThat(testBookingDtoOut2, hasSize(bookingDtoOuts.size()));

        bookings.get(0).setStatus(BookingStatus.WAITING);
        List<BookingDtoOut> testBookingDtoOut3 = bookingService.getBookingItemsByStatus(
                user.getId(), BookingState.WAITING, 0, 10);
        assertThat(testBookingDtoOut3, hasSize(bookingDtoOuts.size()));

        bookings.get(0).setStatus(BookingStatus.REJECTED);
        List<BookingDtoOut> testBookingDtoOut4 = bookingService.getBookingItemsByStatus(
                user.getId(), BookingState.REJECTED, 0, 10);
        assertThat(testBookingDtoOut4, hasSize(bookingDtoOuts.size()));

        bookings.get(0).setStart(start.minusMinutes(20));
        bookings.get(0).setEnd(end.minusMinutes(15));
        List<BookingDtoOut> testBookingDtoOut5 = bookingService.getBookingItemsByStatus(
                user.getId(), BookingState.PAST, 0, 10);
        assertThat(testBookingDtoOut5, hasSize(bookingDtoOuts.size()));

        verify(commonService, times(6))
                .getInDBUser(anyLong());
        verify(commonService, times(6))
                .getPagination(anyInt(), anyInt(), any());
    }
}
