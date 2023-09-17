package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;

    private Long filmCounter;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
        this.filmCounter = this.filmStorage.getLastFilmId();
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long filmId) {
        Film film = filmStorage.getFilmById(filmId);
        if (Objects.isNull(film)) {
            throw new NotFoundException("Не найден фильм id = " + filmId);
        }
        return film;
    }

    public Film addNewFilm(Film film) {
        log.debug("Запрос на добавление фильма: " + film);
        if (!filmStorage.checkFilmIsPresent(film.getId(), film)) {
            if (Objects.isNull(film.getId())) {
                film.setId(getNewFilmId());
            }
            filmStorage.addFilm(film.getId(), film);
        } else {
            log.error("Фильм уже добавлен в сервис");
            throw new InstanceAlreadyExistsException("Фильм уже добавлен");
        }
        log.debug("Добавлен фильм: " + film);
        return film;
    }

    public Film updateFilm(Film film) {
        log.debug("Запрос на изменение фильма: " + film);
        if (filmStorage.checkFilmIsPresent(film.getId())) {
            filmStorage.addFilm(film.getId(), film);
        } else {
            log.error("Неизвестный фильм передан для редактирования");
            throw new NotFoundException("В запросе передан неизвестный фильм для редактирования");
        }
        log.debug("Изменён фильм: " + film);
        return film;
    }

    public Film addLikeToFilm(Long filmId, Long userId) {
        if (!filmStorage.checkFilmIsPresent(filmId)) {
            throw new NotFoundException(String.format("Фильм id %d не найден", filmId));
        }
        Film film = filmStorage.getFilmById(filmId);
        film.getLikes().add(userId);
        return film;
    }

    public Film removeLikeFromFilm(Long filmId, Long userId) {
        if (!filmStorage.checkFilmIsPresent(filmId)) {
            throw new NotFoundException(String.format("Фильм id %d не найден", filmId));
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

    private Long getNewFilmId() {
        this.filmCounter++;
        return filmCounter;
    }
}
