--liquibase formatted sql

changeset: 003:create-users

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50)
);

INSERT INTO users (username, password, role) VALUES ('user', '$2a$10$YkWqGOCxyhPBHNqejaIIBuPKjYwHBDtD8qZ3y8LrOhBb.9Gv1yI2i', 'USER');
INSERT INTO users (username, password, role) VALUES ('admin', '$2a$10$YkWqGOCxyhPBHNqejaIIBuPKjYwHBDtD8qZ3y8LrOhBb.9Gv1yI2i', 'ADMIN');
