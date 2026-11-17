package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.BookService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Безопасность контроллеров")
class SecurityTest {

    @Nested
    @WebMvcTest(BookController.class)
    class BookControllerSecurityTests {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private BookService bookService;

        private final Author author = new Author(1L, "Author_1");
        private final Genre genre = new Genre(1L, "Genre_1");
        private final Book book = new Book(1L, "BookTitle_1", author, List.of(genre), List.of());

        @Test
        @DisplayName("GET /api/books без аутентификации должен возвращать 401")
        void bookEndpointShouldBeProtected() throws Exception {
            when(bookService.findAll()).thenReturn(List.of(book));

            mockMvc.perform(get("/api/books"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser(username = "user", roles = "USER")
        @DisplayName("GET /api/books с аутентифицированным пользователем должен возвращать 200")
        void bookEndpointShouldWorkWithAuth() throws Exception {
            when(bookService.findAll()).thenReturn(List.of(book));

            mockMvc.perform(get("/api/books"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @WebMvcTest(LoginController.class)
    class LoginControllerSecurityTests {

        @Autowired
        private MockMvc mockMvc;

        @Test
        @DisplayName("GET /login должен быть доступен без аутентификации")
        void loginPageShouldBeAccessible() throws Exception {
            mockMvc.perform(get("/login"))
                    .andExpect(status().isOk());
        }
    }
}
