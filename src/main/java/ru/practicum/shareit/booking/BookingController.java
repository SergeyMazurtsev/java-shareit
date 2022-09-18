package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping()
    public ResponseEntity<?> createBooking(@RequestBody BookingDtoIn bookingDtoIn, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(bookingService.createBooking(bookingDtoIn, userId));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<?> patchBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return ResponseEntity.ok(bookingService.patchBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.getBooking(userId, bookingId));
    }

    @GetMapping()
    public ResponseEntity<?> getBookingOwnerByStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL") BookingState state,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(bookingService.getBookingOwnerByStatus(userId, state, from, size));
    }

    @GetMapping("/owner")
    public ResponseEntity<?> getBookingItemsByStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL") BookingState state,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(bookingService.getBookingItemsByStatus(userId, state, from, size));
    }
}
