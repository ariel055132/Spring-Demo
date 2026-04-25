-- V2: Add expiry, soft-delete to qr_codes; add scan_events table

-- Increase original_url column to support URLs up to 2048 characters
ALTER TABLE qr_codes ALTER COLUMN original_url TYPE VARCHAR(2048);

-- Add optional expiration timestamp (NULL means never expires)
ALTER TABLE qr_codes ADD COLUMN IF NOT EXISTS expires_at TIMESTAMP;

-- Add soft-delete flag (true = deleted, redirects return 410 Gone)
ALTER TABLE qr_codes ADD COLUMN IF NOT EXISTS is_deleted BOOLEAN NOT NULL DEFAULT FALSE;

-- Track individual scan events for analytics (matches ScanEvent model in reference)
CREATE TABLE IF NOT EXISTS scan_events (
    id          BIGSERIAL PRIMARY KEY,
    token       VARCHAR(10)  NOT NULL,
    scanned_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    user_agent  VARCHAR(500),
    ip_address  VARCHAR(45)
);

CREATE INDEX IF NOT EXISTS idx_scan_events_token_scanned ON scan_events (token, scanned_at);

COMMENT ON TABLE scan_events IS 'Records individual QR code scan events for analytics';
COMMENT ON COLUMN scan_events.token IS 'Short code of the QR code that was scanned';
COMMENT ON COLUMN scan_events.user_agent IS 'User-Agent header from the scanning request';
COMMENT ON COLUMN scan_events.ip_address IS 'IP address of the scanner';
