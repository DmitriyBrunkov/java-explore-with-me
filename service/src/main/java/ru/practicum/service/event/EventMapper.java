package ru.practicum.service.event;

import lombok.experimental.UtilityClass;
import ru.practicum.service.category.CategoryMapper;
import ru.practicum.service.category.model.Category;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.dto.NewEventDto;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.location.LocationMapper;
import ru.practicum.service.location.model.Location;
import ru.practicum.service.user.UserMapper;
import ru.practicum.service.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {

    public Event toEvent(User initiator, Category category, Location location,
                         LocalDateTime createdOn, EventState state, NewEventDto newEventDto) {
        return new Event(null, newEventDto.getAnnotation(), category, createdOn,
                newEventDto.getDescription(), newEventDto.getEventDate(), initiator, location,
                newEventDto.isPaid(), newEventDto.getParticipantLimit(), null, newEventDto.isRequestModeration(),
                state, newEventDto.getTitle());
    }

    public EventFullDto toEventFullDto(Long views, Event event) {
        return new EventFullDto(event.getAnnotation(), CategoryMapper.toCategoryDto(event.getCategory()),
                event.getCreatedOn(), event.getDescription(), event.getEventDate(), event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()), LocationMapper.toLocationDto(event.getLocation()),
                event.getPaid(), event.getParticipantLimit(), event.getPublishedOn(), event.getRequestModeration(),
                event.getState(), event.getTitle(), views);
    }

    public EventShortDto toEventShortDto(Long views, Event event) {
        return new EventShortDto(event.getAnnotation(), CategoryMapper.toCategoryDto(event.getCategory()),
                event.getEventDate(), event.getId(), UserMapper.toUserShortDto(event.getInitiator()), event.getPaid(), event.getTitle(), views);
    }
}
