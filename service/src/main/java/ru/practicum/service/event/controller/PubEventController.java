package ru.practicum.service.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.event.EventMapper;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.enums.SortType;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.service.EventService;
import ru.practicum.service.request.service.RequestService;
import ru.practicum.service.stats.StatsService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.service.event.model.DateTimeFormat.PATTERN;

@Slf4j
@RestController
@Validated
@RequestMapping("/events")
@RequiredArgsConstructor
public class PubEventController {
    private final EventService eventService;
    private final StatsService statsService;
    private final RequestService requestService;

    @GetMapping
    public List<EventShortDto> getAllEvents(@RequestParam(required = false) String text,
                                            @RequestParam(required = false) Set<@PositiveOrZero Long> categories,
                                            @RequestParam(required = false) Boolean paid,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeStart,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = PATTERN) LocalDateTime rangeEnd,
                                            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                            @RequestParam(defaultValue = "EVENT_DATE") SortType sort,
                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                            @RequestParam(defaultValue = "10") @Positive int size,
                                            HttpServletRequest request) {
        log.info("{}: GET: ALL: text: {} categories: {} paid: {} rangeStart: {} " +
                        "rangeEnd: {} onlyAvailable: {} sort: {} from: {} size: {}", this.getClass().getSimpleName(),
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        Set<Event> events = new HashSet<>(eventService.getAllEventsForPub(text, categories, paid, rangeStart, rangeEnd,
                onlyAvailable, sort, from, size, request.getRemoteAddr()));
        Map<Long, Long> hitsCount;
        if (!events.isEmpty()) {
            requestService.updateConfirmedRequests();
            Map<Long, LocalDateTime> eventsWithDate = new HashMap<>();
            events.forEach(event -> eventsWithDate.put(event.getId(), event.getCreatedOn()));
            hitsCount = statsService.getHitsCount(eventsWithDate);
        } else {
            hitsCount = new HashMap<>();
        }

        List<EventShortDto> resultEventShortList = events.stream()
                .map(event -> EventMapper.toEventShortDto(requestService.getConfirmedRequests(event.getId()), hitsCount.get(event.getId()), event))
                .collect(Collectors.toList());
        if (sort.equals(SortType.VIEWS)) {
            resultEventShortList.sort((o1, o2) -> Math.toIntExact(o2.getViews() - o1.getViews()));
        }
        return resultEventShortList;
    }

    @GetMapping("/{id}")
    public EventFullDto getEvent(@PathVariable @Positive Long id, HttpServletRequest request) {
        log.info("{}: GET: id: {}", this.getClass().getSimpleName(), id);
        statsService.postHit(id, request.getRemoteAddr());
        Event event = eventService.getEventForPub(id);
        Long views = statsService.getHitsCount(event.getId(), event.getCreatedOn());
        requestService.updateConfirmedRequests();
        return EventMapper.toEventFullDto(requestService.getConfirmedRequests(event.getId()), views, event);
    }
}
