CREATE TABLE users
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    password_hash VARCHAR(100) NOT NULL
);

INSERT INTO users (name, password_hash) VALUES ('山田太郎', '$2a$10$UURWyjfHiXkCouDOVMSxFudWwbDnpmimBLJ3twEi13AT6BJRkkUum');