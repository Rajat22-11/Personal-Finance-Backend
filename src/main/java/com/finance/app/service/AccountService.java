package com.finance.app.service;

import com.finance.app.model.Account;
import com.finance.app.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public Account createAccount(Long userId, Account request) {
        Account account = Account.builder()
                .userId(userId)
                .name(request.getName())
                .type(request.getType())
                .currency(request.getCurrency() == null ? "INR" : request.getCurrency())
                .initialBalance(request.getInitialBalance())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts(Long userId) {
        return accountRepository.findByUserIdAndIsActiveTrue(userId);
    }

    @Transactional(readOnly = true)
    public Account getAccountById(Long accountId, Long userId) {
        return accountRepository.findByIdAndUserIdAndIsActiveTrue(accountId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
    }

    public Account updateAccount(Long accountId, Long userId, Account request) {
        Account account = getAccountById(accountId, userId);

        account.setName(request.getName());
        account.setType(request.getType());
        account.setCurrency(request.getCurrency());
        account.setInitialBalance(request.getInitialBalance());
        account.setUpdatedAt(LocalDateTime.now());

        return accountRepository.save(account);
    }

    public void softDeleteAccount(Long accountId, Long userId) {
        Account account = getAccountById(accountId, userId);
        account.setIsActive(false);
        account.setDeletedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
    }
}
