package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Set;

public interface ItemService {
    ItemDto createItem(ItemDto itemDto, Long userId);

    ItemDto patchItem(ItemDto itemDto, Long itemId, Long userId);

    void deleteItem(Long itemId, Long userId);

    ItemDto getItem(Long itemId, Long userId);

    List<ItemDto> getItemsOfUser(Long userId, Integer from, Integer size);

    List<ItemDto> searchItems(String text, Long userId, Integer from, Integer size);

    CommentDto addCommentToItem(Long itemId, Long userId, CommentDto commentDto);

}
