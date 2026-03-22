-- V1__init_supplier_schema.sql
-- Supplier Service schema for vendors and purchase orders

CREATE TABLE suppliers (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name          VARCHAR(255) NOT NULL,
    contact_name  VARCHAR(255),
    email         VARCHAR(255) NOT NULL,
    phone         VARCHAR(50),
    address       VARCHAR(500),
    rating        FLOAT,
    active        BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE purchase_orders (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    po_number     VARCHAR(100) NOT NULL UNIQUE,
    supplier_id   BIGINT NOT NULL,
    status        VARCHAR(50) NOT NULL,
    total_amount  DECIMAL(10, 2),
    expected_date DATE,
    notes         VARCHAR(1000),
    created_by    VARCHAR(100) NOT NULL,
    created_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_po_supplier FOREIGN KEY (supplier_id) REFERENCES suppliers(id)
);

CREATE TABLE po_items (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    po_id         BIGINT NOT NULL,
    product_id    BIGINT NOT NULL,
    product_sku   VARCHAR(100) NOT NULL,
    product_name  VARCHAR(255) NOT NULL,
    quantity      INT NOT NULL,
    unit_price    DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_po_item_po FOREIGN KEY (po_id) REFERENCES purchase_orders(id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX idx_suppliers_name ON suppliers(name);
CREATE INDEX idx_suppliers_active ON suppliers(active);
CREATE INDEX idx_po_number ON purchase_orders(po_number);
CREATE INDEX idx_po_supplier ON purchase_orders(supplier_id);
CREATE INDEX idx_po_status ON purchase_orders(status);
CREATE INDEX idx_po_items_po ON po_items(po_id);
