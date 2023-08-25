package ru.practicum.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.request.service.RequestService;
import ru.practicum.service.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.service.event.model.DateTimeFormat.PATTERN;

@Data
@NoArgsConstructor
public class EventShortDto {
    private RequestService requestService;
    private String annotation;
    private CategoryDto category;
    private long confirmedRequests;
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime eventDate;
    private Long id;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Long views;

    public EventShortDto(String annotation, CategoryDto category, LocalDateTime eventDate, Long id, UserShortDto initiator, Boolean paid, String title, Long views) {
        this.annotation = annotation;
        this.category = category;
        this.eventDate = eventDate;
        this.id = id;
        this.initiator = initiator;
        this.paid = paid;
        this.title = title;
        this.views = views;
    }
}
