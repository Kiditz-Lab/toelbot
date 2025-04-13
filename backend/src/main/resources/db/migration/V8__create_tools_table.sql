CREATE TABLE tools (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    image_url TEXT,
    short_description VARCHAR(150),
    description TEXT,
    command VARCHAR(20),
    -- Name, Description -> get_products -> Use to get products
    tools TEXT,
    args TEXT[],
    env TEXT
);
