package com.finance.app.service;

import com.finance.app.dto.CategoryRequestDto;
import com.finance.app.dto.CategoryResponseDto;
import com.finance.app.model.Category;
import com.finance.app.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponseDto createCategory(Long userId, CategoryRequestDto requestDto) {
        Category category = Category.builder()
                .userId(userId)
                .name(requestDto.getName())
                .type(requestDto.getType())
                .color(requestDto.getColor())
                .icon(requestDto.getIcon())
                .parentId(requestDto.getParentId())
                .depth(requestDto.getDepth())
                .isSystem(false)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return mapToResponseDto(savedCategory);
    }

    @Transactional(readOnly = true)
    public CategoryResponseDto getCategoryById(Long categoryId, Long userId) {
        Category category = categoryRepository.findByIdAndUserIdAndIsActiveTrue(categoryId, userId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        return mapToResponseDto(category);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDto> getAllCategories(Long userId) {
        return categoryRepository.findByUserIdAndIsActiveTrue(userId)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    public CategoryResponseDto updateCategory(Long categoryId, Long userId, CategoryRequestDto requestDto) {
        Category category = categoryRepository.findByIdAndUserIdAndIsActiveTrue(categoryId, userId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        category.setName(requestDto.getName());
        category.setType(requestDto.getType());
        category.setColor(requestDto.getColor());
        category.setIcon(requestDto.getIcon());
        category.setParentId(requestDto.getParentId());
        category.setDepth(requestDto.getDepth());
        category.setUpdatedAt(LocalDateTime.now());

        Category updatedCategory = categoryRepository.save(category);
        return mapToResponseDto(updatedCategory);
    }

    public void softDeleteCategory(Long categoryId, Long userId) {
        Category category = categoryRepository.findByIdAndUserIdAndIsActiveTrue(categoryId, userId)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + categoryId));

        category.setIsActive(false);
        category.setDeletedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());

        categoryRepository.save(category);
    }

    private CategoryResponseDto mapToResponseDto(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .userId(category.getUserId())
                .name(category.getName())
                .type(category.getType())
                .color(category.getColor())
                .icon(category.getIcon())
                .parentId(category.getParentId())
                .depth(category.getDepth())
                .isSystem(category.getIsSystem())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}

