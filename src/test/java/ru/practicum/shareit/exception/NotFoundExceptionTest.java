package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class NotFoundExceptionTest {
    @Test
    void NotFoundException() {
        String s = "Not found.";
        NotFoundException exception = new NotFoundException(s);

        assertThat(exception.getMessage(), equalTo(s));
    }
}
