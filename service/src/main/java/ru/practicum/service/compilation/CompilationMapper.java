package ru.practicum.service.compilation;

import lombok.experimental.UtilityClass;
import ru.practicum.service.compilation.dto.CompilationDto;
import ru.practicum.service.compilation.dto.NewCompilationDto;
import ru.practicum.service.compilation.model.Compilation;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.model.Event;

import java.util.Set;

@UtilityClass
public class CompilationMapper {
    public Compilation toCompilation(Set<Event> events, NewCompilationDto newCompilationDto) {
        return new Compilation(newCompilationDto.getId(), newCompilationDto.getPinned(), newCompilationDto.getTitle(),
                events);
    }

    public CompilationDto toCompilationDto(Set<EventShortDto> events, Compilation compilation) {
        return new CompilationDto(events, compilation.getId(), compilation.getPinned(), compilation.getTitle());
    }
}
