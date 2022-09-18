package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class BookingMapperTest {
    private Booking booking;
    private BookingDtoOut bookingDtoOut;
    private BookingDtoIn bookingDtoIn;
    private BookingShortDto bookingShortDto;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private ItemDto itemDto;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        start = LocalDateTime.now().minusMinutes(10);
        end = LocalDateTime.now().minusMinutes(5);
        item = Item.builder()
                .id(1L)
                .name("Test item")
                .build();
        itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
        user = User.builder()
                .id(1L)
                .name("Test name")
                .build();
        userDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
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
                .build();
        bookingShortDto = BookingShortDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    @Test
    void toBooking() {
        Booking testBooking = BookingMapper.toBooking(bookingDtoIn);

        assertThat(testBooking.getStart(), equalTo(booking.getStart()));
        assertThat(testBooking.getEnd(), equalTo(booking.getEnd()));
    }

    @Test
    void toBookingDtoOut() {
        BookingDtoOut testBookingDtoOut = BookingMapper.toBookingDtoOut(booking);

        assertThat(testBookingDtoOut.getId(), equalTo(bookingDtoOut.getId()));
        assertThat(testBookingDtoOut.getStart(), equalTo(bookingDtoOut.getStart()));
        assertThat(testBookingDtoOut.getEnd(), equalTo(bookingDtoOut.getEnd()));
        assertThat(testBookingDtoOut.getItem(), equalTo(bookingDtoOut.getItem()));
        assertThat(testBookingDtoOut.getBooker(), equalTo(bookingDtoOut.getBooker()));
        assertThat(testBookingDtoOut.getStatus(), equalTo(bookingDtoOut.getStatus()));
    }

    @Test
    void bookingShortDto() {
        BookingShortDto testBookingShortDto = BookingMapper.bookingShortDto(booking);

        assertThat(testBookingShortDto.getId(), equalTo(bookingShortDto.getId()));
        assertThat(testBookingShortDto.getBookerId(), equalTo(bookingShortDto.getBookerId()));
    }
}
