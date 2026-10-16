package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.services.CommentService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/books/{bookId}")
    public String add(@PathVariable Long bookId, @RequestParam String text) {
        if (text != null && !text.isBlank()) {
            commentService.insert(bookId, text);
        }
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @RequestParam Long bookId) {
        commentService.deleteById(id);
        return "redirect:/books/" + bookId;
    }
}
