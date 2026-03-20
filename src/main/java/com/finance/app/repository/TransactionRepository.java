package com.finance.app.repository;

import com.finance.app.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByUserIdAndIsActiveTrue(Long userId, Pageable pageable);
    Optional<Transaction> findByIdAndUserIdAndIsActiveTrue(Long id, Long userId);
}
