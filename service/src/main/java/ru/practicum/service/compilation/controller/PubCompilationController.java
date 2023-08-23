package ru.practicum.service.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.compilation.CompilationMapper;
import ru.practicum.service.compilation.dto.CompilationDto;
import ru.practicum.service.compilation.model.Compilation;
import ru.practicum.service.compilation.service.CompilationService;
import ru.practicum.service.event.EventMapper;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.request.service.RequestService;
import ru.practicum.service.stats.StatsService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/compilations")
@Slf4j
public class PubCompilationController {
    private final CompilationService compilationService;
    private final RequestService requestService;
    private final StatsService statsService;

    @Autowired
    public PubCompilationController(CompilationService compilationService, RequestService requestService, StatsService statsService) {
        this.compilationService = compilationService;
        this.requestService = requestService;
        this.statsService = statsService;
    }

    @GetMapping
    public List<CompilationDto> getAllCompilations(@RequestParam(defaultValue = "false") Boolean pinned,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                   @RequestParam(defaultValue = "10") @PositiveOrZero int size) {
        log.info(this.getClass().getSimpleName() + ": GET: ALL: pinned: {} from: {} size: {}", pinned, from, size);
        return compilationService.getCompilations(pinned, from, size).stream().map(compilation -> {
            Set<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                    .map(event -> EventMapper.toEventShortDto(requestService.getConfirmedRequests(event.getId()),
                            statsService.getHitsCount(event.getId()), event)).collect(Collectors.toSet());
            return CompilationMapper.toCompilationDto(eventShortDtos, compilation);
        }).collect(Collectors.toList());
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable @PositiveOrZero Long compId) {
        log.info(this.getClass().getSimpleName() + ": GET: compId: {}", compId);
        Compilation compilation = compilationService.getCompilation(compId);
        return CompilationMapper.toCompilationDto(compilation.getEvents().stream()
                .map(event -> EventMapper.toEventShortDto(requestService.getConfirmedRequests(event.getId()),
                        statsService.getHitsCount(event.getId()), event)).collect(Collectors.toSet()), compilation);
    }
}
