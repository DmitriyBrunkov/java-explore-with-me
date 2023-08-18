package ru.practicum.stats.server.service;

import ru.practicum.stats.server.model.Hit;
import ru.practicum.stats.server.model.HitStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    Hit postHit(Hit hit);

    List<HitStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
