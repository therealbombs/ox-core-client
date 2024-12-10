-- Insert test clients
INSERT INTO client (client_id, abi, password, fiscal_code, name, surname, preferred_language, failed_attempts, password_change_required, created_at, modified_at)
VALUES 
('12345678', '01234', '8be3c943b1609fffbfc51aad666d0a04adf83c9d54e59d6cc0f06e54d7d6a7d4', 'RSSMRA80A01H501U', 'Mario', 'Rossi', 'IT', 0, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('87654321', '01234', '8be3c943b1609fffbfc51aad666d0a04adf83c9d54e59d6cc0f06e54d7d6a7d4', 'VRDGSP85A01H501V', 'Giuseppe', 'Verdi', 'IT', 0, false, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test accounts
INSERT INTO account (account_id, abi, account_type, account_number, status, iban, created_at, modified_at)
VALUES 
('ACC001', '01234', 'CURRENT', '000123456789', 'ACTIVE', 'IT60X0542811101000000123456', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('ACC002', '01234', 'SAVINGS', '000987654321', 'ACTIVE', 'IT60X0542811101000000987654', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test account holders
INSERT INTO account_holder (holder_id, account_id, client_id, holder_type, created_at)
VALUES 
('HOLD001', 'ACC001', '12345678', 'PRIMARY', CURRENT_TIMESTAMP),
('HOLD002', 'ACC002', '87654321', 'PRIMARY', CURRENT_TIMESTAMP);

-- Insert test audit logs
INSERT INTO audit_log (event_type, client_id, abi, status, message, created_at)
VALUES 
('LOGIN_ATTEMPT', '12345678', '01234', 'SUCCESS', 'Successful login', CURRENT_TIMESTAMP),
('LOGIN_ATTEMPT', '87654321', '01234', 'SUCCESS', 'Successful login', CURRENT_TIMESTAMP);
