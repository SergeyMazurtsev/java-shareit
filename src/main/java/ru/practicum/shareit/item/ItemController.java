package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping("/items")
    public ResponseEntity<?> createItem(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.createItem(itemDto, userId));
    }

    @PatchMapping("/items/{itemId}")
    public ResponseEntity<?> patchItem(@RequestBody ItemDto itemDto, @PathVariable Long itemId,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.patchItem(itemDto, itemId, userId));
    }

    @DeleteMapping("/items/{itemId}")
    public void deleteItem(@PathVariable Long itemId,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        itemService.deleteItem(itemId, userId);
    }

    @GetMapping("/items/{itemId}")
    public ResponseEntity<?> getItem(@PathVariable Long itemId,
                                     @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.getItem(itemId, userId));
    }

    @GetMapping("/items")
    public ResponseEntity<?> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.getItemsOfUser(userId));
    }

    @GetMapping("/items/search")
    public ResponseEntity<?> searchItems(@RequestParam String text,
                                         @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(itemService.searchItems(text, userId));
    }

    @PostMapping("/items/{itemId}/comment")
    public ResponseEntity<?> addCommentToItem(@PathVariable Long itemId,
                                              @RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok(itemService.addCommentToItem(itemId, userId, commentDto));
    }
}
