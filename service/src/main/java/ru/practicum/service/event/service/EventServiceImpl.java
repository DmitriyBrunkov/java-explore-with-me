package ru.practicum.service.event.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.event.enums.SortType;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.exception.model.ObjectNotFoundException;
import ru.practicum.service.validation.PageableValidation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event addEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public Event getEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new ObjectNotFoundException("Event: " + eventId +
                " not found"));
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
    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public List<Event> getAllEventsForAdmin(Set<Long> users,
                                            Set<EventState> states,
                                            Set<Long> categories,
                                            LocalDateTime rangeStart,
                                            LocalDateTime rangeEnd,
                                            int from, int size) {
        Pageable pageable = PageableValidation.validatePageable(from, size);
        return eventRepository.getAllEventsForAdmin(users, states, categories, rangeStart, rangeEnd, pageable);
    }

    @Override
    public List<Event> getAllEventsForPub(String text,
                                          Set<Long> categories,
                                          Boolean paid,
                                          LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd,
                                          SortType sort,
                                          int from, int size) {
        Pageable pageable = PageableValidation.validatePageable(from, size, sort);
        return eventRepository.getAllEventsForPub(text, categories, paid, rangeStart, rangeEnd, pageable);
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
}
