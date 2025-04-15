CREATE TABLE facebook_page_access (
    id UUID PRIMARY KEY,
    agent_id UUID,
    page_id VARCHAR(20),
    access_token TEXT,
    name VARCHAR(255),
    category VARCHAR(255),
    image_url VARCHAR(255),
    access_token TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
);
