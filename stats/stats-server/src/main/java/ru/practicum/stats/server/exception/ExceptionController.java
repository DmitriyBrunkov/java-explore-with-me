package ru.practicum.stats.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.stats.server.exception.model.IntervalValidationException;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIntervalValidationException(final IntervalValidationException e) {
        log.error(e.getMessage());
        return Map.of("error", e.getMessage());
    }
}
