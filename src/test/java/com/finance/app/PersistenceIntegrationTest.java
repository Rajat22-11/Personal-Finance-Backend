package com.finance.app;

import com.finance.app.enums.AccountTypeEnum;
import com.finance.app.enums.CategoryTypeEnum;
import com.finance.app.enums.TransactionTypeEnum;
import com.finance.app.model.Account;
import com.finance.app.model.Category;
import com.finance.app.model.Transaction;
import com.finance.app.model.User;
import com.finance.app.repository.AccountRepository;
import com.finance.app.repository.CategoryRepository;
import com.finance.app.repository.TransactionRepository;
import com.finance.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PersistenceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    // =====================================================
    // 1️⃣ USER TEST
    // =====================================================
    @Test
    void shouldSaveUser() {

        User user = User.builder()
                .email("user@test.com")
                .passwordHash("hashed")
                .firstName("Rajat")
                .lastName("Patel")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("user@test.com");
    }

    // =====================================================
    // 2️⃣ ACCOUNT TEST
    // =====================================================
    @Test
    void shouldSaveAccount() {

        User user = createUser();

        Account account = Account.builder()
                .userId(user.getId())
                .name("Primary")
                .type(AccountTypeEnum.CHECKING)
                .currency("INR")
                .initialBalance(BigDecimal.valueOf(5000))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        Account saved = accountRepository.save(account);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getType()).isEqualTo(AccountTypeEnum.CHECKING);
    }

    // =====================================================
    // 3️⃣ CATEGORY TEST
    // =====================================================
    @Test
    void shouldSaveCategory() {

        User user = createUser();

        Category category = Category.builder()
                .userId(user.getId())
                .name("Food")
                .type(CategoryTypeEnum.EXPENSE)
                .color("#FF5733")
                .icon("food")
                .depth(0)
                .isSystem(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        Category saved = categoryRepository.save(category);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getType()).isEqualTo(CategoryTypeEnum.EXPENSE);
    }

    // =====================================================
    // 4️⃣ TRANSACTION TEST
    // =====================================================
    @Test
    void shouldSaveTransaction() {

        User user = createUser();
        Account account = createAccount(user);
        Category category = createCategory(user);

        Transaction transaction = Transaction.builder()
                .userId(user.getId())
                .accountId(account.getId())
                .categoryId(category.getId())
                .amount(BigDecimal.valueOf(1000))
                .type(TransactionTypeEnum.EXPENSE)
                .description("Dinner")
                .transactionDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        Transaction saved = transactionRepository.save(transaction);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getAmount()).isEqualTo(BigDecimal.valueOf(1000));
    }

    // =====================================================
    // 5️⃣ PAGINATION TEST
    // =====================================================
    @Test
    void shouldPaginateTransactions() {

        User user = createUser();
        Account account = createAccount(user);
        Category category = createCategory(user);

        Transaction tx = Transaction.builder()
                .userId(user.getId())
                .accountId(account.getId())
                .categoryId(category.getId())
                .amount(BigDecimal.valueOf(200))
                .type(TransactionTypeEnum.EXPENSE)
                .transactionDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .isActive(true)
                .build();

        transactionRepository.save(tx);

        var page = transactionRepository.findByUserIdAndIsActiveTrue(
                user.getId(),
                PageRequest.of(0, 5)
        );

        assertThat(page.getContent()).hasSize(1);
    }

    // =====================================================
    // 6️⃣ SOFT DELETE TEST
    // =====================================================
    @Test
    void shouldRespectSoftDelete() {

        User user = createUser();
        Account account = createAccount(user);

        account.setIsActive(false);
        accountRepository.save(account);

        var activeAccounts =
                accountRepository.findByUserIdAndIsActiveTrue(user.getId());

        assertThat(activeAccounts).isEmpty();
    }

    // =====================================================
    // 🔧 Helper Methods
    // =====================================================

    private User createUser() {
        return userRepository.save(
                User.builder()
                        .email("helper@test.com")
                        .passwordHash("hashed")
                        .firstName("Test")
                        .lastName("User")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .isActive(true)
                        .build()
        );
    }

    private Account createAccount(User user) {
        return accountRepository.save(
                Account.builder()
                        .userId(user.getId())
                        .name("HelperAccount")
                        .type(AccountTypeEnum.CHECKING)
                        .currency("INR")
                        .initialBalance(BigDecimal.ZERO)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .isActive(true)
                        .build()
        );
    }

    private Category createCategory(User user) {
        return categoryRepository.save(
                Category.builder()
                        .userId(user.getId())
                        .name("HelperCategory")
                        .type(CategoryTypeEnum.EXPENSE)
                        .color("#123456")
                        .icon("icon")
                        .depth(0)
                        .isSystem(false)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .isActive(true)
                        .build()
        );
    }
}
