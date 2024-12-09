-- Insert test clients
INSERT INTO CLIENT (client_id, abi, fiscal_code, name, surname, preferred_language, last_access, created_at, modified_at)
VALUES
    ('C001', '01234', 'RSSMRA80A01H501A', 'Mario', 'Rossi', 'it', '2024-12-09 21:47:59', '2024-12-09 21:47:59', '2024-12-09 21:47:59'),
    ('C002', '01234', 'VRDLGU85M15H501B', 'Luigi', 'Verdi', 'en', '2024-12-09 21:47:59', '2024-12-09 21:47:59', '2024-12-09 21:47:59'),
    ('C003', '56789', 'BNCGNN90D45H501C', 'Giovanna', 'Bianchi', 'it', '2024-12-09 21:47:59', '2024-12-09 21:47:59', '2024-12-09 21:47:59');

-- Insert accounts for Mario Rossi
INSERT INTO ACCOUNT (account_id, abi, account_type, account_number, status, iban, created_at, modified_at)
VALUES
    ('A001', '01234', 'CURRENT_ACCOUNT', 'CA001', 'ACTIVE', 'IT01234000000CA001', '2024-12-09 21:47:59', '2024-12-09 21:47:59'),
    ('A002', '01234', 'DEPOSIT_ACCOUNT', 'DA001', 'ACTIVE', 'IT01234000000DA001', '2024-12-09 21:47:59', '2024-12-09 21:47:59'),
    ('A003', '01234', 'SECURITIES_ACCOUNT', 'SA001', 'TO_BE_ACTIVATED', 'IT01234000000SA001', '2024-12-09 21:47:59', '2024-12-09 21:47:59');

-- Insert accounts for Luigi Verdi
INSERT INTO ACCOUNT (account_id, abi, account_type, account_number, status, iban, created_at, modified_at)
VALUES
    ('A004', '01234', 'CURRENT_ACCOUNT', 'CA002', 'ACTIVE', 'IT01234000000CA002', '2024-12-09 21:47:59', '2024-12-09 21:47:59'),
    ('A005', '01234', 'SECURITIES_ACCOUNT', 'SA002', 'BLOCKED', 'IT01234000000SA002', '2024-12-09 21:47:59', '2024-12-09 21:47:59');

-- Insert accounts for Giovanna Bianchi
INSERT INTO ACCOUNT (account_id, abi, account_type, account_number, status, iban, created_at, modified_at)
VALUES
    ('A006', '56789', 'CURRENT_ACCOUNT', 'CA003', 'ACTIVE', 'IT56789000000CA003', '2024-12-09 21:47:59', '2024-12-09 21:47:59'),
    ('A007', '56789', 'DEPOSIT_ACCOUNT', 'DA002', 'CLOSED', 'IT56789000000DA002', '2024-12-09 21:47:59', '2024-12-09 21:47:59');

-- Insert account holders for Mario Rossi's accounts
INSERT INTO ACCOUNT_HOLDER (holder_id, account_id, client_id, holder_type, created_at)
VALUES
    ('H001', 'A001', 'C001', 'PRIMARY', '2024-12-09 21:47:59'),
    ('H002', 'A002', 'C001', 'PRIMARY', '2024-12-09 21:47:59'),
    ('H003', 'A003', 'C001', 'PRIMARY', '2024-12-09 21:47:59');

-- Insert account holders for Luigi Verdi's accounts
INSERT INTO ACCOUNT_HOLDER (holder_id, account_id, client_id, holder_type, created_at)
VALUES
    ('H004', 'A004', 'C002', 'PRIMARY', '2024-12-09 21:47:59'),
    ('H005', 'A005', 'C002', 'PRIMARY', '2024-12-09 21:47:59'),
    -- Mario Rossi as secondary holder on Luigi's securities account
    ('H006', 'A005', 'C001', 'SECONDARY', '2024-12-09 21:47:59');

-- Insert account holders for Giovanna Bianchi's accounts
INSERT INTO ACCOUNT_HOLDER (holder_id, account_id, client_id, holder_type, created_at)
VALUES
    ('H007', 'A006', 'C003', 'PRIMARY', '2024-12-09 21:47:59'),
    ('H008', 'A007', 'C003', 'PRIMARY', '2024-12-09 21:47:59');
