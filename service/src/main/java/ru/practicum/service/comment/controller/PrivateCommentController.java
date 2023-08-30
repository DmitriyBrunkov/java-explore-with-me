package ru.practicum.service.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.comment.CommentMapper;
import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.dto.NewCommentDto;
import ru.practicum.service.comment.service.CommentService;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.service.EventService;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class PrivateCommentController {
    private final CommentService commentService;
    private final UserService userService;
    private final EventService eventService;

    @GetMapping("/comments")
    public List<CommentDto> getAllUserComments(@PathVariable @Positive Long userId,
                                               @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                               @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("{}: GET: userId: {} from: {} size: {}", this.getClass().getSimpleName(), userId, from, size);
        return commentService.getComments(userId, from, size).stream().map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addComment(@PathVariable @Positive Long userId,
                                 @PathVariable @Positive Long eventId,
                                 @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("{}: POST: userId: {} eventId: {} newCommentDto: {}", this.getClass()
                .getSimpleName(), userId, eventId, newCommentDto);
        User user = userService.getUser(userId);
        Event event = eventService.getEvent(eventId);
        return CommentMapper.toCommentDto(commentService.addComment(CommentMapper.toComment(user, event, newCommentDto)));
    }

    @PatchMapping("/events/{eventId}/comments/{commentId}")
    public CommentDto updateComment(@PathVariable @Positive Long userId,
                                    @PathVariable @Positive Long commentId,
                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("{}: PATCH: userId: {} commentId: {} newCommentDto: {}", this.getClass()
                .getSimpleName(), userId, commentId, newCommentDto);
        return CommentMapper.toCommentDto(commentService.updateComment(userId, commentId, newCommentDto));
    }

    @DeleteMapping("/events/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long userId,
                              @PathVariable @Positive Long commentId) {
        log.info("{}: DELETE: userId: {} commentId: {}", this.getClass().getSimpleName(), userId, commentId);
        commentService.deleteComment(userId, commentId);
    }
}
