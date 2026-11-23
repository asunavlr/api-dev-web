CREATE TABLE inventory_items (
    id UUID PRIMARY KEY,
    sku VARCHAR(120) NOT NULL UNIQUE,
    available_quantity INTEGER NOT NULL CHECK (available_quantity >= 0),
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE inventory_reservations (
    event_id VARCHAR(36) PRIMARY KEY,
    order_id VARCHAR(36) NOT NULL UNIQUE,
    sku VARCHAR(120) NOT NULL,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    status VARCHAR(20) NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL
);
