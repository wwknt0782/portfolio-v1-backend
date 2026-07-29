CREATE TABLE contact
(
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(100)  NOT NULL,
    company_name VARCHAR(100),
    email        VARCHAR(100)  NOT NULL,
    message      VARCHAR(2000) NOT NULL,
    created_at   TIMESTAMP     NOT NULL
);