-- Insert test clients
INSERT INTO client (client_id, abi, password, fiscal_code, first_name, last_name, email, phone, failed_attempts, locked_until, last_access, previous_access, created_at, modified_at)
VALUES
    ('12345678', '03500', 'Pippero666!', 'RSSMRA80A01H501U', 'Mario', 'Rossi', 'mario.rossi@email.com', '+391234567890', 0, NULL, CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('23456789', '03500', 'Pippero666!', 'VRDLGU85M15H501V', 'Luigi', 'Verdi', 'luigi.verdi@email.com', '+391234567891', 0, NULL, CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('34567890', '03500', 'Pippero666!', 'BNCGNN90D45H501B', 'Giovanna', 'Bianchi', 'giovanna.bianchi@email.com', '+391234567892', 0, NULL, CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('45678901', '03500', 'Pippero666!', 'NREFNC75P07H501C', 'Francesco', 'Neri', 'francesco.neri@email.com', '+391234567893', 0, NULL, CURRENT_TIMESTAMP, NULL, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert test accounts
INSERT INTO account (account_id, abi, account_type, account_number, status, iban, created_at, modified_at)
VALUES
    ('ACC-001', '03500', 'CURRENT', '0350012345678901', 'ACTIVE', 'IT60X0350001234567890123456', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ACC-002', '03500', 'CURRENT', '0350023456789012', 'ACTIVE', 'IT60X0350002345678901234567', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ACC-003', '03500', 'CURRENT', '0350034567890123', 'ACTIVE', 'IT60X0350003456789012345678', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('ACC-004', '03500', 'CURRENT', '0350045678901234', 'ACTIVE', 'IT60X0350004567890123456789', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert account holders
INSERT INTO account_holder (holder_id, account_id, client_id, holder_type, created_at)
VALUES
    ('HOLD-001', 'ACC-001', '12345678', 'PRIMARY', CURRENT_TIMESTAMP),
    ('HOLD-002', 'ACC-002', '23456789', 'PRIMARY', CURRENT_TIMESTAMP),
    ('HOLD-003', 'ACC-003', '34567890', 'PRIMARY', CURRENT_TIMESTAMP),
    ('HOLD-004', 'ACC-004', '45678901', 'PRIMARY', CURRENT_TIMESTAMP);

-- Insert some sample audit logs
INSERT INTO audit_log (client_id, abi, event_type, status, message, ip_address, user_agent, created_at)
VALUES
    ('12345678', '03500', 'LOGIN', 'SUCCESS', 'Successful login', '192.168.1.1', 'Mozilla/5.0', CURRENT_TIMESTAMP),
    ('23456789', '03500', 'LOGIN', 'SUCCESS', 'Successful login', '192.168.1.2', 'Mozilla/5.0', CURRENT_TIMESTAMP),
    ('34567890', '03500', 'LOGIN', 'SUCCESS', 'Successful login', '192.168.1.3', 'Mozilla/5.0', CURRENT_TIMESTAMP),
    ('45678901', '03500', 'LOGIN', 'SUCCESS', 'Successful login', '192.168.1.4', 'Mozilla/5.0', CURRENT_TIMESTAMP);
