package ru.practicum.service.event.service;

import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.event.enums.SortType;
import ru.practicum.service.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface EventService {

    Event addEvent(Event event);

    Event getEvent(Long eventId);

    List<Event> getEvents(Long userId, int from, int size);

    Event getEvent(Long userId, Long eventId);

    Event updateEvent(Event event);

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
                                   SortType sort,
                                   int from, int size);

    Event getEventForPub(Long eventId);

    Set<Event> getEvents(Set<Long> ids);
}
