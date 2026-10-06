package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий на основе Jdbc для работы с жанрами")
@JdbcTest
@Import({JdbcGenreRepository.class})
class JdbcGenreRepositoryTest {

    @Autowired
    private JdbcGenreRepository repositoryJdbc;

    private List<Genre> dbGenres;

    @BeforeEach
    void setUp() {
        dbGenres = getDbGenres();
    }

    @DisplayName("должен загружать список всех жанров")
    @Test
    void shouldReturnAllGenres() {
        List<Genre> actualGenres = repositoryJdbc.findAll();

        assertThat(actualGenres)
                .isNotNull()
                .hasSize(6)
                .containsExactlyInAnyOrderElementsOf(dbGenres);
    }

    @DisplayName("должен загружать жанры по ID")
    @ParameterizedTest
    @MethodSource("getDbGenres")
    void shouldReturnGenresByIds(Genre expectedGenre) {
        Set<Long> ids = Set.of(expectedGenre.getId());
        List<Genre> actualGenres = repositoryJdbc.findAllByIds(ids);

        assertThat(actualGenres)
                .isNotNull()
                .hasSize(1)
                .contains(expectedGenre);
    }

    @DisplayName("должен загружать несколько жанров по списку ID")
    @Test
    void shouldReturnGenresByIdsMultiple() {
        Set<Long> ids = Set.of(1L, 3L, 5L);
        List<Genre> actualGenres = repositoryJdbc.findAllByIds(ids);

        assertThat(actualGenres)
                .isNotNull()
                .hasSize(3)
                .extracting(Genre::getId)
                .containsExactlyInAnyOrder(1L, 3L, 5L);
    }

    @DisplayName("должен возвращать пустой список для пустого набора ID")
    @Test
    void shouldReturnEmptyListForEmptyIds() {
        Set<Long> ids = Set.of();
        List<Genre> actualGenres = repositoryJdbc.findAllByIds(ids);

        assertThat(actualGenres).isEmpty();
    }

    @DisplayName("должен возвращать пустой список для null набора ID")
    @Test
    void shouldReturnEmptyListForNullIds() {
        List<Genre> actualGenres = repositoryJdbc.findAllByIds(null);

        assertThat(actualGenres).isEmpty();
    }

    @DisplayName("должен возвращать пустой список для ID, которых нет в БД")
    @Test
    void shouldReturnEmptyListForNonExistentIds() {
        Set<Long> ids = Set.of(999L, 1000L);
        List<Genre> actualGenres = repositoryJdbc.findAllByIds(ids);

        assertThat(actualGenres).isEmpty();
    }

    private static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }
}
