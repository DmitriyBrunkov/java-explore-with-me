package ru.practicum.service.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.service.comment.dto.NewCommentDto;
import ru.practicum.service.comment.model.Comment;
import ru.practicum.service.comment.repository.CommentsRepository;
import ru.practicum.service.event.service.EventService;
import ru.practicum.service.exception.model.ObjectNotFoundException;
import ru.practicum.service.exception.model.RequestValidationException;
import ru.practicum.service.validation.PageableValidation;

import java.time.LocalDateTime;
import java.util.List;

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
        return commentsRepository.save(comment);
    }

    @Override
    public Comment updateComment(Long userId, Long commentId, NewCommentDto newCommentDto) {
        Comment comment = commentsRepository.findById(commentId).orElseThrow(() -> new ObjectNotFoundException(
                "Comment: " + commentId + " not found"));
        if (!userId.equals(comment.getAuthor().getId())) {
            throw new RequestValidationException("User can edit only his own comments");
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
}
