package ru.practicum.stats.server;

import lombok.experimental.UtilityClass;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.HitStatsDto;
import ru.practicum.stats.server.model.Hit;
import ru.practicum.stats.server.model.HitStats;

@UtilityClass
public class StatsMapper {
    public static HitDto toHitDto(Hit hit) {
        return new HitDto(hit.getApp(), hit.getUri(), hit.getIp(), hit.getTimestamp());
    }

    public static Hit toHit(HitDto hitDto) {
        Hit hit = new Hit();
        hit.setApp(hitDto.getApp());
        hit.setUri(hitDto.getUri());
        hit.setIp(hitDto.getIp());
        hit.setTimestamp(hitDto.getTimestamp());
        return hit;
    }

    public static HitStatsDto toHitStatsDto(HitStats hitStats) {
        return new HitStatsDto(hitStats.getApp(), hitStats.getUri(), hitStats.getHits());
    }

    public static HitStats toHitStats(HitStatsDto hitStatsDto) {
        return new HitStats(hitStatsDto.getApp(), hitStatsDto.getUri(), hitStatsDto.getHits());
    }
}
