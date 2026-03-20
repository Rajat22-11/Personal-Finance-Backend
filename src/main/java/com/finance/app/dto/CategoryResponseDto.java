package com.finance.app.dto;

import com.finance.app.enums.CategoryTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponseDto {

    private Long id;
    private Long userId;
    private String name;
    private CategoryTypeEnum type;
    private String color;
    private String icon;
    private Long parentId;
    private Integer depth;
    private Boolean isSystem;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

