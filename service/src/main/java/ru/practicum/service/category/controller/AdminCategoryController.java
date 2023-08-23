package ru.practicum.service.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.category.CategoryMapper;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/admin/categories")
@Validated
@Slf4j
public class AdminCategoryController {
    private final CategoryService categoryService;

    @Autowired
    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody CategoryDto categoryDto) {
        log.info(this.getClass().getSimpleName() + ": POST: CategoryDto: {}", categoryDto);
        return CategoryMapper.toCategoryDto(categoryService.addCategory(CategoryMapper.toCategory(categoryDto)));
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @PositiveOrZero Long catId) {
        log.info(this.getClass().getSimpleName() + ": DELETE: catId: {}", catId);
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable @PositiveOrZero Long catId,
                                   @Valid @RequestBody CategoryDto categoryDto) {
        log.info(this.getClass().getSimpleName() + ": Patch: catId: {} CategoryDto: ", catId, categoryDto);
        return CategoryMapper.toCategoryDto(categoryService.updateCategory(catId,
                CategoryMapper.toCategory(categoryDto)));
    }
}
