package ru.practicum.service.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.compilation.CompilationMapper;
import ru.practicum.service.compilation.dto.CompilationDto;
import ru.practicum.service.compilation.dto.NewCompilationDto;
import ru.practicum.service.compilation.model.Compilation;
import ru.practicum.service.compilation.service.CompilationService;
import ru.practicum.service.event.EventMapper;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.service.EventService;
import ru.practicum.service.request.service.RequestService;
import ru.practicum.service.stats.StatsService;
import ru.practicum.service.validation.ValidateException;

import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/compilations")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AdminCompilationController {
    private final CompilationService compilationService;
    private final EventService eventService;
    private final StatsService statsService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(
            @RequestBody @Validated(ValidateException.OnCreate.class) NewCompilationDto newCompilationDto) {
        log.info("{}: POST: NewCompilationDto: {}", this.getClass().getSimpleName(), newCompilationDto);
        Set<Event> events;
        Map<Long, Long> hitsCount;
        if (newCompilationDto.getEvents() != null) {
            events = eventService.getEvents(newCompilationDto.getEvents());
            requestService.updateConfirmedRequests();
            Map<Long, LocalDateTime> eventsWithDate = new HashMap<>();
            events.forEach(event -> eventsWithDate.put(event.getId(), event.getCreatedOn()));
            hitsCount = statsService.getHitsCount(eventsWithDate);
        } else {
            hitsCount = new HashMap<>();
            events = new HashSet<>();
        }
        Compilation compilation = compilationService.addCompilation(CompilationMapper.toCompilation(events, newCompilationDto));
        return CompilationMapper.toCompilationDto(events.stream()
                .map(event -> EventMapper.toEventShortDto(requestService.getConfirmedRequests(event.getId()),
                        hitsCount.get(event.getId()), event)).collect(Collectors.toSet()), compilation);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @Positive Long compId) {
        log.info("{}: DELETE: compId: {}", this.getClass().getSimpleName(), compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable @Positive Long compId,
                                            @RequestBody @Validated(ValidateException.OnUpdate.class) NewCompilationDto newCompilationDto) {
        log.info("{}: PATCH: compId: {} NewCompilationDto: {}", this.getClass().getSimpleName(), compId,
                newCompilationDto);
        Set<Event> events;
        Map<Long, Long> hitsCount;
        if (newCompilationDto.getEvents() != null) {
            events = eventService.getEvents(newCompilationDto.getEvents());
            requestService.updateConfirmedRequests();
            Map<Long, LocalDateTime> eventsWithDate = new HashMap<>();
            events.forEach(event -> eventsWithDate.put(event.getId(), event.getCreatedOn()));
            hitsCount = statsService.getHitsCount(eventsWithDate);
        } else {
            events = new HashSet<>();
            hitsCount = new HashMap<>();
        }
        Compilation compilation = compilationService.updateCompilation(compId, CompilationMapper.toCompilation(events,
                newCompilationDto));
        return CompilationMapper.toCompilationDto(events.stream()
                .map(event -> EventMapper.toEventShortDto(requestService.getConfirmedRequests(event.getId()),
                        hitsCount.get(event.getId()), event)).collect(Collectors.toSet()), compilation);
    }
}
