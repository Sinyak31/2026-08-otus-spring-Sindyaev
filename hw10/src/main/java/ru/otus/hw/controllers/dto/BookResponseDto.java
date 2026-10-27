package ru.otus.hw.controllers.dto;

import java.util.List;
import java.util.Set;

public record BookResponseDto(
    Long id,
    String title,
    Long authorId,
    String authorName,
    Set<Long> genresIds,
    List<CommentResponseDto> comments
) {}
