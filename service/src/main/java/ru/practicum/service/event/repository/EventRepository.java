package ru.practicum.service.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiator_Id(Long userId, Pageable pageable);

    Optional<Event> findByInitiator_IdAndId(Long userId, Long id);

    @Query("select e from Event e " +
            "where e.eventDate between :rangeStart and :rangeEnd " +
            "and e.initiator.id in :users or :users is null " +
            "and e.state in :states or :states is null " +
            "and e.category.id in :categories or :categories is null ")
    List<Event> getAllEventsForAdmin(Set<Long> users,
                                     Set<EventState> states,
                                     Set<Long> categories,
                                     LocalDateTime rangeStart,
                                     LocalDateTime rangeEnd,
                                     Pageable pageable);

    @Query("select e from Event e " +
            "where e.state = 'PUBLISHED' " +
            "and e.eventDate between :rangeStart and :rangeEnd " +
            "and (e.paid = :paid or :paid is null) " +
            "and (e.category.id in :categories or :categories is null) " +
            "and (:text is null " +
            " or lower(e.annotation) like lower(CONCAT('%',:text,'%')) " +
            " or lower(e.description) like lower(CONCAT('%',:text,'%'))) ")
    List<Event> getAllEventsForPub(String text,
                                   Set<Long> categories,
                                   Boolean paid,
                                   LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd,
                                   Pageable pageable);

    Optional<Event> findByStateAndId(EventState state, Long eventId);

    Set<Event> findAllByIdIn(Set<Long> ids);
}
