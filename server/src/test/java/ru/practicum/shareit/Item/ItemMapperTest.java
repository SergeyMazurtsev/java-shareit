package ru.practicum.shareit.Item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ItemMapperTest {
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(1L)
                .name("Test")
                .description("Testing")
                .available(true)
                .request(ItemRequest.builder().id(1L).build())
                .comments(Set.of(Comment.builder().id(1L).author(User.builder().id(1L).name("User").build()).build()))
                .build();
        itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .comments(Set.of(CommentDto.builder().id(1L).author("User").build()))
                .build();
    }

    @Test
    void toItem() {
        Item testItem = ItemMapper.toItem(itemDto);

        assertThat(testItem.getId(), equalTo(item.getId()));
        assertThat(testItem.getName(), equalTo(item.getName()));
        assertThat(testItem.getDescription(), equalTo(item.getDescription()));
        assertThat(testItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(testItem.getRequest(), equalTo(item.getRequest()));
    }

    @Test
    void toItemDto() {
        ItemDto testItemDto = ItemMapper.toItemDto(item);

        assertThat(testItemDto.getId(), equalTo(itemDto.getId()));
        assertThat(testItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(testItemDto.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(testItemDto.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(testItemDto.getRequestId(), equalTo(itemDto.getRequestId()));
        assertThat(testItemDto.getComments(), equalTo(itemDto.getComments()));
    }

    @Test
    void patchItem() {
        itemDto.setName("Test2");
        itemDto.setDescription("Testing2");

        ItemMapper.patchItem(itemDto, item);
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
    }
}
