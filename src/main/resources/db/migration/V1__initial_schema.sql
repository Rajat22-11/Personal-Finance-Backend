-- V1__initial_schema.sql
-- Personal Finance Application - Initial Database Schema
-- Description: Complete schema with all core tables, constraints, and indexes

-- ============================================================================
-- TABLE: users
-- Description: User authentication and profile information
-- ============================================================================

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       password_hash VARCHAR(255) NOT NULL,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,

    -- Soft delete
                       is_active BOOLEAN DEFAULT true NOT NULL,
                       deleted_at TIMESTAMP NULL,

    -- Audit fields
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    -- Constraints
                       CONSTRAINT email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$')
    );

-- Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_active ON users(is_active) WHERE is_active = true;

-- Comments
COMMENT ON TABLE users IS 'User accounts with authentication and profile data';
COMMENT ON COLUMN users.password_hash IS 'BCrypt hashed password';

-- ============================================================================
-- TABLE: accounts
-- Description: Financial accounts (checking, savings, credit cards, etc.)
-- ============================================================================

CREATE TABLE accounts (
                          id BIGSERIAL PRIMARY KEY,
                          user_id BIGINT NOT NULL,

                          name VARCHAR(100) NOT NULL,
                          type VARCHAR(50) NOT NULL,
                          currency VARCHAR(3) DEFAULT 'INR' NOT NULL,

    -- Initial balance (user sets this once, then calculated from transactions)
                          initial_balance NUMERIC(15, 2) DEFAULT 0.00 NOT NULL,

    -- Soft delete
                          is_active BOOLEAN DEFAULT true NOT NULL,
                          deleted_at TIMESTAMP NULL,

    -- Audit fields
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    -- Foreign Keys
                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Constraints
                          CONSTRAINT unique_account_name_per_user UNIQUE(user_id, name),
                          CONSTRAINT check_account_type CHECK (type IN ('CHECKING', 'SAVINGS', 'CREDIT', 'INVESTMENT', 'CASH'))
);

-- Indexes
CREATE INDEX idx_accounts_user ON accounts(user_id);
CREATE INDEX idx_accounts_user_active ON accounts(user_id, is_active);
CREATE INDEX idx_accounts_type ON accounts(type);

-- Comments
COMMENT ON TABLE accounts IS 'User financial accounts (bank accounts, credit cards, cash, investments)';
COMMENT ON COLUMN accounts.initial_balance IS 'Starting balance when account was created';

-- ============================================================================
-- TABLE: categories
-- Description: Transaction categories with hierarchical support
-- ============================================================================

CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            user_id BIGINT NULL,  -- NULL for system categories, user_id for user-created

                            name VARCHAR(100) NOT NULL,
                            type VARCHAR(50) NOT NULL,

    -- Visual customization
                            color VARCHAR(7),  -- Hex color code (e.g., #FF5733)
                            icon VARCHAR(50),  -- Icon identifier

    -- Hierarchy support (2 levels max)
                            parent_id BIGINT NULL,
                            depth INTEGER DEFAULT 0 NOT NULL,

    -- System vs user category
                            is_system BOOLEAN DEFAULT false NOT NULL,

    -- Soft delete
                            is_active BOOLEAN DEFAULT true NOT NULL,
                            deleted_at TIMESTAMP NULL,

    -- Audit fields
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    -- Foreign Keys
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                            FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL,

    -- Constraints
                            CONSTRAINT max_category_depth CHECK (depth <= 1),
                            CONSTRAINT system_category_no_user CHECK (
                                (is_system = true AND user_id IS NULL) OR
                                (is_system = false AND user_id IS NOT NULL)
                                ),
                            CONSTRAINT color_format CHECK (color IS NULL OR color ~* '^#[0-9A-Fa-f]{6}$'),
    CONSTRAINT check_category_type CHECK (type IN ('INCOME', 'EXPENSE'))
);

-- Indexes
CREATE INDEX idx_categories_user ON categories(user_id);
CREATE INDEX idx_categories_parent ON categories(parent_id);
CREATE INDEX idx_categories_type ON categories(type);
CREATE INDEX idx_categories_system ON categories(is_system);

-- Comments
COMMENT ON TABLE categories IS 'Transaction categories with 2-level hierarchy support';
COMMENT ON COLUMN categories.depth IS '0 = parent category, 1 = subcategory';
COMMENT ON COLUMN categories.is_system IS 'System categories are pre-seeded and shared across all users';

-- ============================================================================
-- TABLE: transactions
-- Description: Income and expense transactions
-- ============================================================================

CREATE TABLE transactions (
                              id BIGSERIAL PRIMARY KEY,
                              user_id BIGINT NOT NULL,
                              account_id BIGINT NOT NULL,
                              category_id BIGINT NULL,

                              amount NUMERIC(15, 2) NOT NULL,
                              type VARCHAR(50) NOT NULL,

                              description VARCHAR(255),
                              notes TEXT,

                              transaction_date TIMESTAMP NOT NULL,

    -- Receipt/attachment (file path or URL)
                              receipt_path VARCHAR(500) NULL,

    -- Soft delete (transactions should rarely be hard deleted)
                              is_active BOOLEAN DEFAULT true NOT NULL,
                              deleted_at TIMESTAMP NULL,

    -- Audit fields
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    -- Foreign Keys
                              FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                              FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
                              FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,

    -- Constraints
                              CONSTRAINT positive_amount CHECK (amount > 0),
                              CONSTRAINT check_transaction_type CHECK (type IN ('INCOME', 'EXPENSE', 'TRANSFER'))
);

-- Indexes (CRITICAL for performance)
CREATE INDEX idx_transactions_user ON transactions(user_id);
CREATE INDEX idx_transactions_account ON transactions(account_id);
CREATE INDEX idx_transactions_category ON transactions(category_id);
CREATE INDEX idx_transactions_date ON transactions(transaction_date DESC);
CREATE INDEX idx_transactions_user_date ON transactions(user_id, transaction_date DESC);
CREATE INDEX idx_transactions_user_category_date ON transactions(user_id, category_id, transaction_date DESC);
CREATE INDEX idx_transactions_account_date ON transactions(account_id, transaction_date DESC);

-- Comments
COMMENT ON TABLE transactions IS 'Income, expense, and transfer transactions';
COMMENT ON COLUMN transactions.amount IS 'Always positive - use type to indicate income/expense';
COMMENT ON COLUMN transactions.receipt_path IS 'Path to uploaded receipt image or document';

-- ============================================================================
-- TABLE: budgets
-- Description: Budget allocations for categories
-- ============================================================================

CREATE TABLE budgets (
                         id BIGSERIAL PRIMARY KEY,
                         user_id BIGINT NOT NULL,
                         category_id BIGINT NOT NULL,

                         amount NUMERIC(15, 2) NOT NULL,

                         period_type VARCHAR(50) NOT NULL,
                         start_date DATE NOT NULL,
                         end_date DATE NOT NULL,

    -- Alert when spending reaches this percentage (e.g., 80%)
                         alert_threshold INTEGER DEFAULT 80 NOT NULL,

    -- Soft delete
                         is_active BOOLEAN DEFAULT true NOT NULL,
                         deleted_at TIMESTAMP NULL,

    -- Audit fields
                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    -- Foreign Keys
                         FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                         FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,

    -- Constraints
                         CONSTRAINT positive_budget_amount CHECK (amount > 0),
                         CONSTRAINT valid_budget_period CHECK (end_date > start_date),
                         CONSTRAINT valid_alert_threshold CHECK (alert_threshold BETWEEN 0 AND 100),
                         CONSTRAINT check_period_type CHECK (period_type IN ('MONTHLY', 'QUARTERLY', 'YEARLY'))
);

-- Indexes
CREATE INDEX idx_budgets_user ON budgets(user_id);
CREATE INDEX idx_budgets_category ON budgets(category_id);
CREATE INDEX idx_budgets_dates ON budgets(start_date, end_date);
CREATE INDEX idx_budgets_user_active ON budgets(user_id, is_active);

-- Comments
COMMENT ON TABLE budgets IS 'Budget allocations with period and alert settings';
COMMENT ON COLUMN budgets.alert_threshold IS 'Percentage (0-100) that triggers budget alert';

-- ============================================================================
-- TABLE: recurring_transactions
-- Description: Scheduled recurring transactions (salary, rent, subscriptions)
-- ============================================================================

CREATE TABLE recurring_transactions (
                                        id BIGSERIAL PRIMARY KEY,
                                        user_id BIGINT NOT NULL,
                                        account_id BIGINT NOT NULL,
                                        category_id BIGINT NULL,

                                        amount NUMERIC(15, 2) NOT NULL,
                                        type VARCHAR(50) NOT NULL,
                                        description VARCHAR(255) NOT NULL,

                                        frequency VARCHAR(50) NOT NULL,

    -- Schedule dates
                                        start_date DATE NOT NULL,
                                        end_date DATE NULL,  -- NULL means no end date
                                        next_occurrence_date DATE NOT NULL,

    -- Active/inactive
                                        is_active BOOLEAN DEFAULT true NOT NULL,
                                        deleted_at TIMESTAMP NULL,

    -- Audit fields
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    -- Foreign Keys
                                        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                                        FOREIGN KEY (account_id) REFERENCES accounts(id) ON DELETE CASCADE,
                                        FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL,

    -- Constraints
                                        CONSTRAINT positive_recurring_amount CHECK (amount > 0),
                                        CONSTRAINT valid_recurring_period CHECK (end_date IS NULL OR end_date > start_date),
                                        CONSTRAINT check_recurring_type CHECK (type IN ('INCOME', 'EXPENSE', 'TRANSFER')),
                                        CONSTRAINT check_frequency_type CHECK (frequency IN ('DAILY', 'WEEKLY', 'MONTHLY', 'YEARLY'))
);

-- Indexes
CREATE INDEX idx_recurring_user ON recurring_transactions(user_id);
CREATE INDEX idx_recurring_next_date ON recurring_transactions(next_occurrence_date);
CREATE INDEX idx_recurring_active ON recurring_transactions(is_active) WHERE is_active = true;

-- Comments
COMMENT ON TABLE recurring_transactions IS 'Scheduled recurring transactions (salary, rent, bills, subscriptions)';
COMMENT ON COLUMN recurring_transactions.next_occurrence_date IS 'Next date when this transaction should be created';

-- ============================================================================
-- TABLE: recurring_execution_log
-- Description: Tracks execution history of recurring transactions
-- ============================================================================

CREATE TABLE recurring_execution_log (
                                         id BIGSERIAL PRIMARY KEY,
                                         recurring_transaction_id BIGINT NOT NULL,

                                         scheduled_date DATE NOT NULL,  -- When it SHOULD have run
                                         executed_date TIMESTAMP NULL,  -- When it ACTUALLY ran (NULL if skipped/failed)

                                         transaction_id BIGINT NULL,  -- Link to created transaction (NULL if skipped/failed)

                                         status VARCHAR(50) NOT NULL,
                                         notes TEXT,  -- Reason for skip/failure

                                         created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    -- Foreign Keys
                                         FOREIGN KEY (recurring_transaction_id) REFERENCES recurring_transactions(id) ON DELETE CASCADE,
                                         FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE SET NULL,

    -- Constraints
                                         CONSTRAINT unique_execution_per_date UNIQUE(recurring_transaction_id, scheduled_date),
                                         CONSTRAINT check_execution_status CHECK (status IN ('EXECUTED', 'SKIPPED', 'FAILED'))
);

-- Indexes
CREATE INDEX idx_recurring_log_recurring_id ON recurring_execution_log(recurring_transaction_id);
CREATE INDEX idx_recurring_log_status ON recurring_execution_log(status);
CREATE INDEX idx_recurring_log_scheduled_date ON recurring_execution_log(scheduled_date DESC);

-- Comments
COMMENT ON TABLE recurring_execution_log IS 'Audit trail of recurring transaction executions';
COMMENT ON COLUMN recurring_execution_log.status IS 'EXECUTED = created transaction, SKIPPED = user skipped, FAILED = error occurred';

-- ============================================================================
-- TABLE: financial_goals
-- Description: User financial goals (savings targets, debt payoff, etc.)
-- ============================================================================

CREATE TABLE financial_goals (
                                 id BIGSERIAL PRIMARY KEY,
                                 user_id BIGINT NOT NULL,

                                 name VARCHAR(255) NOT NULL,
                                 target_amount NUMERIC(15, 2) NOT NULL,
                                 current_amount NUMERIC(15, 2) DEFAULT 0.00 NOT NULL,

                                 deadline DATE NULL,

                                 status VARCHAR(50) DEFAULT 'ACTIVE' NOT NULL,

    -- Soft delete
                                 is_active BOOLEAN DEFAULT true NOT NULL,
                                 deleted_at TIMESTAMP NULL,

    -- Audit fields
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                                 updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    -- Foreign Keys
                                 FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Constraints
                                 CONSTRAINT positive_goal_target CHECK (target_amount > 0),
                                 CONSTRAINT valid_goal_current_amount CHECK (current_amount >= 0),
                                 CONSTRAINT current_not_exceeds_target CHECK (current_amount <= target_amount OR status = 'COMPLETED'),
                                 CONSTRAINT check_goal_status CHECK (status IN ('ACTIVE', 'COMPLETED', 'CANCELLED'))
);

-- Indexes
CREATE INDEX idx_goals_user ON financial_goals(user_id);
CREATE INDEX idx_goals_status ON financial_goals(status);
CREATE INDEX idx_goals_user_status ON financial_goals(user_id, status);

-- Comments
COMMENT ON TABLE financial_goals IS 'User financial goals and savings targets';
COMMENT ON COLUMN financial_goals.current_amount IS 'Current progress towards goal';

-- ============================================================================
-- TABLE: tags
-- Description: Flexible tagging system for transactions
-- ============================================================================

CREATE TABLE tags (
                      id BIGSERIAL PRIMARY KEY,
                      user_id BIGINT NOT NULL,

                      name VARCHAR(50) NOT NULL,
                      color VARCHAR(7),

                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    -- Foreign Keys
                      FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Constraints
                      CONSTRAINT unique_tag_per_user UNIQUE(user_id, name),
                      CONSTRAINT tag_color_format CHECK (color IS NULL OR color ~* '^#[0-9A-Fa-f]{6}$')
    );

-- Indexes
CREATE INDEX idx_tags_user ON tags(user_id);

-- Comments
COMMENT ON TABLE tags IS 'User-defined tags for flexible transaction categorization';

-- ============================================================================
-- TABLE: transaction_tags
-- Description: Many-to-many relationship between transactions and tags
-- ============================================================================

CREATE TABLE transaction_tags (
                                  transaction_id BIGINT NOT NULL,
                                  tag_id BIGINT NOT NULL,

                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    -- Primary Key (composite)
                                  PRIMARY KEY (transaction_id, tag_id),

    -- Foreign Keys
                                  FOREIGN KEY (transaction_id) REFERENCES transactions(id) ON DELETE CASCADE,
                                  FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_transaction_tags_tag ON transaction_tags(tag_id);

-- Comments
COMMENT ON TABLE transaction_tags IS 'Junction table for many-to-many transactions-tags relationship';

-- ============================================================================
-- TABLE: notifications
-- Description: User notifications (budget alerts, goal progress, etc.)
-- ============================================================================

CREATE TABLE notifications (
                               id BIGSERIAL PRIMARY KEY,
                               user_id BIGINT NOT NULL,

                               title VARCHAR(255) NOT NULL,
                               message TEXT NOT NULL,
                               type VARCHAR(50) NOT NULL,

                               is_read BOOLEAN DEFAULT false NOT NULL,

    -- Optional links to related entities
                               related_entity_type VARCHAR(50) NULL,  -- 'BUDGET', 'GOAL', 'TRANSACTION'
                               related_entity_id BIGINT NULL,

                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                               read_at TIMESTAMP NULL,

    -- Foreign Keys
                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    -- Constraints
                               CONSTRAINT check_notification_type CHECK (type IN ('BUDGET_ALERT', 'GOAL_PROGRESS', 'TRANSACTION', 'RECURRING', 'SYSTEM'))
);

-- Indexes
CREATE INDEX idx_notifications_user ON notifications(user_id);
CREATE INDEX idx_notifications_user_unread ON notifications(user_id, is_read) WHERE is_read = false;
CREATE INDEX idx_notifications_created ON notifications(created_at DESC);

-- Comments
COMMENT ON TABLE notifications IS 'User notifications for budget alerts, goal progress, and system messages';
COMMENT ON COLUMN notifications.related_entity_id IS 'ID of related budget, goal, or transaction';

-- ============================================================================
-- TRIGGERS: Update updated_at timestamp automatically
-- ============================================================================

-- Function to update updated_at column
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Apply trigger to all tables with updated_at column
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_accounts_updated_at BEFORE UPDATE ON accounts
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_transactions_updated_at BEFORE UPDATE ON transactions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_budgets_updated_at BEFORE UPDATE ON budgets
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_recurring_updated_at BEFORE UPDATE ON recurring_transactions
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_goals_updated_at BEFORE UPDATE ON financial_goals
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_categories_updated_at BEFORE UPDATE ON categories
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================================================
-- VIEWS: Commonly used queries
-- ============================================================================

-- View: Account current balance (calculated from transactions)
CREATE OR REPLACE VIEW account_balances AS
SELECT
    a.id AS account_id,
    a.user_id,
    a.name AS account_name,
    a.type AS account_type,
    a.initial_balance,
    COALESCE(
            a.initial_balance + SUM(
                    CASE
                        WHEN t.type = 'INCOME' THEN t.amount
                        WHEN t.type = 'EXPENSE' THEN -t.amount
                        ELSE 0
                        END
                                ),
            a.initial_balance
    ) AS current_balance,
    COUNT(t.id) AS transaction_count,
    MAX(t.transaction_date) AS last_transaction_date
FROM accounts a
         LEFT JOIN transactions t ON a.id = t.account_id AND t.is_active = true
WHERE a.is_active = true
GROUP BY a.id, a.user_id, a.name, a.type, a.initial_balance;

COMMENT ON VIEW account_balances IS 'Current account balances calculated from transactions';

-- View: Budget spending summary
CREATE OR REPLACE VIEW budget_spending AS
SELECT
    b.id AS budget_id,
    b.user_id,
    b.category_id,
    c.name AS category_name,
    b.amount AS budget_amount,
    b.period_type,
    b.start_date,
    b.end_date,
    b.alert_threshold,
    COALESCE(SUM(t.amount), 0) AS spent_amount,
    b.amount - COALESCE(SUM(t.amount), 0) AS remaining_amount,
    ROUND((COALESCE(SUM(t.amount), 0) / b.amount * 100), 2) AS spent_percentage
FROM budgets b
         LEFT JOIN categories c ON b.category_id = c.id
         LEFT JOIN transactions t ON
    b.category_id = t.category_id
        AND t.type = 'EXPENSE'
        AND t.transaction_date BETWEEN b.start_date AND b.end_date
        AND t.is_active = true
WHERE b.is_active = true
GROUP BY b.id, b.user_id, b.category_id, c.name, b.amount, b.period_type,
         b.start_date, b.end_date, b.alert_threshold;

COMMENT ON VIEW budget_spending IS 'Budget vs actual spending with percentages';

-- ============================================================================
-- END OF MIGRATION V1
-- ============================================================================