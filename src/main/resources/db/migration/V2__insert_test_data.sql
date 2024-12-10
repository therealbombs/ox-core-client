-- Insert test clients
INSERT INTO CLIENT (client_id, abi, password, fiscal_code, first_name, last_name, email, phone, failed_attempts, locked_until, last_access, previous_access, created_at, modified_at)
VALUES
    ('12345678', '03500', 'Pippero666!', 'RSSMRA80A01H501U', 'Mario', 'Rossi', 'mario.rossi@email.com', '+391234567890', 0, NULL, '2024-12-09 21:47:59', NULL, '2024-12-09 21:47:59', '2024-12-09 21:47:59'),
    ('23456789', '03500', 'Pippero666!', 'VRDLGU85M15H501V', 'Luigi', 'Verdi', 'luigi.verdi@email.com', '+391234567891', 0, NULL, '2024-12-09 21:47:59', NULL, '2024-12-09 21:47:59', '2024-12-09 21:47:59'),
    ('34567890', '03500', 'Pippero666!', 'BNCGNN90D45H501B', 'Giovanna', 'Bianchi', 'giovanna.bianchi@email.com', '+391234567892', 0, NULL, '2024-12-09 21:47:59', NULL, '2024-12-09 21:47:59', '2024-12-09 21:47:59'),
    ('45678901', '03500', 'Pippero666!', 'NREFNC75P07H501C', 'Francesco', 'Neri', 'francesco.neri@email.com', '+391234567893', 0, NULL, '2024-12-09 21:47:59', NULL, '2024-12-09 21:47:59', '2024-12-09 21:47:59');

-- Insert accounts for Mario Rossi
INSERT INTO ACCOUNT (account_id, abi, account_type, account_number, status, iban, created_at, modified_at)
VALUES
    ('A001', '03500', 'CURRENT_ACCOUNT', 'IT60X0350001234567890123456', 'ACTIVE', 'IT60X0350001234567890123456', '2024-12-09 21:47:59', '2024-12-09 21:47:59');

-- Insert accounts for Luigi Verdi
INSERT INTO ACCOUNT (account_id, abi, account_type, account_number, status, iban, created_at, modified_at)
VALUES
    ('A002', '03500', 'CURRENT_ACCOUNT', 'IT60X0350002345678901234567', 'ACTIVE', 'IT60X0350002345678901234567', '2024-12-09 21:47:59', '2024-12-09 21:47:59');

-- Insert accounts for Giovanna Bianchi
INSERT INTO ACCOUNT (account_id, abi, account_type, account_number, status, iban, created_at, modified_at)
VALUES
    ('A003', '03500', 'CURRENT_ACCOUNT', 'IT60X0350003456789012345678', 'ACTIVE', 'IT60X0350003456789012345678', '2024-12-09 21:47:59', '2024-12-09 21:47:59');

-- Insert accounts for Francesco Rossi
INSERT INTO ACCOUNT (account_id, abi, account_type, account_number, status, iban, created_at, modified_at)
VALUES
    ('A004', '03500', 'CURRENT_ACCOUNT', 'IT60X0350004567890123456789', 'ACTIVE', 'IT60X0350004567890123456789', '2024-12-09 21:47:59', '2024-12-09 21:47:59');

-- Insert account holders for Mario Rossi's accounts
INSERT INTO ACCOUNT_HOLDER (holder_id, account_id, client_id, holder_type, created_at)
VALUES
    ('H001', 'A001', '12345678', 'PRIMARY', '2024-12-09 21:47:59');

-- Insert account holders for Luigi Verdi's accounts
INSERT INTO ACCOUNT_HOLDER (holder_id, account_id, client_id, holder_type, created_at)
VALUES
    ('H002', 'A002', '23456789', 'PRIMARY', '2024-12-09 21:47:59');

-- Insert account holders for Giovanna Bianchi's accounts
INSERT INTO ACCOUNT_HOLDER (holder_id, account_id, client_id, holder_type, created_at)
VALUES
    ('H003', 'A003', '34567890', 'PRIMARY', '2024-12-09 21:47:59');

-- Insert account holders for Francesco Rossi's accounts
INSERT INTO ACCOUNT_HOLDER (holder_id, account_id, client_id, holder_type, created_at)
VALUES
    ('H004', 'A004', '45678901', 'PRIMARY', '2024-12-09 21:47:59');
