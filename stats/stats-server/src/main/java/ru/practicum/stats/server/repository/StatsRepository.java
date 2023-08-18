package ru.practicum.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.server.model.Hit;
import ru.practicum.stats.server.model.HitStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {

    @Query("select new ru.practicum.stats.server.model.HitStats(hit.app, hit.uri, count(hit.ip)) " +
            "from Hit hit " +
            "where hit.timestamp between :start and :end " +
            "and hit.uri in :uris or :uris is null " +
            "group by hit.app, hit.uri " +
            "order by 3 desc ")
    List<HitStats> findHitStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.stats.server.model.HitStats(hit.app, hit.uri, count(distinct hit.ip)) " +
            "from Hit hit " +
            "where hit.timestamp between :start and :end " +
            "and hit.uri in :uris or :uris is null " +
            "group by hit.app, hit.uri " +
            "order by 3 desc")
    List<HitStats> findUniqueHitStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
