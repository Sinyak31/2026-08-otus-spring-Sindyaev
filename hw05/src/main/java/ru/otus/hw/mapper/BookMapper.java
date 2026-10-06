package ru.otus.hw.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class BookMapper implements RowMapper<Book> {

    @Override
    public Book mapRow(ResultSet resultSet, int i) throws SQLException {
        final var id = resultSet.getLong("id");
        final var title = resultSet.getString("title");

        final var authorId = resultSet.getLong("author_id");
        final var authorFullName = resultSet.getString("full_name");
        final var author = new Author(authorId, authorFullName);

        return new Book(id, title, author, List.of());
    }
}