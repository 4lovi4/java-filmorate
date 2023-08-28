package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;


import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final HashSet<Film> films = new HashSet<>();

    @Autowired
    private FilmService filmService;

    @GetMapping
    public List<Film> findAllFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public Film addNewFilm(@RequestBody Film film) {
        return filmService.addNewFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }
}
