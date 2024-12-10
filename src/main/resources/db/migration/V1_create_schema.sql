-- Create Client table
CREATE TABLE client (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id VARCHAR(8) NOT NULL,
    abi VARCHAR(5) NOT NULL,
    password VARCHAR(50) NOT NULL,
    fiscal_code VARCHAR(16) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    failed_attempts INT DEFAULT 0,
    locked_until TIMESTAMP,
    last_access TIMESTAMP,
    previous_access TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_client_id_abi UNIQUE (client_id, abi)
);

-- Create Account table
CREATE TABLE account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id VARCHAR(36) NOT NULL,
    abi VARCHAR(5) NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    account_number VARCHAR(34) NOT NULL,
    status VARCHAR(20) NOT NULL,
    iban VARCHAR(34) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_account_id UNIQUE (account_id),
    CONSTRAINT uk_iban UNIQUE (iban)
);

-- Create Account Holder table
CREATE TABLE account_holder (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    holder_id VARCHAR(36) NOT NULL,
    account_id VARCHAR(36) NOT NULL,
    client_id VARCHAR(8) NOT NULL,
    abi VARCHAR(5) NOT NULL,
    holder_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_holder_id UNIQUE (holder_id),
    CONSTRAINT fk_account_holder_account FOREIGN KEY (account_id) REFERENCES account(account_id),
    CONSTRAINT fk_account_holder_client FOREIGN KEY (client_id, abi) REFERENCES client(client_id, abi)
);

-- Create Audit Log table
CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id VARCHAR(8) NOT NULL,
    abi VARCHAR(5) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    message TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_client FOREIGN KEY (client_id, abi) REFERENCES client(client_id, abi)
);

-- Create indexes
CREATE INDEX idx_client_id_abi ON client(client_id, abi);
CREATE INDEX idx_account_abi ON account(abi);
CREATE INDEX idx_account_status ON account(status);
CREATE INDEX idx_holder_account ON account_holder(account_id);
CREATE INDEX idx_holder_client ON account_holder(client_id, abi);
CREATE INDEX idx_audit_client ON audit_log(client_id, abi);
CREATE INDEX idx_audit_event ON audit_log(event_type);
CREATE INDEX idx_audit_created ON audit_log(created_at);
