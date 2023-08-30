package ru.practicum.service.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.service.comment.dto.NewCommentDto;
import ru.practicum.service.comment.model.Comment;
import ru.practicum.service.comment.repository.CommentsRepository;
import ru.practicum.service.event.enums.EventState;
import ru.practicum.service.event.service.EventService;
import ru.practicum.service.exception.model.ObjectNotFoundException;
import ru.practicum.service.exception.model.RequestValidationException;
import ru.practicum.service.validation.PageableValidation;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentsRepository commentsRepository;
    private final EventService eventService;

    @Override
    public Comment getComment(Long commentId) {
        return commentsRepository.findById(commentId)
                .orElseThrow(() -> new ObjectNotFoundException("Comment: " + commentId + " not found"));
    }

    @Override
    public Comment updateComment(Long commentId, NewCommentDto newCommentDto) {
        Comment comment = commentsRepository.findById(commentId)
                .orElseThrow(() -> new ObjectNotFoundException("Comment: " + commentId + " not found"));
        comment.setText(newCommentDto.getText());
        comment.setIsModified(true);
        comment.setLastChange(LocalDateTime.now());
        return commentsRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        if (!commentsRepository.existsById(commentId)) {
            throw new ObjectNotFoundException("Comment: " + commentId + " not found");
        }
        commentsRepository.deleteById(commentId);
    }

    @Override
    public List<Comment> getComments(Long userId, int from, int size) {
        Pageable pageable = PageableValidation.validatePageable(from, size);
        return commentsRepository.findByAuthor_Id(userId, pageable);
    }

    @Override
    public Comment addComment(Comment comment) {
        if (!comment.getEvent().getState().equals(EventState.PUBLISHED)) {
            throw new RequestValidationException("User can comment only published events");
        }
        return commentsRepository.save(comment);
    }

    @Override
    public Comment updateComment(Long userId, Long commentId, NewCommentDto newCommentDto) {
        Comment comment = commentsRepository.findById(commentId).orElseThrow(() -> new ObjectNotFoundException(
                "Comment: " + commentId + " not found"));
        if (!userId.equals(comment.getAuthor().getId())) {
            throw new RequestValidationException("User can edit only his own comments");
        }
        if (comment.getLastChange().plusHours(1).isBefore(LocalDateTime.now())) {
            throw new RequestValidationException("User can edit comment only within 1 hour");
        }
        comment.setText(newCommentDto.getText());
        comment.setIsModified(true);
        comment.setLastChange(LocalDateTime.now());
        return commentsRepository.save(comment);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        Comment comment = commentsRepository.findById(commentId).orElseThrow(() -> new ObjectNotFoundException(
                "Comment: " + commentId + " not found"));
        if (!userId.equals(comment.getAuthor().getId())) {
            throw new RequestValidationException("User can delete only his own comments");
        }
        commentsRepository.deleteById(commentId);
    }

    @Override
    public List<Comment> getAllComments(Long eventId, int from, int size) {
        if (!eventService.exist(eventId)) {
            throw new ObjectNotFoundException("Event: " + eventId + " not found");
        }
        Pageable pageable = PageableValidation.validatePageable(from, size);
        return commentsRepository.findAllByEvent_IdOrderByLastChange(eventId, pageable);
    }

    @Override
    public Long getCommentsCount(Long eventId) {
        if (!eventService.exist(eventId)) {
            throw new ObjectNotFoundException("Event: " + eventId + " not found");
        }
        return commentsRepository.countAllByEvent_Id(eventId);
    }

    @Override
    public Map<Long, Long> getCommentsCount(Set<Long> eventIds) {
        Map<Long, Long> results = new HashMap<>();
        List<Object[]> resultList = commentsRepository.countAllByEventIdsIn(eventIds);
        for (Object[] resultType : resultList) {
            results.put((Long) resultType[0], (Long) resultType[1]);
        }
        return results;
    }
}
