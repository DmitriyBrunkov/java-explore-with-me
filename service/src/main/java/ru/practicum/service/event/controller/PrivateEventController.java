package ru.practicum.service.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
import ru.practicum.service.exception.model.EventStateException;
import ru.practicum.service.exception.model.RequestValidationException;
import ru.practicum.service.location.model.Location;
import ru.practicum.service.location.service.LocationService;
import ru.practicum.service.request.RequestMapper;
import ru.practicum.service.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.service.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.service.request.dto.ParticipationRequestDto;
import ru.practicum.service.request.enums.RequestStatus;
import ru.practicum.service.request.model.Request;
import ru.practicum.service.request.service.RequestService;
import ru.practicum.service.stats.StatsService;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping("/users/{userId}/events")
@Slf4j
public class PrivateEventController {
    private final EventService eventService;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final RequestService requestService;
    private final StatsService statsService;

    @Autowired
    public PrivateEventController(EventService eventService, UserService userService, CategoryService categoryService,
                                  LocationService locationService, RequestService requestService, StatsService statsService) {
        this.eventService = eventService;
        this.userService = userService;
        this.categoryService = categoryService;
        this.locationService = locationService;
        this.requestService = requestService;
        this.statsService = statsService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable @PositiveOrZero Long userId,
                                 @RequestBody @Valid NewEventDto newEventDto) {
        log.info(this.getClass().getSimpleName() + ": POST: userId: {} newEventDto: {} ", userId, newEventDto);
        User user = userService.getUser(userId);
        Category category = categoryService.getCategory(newEventDto.getCategory());
        Location location = locationService.addLocation(newEventDto.getLocation()
                .getLat(), newEventDto.getLocation().getLon());
        Event event = eventService.addEvent(EventMapper.toEvent(user, category, location,
                LocalDateTime.now(), EventState.PENDING, newEventDto));
        Long views = statsService.getHitsCount(event.getId());
        return EventMapper.toEventFullDto(requestService.getConfirmedRequests(event.getId()), views, event);
    }

    @GetMapping
    public List<EventShortDto> getAllEvents(@PathVariable @PositiveOrZero Long userId,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info(this.getClass().getSimpleName() + ": GET: ALL: userId: {} from: {} size: {}", userId, from, size);
        return eventService.getEvents(userId, from, size).stream()
                .map(event -> EventMapper.toEventShortDto(requestService.getConfirmedRequests(event.getId()),
                        statsService.getHitsCount(event.getId()), event)).collect(Collectors.toList());
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEvent(@PathVariable @PositiveOrZero Long userId,
                                 @PathVariable @PositiveOrZero Long eventId) {
        log.info(this.getClass().getSimpleName() + ": GET: userId: {} eventId: {}", userId, eventId);
        Event event = eventService.getEvent(userId, eventId);
        Long views = statsService.getHitsCount(event.getId());
        return EventMapper.toEventFullDto(requestService.getConfirmedRequests(event.getId()), views, event);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable @PositiveOrZero Long userId,
                                    @PathVariable @PositiveOrZero Long eventId,
                                    @RequestBody @Valid UpdateEventUserRequest updateEventUserRequest) {
        log.info(this.getClass().getSimpleName() + ": PATCH: userId: {} eventId: {} updateEventUserRequest: {}", userId,
                eventId, updateEventUserRequest);
        userService.getUser(userId);
        Event event = eventService.getEvent(eventId);
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStateException("Published event can't be edited");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventStateException("To edit an event, the time before the event must be more than 2 hours");
        }
        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            event.setCategory(categoryService.getCategory(updateEventUserRequest.getCategory()));
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getEventDate() != null) {
            event.setEventDate(updateEventUserRequest.getEventDate());
        }
        if (updateEventUserRequest.getLocation() != null) {
            event.setLocation(locationService.getLocationByLatLon(updateEventUserRequest.getLocation().getLat(),
                    updateEventUserRequest.getLocation().getLon()));
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventUserRequest.getRequestModeration());
        }
        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
            }
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }
        Long views = statsService.getHitsCount(event.getId());
        return EventMapper.toEventFullDto(requestService.getConfirmedRequests(event.getId()), views, eventService.updateEvent(event));
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getParticipationRequestsByUserEvents(@PathVariable @PositiveOrZero Long userId,
                                                                              @PathVariable @PositiveOrZero Long eventId) {
        log.info(this.getClass().getSimpleName() + ": GET: REQUESTS: userId: {} eventId: {}", userId, eventId);
        userService.getUser(userId);
        eventService.getEvent(eventId);
        return requestService.getRequests(eventId).stream().map(RequestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable @PositiveOrZero Long userId,
                                                              @PathVariable @PositiveOrZero Long eventId,
                                                              @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info(this.getClass().getSimpleName() + ": PATCH: REQUESTS: userId: {} eventId: {} updateRequest: {}",
                userId,
                eventId, updateRequest);
        Event event = eventService.getEvent(userId, eventId);
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= requestService.getConfirmedRequests(eventId)) {
            requestService.rejectOverLimitRequestEvent(eventId);
            throw new RequestValidationException("The participant limit has been reached");
        }
        if (!event.getRequestModeration()) {
            throw new RequestValidationException("No confirmation is required for this event");
        }

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        updateRequest.getRequestIds().forEach(requestId -> {
            Request request = requestService.getRequest(requestId);
            if (!request.getStatus().equals(RequestStatus.PENDING)) {
                throw new RequestValidationException("Only events in pending state can be confirmed");
            }
            request.setStatus(updateRequest.getStatus());
            if (event.getParticipantLimit() <= requestService.getConfirmedRequests(eventId)) {
                requestService.rejectOverLimitRequestEvent(eventId);
                request.setStatus(RequestStatus.REJECTED);
                throw new RequestValidationException("The participant limit has been reached");
            }
            requestService.updateRequest(request);
            ParticipationRequestDto resUpdRequest = RequestMapper.toParticipationRequestDto(request);
            assert resUpdRequest != null;
            if (resUpdRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
                confirmedRequests.add(resUpdRequest);
            } else if (resUpdRequest.getStatus().equals(RequestStatus.REJECTED)) {
                rejectedRequests.add(resUpdRequest);
            }
        });
        EventRequestStatusUpdateResult eventRequestStatusUpdateResult = new EventRequestStatusUpdateResult();
        eventRequestStatusUpdateResult.setConfirmedRequests(confirmedRequests);
        eventRequestStatusUpdateResult.setRejectedRequests(rejectedRequests);
        return eventRequestStatusUpdateResult;
    }
}
