package ru.practicum.service.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.service.category.model.Category;
import ru.practicum.service.category.repository.CategoryRepository;
import ru.practicum.service.exception.model.ObjectNotFoundException;
import ru.practicum.service.validation.PageableValidation;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() -> new  ObjectNotFoundException("Category: " + categoryId + " not found"));
    }

    @Override
    public Category addCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        getCategory(categoryId);
        categoryRepository.deleteById(categoryId);
    }

    @Override
    public Category updateCategory(Long categoryId, Category updateCategory) {
        Category category = getCategory(categoryId);
        category.setName(updateCategory.getName());
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories(int from, int size) {
        Pageable pageable = PageableValidation.validatePageable(from, size);
        return categoryRepository.findAll(pageable).toList();
    }
}
