package ru.practicum.service.event.service;

import ru.practicum.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.service.event.dto.UpdateEventUserRequest;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.event.enums.SortType;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.service.request.dto.EventRequestStatusUpdateResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {

    Event addEvent(Event event);

    List<Event> getEvents(Long userId, int from, int size);

    Event getEvent(Long userId, Long eventId);

    Event getEvent(Long eventId);

    Event updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    Event updateEventUser(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<Event> getAllEventsForAdmin(Set<Long> users,
                                     Set<EventState> states,
                                     Set<Long> categories,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     int from, int size);

    List<Event> getAllEventsForPub(String text,
                                   Set<Long> categories,
                                   Boolean paid,
                                   LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd,
                                   Boolean onlyAvailable,
                                   SortType sort,
                                   int from, int size, String ip);

    Event getEventForPub(Long eventId);

    Set<Event> getEvents(Set<Long> ids);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId, Long eventId,
                                                       EventRequestStatusUpdateRequest updateRequest);

    boolean exist(long eventId);
}
