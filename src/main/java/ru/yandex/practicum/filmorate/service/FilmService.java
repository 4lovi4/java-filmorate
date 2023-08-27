package ru.yandex.practicum.filmorate.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class FilmService {

    @Autowired
    FilmValidator filmValidator;

    private final HashSet<Film> films;

    public FilmService() {
        films = new HashSet<>();
    }

    public FilmService(HashSet<Film> films) {
        this.films = films;
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films);
    }

    public Film addNewFilm(Film film) {
        filmValidator.validate(film);
        if (!films.contains(film)) {
            films.add(film);
        }
        else {
            log.error("Фильм уже добавлен в сервис");
            throw new ValidationException("Фильм уже добавлен");
        }
        return film;
    }

    public Film updateFilm(Film film) {
        filmValidator.validate(film);
        if (films.contains(film)) {
            films.remove(film);
            films.add(film);
        }
        else {
            log.error("Неизвестный фильм передан для редактирования");
            throw new ValidationException("В запросе передан неизвестный фильм для редактирования");
        }
        return film;
    }
}
