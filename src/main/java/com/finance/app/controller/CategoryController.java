package com.finance.app.controller;

import com.finance.app.dto.CategoryRequestDto;
import com.finance.app.dto.CategoryResponseDto;
import com.finance.app.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	@PostMapping
	public ResponseEntity<CategoryResponseDto> createCategory(
			@RequestParam Long userId,
			@Valid @RequestBody CategoryRequestDto requestDto
	) {
		CategoryResponseDto created = categoryService.createCategory(userId, requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@GetMapping
	public ResponseEntity<List<CategoryResponseDto>> getAllCategories(@RequestParam Long userId) {
		return ResponseEntity.ok(categoryService.getAllCategories(userId));
	}

	@GetMapping("/{id}")
	public ResponseEntity<CategoryResponseDto> getCategoryById(
			@PathVariable Long id,
			@RequestParam Long userId
	) {
		try {
			return ResponseEntity.ok(categoryService.getCategoryById(id, userId));
		} catch (RuntimeException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
	}

	@PutMapping("/{id}")
	public ResponseEntity<CategoryResponseDto> updateCategory(
			@PathVariable Long id,
			@RequestParam Long userId,
			@Valid @RequestBody CategoryRequestDto requestDto
	) {
		try {
			return ResponseEntity.ok(categoryService.updateCategory(id, userId, requestDto));
		} catch (RuntimeException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCategory(
			@PathVariable Long id,
			@RequestParam Long userId
	) {
		try {
			categoryService.softDeleteCategory(id, userId);
			return ResponseEntity.noContent().build();
		} catch (RuntimeException ex) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
		}
	}
}

