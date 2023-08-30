package ru.practicum.service.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.comment.service.CommentService;
import ru.practicum.service.compilation.CompilationMapper;
import ru.practicum.service.compilation.dto.CompilationDto;
import ru.practicum.service.compilation.model.Compilation;
import ru.practicum.service.compilation.service.CompilationService;
import ru.practicum.service.event.EventMapper;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.request.service.RequestService;
import ru.practicum.service.stats.StatsService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/compilations")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PubCompilationController {
    private final CompilationService compilationService;
    private final StatsService statsService;
    private final RequestService requestService;
    private final CommentService commentService;

    @GetMapping
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                   @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("{}: GET: ALL: pinned: {} from: {} size: {}", this.getClass().getSimpleName(), pinned, from, size);
        List<Compilation> compilations = compilationService.getCompilations(pinned, from, size);
        Set<Event> events = new HashSet<>();
        compilations.forEach(compilation -> events.addAll(compilation.getEvents()));
        Map<Long, Long> hitsCount;
        Map<Long, Long> confirmedRequestsCount;
        Map<Long, Long> commentsCount;
        if (!events.isEmpty()) {
            Set<Long> eventsSet = events.stream().map(Event::getId).collect(Collectors.toSet());
            confirmedRequestsCount = requestService.getConfirmedRequests(eventsSet);
            Map<Long, LocalDateTime> eventsWithDate = new HashMap<>();
            events.forEach(event -> eventsWithDate.put(event.getId(), event.getCreatedOn()));
            hitsCount = statsService.getHitsCount(eventsWithDate);
            commentsCount = commentService.getCommentsCount(eventsSet);
        } else {
            hitsCount = new HashMap<>();
            confirmedRequestsCount = new HashMap<>();
            commentsCount = new HashMap<>();
        }
        return compilations.stream().map(compilation -> {
            Set<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                    .map(event -> EventMapper.toEventShortDto(confirmedRequestsCount.getOrDefault(event.getId(), 0L),
                            hitsCount.getOrDefault(event.getId(), 0L), commentsCount.getOrDefault(event.getId(), 0L), event))
                    .collect(Collectors.toSet());
            return CompilationMapper.toCompilationDto(eventShortDtos, compilation);
        }).collect(Collectors.toList());
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable @Positive Long compId) {
        log.info("{}: GET: compId: {}", this.getClass().getSimpleName(), compId);
        Compilation compilation = compilationService.getCompilation(compId);
        Set<Event> events;
        Map<Long, Long> hitsCount;
        Map<Long, Long> confirmedRequestsCount;
        Map<Long, Long> commentsCount;
        if (!compilation.getEvents().isEmpty()) {
            events = compilation.getEvents();
            Set<Long> eventsSet = events.stream().map(Event::getId).collect(Collectors.toSet());
            confirmedRequestsCount =
                    requestService.getConfirmedRequests(eventsSet);
            Map<Long, LocalDateTime> eventsWithDate = new HashMap<>();
            events.forEach(event -> eventsWithDate.put(event.getId(), event.getCreatedOn()));
            hitsCount = statsService.getHitsCount(eventsWithDate);
            commentsCount = commentService.getCommentsCount(eventsSet);
        } else {
            hitsCount = new HashMap<>();
            confirmedRequestsCount = new HashMap<>();
            commentsCount = new HashMap<>();
        }
        return CompilationMapper.toCompilationDto(compilation.getEvents().stream()
                .map(event -> EventMapper.toEventShortDto(confirmedRequestsCount.getOrDefault(event.getId(), 0L),
                        hitsCount.getOrDefault(event.getId(), 0L), commentsCount.getOrDefault(event.getId(), 0L), event))
                .collect(Collectors.toSet()), compilation);
    }
}
