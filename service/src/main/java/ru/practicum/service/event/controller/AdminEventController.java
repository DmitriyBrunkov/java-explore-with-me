package ru.practicum.service.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.category.service.CategoryService;
import ru.practicum.service.event.EventMapper;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.service.EventService;
import ru.practicum.service.exception.model.EventDateTimeException;
import ru.practicum.service.exception.model.EventStateException;
import ru.practicum.service.location.service.LocationService;
import ru.practicum.service.request.service.RequestService;
import ru.practicum.service.stats.StatsService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.service.event.model.DateTimeFormat.PATTERN;

@RestController
@Validated
@RequestMapping("/admin/events")
@Slf4j
public class AdminEventController {
    private final EventService eventService;
    private final RequestService requestService;
    private final StatsService statsService;
    private final CategoryService categoryService;
    private final LocationService locationService;

    @Autowired
    public AdminEventController(EventService eventService, RequestService requestService, StatsService statsService, CategoryService categoryService, LocationService locationService) {
        this.eventService = eventService;
        this.requestService = requestService;
        this.statsService = statsService;
        this.categoryService = categoryService;
        this.locationService = locationService;
    }

    @GetMapping
    public List<EventFullDto> getAllEvents(@RequestParam(required = false) Set<Long> users,
                                                  @RequestParam(required = false) Set<EventState> states,
                                                  @RequestParam(required = false) Set<Long> categories,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(pattern = PATTERN)
                                                  LocalDateTime rangeStart,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(pattern = PATTERN)
                                                  LocalDateTime rangeEnd,
                                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                  @RequestParam(defaultValue = "10") @Positive int size) {
        log.info(this.getClass().getSimpleName() + ": GET: ALL: users: {} states: {} categories: {} rangeStart: {} " +
                        "rangeEnd: {} from: {} size: {}", users, states, categories, rangeStart, rangeEnd, from, size);
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(30);
        }
        if (rangeEnd.isBefore(rangeStart)) {
            throw new EventDateTimeException("Start must be before end");
        }
        return eventService.getAllEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from, size).stream()
                .map(event -> EventMapper.toEventFullDto(requestService.getConfirmedRequests(event.getId()),
                        statsService.getHitsCount(event.getId()), event)).collect(Collectors.toList());
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable @PositiveOrZero Long eventId,
                                    @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        log.info(this.getClass().getSimpleName() + ": PATCH: eventId: {} updateEventAdminRequest: {} ", eventId, updateEventAdminRequest);
        Event event = eventService.getEvent(eventId);
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new EventStateException("To edit an event, the time before the event must be more than 1 hours");
        }
        if (updateEventAdminRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventAdminRequest.getAnnotation());
        }
        if (updateEventAdminRequest.getCategory() != null) {
            event.setCategory(categoryService.getCategory(updateEventAdminRequest.getCategory()));
        }
        if (updateEventAdminRequest.getDescription() != null) {
            event.setDescription(updateEventAdminRequest.getDescription());
        }
        if (updateEventAdminRequest.getEventDate() != null) {
            event.setEventDate(updateEventAdminRequest.getEventDate());
        }
        if (updateEventAdminRequest.getLocation() != null) {
            event.setLocation(locationService.addLocation(updateEventAdminRequest.getLocation().getLat(),
                    updateEventAdminRequest.getLocation().getLon()));
        }
        if (updateEventAdminRequest.getPaid() != null) {
            event.setPaid(updateEventAdminRequest.getPaid());
        }
        if (updateEventAdminRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventAdminRequest.getParticipantLimit());
        }
        if (updateEventAdminRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventAdminRequest.getRequestModeration());
        }
        if (updateEventAdminRequest.getStateAction() != null) {
            switch (updateEventAdminRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    if (!event.getState().equals(EventState.PENDING)) {
                        throw new EventStateException("Event can't be published if status is not PENDING");
                    }
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    if (event.getState().equals(EventState.PUBLISHED)) {
                        throw new EventStateException("Can't reject published event");
                    }
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
        if (updateEventAdminRequest.getTitle() != null) {
            event.setTitle(updateEventAdminRequest.getTitle());
        }
        Long views = statsService.getHitsCount(event.getId());
        return EventMapper.toEventFullDto(requestService.getConfirmedRequests(event.getId()), views, eventService.updateEvent(event));
    }
}
