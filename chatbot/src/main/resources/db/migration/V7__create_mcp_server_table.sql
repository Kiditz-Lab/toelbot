CREATE TABLE mcp_servers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    agent_id UUID NOT NULL,
    server_name VARCHAR(255) NOT NULL,
    command VARCHAR(255) NOT NULL,
    args TEXT[],
    env TEXT
);
