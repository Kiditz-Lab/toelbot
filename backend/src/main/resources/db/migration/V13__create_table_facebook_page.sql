CREATE TABLE facebook_page (
    id UUID PRIMARY KEY,
    agent_id UUID,
    page_id VARCHAR(20),
    access_token TEXT,
    name VARCHAR(255),
    category VARCHAR(255),
    image_url TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
