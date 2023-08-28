package ru.practicum.stats.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HitStatsDto {
    private String app;
    private String uri;
    private Long hits;
}
