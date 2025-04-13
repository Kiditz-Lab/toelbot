CREATE TABLE agent (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    agent_key VARCHAR(21) NOT NULL,
    is_public BOOL DEFAULT FALSE,
    ai_model VARCHAR(255),
    prompt TEXT,
    temperature DOUBLE PRECISION DEFAULT 0.0,

    created_by VARCHAR(255) NOT NULL,
    version INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);
