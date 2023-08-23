package ru.practicum.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.service.event.enums.UserRequestState;
import ru.practicum.service.location.dto.LocationDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.service.event.model.DateTimeFormat.PATTERN;

@Data
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000)
    String annotation;
    @PositiveOrZero
    Long category;
    @Size(min = 20, max = 7000)
    String description;
    @JsonFormat(pattern = PATTERN)
    @Future
    LocalDateTime eventDate;
    LocationDto location;
    Boolean paid;
    @PositiveOrZero
    Integer participantLimit;
    Boolean requestModeration;
    UserRequestState stateAction;
    @Size(min = 3, max = 120)
    String title;
}
