package com.finance.app.dto;

import com.finance.app.enums.AccountTypeEnum;
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
public class AccountResponseDto {

    private Long id;
    private String name;
    private AccountTypeEnum type;
    private String currency;
    private BigDecimal initialBalance;
    private BigDecimal currentBalance; // Calculated from transactions (future feature)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
