package com.finance.app.dto;

import com.finance.app.enums.CategoryTypeEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequestDto {

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Category type is required")
    private CategoryTypeEnum type;

    @NotBlank(message = "Color is required")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex code")
    private String color;

    @NotBlank(message = "Icon is required")
    @Size(max = 50, message = "Icon must not exceed 50 characters")
    private String icon;

    private Long parentId;

    @NotNull(message = "Depth is required")
    @Min(value = 0, message = "Depth must be at least 0")
    @Max(value = 1, message = "Depth cannot exceed 1")
    private Integer depth;
}

