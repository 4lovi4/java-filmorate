package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;
import java.util.Objects;

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

    public Film addNewFilm(Film film) {
        log.debug("Запрос на добавление фильма: " + film);
        Long currentId = filmCounter;
        if (filmStorage.checkFilmIsPresent(film.getId(), film)) {
            if (Objects.isNull(film.getId())) {
                film.setId(getFilmId());
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
        if (filmStorage.checkFilmIsPresent(film.getId(), film)) {
            filmStorage.addFilm(film.getId(), film);
        } else {
            log.error("Неизвестный фильм передан для редактирования");
            throw new NotFoundException("В запросе передан неизвестный фильм для редактирования");
        }
        log.debug("Изменён фильм: " + film);
        return film;
    }

    private Long getFilmId() {
        return ++filmCounter;
    }
}
