package ru.practicum.service.compilation.service;

import ru.practicum.service.compilation.model.Compilation;

import java.util.List;

public interface CompilationService {
    Compilation addCompilation(Compilation compilation);

    void deleteCompilation(Long compId);

    Compilation updateCompilation(Long compId, Compilation newCompilation);

    List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size);

    Compilation getCompilation(Long compId);
}
