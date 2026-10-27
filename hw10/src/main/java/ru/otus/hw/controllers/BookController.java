package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.controllers.dto.BookDto;
import ru.otus.hw.controllers.dto.BookResponseDto;
import ru.otus.hw.controllers.dto.CommentResponseDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.BookService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    @GetMapping
    public List<BookResponseDto> getAllBooks() {
        return bookService.findAll().stream()
                .map(this::toResponseDto)
                .collect(toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDto> getBookById(@PathVariable Long id) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(id)));
        return ResponseEntity.ok(toResponseDto(book));
    }

    @PostMapping
    public ResponseEntity<BookResponseDto> createBook(@RequestBody BookDto dto) {
        Book book = bookService.insert(dto.title(), dto.authorId(), dto.genresIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponseDto(book));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponseDto> updateBook(@PathVariable Long id, @RequestBody BookDto dto) {
        Book book = bookService.update(id, dto.title(), dto.authorId(), dto.genresIds());
        return ResponseEntity.ok(toResponseDto(book));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> handleEntityNotFound() {
        return ResponseEntity.notFound().build();
    }

    private BookResponseDto toResponseDto(Book book) {
        List<CommentResponseDto> comments = book.getComments().stream()
                .map(comment -> new CommentResponseDto(comment.getId(), comment.getText()))
                .collect(toList());

        return new BookResponseDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor().getId(),
                book.getAuthor().getFullName(),
                book.getGenres().stream().map(g -> g.getId()).collect(java.util.stream.Collectors.toSet()),
                comments
        );
    }
}
