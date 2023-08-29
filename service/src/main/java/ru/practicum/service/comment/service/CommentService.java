package ru.practicum.service.comment.service;

import ru.practicum.service.comment.dto.NewCommentDto;
import ru.practicum.service.comment.model.Comment;

import java.util.List;

public interface CommentService {
    Comment getComment(Long commentId);

    Comment updateComment(Long commentId, NewCommentDto newCommentDto);

    void deleteComment(Long commentId);

    List<Comment> getComments(Long userId, int from, int size);

    Comment addComment(Comment comment);

    Comment updateComment(Long userId, Long commentId, NewCommentDto newCommentDto);

    void deleteComment(Long userId, Long commentId);

    List<Comment> getAllComments(Long eventId, int from, int size);
}
