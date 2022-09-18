package ru.practicum.shareit.Item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ItemTest {
    @Test
    void itemEquals() {
        Item item = new Item();
        Item item1 = new Item();

        assertThat(item.getName(), equalTo(item1.getName()));
        assertThat(item.hashCode(), equalTo(item1.hashCode()));
    }
}
