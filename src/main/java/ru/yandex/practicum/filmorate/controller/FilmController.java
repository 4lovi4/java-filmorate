package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.NotFoundException;
import ru.yandex.practicum.filmorate.service.UserService;

import static ru.yandex.practicum.filmorate.service.UserService.USER_NOT_FOUND_MESSAGE;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class FilmController {

    private FilmService filmService;
    private UserService userService;

    @Autowired
    public FilmController(FilmService filmService,
                          UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping("/films")
    public List<Film> findAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/films/{id}")
    public Film findFilmById(@PathVariable("id") Long filmId) {
        return filmService.getFilmById(filmId);
    }

    @PostMapping("/films")
    public Film addNewFilm(@RequestBody @Valid Film film) {
        return filmService.addNewFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/films/{id}/like/{userId}")
    public Film addLikeToFilm(@PathVariable("id") Long filmId,
                              @PathVariable("userId") Long userId) {
        if (!userService.isUserPresent(userId)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId));
        }
        return filmService.addLikeToFilm(filmId, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public Film removeLikeFromFilm(@PathVariable("id") Long filmId,
                              @PathVariable("userId") Long userId) {
        if (!userService.isUserPresent(userId)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId));
        }
        return filmService.removeLikeFromFilm(filmId, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> findPopularFilms(@RequestParam(name = "count", required = false) Long filmsCount) {
        return filmService.getPopularFilmsByLikes(filmsCount);
    }

    @GetMapping("/mpa")
    public List<Rating> findAllMpa() {
        return filmService.getAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public Rating findMpaById(@PathVariable("id") Integer mpaId) {
        return filmService.getMpaById(mpaId);
    }

    @GetMapping("/genres")
    public List<Genre> findAllGenres() {
        return filmService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre findGenreById(@PathVariable("id") Integer genreId) {
        return filmService.getGenreById(genreId);
    }
}
