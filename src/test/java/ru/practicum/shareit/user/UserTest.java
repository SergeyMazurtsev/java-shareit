package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class UserTest {
    @Test
    void userEqual() {
        Set<Item> items = Set.of(
                Item.builder().id(1L).build(),
                Item.builder().id(2L).build());
        User user = User.builder().id(1L)
                .name("Test").email("qwerty@qqq.ru").items(items).build();
        User user2 = User.builder().id(1L)
                .name("Test").email("qwerty@qqq.ru").items(items).build();
        assertThat(user, is(user2));
        assertThat(user.getItems(), is(user2.getItems()));
        assertThat(user.hashCode(), is(user2.hashCode()));
        User user3 = new User();
        User user4 = new User();
        assertThat(user3.getName(), equalTo(user4.getName()));
    }
}
