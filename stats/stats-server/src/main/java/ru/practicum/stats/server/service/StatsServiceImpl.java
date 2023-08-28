package ru.practicum.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.stats.server.exception.model.IntervalValidationException;
import ru.practicum.stats.server.model.Hit;
import ru.practicum.stats.server.model.HitStats;
import ru.practicum.stats.server.repository.StatsRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public Hit postHit(Hit hit) {
        return statsRepository.save(hit);
    }

    @Override
    public List<HitStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new IntervalValidationException("Start must be before end");
        }
        if (unique) {
            return statsRepository.findUniqueHitStats(start, end, uris);
        }
        return statsRepository.findHitStats(start, end, uris);
    }
}
