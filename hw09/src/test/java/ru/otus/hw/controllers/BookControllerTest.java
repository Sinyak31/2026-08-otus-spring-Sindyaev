package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@DisplayName("BookController")
class BookControllerTest {

    @Nested
    @WebMvcTest(BookController.class)
    @DisplayName("BookController")
    class BookControllerTests {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private BookService bookService;

        @MockitoBean
        private AuthorService authorService;

        @MockitoBean
        private GenreService genreService;

        @MockitoBean
        private CommentService commentService;

        private final Author author = new Author(1L, "Author_1");
        private final Genre genre1 = new Genre(1L, "Genre_1");
        private final Genre genre2 = new Genre(2L, "Genre_2");
        private final Book book1 = new Book(1L, "BookTitle_1", author, List.of(genre1, genre2), List.of());
        private final Book book2 = new Book(2L, "BookTitle_2", author, List.of(genre1), List.of());

        @DisplayName("GET /books — должен возвращать список книг")
        @Test
        void shouldReturnBooksList() throws Exception {
            when(bookService.findAll()).thenReturn(List.of(book1, book2));
            when(authorService.findAll()).thenReturn(List.of(author));
            when(genreService.findAll()).thenReturn(List.of(genre1, genre2));

            mockMvc.perform(get("/books"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("books/list"));
            verify(bookService).findAll();
        }

        @DisplayName("GET /books/form — форма создания")
        @Test
        void shouldReturnCreateForm() throws Exception {
            when(authorService.findAll()).thenReturn(List.of(author));
            when(genreService.findAll()).thenReturn(List.of(genre1, genre2));

            mockMvc.perform(get("/books/form"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("books/form"));
        }

        @DisplayName("GET /books/form?id=1 — форма редактирования")
        @Test
        void shouldReturnEditForm() throws Exception {
            when(bookService.findById(1L)).thenReturn(java.util.Optional.of(book1));
            when(authorService.findAll()).thenReturn(List.of(author));
            when(genreService.findAll()).thenReturn(List.of(genre1, genre2));

            mockMvc.perform(get("/books/form").param("id", "1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("books/form"));
        }

        @DisplayName("POST /books — должен создавать книгу и редиректить на список")
        @Test
        void shouldCreateBook() throws Exception {
            when(bookService.insert("New Book", 1L, Set.of(1L, 2L)))
                    .thenReturn(new Book(3L, "New Book", author, List.of(genre1, genre2), List.of()));

            mockMvc.perform(post("/books")
                            .param("title", "New Book")
                            .param("authorId", "1")
                            .param("genresIds", "1", "2"))
                    .andExpect(redirectedUrl("/books"));

            verify(bookService).insert("New Book", 1L, Set.of(1L, 2L));
        }

        @DisplayName("POST /books/{id} — должен обновлять книгу и редиректить на список")
        @Test
        void shouldUpdateBook() throws Exception {
            when(bookService.update(1L, "Updated Book", 1L, Set.of(1L)))
                    .thenReturn(new Book(1L, "Updated Book", author, List.of(genre1), List.of()));

            mockMvc.perform(post("/books/1")
                            .param("title", "Updated Book")
                            .param("authorId", "1")
                            .param("genresIds", "1"))
                    .andExpect(redirectedUrl("/books"));

            verify(bookService).update(eq(1L), eq("Updated Book"), eq(1L), any());
        }

        @DisplayName("POST /books/{id}/delete — должен удалять книгу и редиректить на список")
        @Test
        void shouldDeleteBook() throws Exception {
            doNothing().when(bookService).deleteById(1L);

            mockMvc.perform(post("/books/1/delete"))
                    .andExpect(redirectedUrl("/books"));

            verify(bookService).deleteById(1L);
        }

        @DisplayName("GET /books/{id} — должен возвращать детальную страницу книги")
        @Test
        void shouldReturnBookDetail() throws Exception {
            when(bookService.findById(1L)).thenReturn(java.util.Optional.of(book1));

            mockMvc.perform(get("/books/1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("books/detail"));
            verify(bookService).findById(1L);
        }

        @DisplayName("GET /books/999 — книга не найдена, редирект на главную")
        @Test
        void shouldRedirectWhenBookNotFound() throws Exception {
            when(bookService.findById(999L)).thenReturn(java.util.Optional.empty());

            mockMvc.perform(get("/books/999"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/books"));
        }

        @DisplayName("POST /books с пустым title — возвращает форму с ошибкой и введёнными значениями")
        @Test
        void shouldReturnFormWithErrorsWhenCreateInvalid() throws Exception {
            when(authorService.findAll()).thenReturn(List.of(author));
            when(genreService.findAll()).thenReturn(List.of(genre1, genre2));

            mockMvc.perform(post("/books")
                            .param("title", "")
                            .param("authorId", "1")
                            .param("genresIds", "1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("books/form"))
                    .andExpect(model().attributeHasFieldErrors("book", "title"))
                    .andExpect(result -> {
                        BookDto dto = (BookDto) result.getModelAndView().getModel().get("book");
                        assertThat(dto).isNotNull();
                        assertThat(dto.title()).isEmpty();
                        assertThat(dto.authorId()).isEqualTo(1L);
                        assertThat(dto.genresIds()).containsExactly(1L);
                    });

            verify(bookService, never()).insert(anyString(), anyLong(), any());
        }

        @DisplayName("POST /books без authorId — возвращает форму с ошибкой")
        @Test
        void shouldReturnFormWithErrorsWhenAuthorIdMissing() throws Exception {
            when(authorService.findAll()).thenReturn(List.of(author));
            when(genreService.findAll()).thenReturn(List.of(genre1, genre2));

            mockMvc.perform(post("/books")
                            .param("title", "New Book")
                            .param("genresIds", "1"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("books/form"))
                    .andExpect(model().attributeHasFieldErrors("book", "authorId"))
                    .andExpect(result -> {
                        BookDto dto = (BookDto) result.getModelAndView().getModel().get("book");
                        assertThat(dto).isNotNull();
                        assertThat(dto.title()).isEqualTo("New Book");
                        assertThat(dto.authorId()).isNull();
                        assertThat(dto.genresIds()).containsExactly(1L);
                    });

            verify(bookService, never()).insert(anyString(), anyLong(), any());
        }

        @DisplayName("POST /books/{id} с пустым title — сохраняет введённые значения и id")
        @Test
        void shouldReturnFormWithEnteredValuesWhenUpdateInvalid() throws Exception {
            when(authorService.findAll()).thenReturn(List.of(author));
            when(genreService.findAll()).thenReturn(List.of(genre1, genre2));

            mockMvc.perform(post("/books/1")
                            .param("title", "")
                            .param("authorId", "1")
                            .param("genresIds", "2"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("books/form"))
                    .andExpect(model().attributeHasFieldErrors("book", "title"))
                    .andExpect(model().attribute("bookId", 1L))
                    .andExpect(result -> {
                        BookDto dto = (BookDto) result.getModelAndView().getModel().get("book");
                        assertThat(dto).isNotNull();
                        assertThat(dto.title()).isEmpty();
                        assertThat(dto.authorId()).isEqualTo(1L);
                        assertThat(dto.genresIds()).containsExactly(2L);
                    });

            verify(bookService, never()).update(anyLong(), anyString(), anyLong(), any());
        }
    }

}