-- Add security-related columns to CLIENT table
ALTER TABLE CLIENT
    ADD COLUMN IF NOT EXISTS previous_access TIMESTAMP,
    ADD COLUMN IF NOT EXISTS failed_attempts INTEGER DEFAULT 0,
    ADD COLUMN IF NOT EXISTS locked_until TIMESTAMP,
    ADD COLUMN IF NOT EXISTS password VARCHAR(255),
    ADD COLUMN IF NOT EXISTS password_change_required BOOLEAN DEFAULT TRUE;

-- Add comments for better documentation
COMMENT ON COLUMN CLIENT.previous_access IS 'Timestamp of the penultimate successful login';
COMMENT ON COLUMN CLIENT.failed_attempts IS 'Number of consecutive failed login attempts';
COMMENT ON COLUMN CLIENT.locked_until IS 'Timestamp until when the account is locked due to too many failed attempts';
COMMENT ON COLUMN CLIENT.password IS 'Hashed password for client authentication';
COMMENT ON COLUMN CLIENT.password_change_required IS 'Flag indicating if the client needs to change their password';
