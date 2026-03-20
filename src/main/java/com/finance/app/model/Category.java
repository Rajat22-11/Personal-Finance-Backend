package com.finance.app.model;

import com.finance.app.enums.CategoryTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Category extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId; // NULL for system categories

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoryTypeEnum type;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String icon;

    @Column(name = "parent_id")
    private Long parentId;

    @Column(nullable = false)
    private Integer depth = 0;

    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;
}
