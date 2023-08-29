package ru.practicum.service.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewCommentDto {
    @NotBlank
    @Size(min = 5, max = 4000)
    String text;
}
