CREATE TABLE users
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    password_hash VARCHAR(100) NOT NULL
);

INSERT INTO users (name, password_hash) VALUES ('user', '$2a$10$ChiWjJX05KGF6g0OLR3mmOLq8KfAOoLz6PNwoehaMSz0fHy3jY3P2');