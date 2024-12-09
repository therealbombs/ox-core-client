-- Initialize passwords for existing clients with BCrypt hashed passwords
-- Default password format: clientId + '@Ox2024!'
-- Example: For client C001, password will be 'C001@Ox2024!'
UPDATE CLIENT
SET 
    password = CASE client_id
        WHEN 'C001' THEN '$2a$10$xn3LI/AjqicFYZFruSwve.ODd6/Zcn9RBbPufZ6g8sfZkjYlS4ki2'  -- C001@Ox2024!
        WHEN 'C002' THEN '$2a$10$2.6Y9HGo0IuR6LJ/uYH.p.3h.YQo1xjE/XxwYYX.0LSYQoK/IH6EK'  -- C002@Ox2024!
        WHEN 'C003' THEN '$2a$10$UYtMoWpgR0QY0.EAPFDn6.zmEPH/2EdaGY45XF1iJBK9p7Q6dBMfO'  -- C003@Ox2024!
    END,
    password_change_required = TRUE,
    failed_attempts = 0
WHERE password IS NULL;
