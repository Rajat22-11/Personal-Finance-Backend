package com.finance.app;

import com.finance.app.enums.AccountTypeEnum;
import com.finance.app.model.Account;
import com.finance.app.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class AccountRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void shouldSaveAccount() {
        Account account = Account.builder()
                .userId(1L)
                .name("Test Account")
                .type(AccountTypeEnum.SAVINGS)
                .currency("INR")
                .initialBalance(BigDecimal.valueOf(1000))
                .isActive(true)
                .build();

        Account saved = accountRepository.save(account);

        assertNotNull(saved.getId());
    }
}

