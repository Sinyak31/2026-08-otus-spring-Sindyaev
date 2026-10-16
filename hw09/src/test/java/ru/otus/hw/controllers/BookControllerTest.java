package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@DisplayName("Тесты контроллеров")
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
    }

    @Nested
    @WebMvcTest(CommentController.class)
    @DisplayName("CommentController")
    class CommentControllerTests {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private CommentService commentService;

        private final Book book1 = new Book(1L, "BookTitle_1", new Author(1L, "Author_1"), List.of(), List.of());
        private final Comment comment = new Comment(1L, "Great book!", book1);

        @DisplayName("POST /comments/books/{bookId} — должен добавлять комментарий")
        @Test
        void shouldAddComment() throws Exception {
            when(commentService.insert(1L, "Great book!")).thenReturn(comment);

            mockMvc.perform(post("/comments/books/1")
                            .param("text", "Great book!"))
                    .andExpect(redirectedUrl("/books/1"));

            verify(commentService).insert(1L, "Great book!");
        }

        @DisplayName("POST /comments/{id}/delete — должен удалять комментарий")
        @Test
        void shouldDeleteComment() throws Exception {
            doNothing().when(commentService).deleteById(1L);

            mockMvc.perform(post("/comments/1/delete")
                            .param("bookId", "1"))
                    .andExpect(redirectedUrl("/books/1"));

            verify(commentService).deleteById(1L);
        }
    }
}
