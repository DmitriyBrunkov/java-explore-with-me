package ru.practicum.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HitStatsDto {
    String app;
    String uri;
    Long hits;
}
