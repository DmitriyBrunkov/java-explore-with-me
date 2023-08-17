package ru.practicum.stats.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HitStatsDto {
    private String app;
    private String uri;
    private Long hits;
}
