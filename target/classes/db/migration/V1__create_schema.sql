CREATE TABLE client (
    client_id VARCHAR(8) NOT NULL,
    abi VARCHAR(5) NOT NULL,
    password VARCHAR(255) NOT NULL,
    fiscal_code VARCHAR(16) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    preferred_language VARCHAR(2),
    failed_attempts INT DEFAULT 0,
    locked_until TIMESTAMP,
    password_change_required BOOLEAN DEFAULT FALSE,
    last_access TIMESTAMP,
    previous_access TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (client_id, abi)
);

CREATE TABLE account (
    account_id VARCHAR(34) NOT NULL,
    abi VARCHAR(5) NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    account_number VARCHAR(34) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    iban VARCHAR(34) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (account_id),
    CONSTRAINT uk_account_number UNIQUE (account_number),
    CONSTRAINT uk_iban UNIQUE (iban)
);

CREATE TABLE account_holder (
    holder_id VARCHAR(36) NOT NULL,
    account_id VARCHAR(34) NOT NULL,
    client_id VARCHAR(8) NOT NULL,
    holder_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (holder_id),
    CONSTRAINT fk_account_holder_account FOREIGN KEY (account_id) REFERENCES account(account_id),
    CONSTRAINT fk_account_holder_client FOREIGN KEY (client_id) REFERENCES client(client_id)
);

CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(50) NOT NULL,
    client_id VARCHAR(8),
    abi VARCHAR(5),
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    status VARCHAR(20) NOT NULL,
    message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes
CREATE INDEX idx_client_fiscal_code ON client(fiscal_code);
CREATE INDEX idx_account_abi ON account(abi);
CREATE INDEX idx_account_status ON account(status);
CREATE INDEX idx_holder_account ON account_holder(account_id);
CREATE INDEX idx_holder_client ON account_holder(client_id);
CREATE INDEX idx_audit_client ON audit_log(client_id);
CREATE INDEX idx_audit_event ON audit_log(event_type);
CREATE INDEX idx_audit_created ON audit_log(created_at);
