package ru.practicum.service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.event.EventMapper;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.service.EventService;
import ru.practicum.service.request.service.RequestService;
import ru.practicum.service.stats.StatsService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.service.event.model.DateTimeFormat.PATTERN;

@RestController
@Validated
@RequestMapping("/admin/events")
@Slf4j
@RequiredArgsConstructor
public class AdminEventController {
    private final EventService eventService;
    private final StatsService statsService;
    private final RequestService requestService;

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
        log.info("{}: GET: ALL: users: {} states: {} categories: {} rangeStart: {} " +
                        "rangeEnd: {} from: {} size: {}", this.getClass().getSimpleName(), users, states, categories,
                rangeStart, rangeEnd, from, size);
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(30);
        }
        Set<Event> events = new HashSet<>(eventService.getAllEventsForAdmin(users, states, categories, rangeStart, rangeEnd, from,
                size));
        Map<Long, Long> hitsCount;
        Map<Long, Long> confirmedRequestsCount;
        if (!events.isEmpty()) {
            confirmedRequestsCount = requestService.getConfirmedRequests(events.stream().map(Event::getId)
                    .collect(Collectors.toSet()));
            Map<Long, LocalDateTime> eventsWithDate = new HashMap<>();
            events.forEach(event -> eventsWithDate.put(event.getId(), event.getCreatedOn()));
            hitsCount = statsService.getHitsCount(eventsWithDate);
        } else {
            hitsCount = new HashMap<>();
            confirmedRequestsCount = new HashMap<>();
        }
        return events.stream()
                .map(event -> EventMapper.toEventFullDto(confirmedRequestsCount.getOrDefault(event.getId(), 0L),
                        hitsCount.getOrDefault(event.getId(), 0L), event))
                .collect(Collectors.toList());
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEvent(@PathVariable @Positive Long eventId,
                                    @RequestBody @Valid UpdateEventAdminRequest updateEventAdminRequest) {
        log.info("{}: PATCH: eventId: {} updateEventAdminRequest: {} ", this.getClass()
                .getSimpleName(), eventId, updateEventAdminRequest);
        Event event = eventService.updateEventAdmin(eventId, updateEventAdminRequest);
        Long views = statsService.getHitsCount(event.getId(), event.getCreatedOn());
        return EventMapper.toEventFullDto(requestService.getConfirmedRequests(event.getId()), views, event);
    }
}
