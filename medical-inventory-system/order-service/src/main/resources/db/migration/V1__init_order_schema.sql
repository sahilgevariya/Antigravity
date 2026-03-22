-- V1__init_order_schema.sql
-- Order Service initial schema

CREATE TABLE orders (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_number        VARCHAR(100) NOT NULL UNIQUE,
    status              VARCHAR(50)  NOT NULL,
    requesting_user     VARCHAR(100) NOT NULL, -- username or ID from Auth service
    requesting_location VARCHAR(100) NOT NULL,
    approved_by         VARCHAR(100),
    approved_at         TIMESTAMP,
    remarks             VARCHAR(1000),
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    order_id            BIGINT       NOT NULL,
    product_id          BIGINT       NOT NULL, -- Reference to Inventory service product ID
    product_sku         VARCHAR(100) NOT NULL,
    product_name        VARCHAR(255) NOT NULL,
    quantity_requested  INT          NOT NULL,
    quantity_fulfilled  INT          NOT NULL DEFAULT 0,
    notes               VARCHAR(500),
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_orders_number ON orders(order_number);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_requesting_user ON orders(requesting_user);
CREATE INDEX idx_order_items_order ON order_items(order_id);
CREATE INDEX idx_order_items_product ON order_items(product_id);
