package com.finance.app.repository;

import com.finance.app.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUserIdAndIsActiveTrue(Long userId);
    Optional<Account> findByIdAndUserIdAndIsActiveTrue(Long id, Long userId);
}
