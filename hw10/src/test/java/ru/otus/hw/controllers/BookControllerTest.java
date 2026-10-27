package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("BookController")
class BookControllerTest {

    @Nested
    @WebMvcTest(BookController.class)
    @DisplayName("BookController")
    class Tests {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private BookService bookService;

        @Autowired
        private ObjectMapper objectMapper;

        private final Author author = new Author(1L, "Author_1");
        private final Genre genre1 = new Genre(1L, "Genre_1");
        private final Genre genre2 = new Genre(2L, "Genre_2");
        private final Comment comment = new Comment(1L, "Comment_1", null);
        private final Book book1 = new Book(1L, "BookTitle_1", author, List.of(genre1, genre2), List.of(comment));
        private final Book book2 = new Book(2L, "BookTitle_2", author, List.of(genre1), List.of());

        @DisplayName("GET /api/books — должен возвращать список книг")
        @Test
        void shouldReturnAllBooks() throws Exception {
            when(bookService.findAll()).thenReturn(List.of(book1, book2));

            mockMvc.perform(get("/api/books"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].title").value("BookTitle_1"))
                    .andExpect(jsonPath("$[0].authorId").value(1))
                    .andExpect(jsonPath("$[0].authorName").value("Author_1"));
        }

        @DisplayName("GET /api/books/{id} — должен возвращать книгу по ID")
        @Test
        void shouldReturnBookById() throws Exception {
            when(bookService.findById(1L)).thenReturn(Optional.of(book1));

            mockMvc.perform(get("/api/books/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("BookTitle_1"))
                    .andExpect(jsonPath("$.authorId").value(1))
                    .andExpect(jsonPath("$.genresIds").isArray())
                    .andExpect(jsonPath("$.genresIds.length()").value(2));
        }

        @DisplayName("GET /api/books/{id} — должен возвращать 404 если книга не найдена")
        @Test
        void shouldReturn404WhenBookNotFound() throws Exception {
            when(bookService.findById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/books/999"))
                    .andExpect(status().isNotFound());
        }

        @DisplayName("POST /api/books — должен создавать книгу")
        @Test
        void shouldCreateBook() throws Exception {
            Book newBook = new Book(3L, "New Book", author, List.of(genre1), List.of());
            BookDto dto = new BookDto("New Book", 1L, Set.of(1L));

            when(bookService.insert("New Book", 1L, Set.of(1L))).thenReturn(newBook);

            mockMvc.perform(post("/api/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(3))
                    .andExpect(jsonPath("$.title").value("New Book"));

            verify(bookService).insert("New Book", 1L, Set.of(1L));
        }

        @DisplayName("PUT /api/books/{id} — должен обновлять книгу")
        @Test
        void shouldUpdateBook() throws Exception {
            Book updatedBook = new Book(1L, "Updated Book", author, List.of(genre2), List.of());
            BookDto dto = new BookDto("Updated Book", 1L, Set.of(2L));

            when(bookService.update(1L, "Updated Book", 1L, Set.of(2L))).thenReturn(updatedBook);

            mockMvc.perform(put("/api/books/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Updated Book"))
                    .andExpect(jsonPath("$.genresIds").isArray())
                    .andExpect(jsonPath("$.genresIds.length()").value(1));

            verify(bookService).update(eq(1L), eq("Updated Book"), eq(1L), any());
        }

        @DisplayName("DELETE /api/books/{id} — должен удалять книгу")
        @Test
        void shouldDeleteBook() throws Exception {
            doNothing().when(bookService).deleteById(1L);

            mockMvc.perform(delete("/api/books/1"))
                    .andExpect(status().isNoContent());

            verify(bookService).deleteById(1L);
        }
    }
}
