package ru.practicum.service.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.service.comment.model.Comment;

import java.util.List;
import java.util.Set;

public interface CommentsRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByAuthor_Id(Long userId, Pageable pageable);

    List<Comment> findAllByEvent_IdOrderByLastChange(Long eventId, Pageable pageable);

    Long countAllByEvent_Id(Long eventId);

    @Query(value = "select c.event.id, count(*) " +
            "from Comment c " +
            "where c.event.id in :eventIds " +
            "group by c.event.id")
    List<Object[]> countAllByEventIdsIn(Set<Long> eventIds);
}
