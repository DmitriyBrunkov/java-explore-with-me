package ru.practicum.stats.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HitStats {
    String app;
    String uri;
    Long hits;
}
