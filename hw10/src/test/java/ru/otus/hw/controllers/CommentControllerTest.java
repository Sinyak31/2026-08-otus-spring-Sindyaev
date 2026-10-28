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
import ru.otus.hw.controllers.CommentController.CommentRequestDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("CommentController")
class CommentControllerTest {

    @Nested
    @WebMvcTest(CommentController.class)
    @DisplayName("CommentController")
    class Tests {

        @Autowired
        private MockMvc mockMvc;

        @MockitoBean
        private CommentService commentService;

        @Autowired
        private ObjectMapper objectMapper;

        private final Book book = new Book(1L, "BookTitle_1", new Author(1L, "Author_1"), List.of(), List.of());
        private final Comment comment = new Comment(1L, "Great book!", book);

        @DisplayName("POST /api/books/{bookId}/comments — должен добавлять комментарий")
        @Test
        void shouldAddComment() throws Exception {
            CommentRequestDto dto = new CommentRequestDto("Great book!");

            when(commentService.insert(1L, "Great book!")).thenReturn(comment);

            mockMvc.perform(post("/api/books/1/comments")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.text").value("Great book!"));

            verify(commentService).insert(1L, "Great book!");
        }

        @DisplayName("DELETE /api/comments/{id} — должен удалять комментарий")
        @Test
        void shouldDeleteComment() throws Exception {
            doNothing().when(commentService).deleteById(1L);

            mockMvc.perform(delete("/api/comments/1"))
                    .andExpect(status().isNoContent());

            verify(commentService).deleteById(1L);
        }
    }
}
