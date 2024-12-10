CREATE TABLE CLIENT (
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
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_client_id_abi UNIQUE (client_id, abi)
);

CREATE TABLE ACCOUNT (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_id VARCHAR(255) NOT NULL UNIQUE,
    abi VARCHAR(5) NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    account_number VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL,
    iban VARCHAR(34) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL,
    modified_at TIMESTAMP NOT NULL
);

CREATE TABLE ACCOUNT_HOLDER (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    holder_id VARCHAR(255) NOT NULL UNIQUE,
    account_id VARCHAR(255) NOT NULL,
    client_id VARCHAR(255) NOT NULL,
    holder_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (account_id) REFERENCES ACCOUNT(account_id),
    FOREIGN KEY (client_id) REFERENCES CLIENT(client_id)
);

CREATE TABLE AUDIT_LOG (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id VARCHAR(8) NOT NULL,
    abi VARCHAR(5) NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,
    message TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_client_id ON AUDIT_LOG(client_id);
CREATE INDEX idx_audit_event_type ON AUDIT_LOG(event_type);
CREATE INDEX idx_audit_created_at ON AUDIT_LOG(created_at);
