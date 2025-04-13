CREATE TABLE training_data (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    type VARCHAR(20) NOT NULL,
    url TEXT NOT NULL,
    file TEXT NOT NULL,
    mime_type VARCHAR(60) NOT NULL,
    prefix VARCHAR(14) NOT NULL,
    agent_id UUID,
    size BIGINT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL,
    created_by VARCHAR(255),
    progress BOOLEAN NOT NULL DEFAULT FALSE
);
