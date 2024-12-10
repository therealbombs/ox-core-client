-- Insert test clients
INSERT INTO client (client_id, abi, name) VALUES
('CLIENT01', '01234', 'Test Bank 1'),
('CLIENT02', '56789', 'Test Bank 2');

-- Insert test accounts
INSERT INTO account (account_id, client_id, abi, account_type) VALUES
('IT60X0542811101000000123456', 'CLIENT01', '01234', 'CHECKING'),
('IT60X0542811101000000789012', 'CLIENT02', '56789', 'SAVINGS');

-- Insert test account holders
INSERT INTO account_holder (holder_id, client_id, abi, account_id, first_name, last_name, fiscal_code) VALUES
('550e8400-e29b-41d4-a716-446655440000', 'CLIENT01', '01234', 'IT60X0542811101000000123456', 'John', 'Doe', 'DOEJHN80A01F205X'),
('550e8400-e29b-41d4-a716-446655440001', 'CLIENT02', '56789', 'IT60X0542811101000000789012', 'Jane', 'Smith', 'SMTJNE85A41F205Y');

-- Insert test audit logs
INSERT INTO audit_log (entity_type, entity_id, action, details) VALUES
('CLIENT', 'CLIENT01', 'CREATE', 'Created new client Test Bank 1'),
('ACCOUNT', 'IT60X0542811101000000123456', 'CREATE', 'Created new checking account');
