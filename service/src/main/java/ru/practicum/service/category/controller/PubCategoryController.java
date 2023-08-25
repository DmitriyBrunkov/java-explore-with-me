package ru.practicum.service.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.category.CategoryMapper;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/categories")
@Validated
@Slf4j
@RequiredArgsConstructor
public class PubCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0", required = false) int from,
                                              @RequestParam(defaultValue = "10", required = false) int size) {
        log.info("{}: GET: ALL: from: {} size: {}", this.getClass().getSimpleName(), from, size);
        return categoryService.getAllCategories(from, size).stream().map(CategoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info("{}: GET: catId: {}", this.getClass().getSimpleName(), catId);
        return CategoryMapper.toCategoryDto(categoryService.getCategory(catId));
    }
}
