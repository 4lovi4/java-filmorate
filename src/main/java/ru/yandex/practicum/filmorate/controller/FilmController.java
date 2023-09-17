package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.NotFoundException;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
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

    @GetMapping
    public List<Film> findAllFilms() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable("id") Long filmId) {
        return filmService.getFilmById(filmId);
    }

    @PostMapping
    public Film addNewFilm(@RequestBody @Valid Film film) {
        return filmService.addNewFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLikeToFilm(@PathVariable("id") Long filmId,
                              @PathVariable("userId") Long userId) {
        if (!userService.isUserPresent(userId)) {
            throw new NotFoundException(String.format("Пользователь id %d не найден", userId));
        }
        return filmService.addLikeToFilm(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLikeFromFilm(@PathVariable("id") Long filmId,
                              @PathVariable("userId") Long userId) {
        if (!userService.isUserPresent(userId)) {
            throw new NotFoundException(String.format("Пользователь id %d не найден", userId));
        }
        return filmService.removeLikeFromFilm(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> findPopularFilms(@RequestParam(name = "count", required = false) Long filmsCount) {
        return filmService.getPopularFilmsByLikes(filmsCount);
    }

}
