package ru.practicum.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.service.location.dto.LocationDto;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

import static ru.practicum.service.event.model.DateTimeFormat.PATTERN;

@Data
public class NewEventDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @PositiveOrZero
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = PATTERN)
    @Future
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    private Boolean paid = false;

    @PositiveOrZero
    private Integer participantLimit = 0;

    private Boolean requestModeration = true;

    @Size(min = 3, max = 120)
    private String title;
}
