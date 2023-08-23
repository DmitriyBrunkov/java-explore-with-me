package ru.practicum.service.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.HitStatsDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {
    private static final String APP = "main-service";
    private static final String URI_PREFIX = "/events";
    private final StatsClient statsClient;

    public void postHit(Long eventId) {
        String uri = URI_PREFIX + (eventId == null ? "" : "/" + eventId);
        statsClient.hitStat(HitDto.builder()
                .app(APP)
                .ip("127.0.0.1")
                .uri(uri)
                .build());
    }

    public Long getHitsCount(Long eventId) {
        List<String> uris = List.of("/events/" + eventId);
        ResponseEntity<List<HitStatsDto>> entity = statsClient.getStat(
                LocalDateTime.of(1970, 1, 1, 1, 1),
                LocalDateTime.of(3970, 1, 1, 1, 1),
                uris,
                true);

        if (entity.getBody().isEmpty()) {
            return 0L;
        }
        return entity.getBody().stream().mapToLong(HitStatsDto::getHits).sum();

    }
}
