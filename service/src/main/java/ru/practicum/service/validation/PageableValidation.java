package ru.practicum.service.validation;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.service.event.enums.SortType;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@UtilityClass
public class PageableValidation {
    public Pageable validatePageable(@Valid @Min(value = 0) Integer from,
                                     @Valid @Min(value = 1) Integer size) {
        return PageRequest.of(from == 0 ? 0 : from / size, size);
    }

    public Pageable validatePageable(@Valid @Min(value = 0) Integer from,
                                     @Valid @Min(value = 1) Integer size,
                                     SortType sort) {
        if (sort.equals(SortType.EVENT_DATE)) {
            return PageRequest.of(from == 0 ? 0 : from / size, size, Sort.by("eventDate").descending());
        }
        return validatePageable(from, size);
    }
}
