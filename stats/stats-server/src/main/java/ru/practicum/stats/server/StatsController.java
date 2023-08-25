package ru.practicum.stats.server;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.HitStatsDto;
import ru.practicum.stats.server.exception.model.IntervalValidationException;
import ru.practicum.stats.server.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto postHit(@RequestBody HitDto hitDto) {
        log.info("{}: POST: Body: {}", this.getClass().getSimpleName(), hitDto);
        return StatsMapper.toHitDto(statsService.postHit(StatsMapper.toHit(hitDto)));
    }

    @GetMapping("/stats")
    public List<HitStatsDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                      @RequestParam(required = false) List<String> uris,
                                      @RequestParam(defaultValue = "false") boolean unique) {
        log.info("{}: Get: start: {}, end: {}, uris: {}, unique: {}", this.getClass().getSimpleName(), start, end,
                uris, unique);
        if (start.isAfter(end)) {
            throw new IntervalValidationException("Start must be before end");
        }
        return statsService.getStats(start, end, uris, unique).stream().map(StatsMapper::toHitStatsDto)
                .collect(Collectors.toList());
    }
}
