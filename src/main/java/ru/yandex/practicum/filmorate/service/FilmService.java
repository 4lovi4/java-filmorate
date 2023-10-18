package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    private Long filmCounter;

    private static final String FILM_NOT_FOUND_MESSAGE = "Фильм id = %d не найден";

    @Autowired
    public FilmService(@Qualifier("dataBaseFilmStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
        this.filmCounter = this.filmStorage.getLastFilmId();
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (Objects.isNull(film)) {
            throw new NotFoundException(String.format(FILM_NOT_FOUND_MESSAGE, filmId));
        }
        return film;
    }

    public Film addNewFilm(Film film) {
        log.info("Запрос на добавление фильма: " + film);
        if (!filmStorage.checkFilmIsPresent(film.getId(), film)) {
            if (Objects.isNull(film.getId())) {
                film.setId(getNewFilmId());
            }
            filmStorage.addFilm(film.getId(), film);
        } else {
            log.error("Фильм уже добавлен в сервис");
            throw new InstanceAlreadyExistsException("Фильм уже добавлен");
        }
        log.info("Добавлен фильм: " + film);
        return film;
    }

    public Film updateFilm(Film film) {
        log.info("Запрос на изменение фильма: " + film);
        if (filmStorage.checkFilmIsPresent(film.getId())) {
            filmStorage.addFilm(film.getId(), film);
        } else {
            log.error("Неизвестный фильм передан для редактирования");
            throw new NotFoundException(String.format(FILM_NOT_FOUND_MESSAGE, film.getId()));
        }
        log.info("Изменён фильм: " + film);
        return film;
    }

    public Film addLikeToFilm(Long filmId, Long userId) {
        if (!filmStorage.checkFilmIsPresent(filmId)) {
            throw new NotFoundException(String.format(FILM_NOT_FOUND_MESSAGE, filmId));
        }
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
        return film;
    }

    public Film removeLikeFromFilm(Long filmId, Long userId) {
        if (!filmStorage.checkFilmIsPresent(filmId)) {
            throw new NotFoundException(String.format(FILM_NOT_FOUND_MESSAGE, filmId));
        }
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().remove(userId);
        return film;
    }

    public List<Film> getPopularFilmsByLikes(Long count) {
        count = Objects.isNull(count) ? 10L : count;
        return filmStorage.getAllFilms()
                .stream()
                .sorted((f1, f2) ->
                        Integer.compare(
                                f2.getLikes().size(),
                                f1.getLikes().size()
                        ))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Rating getMpaById(int mpaId) {
        return null;
    }

    private Long getNewFilmId() {
        this.filmCounter++;
        return filmCounter;
    }
}
