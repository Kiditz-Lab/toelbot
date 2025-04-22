CREATE TABLE model (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(60) NOT NULL,
    vendor VARCHAR(60) NOT NULL,
    model VARCHAR(60) NOT NULL
);
