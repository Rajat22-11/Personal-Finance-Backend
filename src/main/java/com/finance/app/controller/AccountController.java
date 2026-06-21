package com.finance.app.controller;

import com.finance.app.dto.AccountRequestDto;
import com.finance.app.dto.AccountResponseDto;
import com.finance.app.model.Account;
import com.finance.app.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponseDto> createAccount(
            @RequestParam Long userId,
            @Valid @RequestBody AccountRequestDto requestDto
    ) {
        Account request = Account.builder()
                .name(requestDto.getName())
                .type(requestDto.getType())
                .currency(requestDto.getCurrency())
                .initialBalance(requestDto.getInitialBalance())
                .build();

        Account created = accountService.createAccount(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponseDto(created));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDto>> getAllAccounts(@RequestParam Long userId) {
        List<AccountResponseDto> accounts = accountService.getAllAccounts(userId)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDto> getAccountById(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        try {
            Account account = accountService.getAccountById(id, userId);
            return ResponseEntity.ok(mapToResponseDto(account));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDto> updateAccount(
            @PathVariable Long id,
            @RequestParam Long userId,
            @Valid @RequestBody AccountRequestDto requestDto
    ) {
        try {
            Account request = Account.builder()
                    .name(requestDto.getName())
                    .type(requestDto.getType())
                    .currency(requestDto.getCurrency())
                    .initialBalance(requestDto.getInitialBalance())
                    .build();

            Account updated = accountService.updateAccount(id, userId, request);
            return ResponseEntity.ok(mapToResponseDto(updated));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        try {
            accountService.softDeleteAccount(id, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    private AccountResponseDto mapToResponseDto(Account account) {
        return AccountResponseDto.builder()
                .id(account.getId())
                .name(account.getName())
                .type(account.getType())
                .currency(account.getCurrency())
                .initialBalance(account.getInitialBalance())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}

