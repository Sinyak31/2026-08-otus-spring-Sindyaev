package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

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