package com.finance.app.service;

import com.finance.app.dto.TransactionRequestDto;
import com.finance.app.dto.TransactionResponseDto;
import com.finance.app.model.Transaction;
import com.finance.app.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionResponseDto createTransaction(Long userId, TransactionRequestDto requestDto) {
        Transaction transaction = Transaction.builder()
                .userId(userId)
                .accountId(requestDto.getAccountId())
                .categoryId(requestDto.getCategoryId())
                .amount(requestDto.getAmount())
                .type(requestDto.getType())
                .description(requestDto.getDescription())
                .notes(requestDto.getNotes())
                .transactionDate(requestDto.getTransactionDate())
                .receiptPath(requestDto.getReceiptPath())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponseDto(saved);
    }

    @Transactional(readOnly = true)
    public TransactionResponseDto getTransactionById(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findByIdAndUserIdAndIsActiveTrue(transactionId, userId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));

        return mapToResponseDto(transaction);
    }

    @Transactional(readOnly = true)
    public Page<TransactionResponseDto> getAllTransactions(Long userId, Pageable pageable) {
        return transactionRepository.findByUserIdAndIsActiveTrue(userId, pageable)
                .map(this::mapToResponseDto);
    }

    public TransactionResponseDto updateTransaction(Long transactionId, Long userId, TransactionRequestDto requestDto) {
        Transaction transaction = transactionRepository.findByIdAndUserIdAndIsActiveTrue(transactionId, userId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));

        transaction.setAccountId(requestDto.getAccountId());
        transaction.setCategoryId(requestDto.getCategoryId());
        transaction.setAmount(requestDto.getAmount());
        transaction.setType(requestDto.getType());
        transaction.setDescription(requestDto.getDescription());
        transaction.setNotes(requestDto.getNotes());
        transaction.setTransactionDate(requestDto.getTransactionDate());
        transaction.setReceiptPath(requestDto.getReceiptPath());
        transaction.setUpdatedAt(LocalDateTime.now());

        Transaction updated = transactionRepository.save(transaction);
        return mapToResponseDto(updated);
    }

    public void softDeleteTransaction(Long transactionId, Long userId) {
        Transaction transaction = transactionRepository.findByIdAndUserIdAndIsActiveTrue(transactionId, userId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));

        transaction.setIsActive(false);
        transaction.setDeletedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());

        transactionRepository.save(transaction);
    }

    private TransactionResponseDto mapToResponseDto(Transaction transaction) {
        return TransactionResponseDto.builder()
                .id(transaction.getId())
                .userId(transaction.getUserId())
                .accountId(transaction.getAccountId())
                .categoryId(transaction.getCategoryId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .notes(transaction.getNotes())
                .transactionDate(transaction.getTransactionDate())
                .receiptPath(transaction.getReceiptPath())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
}
