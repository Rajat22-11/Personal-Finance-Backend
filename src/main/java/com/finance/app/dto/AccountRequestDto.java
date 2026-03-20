package com.finance.app.dto;

import com.finance.app.enums.AccountTypeEnum;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequestDto {

    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Account name must not exceed 100 characters")
    private String name;

    @NotNull(message = "Account type is required")
    private AccountTypeEnum type;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 characters (e.g., INR)")
    private String currency = "INR";

    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.0", message = "Initial balance must be greater than or equal to 0")
    private BigDecimal initialBalance;
}
