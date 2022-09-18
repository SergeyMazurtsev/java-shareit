package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ItemRequestTest {
    @Test
    void requestEquals() {
        User user = User.builder().id(1L).build();
        LocalDateTime time = LocalDateTime.now();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L).description("qwerty").requestor(user).created(time).build();
        ItemRequest itemRequest2 = ItemRequest.builder()
                .id(1L).description("qwerty").requestor(user).created(time).build();
        assertThat(itemRequest, is(itemRequest2));
        assertThat(itemRequest.hashCode(), is(itemRequest2.hashCode()));
        assertThat(itemRequest.getId(), is(itemRequest2.getId()));
        assertThat(itemRequest.getDescription(), is(itemRequest2.getDescription()));
        assertThat(itemRequest.getRequestor(), is(itemRequest2.getRequestor()));
        assertThat(itemRequest.getCreated(), is(itemRequest2.getCreated()));

        ItemRequest itemRequest3 = new ItemRequest();
        ItemRequest itemRequest4 = new ItemRequest();
        assertThat(itemRequest3.getDescription(), is(itemRequest4.getDescription()));
    }
}
