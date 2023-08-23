package ru.practicum.service.exception.model;

public class EventStateException extends RuntimeException {
    public EventStateException(String message) {
        super(message);
    }
}
