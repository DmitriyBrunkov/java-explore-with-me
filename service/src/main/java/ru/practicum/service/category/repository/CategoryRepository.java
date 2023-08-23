package ru.practicum.service.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.service.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
