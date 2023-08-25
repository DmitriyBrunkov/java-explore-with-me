package ru.practicum.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.service.location.dto.LocationDto;

import javax.validation.constraints.Future;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.service.event.model.DateTimeFormat.PATTERN;

@Data
public class UpdateEventRequest {
    @Size(min = 20, max = 2000)
    private String annotation;
    @Positive
    private Long category;
    @Size(min = 20, max = 7000)
    private String description;
    @JsonFormat(pattern = PATTERN)
    @Future
    private LocalDateTime eventDate;
    private LocationDto location;
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    @Size(min = 3, max = 120)
    private String title;
}
