package ru.practicum.service.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.service.exception.model.*;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ExceptionController {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleObjectNotFoundException(final ObjectNotFoundException e) {
        log.error(e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .reason("The required object was not found")
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBedRequestException(final BindException e) {
        log.error(e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(EventDateTimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBedRequestException(final RuntimeException e) {
        log.error(e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler({EventStateException.class,
            RequestValidationException.class,
            DataIntegrityViolationException.class,
            ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictEx(final RuntimeException e) {
        log.error(e.getMessage());
        return ApiError.builder()
                .status(HttpStatus.CONFLICT)
                .message(e.getMessage())
                .build();
    }
}
