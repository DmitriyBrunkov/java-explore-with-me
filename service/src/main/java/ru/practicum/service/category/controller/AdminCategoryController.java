package ru.practicum.service.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.category.CategoryMapper;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/categories")
@Validated
@Slf4j
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@RequestBody @Valid CategoryDto categoryDto) {
        log.info("{}: POST: CategoryDto: {}", this.getClass().getSimpleName(), categoryDto);
        return CategoryMapper.toCategoryDto(categoryService.addCategory(CategoryMapper.toCategory(categoryDto)));
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long catId) {
        log.info("{}: DELETE: catId: {}", this.getClass().getSimpleName(), catId);
        categoryService.deleteCategory(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId, @RequestBody @Valid CategoryDto categoryDto) {
        log.info("{}: Patch: catId: {} CategoryDto: {}", this.getClass().getSimpleName(), catId, categoryDto);
        return CategoryMapper.toCategoryDto(categoryService.updateCategory(catId,
                CategoryMapper.toCategory(categoryDto)));
    }
}
