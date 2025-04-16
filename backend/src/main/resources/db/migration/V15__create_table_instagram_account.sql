CREATE TABLE instagram_account (
    id TEXT PRIMARY KEY,
    user_id TEXT NOT NULL,
    profile_picture_url TEXT,
    name TEXT,
    username TEXT
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
