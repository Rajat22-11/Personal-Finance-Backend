package com.finance.app.repository;

import com.finance.app.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByUserIdAndIsActiveTrue(Long userId);
    Optional<Category> findByIdAndUserIdAndIsActiveTrue(Long id, Long userId);
}
