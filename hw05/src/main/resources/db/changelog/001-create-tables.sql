--liquibase formatted sql

-- changeset a.sindyaev:1
CREATE TABLE authors (
    id BIGSERIAL PRIMARY KEY,
    full_name VARCHAR(255)
);

-- changeset a.sindyaev:2
CREATE TABLE genres (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255)
);

-- changeset a.sindyaev:3
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    author_id BIGINT REFERENCES authors(id) ON DELETE CASCADE
);

-- changeset a.sindyaev:4
CREATE TABLE books_genres (
    book_id BIGINT REFERENCES books(id) ON DELETE CASCADE,
    genre_id BIGINT REFERENCES genres(id) ON DELETE CASCADE,
    PRIMARY KEY (book_id, genre_id)
);