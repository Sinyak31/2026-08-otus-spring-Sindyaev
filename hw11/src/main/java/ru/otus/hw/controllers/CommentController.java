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
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.comment.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.mapper.CommentMapper;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    private final CommentMapper commentMapper;

    @PostMapping("/books/{bookId}/comments")
    public Mono<ResponseEntity<CommentDto>> addComment(
            @PathVariable String bookId,
            @RequestBody CommentRequestDto dto) {
        return bookRepository.findById(bookId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "Book with id='%s' not found".formatted(bookId))))
                .map(book -> {
                    Comment comment = new Comment();
                    comment.setText(dto.text());
                    comment.setBook(book);
                    return comment;
                })
                .flatMap(commentRepository::save)
                .map(commentMapper::toCommentDto)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @DeleteMapping("/comments/{id}")
    public Mono<ResponseEntity<Void>> deleteComment(@PathVariable String id) {
        return commentRepository.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    public record CommentRequestDto(String text) {}
}
