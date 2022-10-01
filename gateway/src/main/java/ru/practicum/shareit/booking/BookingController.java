package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/bookings")
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody @Valid BookingDto bookingDto,
                                                @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Create booking={}, of user={}", bookingDto, userId);
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<?> patchBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long bookingId,
                                          @NotNull @RequestParam Boolean approved) {
        log.info("Patch booking={}, of user={}, with flag={}", bookingId, userId, approved);
        return bookingClient.patchBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking={}, of user={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping()
    public ResponseEntity<Object> getBookingOwnerByStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam).orElse(null);
        if (state == null) {
            String message = "Unknown state: " + stateParam;
            Map<String, String> answer = new HashMap<>();
            answer.put("error", message);
            return new ResponseEntity<Object>(answer, HttpStatus.BAD_REQUEST);
        }
        log.info("Get pages from={}, size={}, with state={}, bookings of user={}", from, size, stateParam, userId);
        return bookingClient.getBookingOwnerByStatus(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingItemsByStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                          @RequestParam(name = "state", defaultValue = "ALL") String stateParam,
                                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam).orElse(null);
        if (state == null) {
            String message = "Unknown state: " + stateParam;
            Map<String, String> answer = new HashMap<>();
            answer.put("error", message);
            return new ResponseEntity<Object>(answer, HttpStatus.BAD_REQUEST);
        }
        log.info("Get pages from={}, size={}, with state={}, bookings of user={}", from, size, stateParam, userId);
        return bookingClient.getBookingItemsByStatus(userId, state, from, size);
    }
}
