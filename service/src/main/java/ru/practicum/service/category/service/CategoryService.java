package ru.practicum.service.category.service;

import ru.practicum.service.category.model.Category;

import java.util.List;

public interface CategoryService {
    Category getCategory(Long categoryId);

    Category addCategory(Category category);

    void deleteCategory(Long categoryId);

    Category updateCategory(Long categoryId, Category updateCategory);

    List<Category> getAllCategories(int from, int size);
}
