package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@DisplayName("Репозиторий на основе Spring Data JPA для работы с книгами")
@DataJpaTest
class JpaBookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        List<Author> dbAuthors = getDbAuthors();
        List<Genre> dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }

    @DisplayName("должен загружать книгу по id")
    @ParameterizedTest
    @MethodSource("getDbBooks")
    void shouldReturnCorrectBookById(Book expectedBook) {
        var actualBook = bookRepository.findById(expectedBook.getId()).orElseThrow();

        assertThat(actualBook.getId()).isEqualTo(expectedBook.getId());
        assertThat(actualBook.getTitle()).isEqualTo(expectedBook.getTitle());

        assertThat(actualBook.getAuthor().getId()).isEqualTo(expectedBook.getAuthor().getId());
        assertThat(actualBook.getAuthor().getFullName()).isEqualTo(expectedBook.getAuthor().getFullName());

        assertThat(actualBook.getGenres())
                .extracting(Genre::getId, Genre::getName)
                .containsExactlyInAnyOrderElementsOf(
                        expectedBook.getGenres().stream()
                                .map(g -> tuple(g.getId(), g.getName()))
                                .toList()
                );
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookRepository.findAll();
        var expectedBooks = dbBooks;

        assertThat(actualBooks)
                .usingRecursiveComparison()
                .isEqualTo(expectedBooks);

        actualBooks.forEach(System.out::println);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var author = authorRepository.findById(1L).orElseThrow();
        var genres = genreRepository.findAllById(Set.of(1L, 3L));

        var expectedBook = new Book(null, "BookTitle_10500", author, genres);

        var returnedBook = bookRepository.save(expectedBook);

        assertThat(returnedBook)
                .isNotNull()
                .matches(book -> book.getId() != null && book.getId() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBook);

        assertThat(bookRepository.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var author = authorRepository.findById(3L).orElseThrow();
        var genres = genreRepository.findAllById(Set.of(5L, 6L));

        var expectedBook = new Book(1L, "BookTitle_10500", author, genres);

        assertThat(bookRepository.findById(expectedBook.getId()))
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isNotEqualTo(expectedBook);

        var returnedBook = bookRepository.save(expectedBook);

        assertThat(returnedBook)
                .isNotNull()
                .matches(book -> book.getId() != null && book.getId() > 0)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBook);

        assertThat(bookRepository.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteBook() {
        assertThat(bookRepository.findById(1L)).isPresent();

        bookRepository.deleteById(1L);

        assertThat(bookRepository.findById(1L)).isEmpty();
    }

    private static List<Author> getDbAuthors() {
        return LongStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }

    private static List<Genre> getDbGenres() {
        return LongStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }

    private static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return LongStream.range(1, 4).boxed()
                .map(id -> new Book(
                        id,
                        "BookTitle_" + id,
                        dbAuthors.get((int) (id - 1)),
                        dbGenres.subList((int) ((id - 1) * 2), (int) ((id - 1) * 2 + 2))
                ))
                .toList();
    }

    private static List<Book> getDbBooks() {
        return getDbBooks(getDbAuthors(), getDbGenres());
    }
}