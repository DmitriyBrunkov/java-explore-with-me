package ru.practicum.service.event.service;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.service.category.service.CategoryService;
import ru.practicum.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.service.event.dto.UpdateEventRequest;
import ru.practicum.service.event.dto.UpdateEventUserRequest;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.event.enums.SortType;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.exception.model.EventDateTimeException;
import ru.practicum.service.exception.model.EventStateException;
import ru.practicum.service.exception.model.ObjectNotFoundException;
import ru.practicum.service.exception.model.RequestValidationException;
import ru.practicum.service.location.service.LocationService;
import ru.practicum.service.request.RequestMapper;
import ru.practicum.service.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.service.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.service.request.dto.ParticipationRequestDto;
import ru.practicum.service.request.enums.RequestStatus;
import ru.practicum.service.request.model.Request;
import ru.practicum.service.request.service.RequestService;
import ru.practicum.service.stats.StatsService;
import ru.practicum.service.validation.PageableValidation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final RequestService requestService;
    private final StatsService statsService;

    @Override
    public Event addEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public List<Event> getEvents(Long userId, int from, int size) {
        Pageable pageable = PageableValidation.validatePageable(from, size);
        return eventRepository.findAllByInitiator_Id(userId, pageable);
    }

    @Override
    public Event getEvent(Long userId, Long eventId) {
        return eventRepository.findByInitiator_IdAndId(userId, eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Event: " + eventId +
                        " with initiator: " + userId + " not found"));
    }

    @Override
    public Event updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Event: " + eventId + " not found"));
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new EventStateException("To edit an event, the time before the event must be more than 1 hours");
        }
        updateEvent(event, updateEventAdminRequest);
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
        return eventRepository.save(event);
    }

    @Override
    public Event updateEventUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event event = eventRepository.findByInitiator_IdAndId(userId, eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Event: " + eventId + " for user: " + userId + " not found"));
        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new EventStateException("Published event can't be edited");
        }
        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new EventStateException("To edit an event, the time before the event must be more than 2 hours");
        }
        updateEvent(event, updateEventUserRequest);
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
        return eventRepository.save(event);
    }

    @Override
    public List<Event> getAllEventsForAdmin(Set<Long> users,
                                            Set<EventState> states,
                                            Set<Long> categories,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            int from, int size) {
        if (rangeEnd.isBefore(rangeStart)) {
            throw new EventDateTimeException("Start must be before end");
        }
        Pageable pageable = PageableValidation.validatePageable(from, size);
        return eventRepository.getAllEventsForAdmin(users, states, categories, rangeStart, rangeEnd, pageable);
    }

    @Override
    public List<Event> getAllEventsForPub(String text,
                                          Set<Long> categories,
                                          Boolean paid,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          Boolean onlyAvailable,
                                          SortType sort,
                                          int from, int size, String ip) {
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(30);
        }
        if (rangeEnd.isBefore(rangeStart)) {
            throw new EventDateTimeException("Start must be before end");
        }
        statsService.postHit(null, ip);
        Pageable pageable = PageableValidation.validatePageable(from, size, sort);
        List<Event> resultEventList = eventRepository.getAllEventsForPub(text, categories, paid, rangeStart, rangeEnd,
                pageable);
        if (onlyAvailable) {
            resultEventList = resultEventList.stream().filter(event ->
                            event.getParticipantLimit() == 0 || event.getParticipantLimit() < requestService.getConfirmedRequests(event.getId()))
                    .collect(Collectors.toList());

        }
        return resultEventList;
    }

    @Override
    public Event getEventForPub(Long eventId) {
        return eventRepository.findByStateAndId(EventState.PUBLISHED, eventId)
                .orElseThrow(() -> new ObjectNotFoundException("Event: " + eventId + " not found"));
    }

    @Override
    public Set<Event> getEvents(Set<Long> ids) {
        return eventRepository.findAllByIdIn(ids);
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest updateRequest) {
        Event event = getEvent(userId, eventId);
        requestService.updateConfirmedRequests();
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

    private void updateEvent(Event event, UpdateEventRequest updateEventRequest) {
        if (!StringUtils.isBlank(updateEventRequest.getAnnotation())) {
            event.setAnnotation(updateEventRequest.getAnnotation());
        }
        if (updateEventRequest.getCategory() != null) {
            event.setCategory(categoryService.getCategory(updateEventRequest.getCategory()));
        }
        if (!StringUtils.isBlank(updateEventRequest.getDescription())) {
            event.setDescription(updateEventRequest.getDescription());
        }
        if (updateEventRequest.getEventDate() != null) {
            event.setEventDate(updateEventRequest.getEventDate());
        }
        if (updateEventRequest.getLocation() != null) {
            event.setLocation(locationService.addLocation(updateEventRequest.getLocation().getLat(),
                    updateEventRequest.getLocation().getLon()));
        }
        if (updateEventRequest.getPaid() != null) {
            event.setPaid(updateEventRequest.getPaid());
        }
        if (updateEventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventRequest.getParticipantLimit());
        }
        if (updateEventRequest.getRequestModeration() != null) {
            event.setRequestModeration(updateEventRequest.getRequestModeration());
        }
        if (!StringUtils.isBlank(updateEventRequest.getTitle())) {
            event.setTitle(updateEventRequest.getTitle());
        }
    }

    @Override
    public boolean exist(long eventId) {
        return eventRepository.existsById(eventId);
    }
}
