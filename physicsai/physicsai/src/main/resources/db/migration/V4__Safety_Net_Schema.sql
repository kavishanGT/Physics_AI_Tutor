-- V4: Safety net - ensures all core tables exist with correct schema.
-- This is a no-op if V2 and V3 already ran successfully.

-- Ensure users table has phone_number column (in case V3 didn't run)
ALTER TABLE users ADD COLUMN IF NOT EXISTS phone_number VARCHAR(20) UNIQUE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS display_name VARCHAR(100);

-- Ensure conversations table has the correct columns
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS user_id BIGINT;
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS title VARCHAR(255);
ALTER TABLE conversations ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP;

-- Ensure messages table exists
CREATE TABLE IF NOT EXISTS messages (
    id              BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    role            VARCHAR(20) NOT NULL,
    content         TEXT NOT NULL,
    created_at      TIMESTAMP NOT NULL,
    CONSTRAINT fk_message_conversation FOREIGN KEY (conversation_id) REFERENCES conversations(id)
);
