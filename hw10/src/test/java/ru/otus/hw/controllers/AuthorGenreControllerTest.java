package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("AuthorGenreController")
class AuthorGenreControllerTest {

    @Nested
    @WebMvcTest(AuthorGenreController.class)
    @DisplayName("AuthorGenreController")
    class Tests {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private AuthorService authorService;

        @MockitoBean
        private GenreService genreService;

        @DisplayName("GET /api/authors — должен возвращать список авторов")
        @Test
        void shouldReturnAuthorsList() throws Exception {
            when(authorService.findAll()).thenReturn(List.of(
                    new Author(1L, "Author_1"),
                    new Author(2L, "Author_2")
            ));

            mockMvc.perform(get("/api/authors"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].fullName").value("Author_1"));
        }

        @DisplayName("GET /api/genres — должен возвращать список жанров")
        @Test
        void shouldReturnGenresList() throws Exception {
            when(genreService.findAll()).thenReturn(List.of(
                    new Genre(1L, "Genre_1"),
                    new Genre(2L, "Genre_2")
            ));

            mockMvc.perform(get("/api/genres"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("Genre_1"));
        }
    }
}
