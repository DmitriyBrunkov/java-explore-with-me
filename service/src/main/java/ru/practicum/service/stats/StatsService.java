package ru.practicum.service.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.HitStatsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class StatsService {
    @Value("${service.app-name}")
    private String app;
    private static final String URI_PREFIX = "/events";
    private final StatsClient statsClient;

    public void postHit(Long eventId, String ip) {
        String uri = URI_PREFIX + (eventId == null ? "" : "/" + eventId);
        statsClient.hitStat(HitDto.builder()
                .app(app)
                .ip(ip)
                .uri(uri)
                .build());
    }

    public Long getHitsCount(Long eventId, LocalDateTime createdOn) {
        List<String> uris = List.of("/events/" + eventId);
        ResponseEntity<List<HitStatsDto>> entity = statsClient.getStat(
                createdOn,
                LocalDateTime.now(),
                uris,
                true);

        if (Objects.requireNonNull(entity.getBody()).isEmpty()) {
            return 0L;
        }
        return entity.getBody().stream().mapToLong(HitStatsDto::getHits).sum();

    }
}
