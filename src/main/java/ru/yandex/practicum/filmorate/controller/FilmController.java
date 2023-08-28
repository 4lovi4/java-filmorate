package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.NotFoundException;
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
    ResponseEntity onValidationError(HttpServletRequest request, Exception exception) {
        log.error("Request: " + request.getRequestURL() + " raised " + exception);
        String payload = "{\"path\":" + "\"" + request.getRequestURL() + "\"" + ",\"error\":" + "\"" + exception + "\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        ResponseEntity errorResponse = new ResponseEntity<>(payload, headers, HttpStatus.BAD_REQUEST);
        return errorResponse;
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity onNotFoundError(HttpServletRequest request, Exception exception) {
        log.error("Request: " + request.getRequestURL() + " raised " + exception);
        String payload = "{\"path\":" + "\"" + request.getRequestURL() + "\"" + ",\"error\":" + "\"" + exception + "\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        ResponseEntity errorResponse = new ResponseEntity<>(payload, headers, HttpStatus.NOT_FOUND);
        return errorResponse;
    }
}
