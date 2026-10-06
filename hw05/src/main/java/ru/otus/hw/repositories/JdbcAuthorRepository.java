package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.mapper.AuthorMapper;
import ru.otus.hw.models.Author;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public List<Author> findAll() {
        return namedParameterJdbcOperations.query("select * from authors", new AuthorMapper());
    }

    @Override
    public Optional<Author> findById(long id) {
        final var params = Collections.singletonMap("id", id);
        return namedParameterJdbcOperations.query(
                        "select id, full_name from authors where id = :id", params, new AuthorMapper())
                .stream()
                .findFirst();
    }

}
