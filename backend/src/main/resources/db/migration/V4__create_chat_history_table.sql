CREATE TABLE chat_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    agent_id UUID NOT NULL,
    chat_id VARCHAR(36) NOT NULL,
    bot_message TEXT,
    user_message TEXT,
    ip_address VARCHAR(20),
    country_code VARCHAR(5),
    country VARCHAR(255),
    region_name VARCHAR(255),
    model VARCHAR(255),
    region_code VARCHAR(5),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
