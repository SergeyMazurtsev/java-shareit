package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.comparator.BooleanComparator;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingState;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ErrorHandlerTest {
    private ErrorHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ErrorHandler();
    }

    @Test
    void validatorHandler() {
        ResponseEntity<?> response = handler.validatorHandler(new ValidatorException(""));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void userEmailHandler() {
        ResponseEntity<?> response = handler.userEmailHandler(new IdViolationException(""));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.CONFLICT));
    }

    @Test
    void notFoundHandler() {
        ResponseEntity<?> response = handler.notFoundHandler(new NotFoundException(""));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }

    @Test
    void badRequestHandler() {
        ResponseEntity<?> response = handler.badRequestHandler(new ServletRequestBindingException(""));
        assertThat(response.getStatusCode(), equalTo(HttpStatus.BAD_REQUEST));
    }
}
