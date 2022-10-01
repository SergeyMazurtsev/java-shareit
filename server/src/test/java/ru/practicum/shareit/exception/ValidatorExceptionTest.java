package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ValidatorExceptionTest {
    @Test
    void ValidatorException() {
        String s = "Validator.";
        ValidatorException exception = new ValidatorException(s);

        assertThat(exception.getMessage(), equalTo(s));
    }
}
