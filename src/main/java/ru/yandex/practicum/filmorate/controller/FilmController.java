package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final HashSet<Film> films = new HashSet<>();

    @Autowired
    FilmService filmService;

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

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ModelAndView onValidationError(HttpServletRequest request, Exception exception) {
        log.error("Request: " + request.getRequestURL() + " raised " + exception);
        ModelAndView errorResponse = new ModelAndView();
        errorResponse.addObject("exception", exception);
        errorResponse.addObject("url", request.getRequestURL());
        errorResponse.setViewName("error");
        return errorResponse;
    }
}
