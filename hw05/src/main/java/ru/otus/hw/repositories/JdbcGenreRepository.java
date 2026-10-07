package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.mapper.GenreMapper;
import ru.otus.hw.models.Genre;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JdbcGenreRepository implements GenreRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public List<Genre> findAll() {
        return namedParameterJdbcOperations.query("select * from genres", new GenreMapper());
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            return Collections.emptyList();
        }
        final var params = Collections.singletonMap("ids", ids);
        return namedParameterJdbcOperations.query(
                "select * from genres where id in (:ids)",
                params,
                new GenreMapper());
    }


}
