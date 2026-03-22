-- V1__init_inventory_schema.sql
-- Inventory Service initial schema

CREATE TABLE categories (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(500),
    parent_id   BIGINT,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES categories(id)
);

CREATE TABLE locations (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    type        VARCHAR(50)  NOT NULL,
    address     VARCHAR(500),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE products (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name                VARCHAR(255) NOT NULL,
    sku                 VARCHAR(100) NOT NULL UNIQUE,
    barcode             VARCHAR(100) UNIQUE,
    description         VARCHAR(1000),
    category_id         BIGINT       NOT NULL,
    unit_price          DECIMAL(12, 2) NOT NULL,
    unit_of_measurement VARCHAR(50)  NOT NULL DEFAULT 'UNIT',
    manufacturer        VARCHAR(255),
    requires_prescription BOOLEAN    NOT NULL DEFAULT FALSE,
    is_controlled       BOOLEAN      NOT NULL DEFAULT FALSE,
    min_stock_level     INT          NOT NULL DEFAULT 10,
    max_stock_level     INT          NOT NULL DEFAULT 1000,
    reorder_point       INT          NOT NULL DEFAULT 20,
    expiry_warning_days INT          NOT NULL DEFAULT 30,
    is_active           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE stock (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id      BIGINT    NOT NULL,
    location_id     BIGINT    NOT NULL,
    quantity        INT       NOT NULL DEFAULT 0,
    batch_number    VARCHAR(100),
    expiry_date     DATE,
    last_restocked  TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stock_product  FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_stock_location FOREIGN KEY (location_id) REFERENCES locations(id),
    CONSTRAINT uq_stock_product_location_batch UNIQUE (product_id, location_id, batch_number)
);

CREATE TABLE stock_movements (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    product_id      BIGINT       NOT NULL,
    from_location_id BIGINT,
    to_location_id  BIGINT,
    quantity         INT         NOT NULL,
    movement_type    VARCHAR(50) NOT NULL,
    reference_number VARCHAR(100),
    reason           VARCHAR(500),
    performed_by     VARCHAR(100),
    created_at       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movement_product  FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_movement_from     FOREIGN KEY (from_location_id) REFERENCES locations(id),
    CONSTRAINT fk_movement_to       FOREIGN KEY (to_location_id) REFERENCES locations(id)
);

-- Indexes
CREATE INDEX idx_products_sku ON products(sku);
CREATE INDEX idx_products_barcode ON products(barcode);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_stock_product ON stock(product_id);
CREATE INDEX idx_stock_location ON stock(location_id);
CREATE INDEX idx_stock_expiry ON stock(expiry_date);
CREATE INDEX idx_movements_product ON stock_movements(product_id);
CREATE INDEX idx_movements_type ON stock_movements(movement_type);
CREATE INDEX idx_movements_created ON stock_movements(created_at);
