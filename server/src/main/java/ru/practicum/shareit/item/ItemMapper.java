package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.UserMapper;

import java.util.stream.Collectors;

@Component
public class ItemMapper {
    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .request((itemDto.getRequestId() != null) ?
                        ItemRequest.builder().id(itemDto.getRequestId()).build() : null)
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId((item.getRequest() != null) ? item.getRequest().getId() : null)
                .comments((item.getComments() != null) ? item.getComments().stream().map(CommentMapper::toCommentDto)
                        .collect(Collectors.toSet()) : null)
                .owner((item.getOwner() != null) ? UserMapper.toUserDto(item.getOwner()) : null)
                .build();
    }

    public static void patchItem(ItemDto itemDto, Item item) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
    }
}
