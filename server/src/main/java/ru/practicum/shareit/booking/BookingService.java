package ru.practicum.shareit.booking;


import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;

import java.util.List;

public interface BookingService {
    BookingDtoOut createBooking(BookingDtoIn bookingDtoIn, Long userId);

    BookingDtoOut patchBooking(Long userId, Long bookingId, Boolean approve);

    BookingDtoOut getBooking(Long userId, Long bookingId);

    List<BookingDtoOut> getBookingOwnerByStatus(Long userId, BookingState approved, Integer from, Integer size);

    List<BookingDtoOut> getBookingItemsByStatus(Long userId, BookingState approved, Integer from, Integer size);

}
