package ru.practicum.stats.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HitStats {
    private String app;
    private String uri;
    private Long hits;
}
