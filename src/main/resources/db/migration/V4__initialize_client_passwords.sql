-- Initialize passwords for existing clients
-- The password will be hashed by the application, this is just a temporary setup
-- Default password format: clientId + '@Ox2024!'
UPDATE CLIENT
SET password = CONCAT(client_id, '@Ox2024!')
WHERE password IS NULL;
