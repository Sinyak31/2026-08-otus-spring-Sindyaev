package ru.otus.hw.services;

import ru.otus.hw.models.Comment;

import java.util.List;

public interface CommentService {

    Comment insert(long bookId, String text);

    Comment findById(long id);

    List<Comment> findByBookId(long bookId);

    void deleteById(long id);
}
