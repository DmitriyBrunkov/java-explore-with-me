package ru.practicum.service.compilation.dto;

import lombok.Data;
import ru.practicum.service.validation.ValidateException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
public class NewCompilationDto {
    private Long id;
    private Set<Long> events = new HashSet<>();
    private Boolean pinned = false;
    @NotBlank(groups = ValidateException.OnCreate.class)
    @Size(min = 1, max = 50, groups = {ValidateException.OnCreate.class, ValidateException.OnUpdate.class})
    private String title;
}
