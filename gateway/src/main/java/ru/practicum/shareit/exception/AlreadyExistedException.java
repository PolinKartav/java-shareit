package ru.practicum.shareit.exception;

public class AlreadyExistedException extends RuntimeException {
    public AlreadyExistedException(String message) {
        super(message);
    }
}
