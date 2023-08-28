package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class FilmService {

    @Autowired
    FilmValidator filmValidator;

    private final HashSet<Film> films;

    private Long filmCounter;

    public FilmService() {
        filmCounter = 0L;
        films = new HashSet<>();
    }

    public FilmService(HashSet<Film> films) {
        filmCounter = (long) films.size();
        this.films = films;
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films);
    }

    public Film addNewFilm(Film film) {
        filmValidator.validate(film);
        if (!films.contains(film)) {
            if (Objects.isNull(film.getId()) || films.stream().anyMatch(f -> f.getId() == film.getId())) {
                filmCounter++;
                film.setId(filmCounter);
            }
            films.add(film);
        } else {
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
        } else {
            log.error("Неизвестный фильм передан для редактирования");
            throw new NotFoundException("В запросе передан неизвестный фильм для редактирования");
        }
        return film;
    }
}
