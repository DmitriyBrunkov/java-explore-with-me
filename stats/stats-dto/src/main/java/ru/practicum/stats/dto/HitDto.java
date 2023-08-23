package ru.practicum.stats.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class HitDto {
    @NotBlank
    String app;
    @NotBlank
    String uri;
    @NotBlank
    String ip;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Builder.Default()
    LocalDateTime timestamp = LocalDateTime.now();
}
