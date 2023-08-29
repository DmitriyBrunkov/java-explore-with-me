package ru.practicum.service.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.service.comment.model.Comment;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByAuthor_Id(Long userId, Pageable pageable);

    List<Comment> findAllByEvent_IdOrderByLastChange(Long eventId, Pageable pageable);
}
