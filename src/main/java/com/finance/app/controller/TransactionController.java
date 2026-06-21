package com.finance.app.controller;

import com.finance.app.dto.TransactionRequestDto;
import com.finance.app.dto.TransactionResponseDto;
import com.finance.app.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDto> createTransaction(
            @RequestParam Long userId,
            @Valid @RequestBody TransactionRequestDto requestDto
    ) {
        TransactionResponseDto created = transactionService.createTransaction(userId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponseDto>> getAllTransactions(
            @RequestParam Long userId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(transactionService.getAllTransactions(userId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> getTransactionById(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        try {
            return ResponseEntity.ok(transactionService.getTransactionById(id, userId));
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponseDto> updateTransaction(
            @PathVariable Long id,
            @RequestParam Long userId,
            @Valid @RequestBody TransactionRequestDto requestDto
    ) {
        try {
            return ResponseEntity.ok(transactionService.updateTransaction(id, userId, requestDto));
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        try {
            transactionService.softDeleteTransaction(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage(), ex);
        }
    }
}

