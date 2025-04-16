CREATE TABLE instagram_account (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id TEXT NOT NULL,
    agent_id UUID,
    profile_picture_url TEXT,
    name TEXT,
    username TEXT,
    active BOOLEAN,
    token TEXT,
    expires_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
