package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.controllers.dto.CommentResponseDto;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/books/{bookId}/comments")
    public ResponseEntity<CommentResponseDto> addComment(
            @PathVariable Long bookId,
            @RequestBody CommentRequestDto dto) {
        Comment comment = commentService.insert(bookId, dto.text());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CommentResponseDto(comment.getId(), comment.getText()));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    public record CommentRequestDto(String text) {}
}
