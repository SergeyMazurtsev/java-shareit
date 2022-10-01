package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ItemRequestDtoOutTest {
    @Test
    void ItemRequestDtoOutEqual() {
        ItemRequestDtoOut requestDtoOut = new ItemRequestDtoOut();
        ItemRequestDtoOut requestDtoOut1 = new ItemRequestDtoOut();
        assertThat(requestDtoOut.getDescription(), is(requestDtoOut1.getDescription()));
    }
}
