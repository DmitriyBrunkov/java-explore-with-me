package ru.practicum.service.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.compilation.CompilationMapper;
import ru.practicum.service.compilation.dto.CompilationDto;
import ru.practicum.service.compilation.model.Compilation;
import ru.practicum.service.compilation.service.CompilationService;
import ru.practicum.service.event.EventMapper;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.request.service.RequestService;
import ru.practicum.service.stats.StatsService;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Set;
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

    @GetMapping
    public List<CompilationDto> getAllCompilations(@RequestParam(required = false) Boolean pinned,
                                                   @RequestParam(defaultValue = "0", required = false) int from,
                                                   @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("{}: GET: ALL: pinned: {} from: {} size: {}", this.getClass().getSimpleName(), pinned, from, size);

        return compilationService.getCompilations(pinned, from, size).stream().map(compilation -> {
            Set<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                    .map(event -> {
                        EventShortDto eventShortDto =
                                EventMapper.toEventShortDto(statsService.getHitsCount(event.getId(), event.getCreatedOn()), event);
                        eventShortDto.setConfirmedRequests(requestService.getConfirmedRequests(eventShortDto.getId()));
                        return eventShortDto;
                    })
                    .collect(Collectors.toSet());
            return CompilationMapper.toCompilationDto(eventShortDtos, compilation);
        }).collect(Collectors.toList());
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable @Positive Long compId) {
        log.info("{}: GET: compId: {}", this.getClass().getSimpleName(), compId);
        Compilation compilation = compilationService.getCompilation(compId);
        return CompilationMapper.toCompilationDto(compilation.getEvents().stream()
                .map(event -> {
                    EventShortDto eventShortDto =
                            EventMapper.toEventShortDto(statsService.getHitsCount(event.getId(), event.getCreatedOn()), event);
                    eventShortDto.setConfirmedRequests(requestService.getConfirmedRequests(eventShortDto.getId()));
                    return eventShortDto;
                })
                .collect(Collectors.toSet()), compilation);
    }
}
