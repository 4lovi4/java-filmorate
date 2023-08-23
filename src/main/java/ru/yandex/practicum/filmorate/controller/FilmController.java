package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final HashSet<Film> films = new HashSet<>();
    private static final LocalDate oldestReleaseDate = LocalDate
            .parse("1895-12-28", DateTimeFormatter.ISO_LOCAL_DATE);

    @GetMapping
    public List<Film> getAllFilms() {
        return new ArrayList<>(films);
    }

    @PostMapping
    public Film addNewFilm(@RequestBody Film film) {
        validateFilm(film);
        films.add(film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        validateFilm(film);
        if (films.contains(film)) {
            films.remove(film);
            films.add(film);
        }
        else {
            films.add(film);
        }
        return film;
    }

    private void validateFilm(Film film) {
        if (film.getName().isEmpty()) {
            log.error("Поле name пустое");
            throw new ValidationException("Поле name не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            log.error("Длина описания фильма в поле description больше 200 символов");
            throw new ValidationException("Длина описания фильма в поле description не может быть больше 200 символов");
        }

        if (film.getReleaseDate().isBefore(oldestReleaseDate)) {
            log.error("Дата релиза фильма раньше предельной даты в истории кинематографа " +
                    oldestReleaseDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            throw new ValidationException("Дата выпуска фильма старше " +
                    oldestReleaseDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма duration = " + film.getDuration() +
                    " должна быть > 0");
            throw new ValidationException("Продолжительность фильма duration должна быть положительной");
        }
    }
}
