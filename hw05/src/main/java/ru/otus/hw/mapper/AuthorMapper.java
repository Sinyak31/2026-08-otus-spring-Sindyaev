package ru.otus.hw.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthorMapper implements RowMapper<Author> {

    @Override
    public Author mapRow(ResultSet resultSet, int i) throws SQLException {
        final var id = resultSet.getLong("id");
        final var name = resultSet.getString("full_name");
        return new Author(id, name);
    }

}
