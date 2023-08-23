package ru.practicum.service.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.category.CategoryMapper;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
@Validated
@Slf4j
public class PubCategoryController {
    private final CategoryService categoryService;

    @Autowired
    public PubCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                              @RequestParam(defaultValue = "10") @Positive int size) {
        log.info(this.getClass().getSimpleName() + ": GET: ALL: from: {} size: {}", from, size);
        return categoryService.getAllCategories(from, size).stream().map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@Positive @PathVariable Long catId) {
        log.info(this.getClass().getSimpleName() + ": GET: catId: {}", catId);
        return CategoryMapper.toCategoryDto(categoryService.getCategory(catId));
    }
}
