package com.finance.app.dto;

import com.finance.app.enums.TransactionTypeEnum;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequestDto {

    @NotNull(message = "Account id is required")
    private Long accountId;

    @NotNull(message = "Category id is required")
    private Long categoryId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    private TransactionTypeEnum type;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    private String notes;

    @NotNull(message = "Transaction date is required")
    private LocalDateTime transactionDate;

    @Size(max = 500, message = "Receipt path must not exceed 500 characters")
    private String receiptPath;
}
