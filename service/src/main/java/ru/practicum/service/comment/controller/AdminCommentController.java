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

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/admin/comments")
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping("/{commentId}")
    public CommentDto getComment(@PathVariable @Positive Long commentId) {
        log.info("{}: GET: commentId: {}", this.getClass().getSimpleName(), commentId);
        return CommentMapper.toCommentDto(commentService.getComment(commentId));
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateComment(@PathVariable @Positive Long commentId,
                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        log.info("{}: PATCH: commentId: {} newCommentDto: {}", this.getClass().getSimpleName(), commentId, newCommentDto);
        return CommentMapper.toCommentDto(commentService.updateComment(commentId, newCommentDto));
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable @Positive Long commentId) {
        log.info("{}: DELETE: commentId: {}", this.getClass().getSimpleName(), commentId);
        commentService.deleteComment(commentId);
    }
}
