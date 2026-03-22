-- V1__init_payment_schema.sql
-- Payment Service schema for tracking payments

CREATE TABLE payments (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    transaction_id   VARCHAR(100) NOT NULL UNIQUE,
    reference_number VARCHAR(100) NOT NULL,
    reference_type   VARCHAR(50) NOT NULL,  -- e.g., 'ORDER', 'PURCHASE_ORDER'
    amount           DECIMAL(10, 2) NOT NULL,
    currency         VARCHAR(10) NOT NULL DEFAULT 'USD',
    payment_method   VARCHAR(50) NOT NULL,
    status           VARCHAR(50) NOT NULL,
    payment_date     TIMESTAMP,
    notes            VARCHAR(500),
    processed_by     VARCHAR(100),
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes
CREATE INDEX idx_payment_transaction ON payments(transaction_id);
CREATE INDEX idx_payment_reference ON payments(reference_number, reference_type);
CREATE INDEX idx_payment_status ON payments(status);
