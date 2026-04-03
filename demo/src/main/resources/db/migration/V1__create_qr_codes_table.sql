-- QR Code Management System Database Schema
-- Creates table for storing QR code mappings

CREATE TABLE IF NOT EXISTS qr_codes (
    id BIGSERIAL PRIMARY KEY,
    short_code VARCHAR(10) NOT NULL UNIQUE,
    original_url VARCHAR(20) NOT NULL,
    user_id VARCHAR(50),
    width INTEGER,
    height INTEGER,
    scan_count BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_scanned_at TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_qr_codes_short_code ON qr_codes(short_code);
CREATE INDEX IF NOT EXISTS idx_qr_codes_user_id ON qr_codes(user_id);

-- Add comments
COMMENT ON TABLE qr_codes IS 'Stores QR code mappings for URL shortening and redirection';
COMMENT ON COLUMN qr_codes.short_code IS 'Unique short code used in QR redirect URL';
COMMENT ON COLUMN qr_codes.original_url IS 'Original URL that the QR code redirects to';
COMMENT ON COLUMN qr_codes.user_id IS 'ID of the user who created the QR code';
COMMENT ON COLUMN qr_codes.scan_count IS 'Number of times the QR code has been scanned';
COMMENT ON COLUMN qr_codes.last_scanned_at IS 'Timestamp of the last scan';
