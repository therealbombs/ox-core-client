CREATE TABLE client (
    client_id VARCHAR(8) NOT NULL,
    abi VARCHAR(5) NOT NULL,
    name VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (client_id, abi)
);

CREATE TABLE account (
    account_id VARCHAR(34) NOT NULL,
    client_id VARCHAR(8) NOT NULL,
    abi VARCHAR(5) NOT NULL,
    account_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (account_id),
    CONSTRAINT fk_account_client FOREIGN KEY (client_id, abi) REFERENCES client(client_id, abi)
);

CREATE TABLE account_holder (
    holder_id VARCHAR(36) NOT NULL,
    client_id VARCHAR(8) NOT NULL,
    abi VARCHAR(5) NOT NULL,
    account_id VARCHAR(34) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    fiscal_code VARCHAR(16),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (holder_id),
    CONSTRAINT fk_account_holder_client FOREIGN KEY (client_id, abi) REFERENCES client(client_id, abi),
    CONSTRAINT fk_account_holder_account FOREIGN KEY (account_id) REFERENCES account(account_id)
);

CREATE TABLE audit_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entity_type VARCHAR(50) NOT NULL,
    entity_id VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details TEXT
);

CREATE INDEX idx_holder_client ON account_holder(client_id, abi);
CREATE INDEX idx_account_client ON account(client_id, abi);
