package ru.practicum.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.location.dto.LocationDto;
import ru.practicum.service.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.service.event.model.DateTimeFormat.PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    @JsonFormat(pattern = PATTERN)
    LocalDateTime createdOn;
    String description;
    @JsonFormat(pattern = PATTERN)
    LocalDateTime eventDate;
    Long id;
    UserShortDto initiator;
    LocationDto location;
    Boolean paid;
    Integer participantLimit;
    @JsonFormat(pattern = PATTERN)
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventState state;
    String title;
    Long views;
}
