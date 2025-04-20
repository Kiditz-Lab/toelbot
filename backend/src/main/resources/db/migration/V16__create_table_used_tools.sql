CREATE TABLE used_tool (
    server_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    PRIMARY KEY (server_id, name),
    FOREIGN KEY (server_id) REFERENCES mcp_servers(id) ON DELETE CASCADE
);
