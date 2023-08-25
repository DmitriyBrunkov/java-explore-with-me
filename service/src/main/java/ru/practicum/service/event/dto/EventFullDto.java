package ru.practicum.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.location.dto.LocationDto;
import ru.practicum.service.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.service.event.model.DateTimeFormat.PATTERN;

@Data
@NoArgsConstructor
public class EventFullDto {
    private String annotation;
    private CategoryDto category;
    private long confirmedRequests;
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime eventDate;
    private Long id;
    private UserShortDto initiator;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    private EventState state;
    private String title;
    private Long views;

    public EventFullDto(String annotation, CategoryDto category, LocalDateTime createdOn, String description, LocalDateTime eventDate, Long id, UserShortDto initiator, LocationDto location, Boolean paid, Integer participantLimit, LocalDateTime publishedOn, Boolean requestModeration, EventState state, String title, Long views) {
        this.annotation = annotation;
        this.category = category;
        this.createdOn = createdOn;
        this.description = description;
        this.eventDate = eventDate;
        this.id = id;
        this.initiator = initiator;
        this.location = location;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.requestModeration = requestModeration;
        this.state = state;
        this.title = title;
        this.views = views;
    }
}
