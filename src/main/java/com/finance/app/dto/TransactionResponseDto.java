package com.finance.app.dto;

import com.finance.app.enums.TransactionTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDto {
    
    private Long id;
    private Long userId;
    private Long accountId;
    private Long categoryId;
    private BigDecimal amount;
    private TransactionTypeEnum type;
    private String description;
    private String notes;
    private LocalDateTime transactionDate;
    private String receiptPath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
