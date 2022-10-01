package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@Slf4j
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ResponseEntity<?> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody ItemRequestDtoIn requestDtoIn) {
        return ResponseEntity.ok(requestService.createRequest(userId, LocalDateTime.now(), requestDtoIn));
    }

    @GetMapping
    public ResponseEntity<?> getRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(requestService.getRequests(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getRequestsPage(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok(requestService.getRequestsPage(userId, from, size));
    }

    @GetMapping({"/{requestId}"})
    public ResponseEntity<?> getRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable Long requestId) {
        return ResponseEntity.ok(requestService.getRequestById(userId, requestId));
    }
}
