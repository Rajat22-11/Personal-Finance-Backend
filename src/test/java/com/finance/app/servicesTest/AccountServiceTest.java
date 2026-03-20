package com.finance.app.servicesTest;

import com.finance.app.enums.AccountTypeEnum;
import com.finance.app.model.Account;
import com.finance.app.repository.AccountRepository;
import com.finance.app.service.AccountService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void createAccount_shouldDefaultCurrencyToINR() {
        Account request = Account.builder()
                .name("Main Account")
                .type(AccountTypeEnum.CHECKING)
                .currency("INR")
                .initialBalance(new BigDecimal("1000.00"))
                .build();

        when(accountRepository.save(org.mockito.ArgumentMatchers.any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account created = accountService.createAccount(10L, request);

        assertEquals(10L, created.getUserId());
        assertEquals("INR", created.getCurrency());
        assertTrue(created.getIsActive());
    }

    @Test
    void getAllAccounts_shouldReturnActiveAccounts() {
        Account a1 = Account.builder().id(1L).userId(10L).name("A").type(AccountTypeEnum.CHECKING).currency("INR").initialBalance(BigDecimal.ZERO).build();
        Account a2 = Account.builder().id(2L).userId(10L).name("B").type(AccountTypeEnum.SAVINGS).currency("INR").initialBalance(BigDecimal.ZERO).build();

        when(accountRepository.findByUserIdAndIsActiveTrue(10L)).thenReturn(List.of(a1, a2));

        List<Account> accounts = accountService.getAllAccounts(10L);

        assertEquals(2, accounts.size());
    }

    @Test
    void getAccountById_shouldReturnAccount() {
        Account account = Account.builder().id(1L).userId(10L).name("A").type(AccountTypeEnum.CASH).currency("INR").initialBalance(BigDecimal.ZERO).build();
        when(accountRepository.findByIdAndUserIdAndIsActiveTrue(1L, 10L)).thenReturn(Optional.of(account));

        Account result = accountService.getAccountById(1L, 10L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getAccountById_shouldThrowWhenMissing() {
        when(accountRepository.findByIdAndUserIdAndIsActiveTrue(99L, 10L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> accountService.getAccountById(99L, 10L));

        assertEquals("Account not found", ex.getMessage());
    }

    @Test
    void updateAccount_shouldUpdateFields() {
        Account existing = Account.builder()
                .id(1L)
                .userId(10L)
                .name("Old")
                .type(AccountTypeEnum.CHECKING)
                .currency("INR")
                .initialBalance(new BigDecimal("100.00"))
                .build();

        Account request = Account.builder()
                .name("Updated")
                .type(AccountTypeEnum.SAVINGS)
                .currency("USD")
                .initialBalance(new BigDecimal("200.00"))
                .build();

        when(accountRepository.findByIdAndUserIdAndIsActiveTrue(1L, 10L)).thenReturn(Optional.of(existing));
        when(accountRepository.save(existing)).thenReturn(existing);

        Account updated = accountService.updateAccount(1L, 10L, request);

        assertEquals("Updated", updated.getName());
        assertEquals(AccountTypeEnum.SAVINGS, updated.getType());
        assertEquals("USD", updated.getCurrency());
        assertEquals(new BigDecimal("200.00"), updated.getInitialBalance());
    }

    @Test
    void softDeleteAccount_shouldMarkInactiveAndSetDeletedAt() {
        Account account = Account.builder().id(1L).userId(10L).name("A").type(AccountTypeEnum.CHECKING).currency("INR").initialBalance(BigDecimal.ZERO).isActive(true).build();
        when(accountRepository.findByIdAndUserIdAndIsActiveTrue(1L, 10L)).thenReturn(Optional.of(account));
        when(accountRepository.save(account)).thenReturn(account);

        accountService.softDeleteAccount(1L, 10L);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        Account saved = captor.getValue();
        assertFalse(saved.getIsActive());
        assertTrue(saved.getDeletedAt() != null);
    }
}

