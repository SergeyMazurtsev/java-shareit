package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ItemRequestMapperTest {
    private ItemRequestDtoIn itemRequestDtoIn;
    private ItemRequestDtoOut itemRequestDtoOut;
    private ItemRequest itemRequest;
    private User user;
    private Set<ItemDto> items = Set.of(ItemDto.builder()
            .id(1L).description("Test").available(true).build());

    @BeforeEach
    void setUp() {
        LocalDateTime time = LocalDateTime.now();
        user = User.builder()
                .id(1L)
                .name("Test")
                .email("qwerty@qq.ru").build();
        itemRequestDtoIn = ItemRequestDtoIn.builder()
                .id(1L)
                .description("Testing").build();
        itemRequestDtoOut = ItemRequestDtoOut.builder()
                .id(1L)
                .description("Testing")
                .created(time).build();
        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Testing")
                .created(time)
                .requestor(user).build();
    }

    @Test
    void toRequestDtoOut() {
        ItemRequestDtoOut testRequestOut = ItemRequestMapper.toRequestDtoOut(itemRequest);

        assertThat(testRequestOut.getId(), equalTo(itemRequestDtoOut.getId()));
        assertThat(testRequestOut.getDescription(), equalTo(itemRequestDtoOut.getDescription()));
        assertThat(testRequestOut.getCreated(), equalTo(itemRequestDtoOut.getCreated()));
    }

    @Test
    void toRequest() {
        ItemRequest testRequest = ItemRequestMapper.toRequest(itemRequestDtoIn);

        assertThat(testRequest.getId(), equalTo(itemRequest.getId()));
        assertThat(testRequest.getDescription(), equalTo(itemRequest.getDescription()));
    }
}
