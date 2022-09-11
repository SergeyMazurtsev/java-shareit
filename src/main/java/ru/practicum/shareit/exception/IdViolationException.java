package ru.practicum.shareit.exception;

public class IdViolationException extends RuntimeException {
    public IdViolationException(String message) {
        super(message);
    }
}
