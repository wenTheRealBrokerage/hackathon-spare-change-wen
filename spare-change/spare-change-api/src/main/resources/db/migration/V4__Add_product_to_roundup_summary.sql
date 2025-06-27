ALTER TABLE roundup_summaries ADD COLUMN product_id VARCHAR(20) DEFAULT 'BTC-USD';
UPDATE roundup_summaries SET product_id = 'BTC-USD' WHERE product_id IS NULL;