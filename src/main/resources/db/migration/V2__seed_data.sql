-- V2__seed_data.sql
-- Personal Finance Application - Seed Data
-- Created: 2024-02-10
-- Description: System categories and sample test data

-- ============================================================================
-- SYSTEM CATEGORIES (India-focused)
-- ============================================================================

-- Income Categories
INSERT INTO categories (id, user_id, name, type, color, icon, parent_id, depth, is_system) VALUES
(1, NULL, 'Salary & Wages', 'INCOME', '#10B981', 'briefcase', NULL, 0, true),
(2, NULL, 'Business Income', 'INCOME', '#059669', 'trending-up', NULL, 0, true),
(3, NULL, 'Investments', 'INCOME', '#3B82F6', 'bar-chart', NULL, 0, true),
(4, NULL, 'Gifts Received', 'INCOME', '#8B5CF6', 'gift', NULL, 0, true),
(5, NULL, 'Refunds', 'INCOME', '#6366F1', 'rotate-ccw', NULL, 0, true),
(6, NULL, 'Other Income', 'INCOME', '#A855F7', 'plus-circle', NULL, 0, true);

-- Expense Categories - Food & Dining
INSERT INTO categories (id, user_id, name, type, color, icon, parent_id, depth, is_system) VALUES
(101, NULL, 'Food & Dining', 'EXPENSE', '#EF4444', 'utensils', NULL, 0, true),
(102, NULL, 'Groceries', 'EXPENSE', '#DC2626', 'shopping-cart', 101, 1, true),
(103, NULL, 'Restaurants', 'EXPENSE', '#B91C1C', 'coffee', 101, 1, true),
(104, NULL, 'Food Delivery', 'EXPENSE', '#991B1B', 'truck', 101, 1, true);

-- Expense Categories - Transportation
INSERT INTO categories (id, user_id, name, type, color, icon, parent_id, depth, is_system) VALUES
(201, NULL, 'Transportation', 'EXPENSE', '#F59E0B', 'car', NULL, 0, true),
(202, NULL, 'Fuel', 'EXPENSE', '#D97706', 'fuel', 201, 1, true),
(203, NULL, 'Public Transport', 'EXPENSE', '#B45309', 'bus', 201, 1, true),
(204, NULL, 'Auto/Taxi', 'EXPENSE', '#92400E', 'navigation', 201, 1, true),
(205, NULL, 'Vehicle Maintenance', 'EXPENSE', '#78350F', 'tool', 201, 1, true);

-- Expense Categories - Housing
INSERT INTO categories (id, user_id, name, type, color, icon, parent_id, depth, is_system) VALUES
(301, NULL, 'Housing', 'EXPENSE', '#8B5CF6', 'home', NULL, 0, true),
(302, NULL, 'Rent', 'EXPENSE', '#7C3AED', 'key', 301, 1, true),
(303, NULL, 'Maintenance', 'EXPENSE', '#6D28D9', 'wrench', 301, 1, true),
(304, NULL, 'Property Tax', 'EXPENSE', '#5B21B6', 'file-text', 301, 1, true);

-- Expense Categories - Utilities
INSERT INTO categories (id, user_id, name, type, color, icon, parent_id, depth, is_system) VALUES
(401, NULL, 'Utilities', 'EXPENSE', '#06B6D4', 'zap', NULL, 0, true),
(402, NULL, 'Electricity', 'EXPENSE', '#0891B2', 'zap', 401, 1, true),
(403, NULL, 'Water', 'EXPENSE', '#0E7490', 'droplet', 401, 1, true),
(404, NULL, 'Internet', 'EXPENSE', '#155E75', 'wifi', 401, 1, true),
(405, NULL, 'Mobile', 'EXPENSE', '#164E63', 'smartphone', 401, 1, true),
(406, NULL, 'Gas/LPG', 'EXPENSE', '#083344', 'flame', 401, 1, true);

-- Expense Categories - Healthcare
INSERT INTO categories (id, user_id, name, type, color, icon, parent_id, depth, is_system) VALUES
(501, NULL, 'Healthcare', 'EXPENSE', '#EC4899', 'heart', NULL, 0, true),
(502, NULL, 'Doctor Visits', 'EXPENSE', '#DB2777', 'stethoscope', 501, 1, true),
(503, NULL, 'Medicines', 'EXPENSE', '#BE185D', 'pill', 501, 1, true),
(504, NULL, 'Health Insurance', 'EXPENSE', '#9F1239', 'shield', 501, 1, true),
(505, NULL, 'Lab Tests', 'EXPENSE', '#831843', 'activity', 501, 1, true);

-- Expense Categories - Shopping
INSERT INTO categories (id, user_id, name, type, color, icon, parent_id, depth, is_system) VALUES
(601, NULL, 'Shopping', 'EXPENSE', '#F97316', 'shopping-bag', NULL, 0, true),
(602, NULL, 'Clothing', 'EXPENSE', '#EA580C', 'shirt', 601, 1, true),
(603, NULL, 'Electronics', 'EXPENSE', '#C2410C', 'laptop', 601, 1, true),
(604, NULL, 'Home & Garden', 'EXPENSE', '#9A3412', 'home', 601, 1, true);

-- Expense Categories - Entertainment
INSERT INTO categories (id, user_id, name, type, color, icon, parent_id, depth, is_system) VALUES
(701, NULL, 'Entertainment', 'EXPENSE', '#14B8A6', 'tv', NULL, 0, true),
(702, NULL, 'Movies & Shows', 'EXPENSE', '#0D9488', 'film', 701, 1, true),
(703, NULL, 'Streaming Services', 'EXPENSE', '#0F766E', 'play-circle', 701, 1, true),
(704, NULL, 'Sports & Hobbies', 'EXPENSE', '#115E59', 'dumbbell', 701, 1, true);

-- Expense Categories - Education
INSERT INTO categories (id, user_id, name, type, color, icon, parent_id, depth, is_system) VALUES
(801, NULL, 'Education', 'EXPENSE', '#6366F1', 'book', NULL, 0, true),
(802, NULL, 'Tuition Fees', 'EXPENSE', '#4F46E5', 'graduation-cap', 801, 1, true),
(803, NULL, 'Books & Supplies', 'EXPENSE', '#4338CA', 'book-open', 801, 1, true),
(804, NULL, 'Online Courses', 'EXPENSE', '#3730A3', 'laptop', 801, 1, true);

-- Expense Categories - Personal Care
INSERT INTO categories (id, user_id, name, type, color, icon, parent_id, depth, is_system) VALUES
(901, NULL, 'Personal Care', 'EXPENSE', '#A855F7', 'user', NULL, 0, true),
(902, NULL, 'Salon & Spa', 'EXPENSE', '#9333EA', 'scissors', 901, 1, true),
(903, NULL, 'Cosmetics', 'EXPENSE', '#7E22CE', 'sparkles', 901, 1, true);

-- Expense Categories - Insurance
INSERT INTO categories (id, user_id, name, type, color, icon, parent_id, depth, is_system) VALUES
(1001, NULL, 'Insurance', 'EXPENSE', '#64748B', 'shield', NULL, 0, true),
(1002, NULL, 'Life Insurance', 'EXPENSE', '#475569', 'heart', 1001, 1, true),
(1003, NULL, 'Vehicle Insurance', 'EXPENSE', '#334155', 'car', 1001, 1, true);

-- Expense Categories - Subscriptions
INSERT INTO categories (id, user_id, name, type, color, icon, parent_id, depth, is_system) VALUES
(1101, NULL, 'Subscriptions', 'EXPENSE', '#84CC16', 'repeat', NULL, 0, true),
(1102, NULL, 'Gym Membership', 'EXPENSE', '#65A30D', 'dumbbell', 1101, 1, true),
(1103, NULL, 'Digital Services', 'EXPENSE', '#4D7C0F', 'cloud', 1101, 1, true);

-- Expense Categories - Travel
INSERT INTO categories (id, user_id, name, type, color, icon, parent_id, depth, is_system) VALUES
(1201, NULL, 'Travel', 'EXPENSE', '#0EA5E9', 'plane', NULL, 0, true),
(1202, NULL, 'Hotel & Accommodation', 'EXPENSE', '#0284C7', 'bed', 1201, 1, true),
(1203, NULL, 'Flight Tickets', 'EXPENSE', '#0369A1', 'plane-takeoff', 1201, 1, true);

-- Expense Categories - Others
INSERT INTO categories (id, user_id, name, type, color, icon, parent_id, depth, is_system) VALUES
(1301, NULL, 'Taxes', 'EXPENSE', '#71717A', 'file-text', NULL, 0, true),
(1401, NULL, 'Donations & Charity', 'EXPENSE', '#FB923C', 'heart-handshake', NULL, 0, true),
(1501, NULL, 'Other Expenses', 'EXPENSE', '#94A3B8', 'more-horizontal', NULL, 0, true);

-- Update sequence for categories
SELECT setval('categories_id_seq', 2000, true);

-- ============================================================================
-- SAMPLE TEST DATA (for development/testing)
-- ============================================================================

-- Sample User (password: Test@123 - BCrypt hash)
INSERT INTO users (id, email, password_hash, first_name, last_name) VALUES
(1, 'demo@financeapp.com', '$2a$10$rVYy3YkzRq8qN5X5PJzLQ.Y8qJ9.p3LKZ5HkJxKvQ9mKJYxQJ9K1.', 'Demo', 'User');

-- Sample Accounts
INSERT INTO accounts (id, user_id, name, type, initial_balance, currency) VALUES
(1, 1, 'HDFC Savings Account', 'SAVINGS', 50000.00, 'INR'),
(2, 1, 'ICICI Credit Card', 'CREDIT', 0.00, 'INR'),
(3, 1, 'Cash Wallet', 'CASH', 5000.00, 'INR'),
(4, 1, 'Zerodha Demat', 'INVESTMENT', 100000.00, 'INR');

-- Sample Transactions (Last 3 months)
INSERT INTO transactions (user_id, account_id, category_id, amount, type, description, transaction_date) VALUES
-- January 2024
(1, 1, 1, 75000.00, 'INCOME', 'January Salary', '2024-01-01'),
(1, 1, 302, 15000.00, 'EXPENSE', 'Monthly Rent', '2024-01-05'),
(1, 1, 402, 1200.00, 'EXPENSE', 'Electricity Bill', '2024-01-10'),
(1, 1, 102, 4500.00, 'EXPENSE', 'Monthly Groceries', '2024-01-15'),
(1, 2, 202, 3000.00, 'EXPENSE', 'Fuel', '2024-01-18'),
(1, 3, 103, 850.00, 'EXPENSE', 'Dinner at Restaurant', '2024-01-20'),
(1, 1, 405, 599.00, 'EXPENSE', 'Jio Mobile Recharge', '2024-01-22'),

-- February 2024
(1, 1, 1, 75000.00, 'INCOME', 'February Salary', '2024-02-01'),
(1, 1, 302, 15000.00, 'EXPENSE', 'Monthly Rent', '2024-02-05'),
(1, 1, 402, 1350.00, 'EXPENSE', 'Electricity Bill', '2024-02-08'),
(1, 1, 102, 5200.00, 'EXPENSE', 'Monthly Groceries', '2024-02-12'),
(1, 2, 202, 3500.00, 'EXPENSE', 'Fuel', '2024-02-15'),
(1, 2, 703, 199.00, 'EXPENSE', 'Netflix Subscription', '2024-02-18'),
(1, 1, 503, 850.00, 'EXPENSE', 'Medicines', '2024-02-20'),
(1, 3, 104, 450.00, 'EXPENSE', 'Swiggy Order', '2024-02-22'),
(1, 1, 404, 999.00, 'EXPENSE', 'Airtel Fiber Internet', '2024-02-25');

-- Sample Budgets (February 2024)
INSERT INTO budgets (user_id, category_id, amount, period_type, start_date, end_date, alert_threshold) VALUES
(1, 101, 10000.00, 'MONTHLY', '2024-02-01', '2024-02-29', 80),  -- Food & Dining
(1, 201, 5000.00, 'MONTHLY', '2024-02-01', '2024-02-29', 75),   -- Transportation
(1, 401, 3000.00, 'MONTHLY', '2024-02-01', '2024-02-29', 85),   -- Utilities
(1, 601, 5000.00, 'MONTHLY', '2024-02-01', '2024-02-29', 80);   -- Shopping

-- Sample Recurring Transactions
INSERT INTO recurring_transactions (user_id, account_id, category_id, amount, type, description, frequency, start_date, next_occurrence_date) VALUES
(1, 1, 1, 75000.00, 'INCOME', 'Monthly Salary', 'MONTHLY', '2024-01-01', '2024-03-01'),
(1, 1, 302, 15000.00, 'EXPENSE', 'Monthly Rent', 'MONTHLY', '2024-01-05', '2024-03-05'),
(1, 2, 703, 199.00, 'EXPENSE', 'Netflix Subscription', 'MONTHLY', '2024-01-18', '2024-03-18'),
(1, 1, 404, 999.00, 'EXPENSE', 'Airtel Fiber', 'MONTHLY', '2024-01-25', '2024-03-25'),
(1, 1, 1102, 2000.00, 'EXPENSE', 'Gym Membership', 'MONTHLY', '2024-01-10', '2024-03-10');

-- Sample Financial Goals
INSERT INTO financial_goals (user_id, name, target_amount, current_amount, deadline, status) VALUES
(1, 'Emergency Fund', 100000.00, 35000.00, '2024-12-31', 'ACTIVE'),
(1, 'Vacation to Goa', 50000.00, 12000.00, '2024-06-30', 'ACTIVE'),
(1, 'New Laptop', 80000.00, 20000.00, '2024-08-31', 'ACTIVE');

-- Sample Tags
INSERT INTO tags (id, user_id, name, color) VALUES
(1, 1, 'Tax Deductible', '#10B981'),
(2, 1, 'Work Related', '#3B82F6'),
(3, 1, 'Family', '#EC4899'),
(4, 1, 'Essential', '#EF4444'),
(5, 1, 'Luxury', '#8B5CF6');

-- Sample Transaction Tags
INSERT INTO transaction_tags (transaction_id, tag_id) VALUES
(1, 2),  -- Salary is work related
(2, 4),  -- Rent is essential
(6, 5),  -- Restaurant is luxury
(13, 3); -- Medicines is family

-- Sample Notifications
INSERT INTO notifications (user_id, title, message, type, related_entity_type, related_entity_id) VALUES
(1, 'Budget Alert', 'You have spent 85% of your Food & Dining budget for February', 'BUDGET_ALERT', 'BUDGET', 1),
(1, 'Goal Progress', 'You are 35% towards your Emergency Fund goal!', 'GOAL_PROGRESS', 'GOAL', 1),
(1, 'Recurring Payment', 'Rent payment of ₹15,000 is due on 5th March', 'RECURRING', 'RECURRING_TRANSACTION', 2);

-- Update sequences
SELECT setval('users_id_seq', 100, true);
SELECT setval('accounts_id_seq', 100, true);
SELECT setval('transactions_id_seq', 1000, true);
SELECT setval('budgets_id_seq', 100, true);
SELECT setval('recurring_transactions_id_seq', 100, true);
SELECT setval('financial_goals_id_seq', 100, true);
SELECT setval('tags_id_seq', 100, true);
SELECT setval('notifications_id_seq', 100, true);

-- ============================================================================
-- END OF SEED DATA
-- ============================================================================
