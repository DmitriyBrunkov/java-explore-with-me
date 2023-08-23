package ru.practicum.service.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.validation.constraints.PositiveOrZero;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/compilations")
@Slf4j
public class AdminCompilationController {
    private final CompilationService compilationService;
    private final EventService eventService;
    private final RequestService requestService;
    private final StatsService statsService;

    @Autowired
    public AdminCompilationController(CompilationService compilationService, EventService eventService, RequestService requestService, StatsService statsService) {
        this.compilationService = compilationService;
        this.eventService = eventService;
        this.requestService = requestService;
        this.statsService = statsService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(
            @RequestBody @Validated(ValidateException.OnCreate.class) NewCompilationDto newCompilationDto) {
        log.info(this.getClass().getSimpleName() + ": POST: NewCompilationDto: {}", newCompilationDto);
        Set<Event> events = eventService.getEvents(newCompilationDto.getEvents());
        Compilation compilation = compilationService.addCompilation(CompilationMapper.toCompilation(events, newCompilationDto));
        return CompilationMapper.toCompilationDto(events.stream()
                .map(event -> EventMapper.toEventShortDto(requestService.getConfirmedRequests(event.getId()),
                        statsService.getHitsCount(event.getId()), event)).collect(Collectors.toSet()), compilation);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @PositiveOrZero Long compId) {
        log.info(this.getClass().getSimpleName() + ": DELETE: compId: {}", compId);
        compilationService.deleteCompilation(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@PathVariable @PositiveOrZero Long compId,
                                            @RequestBody @Validated(ValidateException.OnUpdate.class)
                                            NewCompilationDto newCompilationDto) {
        log.info(this.getClass().getSimpleName() + ": PATCH: compId: {} NewCompilationDto: {}", compId,
                newCompilationDto);
        Set<Event> events = new HashSet<>();
        if (!newCompilationDto.getEvents().isEmpty()) {
            events = newCompilationDto.getEvents().stream().map(eventService::getEvent)
                    .collect(Collectors.toSet());
        }
        Compilation compilation = compilationService.updateCompilation(compId, CompilationMapper.toCompilation(events,
                newCompilationDto));
        return CompilationMapper.toCompilationDto(compilation.getEvents().stream()
                .map(event -> EventMapper.toEventShortDto(requestService.getConfirmedRequests(event.getId()),
                        statsService.getHitsCount(event.getId()), event)).collect(Collectors.toSet()), compilation);
    }
}
