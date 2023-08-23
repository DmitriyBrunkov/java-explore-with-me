package ru.practicum.service.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.event.EventMapper;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.enums.SortType;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.service.EventService;
import ru.practicum.service.exception.model.EventDateTimeException;
import ru.practicum.service.request.service.RequestService;
import ru.practicum.service.stats.StatsService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.service.event.model.DateTimeFormat.PATTERN;

@Slf4j
@RestController
@Validated
@RequestMapping("/events")
public class PubEventController {
    private final EventService eventService;
    private final StatsService statsService;
    private final RequestService requestService;

    @Autowired
    public PubEventController(EventService eventService, StatsService statsService, RequestService requestService) {
        this.eventService = eventService;
        this.statsService = statsService;
        this.requestService = requestService;
    }

    @GetMapping
    public List<EventShortDto> getAllEvents(@RequestParam(required = false) String text,
                                            @RequestParam(required = false) Set<@PositiveOrZero Long> categories,
                                            @RequestParam(required = false) Boolean paid,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeStart,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeEnd,
                                            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                            @RequestParam(defaultValue = "EVENT_DATE") SortType sort,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size) {
        log.info(this.getClass().getSimpleName() + ": GET: ALL: text: {} categories: {} paid: {} rangeStart: {} " +
                        "rangeEnd: {} onlyAvailable: {} sort: {} from: {} size: {}", text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        if (rangeStart == null) {
            rangeStart = LocalDateTime.now();
        }
        if (rangeEnd == null) {
            rangeEnd = LocalDateTime.now().plusYears(30);
        }
        if (rangeEnd.isBefore(rangeStart)) {
            throw new EventDateTimeException("Start must be before end");
        }
        statsService.postHit(null);
        List<Event> resultEventList = eventService.getAllEventsForPub(text, categories, paid, rangeStart, rangeEnd,
                sort, from, size);
        if (onlyAvailable) {
            resultEventList = resultEventList.stream().filter(event ->
                            event.getParticipantLimit() == 0 || event.getParticipantLimit() < requestService.getConfirmedRequests(event.getId()))
                    .collect(Collectors.toList());

        }
        List<EventShortDto> resultEventShortList = resultEventList.stream()
                .map(event -> EventMapper.toEventShortDto(requestService.getConfirmedRequests(event.getId()),
                        statsService.getHitsCount(event.getId()), event)).collect(Collectors.toList());

        if (sort.equals(SortType.VIEWS)) {
            resultEventShortList.sort((o1, o2) -> Math.toIntExact(o2.getViews() - o1.getViews()));
        }
        return resultEventShortList;
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable @PositiveOrZero Long id) {
        log.info(this.getClass().getSimpleName() + ": GET: id: {}", id);
        statsService.postHit(id);
        Event event = eventService.getEventForPub(id);
        Long views = statsService.getHitsCount(event.getId());
        return EventMapper.toEventFullDto(requestService.getConfirmedRequests(event.getId()), views, event);
    }
}
