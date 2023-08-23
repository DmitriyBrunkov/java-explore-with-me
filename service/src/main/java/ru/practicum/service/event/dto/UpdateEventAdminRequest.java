package ru.practicum.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.service.event.enums.AdminRequestState;
import ru.practicum.service.location.dto.LocationDto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Future;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static ru.practicum.service.event.model.DateTimeFormat.PATTERN;

@Data
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000)
    private String annotation;
    @PositiveOrZero
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
    @Enumerated(EnumType.STRING)
    private AdminRequestState stateAction;
    @Size(min = 3, max = 120)
    private String title;
}
