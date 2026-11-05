package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.otus.hw.dto.author.AuthorDto;
import ru.otus.hw.dto.genre.GenreDto;
import ru.otus.hw.mapper.AuthorMapper;
import ru.otus.hw.mapper.GenreMapper;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.GenreRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthorGenreController {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final AuthorMapper authorMapper;

    private final GenreMapper genreMapper;

    @GetMapping("/authors")
    public Flux<AuthorDto> getAllAuthors() {
        return authorRepository.findAll().map(authorMapper::toAuthorDto);
    }

    @GetMapping("/genres")
    public Flux<GenreDto> getAllGenres() {
        return genreRepository.findAll().map(genreMapper::toGenreDto);
    }
}
