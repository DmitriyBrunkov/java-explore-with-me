package ru.practicum.service.compilation.service;

import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.service.compilation.model.Compilation;
import ru.practicum.service.compilation.repository.CompilationRepository;
import ru.practicum.service.exception.model.ObjectNotFoundException;
import ru.practicum.service.validation.PageableValidation;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;

    @Override
    public Compilation addCompilation(Compilation compilation) {
        return compilationRepository.save(compilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        if (!compilationRepository.existsById(compId)) {
            throw new ObjectNotFoundException("Compilation: " + compId + " not found");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    public Compilation updateCompilation(Long compId, Compilation newCompilation) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new ObjectNotFoundException(
                "Compilation: " + compId + " not found"));
        if (!newCompilation.getEvents().isEmpty()) {
            compilation.setEvents(newCompilation.getEvents());
        }
        if (newCompilation.getPinned() != null) {
            compilation.setPinned(newCompilation.getPinned());
        }
        if (!StringUtils.isBlank(newCompilation.getTitle())) {
            compilation.setTitle(newCompilation.getTitle());
        }
        return compilationRepository.save(compilation);
    }

    @Override
    public List<Compilation> getCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageableValidation.validatePageable(from, size);
        if (pinned != null) {
            return compilationRepository.findAllByPinned(pinned, pageable);
        }
        return compilationRepository.findAll(pageable).toList();
    }

    @Override
    public Compilation getCompilation(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new ObjectNotFoundException("Compilation: " + compId + " not found"));
    }
}
