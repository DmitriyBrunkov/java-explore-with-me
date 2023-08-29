package ru.practicum.service.comment;

import lombok.experimental.UtilityClass;
import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.dto.NewCommentDto;
import ru.practicum.service.comment.model.Comment;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.user.model.User;

@UtilityClass
public class CommentMapper {
    public CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getAuthor().getName(), comment.getEvent().getTitle(),
                comment.getText(), comment.getIsModified(), comment.getLastChange());
    }

    public Comment toComment(User user, Event event, NewCommentDto newCommentDto) {
        Comment comment = new Comment();
        comment.setAuthor(user);
        comment.setEvent(event);
        comment.setText(newCommentDto.getText());
        return comment;
    }
}
