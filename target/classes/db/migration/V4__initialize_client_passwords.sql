-- Initialize passwords for existing clients
-- Note: These passwords will be hashed by the application when used
-- Default password format: clientId + '@Ox2024!'
-- Example: For client C001, password will be 'C001@Ox2024!'
UPDATE CLIENT
SET 
    password = CONCAT(client_id, '@Ox2024!'),
    password_change_required = TRUE
WHERE password IS NULL;
