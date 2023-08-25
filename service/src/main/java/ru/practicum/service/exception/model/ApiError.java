package ru.practicum.service.exception.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.service.event.model.DateTimeFormat.PATTERN;

@Builder
@Data
public class ApiError {
    private List<String> errors;
    private String message;
    private String reason;
    private HttpStatus status;
    @Builder.Default
    private String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(PATTERN));
}
