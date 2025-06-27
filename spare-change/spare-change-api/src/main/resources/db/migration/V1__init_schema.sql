CREATE TABLE transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant VARCHAR(255),
    amount_usd DECIMAL(10,2),
    ts TIMESTAMP,
    spare_usd DECIMAL(10,2),
    status VARCHAR(50),
    coinbase_order_id VARCHAR(255)
);

CREATE TABLE roundup_summaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    total_usd DECIMAL(10,2),
    created_at TIMESTAMP,
    coinbase_order_id VARCHAR(255)
);

CREATE INDEX idx_tx_status ON transactions(status);
CREATE INDEX idx_tx_ts ON transactions(ts);