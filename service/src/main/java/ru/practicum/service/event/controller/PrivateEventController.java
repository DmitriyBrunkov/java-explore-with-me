package ru.practicum.service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.category.model.Category;
import ru.practicum.service.category.service.CategoryService;
import ru.practicum.service.event.EventMapper;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.dto.NewEventDto;
import ru.practicum.service.event.dto.UpdateEventUserRequest;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.service.EventService;
import ru.practicum.service.location.model.Location;
import ru.practicum.service.location.service.LocationService;
import ru.practicum.service.request.RequestMapper;
import ru.practicum.service.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.service.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.service.request.dto.ParticipationRequestDto;
import ru.practicum.service.request.service.RequestService;
import ru.practicum.service.stats.StatsService;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping("/users/{userId}/events")
@Slf4j
@RequiredArgsConstructor
public class PrivateEventController {
    private final EventService eventService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final RequestService requestService;
    private final StatsService statsService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable @Positive Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        log.info("{}: POST: userId: {} newEventDto: {} ", this.getClass().getSimpleName(), userId, newEventDto);
        User user = userService.getUser(userId);
        Category category = categoryService.getCategory(newEventDto.getCategory());
        Location location = locationService.addLocation(newEventDto.getLocation()
                .getLat(), newEventDto.getLocation().getLon());
        Event event = eventService.addEvent(EventMapper.toEvent(user, category, location,
                LocalDateTime.now(), EventState.PENDING, newEventDto));
        Long views = statsService.getHitsCount(event.getId(), event.getCreatedOn());
        return EventMapper.toEventFullDto(views, event);
    }

    @GetMapping
    public List<EventShortDto> getAllEvents(@PathVariable @Positive Long userId,
                                            @RequestParam(defaultValue = "0", required = false) int from,
                                            @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("{}: GET: ALL: userId: {} from: {} size: {}", this.getClass().getSimpleName(), userId, from, size);
        return eventService.getEvents(userId, from, size).stream()
                .map(event -> {
                    EventShortDto eventShortDto =
                            EventMapper.toEventShortDto(statsService.getHitsCount(event.getId(), event.getCreatedOn()), event);
                    eventShortDto.setConfirmedRequests(requestService.getConfirmedRequests(eventShortDto.getId()));
                    return eventShortDto;
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId) {
        log.info("{}: GET: userId: {} eventId: {}", this.getClass().getSimpleName(), userId, eventId);
        Event event = eventService.getEvent(userId, eventId);
        Long views = statsService.getHitsCount(event.getId(), event.getCreatedOn());
        return EventMapper.toEventFullDto(views, event);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info("{}: PATCH: userId: {} eventId: {} updateEventUserRequest: {}", this.getClass().getSimpleName(),
                userId, eventId, updateEventUserRequest);
        Event event = eventService.updateEventUser(userId, eventId, updateEventUserRequest);
        Long views = statsService.getHitsCount(event.getId(), event.getCreatedOn());
        return EventMapper.toEventFullDto(views, event);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequestsByUserEvents(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId) {
        log.info("{}: GET: REQUESTS: userId: {} eventId: {}", this.getClass().getSimpleName(), userId, eventId);
        userService.getUser(userId);
        eventService.getEvent(eventId);
        return requestService.getRequests(eventId).stream().map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable @Positive Long userId, @PathVariable @Positive Long eventId,
                                                              @RequestBody @Valid EventRequestStatusUpdateRequest updateRequest) {
        log.info("{}: PATCH: REQUESTS: userId: {} eventId: {} updateRequest: {}", this.getClass()
                .getSimpleName(), userId, eventId, updateRequest);
        return eventService.updateRequestStatus(userId, eventId, updateRequest);
    }
}
