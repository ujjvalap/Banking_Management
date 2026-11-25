INSERT INTO accounts
(account_number, account_type, balance, status, customer_id, created_at)
SELECT 'SB100001', 'SAVINGS', 10000.00, 'ACTIVE', 1, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (
    SELECT 1 FROM accounts WHERE account_number = 'SB100001'
);

INSERT INTO accounts
(account_number, account_type, balance, status, customer_id, created_at)
SELECT 'SB100002', 'CURRENT', 25000.00, 'ACTIVE', 2, CURRENT_TIMESTAMP
    WHERE NOT EXISTS (
    SELECT 1 FROM accounts WHERE account_number = 'SB100002'
);