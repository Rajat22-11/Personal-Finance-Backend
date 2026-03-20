package com.finance.app.servicesTest;

import com.finance.app.dto.TransactionRequestDto;
import com.finance.app.dto.TransactionResponseDto;
import com.finance.app.enums.TransactionTypeEnum;
import com.finance.app.model.Transaction;
import com.finance.app.repository.TransactionRepository;
import com.finance.app.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void createTransaction_shouldMapRequestAndReturnResponse() {
        TransactionRequestDto request = new TransactionRequestDto(
                2L,
                3L,
                new BigDecimal("250.00"),
                TransactionTypeEnum.EXPENSE,
                "Dinner",
                "Team dinner",
                LocalDateTime.of(2026, 1, 10, 20, 0),
                "receipts/dinner.png"
        );

        when(transactionRepository.save(org.mockito.ArgumentMatchers.any(Transaction.class)))
                .thenAnswer(invocation -> {
                    Transaction tx = invocation.getArgument(0);
                    tx.setId(100L);
                    return tx;
                });

        TransactionResponseDto response = transactionService.createTransaction(1L, request);

        assertEquals(100L, response.getId());
        assertEquals(1L, response.getUserId());
        assertEquals(2L, response.getAccountId());
        assertEquals(3L, response.getCategoryId());
        assertEquals(new BigDecimal("250.00"), response.getAmount());
    }

    @Test
    void getTransactionById_shouldReturnMappedResponse() {
        Transaction tx = Transaction.builder()
                .id(11L)
                .userId(1L)
                .accountId(2L)
                .categoryId(3L)
                .amount(new BigDecimal("120.00"))
                .type(TransactionTypeEnum.EXPENSE)
                .description("Cab")
                .transactionDate(LocalDateTime.now())
                .build();

        when(transactionRepository.findByIdAndUserIdAndIsActiveTrue(11L, 1L)).thenReturn(Optional.of(tx));

        TransactionResponseDto response = transactionService.getTransactionById(11L, 1L);

        assertEquals(11L, response.getId());
        assertEquals("Cab", response.getDescription());
    }

    @Test
    void getTransactionById_shouldThrowWhenMissing() {
        when(transactionRepository.findByIdAndUserIdAndIsActiveTrue(999L, 1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> transactionService.getTransactionById(999L, 1L));

        assertEquals("Transaction not found with id: 999", ex.getMessage());
    }

    @Test
    void getAllTransactions_shouldMapPageContent() {
        Transaction tx = Transaction.builder()
                .id(1L)
                .userId(1L)
                .accountId(2L)
                .categoryId(3L)
                .amount(new BigDecimal("99.00"))
                .type(TransactionTypeEnum.EXPENSE)
                .description("Groceries")
                .transactionDate(LocalDateTime.now())
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Transaction> page = new PageImpl<>(List.of(tx), pageable, 1);
        when(transactionRepository.findByUserIdAndIsActiveTrue(1L, pageable)).thenReturn(page);

        Page<TransactionResponseDto> result = transactionService.getAllTransactions(1L, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Groceries", result.getContent().get(0).getDescription());
    }

    @Test
    void updateTransaction_shouldUpdateFields() {
        Transaction existing = Transaction.builder()
                .id(5L)
                .userId(1L)
                .accountId(2L)
                .categoryId(3L)
                .amount(new BigDecimal("50.00"))
                .type(TransactionTypeEnum.EXPENSE)
                .description("Old")
                .transactionDate(LocalDateTime.now())
                .build();

        TransactionRequestDto request = new TransactionRequestDto(
                8L,
                9L,
                new BigDecimal("500.00"),
                TransactionTypeEnum.TRANSFER,
                "Updated",
                "updated-notes",
                LocalDateTime.of(2026, 2, 1, 10, 0),
                "new/path.pdf"
        );

        when(transactionRepository.findByIdAndUserIdAndIsActiveTrue(5L, 1L)).thenReturn(Optional.of(existing));
        when(transactionRepository.save(existing)).thenReturn(existing);

        TransactionResponseDto updated = transactionService.updateTransaction(5L, 1L, request);

        assertEquals(8L, updated.getAccountId());
        assertEquals(9L, updated.getCategoryId());
        assertEquals(new BigDecimal("500.00"), updated.getAmount());
        assertEquals(TransactionTypeEnum.TRANSFER, updated.getType());
        assertEquals("Updated", updated.getDescription());
    }

    @Test
    void softDeleteTransaction_shouldMarkInactiveAndSetDeletedAt() {
        Transaction tx = Transaction.builder()
                .id(5L)
                .userId(1L)
                .accountId(2L)
                .categoryId(3L)
                .amount(new BigDecimal("30.00"))
                .type(TransactionTypeEnum.EXPENSE)
                .transactionDate(LocalDateTime.now())
                .isActive(true)
                .build();

        when(transactionRepository.findByIdAndUserIdAndIsActiveTrue(5L, 1L)).thenReturn(Optional.of(tx));
        when(transactionRepository.save(tx)).thenReturn(tx);

        transactionService.softDeleteTransaction(5L, 1L);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        Transaction saved = captor.getValue();
        assertFalse(saved.getIsActive());
        assertTrue(saved.getDeletedAt() != null);
    }
}

