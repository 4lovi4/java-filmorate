package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.*;

@Service
@Slf4j
public class FilmService {

    @Autowired
    FilmValidator filmValidator;

    private final HashMap<Long, Film> films;

    private Long filmCounter;

    public FilmService() {
        filmCounter = 0L;
        films = new HashMap<>();
    }

    public FilmService(HashMap<Long, Film> films) {
        filmCounter = (long) films.keySet().size();
        this.films = films;
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Film addNewFilm(Film film) {
        filmValidator.validate(film);
        Long currentId = filmCounter;
        if (!films.containsKey(film.getId()) || !films.values().contains(film)) {
            if (Objects.isNull(film.getId())) {
                filmCounter++;
                currentId = filmCounter;
                film.setId(currentId);
            } else {
                currentId = film.getId();
            }
            films.put(currentId, film);
        } else {
            log.error("Фильм уже добавлен в сервис");
            throw new ValidationException("Фильм уже добавлен");
        }
        return film;
    }

    public Film updateFilm(Film film) {
        filmValidator.validate(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else {
            log.error("Неизвестный фильм передан для редактирования");
            throw new NotFoundException("В запросе передан неизвестный фильм для редактирования");
        }
        return film;
    }
}
