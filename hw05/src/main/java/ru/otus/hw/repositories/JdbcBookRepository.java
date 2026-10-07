package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.BookMapper;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    @Override
    public Optional<Book> findById(long id) {
        Book book = namedParameterJdbcOperations.query(
                "select b.id, b.title, b.author_id, a.full_name " +
                        "from books b join authors a on a.id = b.author_id " +
                        "where b.id = :id",
                new MapSqlParameterSource().addValue("id", id),
                new BookResultSetExtractor());

        if (Objects.isNull(book)) {
            return Optional.empty();
        }

        loadGenresFor(book);
        return Optional.of(book);
    }

    @Override
    public List<Book> findAll() {
        final var genres = genreRepository.findAll();
        final var books = getAllBooksWithoutGenres();
        final var relations = getAllGenreRelations();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        final var params = Map.of("id", id);
        namedParameterJdbcOperations.update("delete from books_genres where book_id = :id", params);
        namedParameterJdbcOperations.update("delete from books where id = :id", params);
    }

    private List<Book> getAllBooksWithoutGenres() {
        return namedParameterJdbcOperations.query(
                "select b.id, b.title, b.author_id, a.full_name " +
                        "from books b join authors a on a.id = b.author_id",
                new BookMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        return namedParameterJdbcOperations.query(
                "select book_id, genre_id from books_genres",
                (rs, rowNum) -> new BookGenreRelation(
                        rs.getLong("book_id"),
                        rs.getLong("genre_id")
                ));
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        final var genreById = genres.stream()
                .collect(Collectors.toMap(Genre::getId, g -> g));

        final var genreIdsByBookId = relations.stream()
                .collect(Collectors.groupingBy(
                        BookGenreRelation::bookId,
                        Collectors.mapping(BookGenreRelation::genreId, Collectors.toList())
                ));

        for (var book : booksWithoutGenres) {
            final var genreIds = genreIdsByBookId.getOrDefault(book.getId(), List.of());
            final var bookGenres = genreIds.stream()
                    .map(genreById::get)
                    .filter(Objects::nonNull)
                    .toList();
            book.setGenres(bookGenres);
        }
    }

    private Book insert(Book book) {
        final var keyHolder = new GeneratedKeyHolder();

        final var params = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("authorId", book.getAuthor().getId());

        namedParameterJdbcOperations.update(
                "insert into books (title, author_id) values (:title, :authorId)",
                params,
                keyHolder);

        //noinspection DataFlowIssue
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        loadGenresFor(book);
        return book;
    }

    private Book update(Book book) {
        final var params = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("authorId", book.getAuthor().getId())
                .addValue("id", book.getId());

        final var updated = namedParameterJdbcOperations.update(
                "update books set title = :title, author_id = :authorId where id = :id",
                params);

        if (updated == 0) {
            throw new EntityNotFoundException("Book not found with id: " + book.getId());
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);
        loadGenresFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        if (Objects.isNull(book.getGenres()) || book.getGenres().isEmpty()) {
            return;
        }

        final var params = book.getGenres().stream()
                .map(genre -> new MapSqlParameterSource()
                        .addValue("bookId", book.getId())
                        .addValue("genreId", genre.getId()))
                .toArray(SqlParameterSource[]::new);

        namedParameterJdbcOperations.batchUpdate(
                "insert into books_genres (book_id, genre_id) values (:bookId, :genreId)",
                params);
    }

    private void removeGenresRelationsFor(Book book) {
        final var params = new MapSqlParameterSource().addValue("id", book.getId());
        namedParameterJdbcOperations.update("delete from books_genres where book_id = :id", params);
    }

    private void loadGenresFor(Book book) {
        final var allGenres = genreRepository.findAll();
        final var relations = getAllGenreRelations();

        final var genreById = allGenres.stream()
                .collect(Collectors.toMap(Genre::getId, g -> g));

        final var genreIds = relations.stream()
                .filter(r -> r.bookId == book.getId())
                .map(BookGenreRelation::genreId)
                .map(genreById::get)
                .filter(Objects::nonNull)
                .toList();

        book.setGenres(genreIds);
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            List<Book> books = new ArrayList<>();
            while (rs.next()) {
                final var id = rs.getLong("id");
                final var title = rs.getString("title");
                final var authorId = rs.getLong("author_id");
                final var authorFullName = rs.getString("full_name");

                final var author = new Author(authorId, authorFullName);
                final var book = new Book(id, title, author, new ArrayList<>());
                books.add(book);
            }
            return books.isEmpty() ? null : books.get(0);
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }

}
