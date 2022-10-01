package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class IdViolationExceptionTest {
    @Test
    void IdViolationException() {
        String s = "Id validation.";
        IdViolationException exception = new IdViolationException(s);

        assertThat(exception.getMessage(), equalTo(s));
    }
}
