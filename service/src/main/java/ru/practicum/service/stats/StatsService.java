package ru.practicum.service.stats;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.HitStatsDto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {
    private static final String URI_PREFIX = "/events";
    private final StatsClient statsClient;
    @Value("${service.app-name}")
    private String app;

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

    public Map<Long, Long> getHitsCount(Map<Long, LocalDateTime> events) {
        if (events.isEmpty()) {
            return new HashMap<>();
        }
        List<String> uris = events.keySet().stream().map(key -> "/events/" + key.toString())
                .collect(Collectors.toList());
        ResponseEntity<List<HitStatsDto>> entity = statsClient.getStat(
                events.values().stream().min(LocalDateTime::compareTo).orElse(LocalDateTime.now().minusSeconds(1)),
                LocalDateTime.now(),
                uris,
                true);
        Map<Long, Long> result = new HashMap<>();
        for (HitStatsDto hitStatsDto : Objects.requireNonNull(entity.getBody())) {
            result.put(Long.getLong(hitStatsDto.getUri().replace("/events/", "")), hitStatsDto.getHits());
        }
        return result;
    }
}
