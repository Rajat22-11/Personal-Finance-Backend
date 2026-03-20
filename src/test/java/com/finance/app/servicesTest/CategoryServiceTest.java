package com.finance.app.servicesTest;

import com.finance.app.dto.CategoryRequestDto;
import com.finance.app.dto.CategoryResponseDto;
import com.finance.app.enums.CategoryTypeEnum;
import com.finance.app.model.Category;
import com.finance.app.repository.CategoryRepository;
import com.finance.app.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void createCategory_shouldSetUserOwnedAndReturnResponse() {
        CategoryRequestDto request = new CategoryRequestDto(
                "Food",
                CategoryTypeEnum.EXPENSE,
                "#FF5733",
                "utensils",
                null,
                0
        );

        when(categoryRepository.save(org.mockito.ArgumentMatchers.any(Category.class)))
                .thenAnswer(invocation -> {
                    Category saved = invocation.getArgument(0);
                    saved.setId(11L);
                    return saved;
                });

        CategoryResponseDto response = categoryService.createCategory(7L, request);

        assertEquals(11L, response.getId());
        assertEquals(7L, response.getUserId());
        assertEquals("Food", response.getName());
        assertFalse(response.getIsSystem());
    }

    @Test
    void getCategoryById_shouldReturnMappedResponse() {
        Category category = Category.builder()
                .id(1L)
                .userId(7L)
                .name("Travel")
                .type(CategoryTypeEnum.EXPENSE)
                .color("#123456")
                .icon("car")
                .depth(0)
                .isSystem(false)
                .build();

        when(categoryRepository.findByIdAndUserIdAndIsActiveTrue(1L, 7L)).thenReturn(Optional.of(category));

        CategoryResponseDto response = categoryService.getCategoryById(1L, 7L);

        assertEquals(1L, response.getId());
        assertEquals("Travel", response.getName());
    }

    @Test
    void getCategoryById_shouldThrowWhenMissing() {
        when(categoryRepository.findByIdAndUserIdAndIsActiveTrue(99L, 7L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> categoryService.getCategoryById(99L, 7L));

        assertEquals("Category not found with id: 99", ex.getMessage());
    }

    @Test
    void getAllCategories_shouldMapList() {
        Category c1 = Category.builder().id(1L).userId(7L).name("Food").type(CategoryTypeEnum.EXPENSE).color("#111111").icon("food").depth(0).isSystem(false).build();
        Category c2 = Category.builder().id(2L).userId(7L).name("Salary").type(CategoryTypeEnum.INCOME).color("#222222").icon("money").depth(0).isSystem(false).build();

        when(categoryRepository.findByUserIdAndIsActiveTrue(7L)).thenReturn(List.of(c1, c2));

        List<CategoryResponseDto> result = categoryService.getAllCategories(7L);

        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).getName());
        assertEquals("Salary", result.get(1).getName());
    }

    @Test
    void updateCategory_shouldUpdateFields() {
        Category existing = Category.builder()
                .id(5L)
                .userId(7L)
                .name("Old")
                .type(CategoryTypeEnum.EXPENSE)
                .color("#000000")
                .icon("old")
                .depth(0)
                .isSystem(false)
                .build();

        CategoryRequestDto request = new CategoryRequestDto(
                "New",
                CategoryTypeEnum.INCOME,
                "#AAAAAA",
                "new-icon",
                1L,
                1
        );

        when(categoryRepository.findByIdAndUserIdAndIsActiveTrue(5L, 7L)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(existing)).thenReturn(existing);

        CategoryResponseDto updated = categoryService.updateCategory(5L, 7L, request);

        assertEquals("New", updated.getName());
        assertEquals(CategoryTypeEnum.INCOME, updated.getType());
        assertEquals("#AAAAAA", updated.getColor());
        assertEquals("new-icon", updated.getIcon());
        assertEquals(1L, updated.getParentId());
        assertEquals(1, updated.getDepth());
    }

    @Test
    void softDeleteCategory_shouldMarkInactiveAndSetDeletedAt() {
        Category category = Category.builder()
                .id(2L)
                .userId(7L)
                .name("Food")
                .type(CategoryTypeEnum.EXPENSE)
                .color("#123123")
                .icon("food")
                .depth(0)
                .isSystem(false)
                .isActive(true)
                .build();

        when(categoryRepository.findByIdAndUserIdAndIsActiveTrue(2L, 7L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);

        categoryService.softDeleteCategory(2L, 7L);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        Category saved = captor.getValue();
        assertFalse(saved.getIsActive());
        assertTrue(saved.getDeletedAt() != null);
    }
}

