package ru.practicum.service.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.comment.CommentMapper;
import ru.practicum.service.comment.dto.CommentDto;
import ru.practicum.service.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events/{eventId}/comments")
public class PubCommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getAllComments(@PathVariable @Positive Long eventId,
                                           @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(defaultValue = "10") @Positive int size) {
        log.info("{}: GET: eventId: {} from: {} size: {}", this.getClass().getSimpleName(), eventId, from, size);
        return commentService.getAllComments(eventId, from, size).stream().map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }
}
