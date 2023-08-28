package ru.practicum.service.category;

import lombok.experimental.UtilityClass;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.model.Category;

@UtilityClass
public class CategoryMapper {
    public Category toCategory(CategoryDto categoryDto) {
        return new Category(categoryDto.getId(), categoryDto.getName());
    }

    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }
}
