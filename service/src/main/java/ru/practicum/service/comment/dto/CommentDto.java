package ru.practicum.service.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

import static ru.practicum.service.event.model.DateTimeFormat.PATTERN;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String userName;
    private String titleEvent;
    private String text;
    private Boolean isModified;
    @JsonFormat(pattern = PATTERN)
    private LocalDateTime lastChange;
}
