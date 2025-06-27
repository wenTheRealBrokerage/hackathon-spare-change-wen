CREATE TABLE mock_orders (
    id VARCHAR(255) PRIMARY KEY,
    product_id VARCHAR(50) NOT NULL,
    side VARCHAR(10) NOT NULL,
    type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    settled BOOLEAN NOT NULL,
    created_at TIMESTAMP NOT NULL,
    filled_size DECIMAL(19, 8),
    executed_value DECIMAL(19, 2),
    fill_fees DECIMAL(19, 2)
);