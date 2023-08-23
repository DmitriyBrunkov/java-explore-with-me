package ru.practicum.service.exception.model;

public class RequestValidationException extends RuntimeException {
    public RequestValidationException(String message) {
        super(message);
    }
}
