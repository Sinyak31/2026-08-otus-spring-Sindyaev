package ru.otus.hw.controllers.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record BookDto(
    @NotBlank String title,
    @NotNull Long authorId,
    Set<Long> genresIds
) {}
