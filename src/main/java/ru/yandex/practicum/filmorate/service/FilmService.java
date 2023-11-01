package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
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
        this.filmCounter = this.filmStorage.getLastFilmIdFromStorage();
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilmsFromStorage();
    }

    public Film getFilmById(Long filmId) {
        Film film = filmStorage.getFilmByIdFromStorage(filmId);
        if (Objects.isNull(film)) {
            throw new NotFoundException(String.format(FILM_NOT_FOUND_MESSAGE, filmId));
        }
        return film;
    }

    public Film addNewFilm(Film film) {
        log.info("Запрос на добавление фильма: " + film);
        if (!filmStorage.checkFilmIsPresentInStorage(film.getId(), film)) {
            Long filmIdAdded = filmStorage.addFilmToStorage(null, film);
            film.setId(filmIdAdded);
        } else {
            log.error("Фильм уже добавлен в сервис");
            throw new InstanceAlreadyExistsException("Фильм уже добавлен");
        }
        log.info("Добавлен фильм: " + film);
        return film;
    }

    public Film updateFilm(Film film) {
        log.info("Запрос на изменение фильма: " + film);
        if (filmStorage.checkFilmIsPresentInStorage(film.getId())) {
            filmStorage.addFilmToStorage(film.getId(), film);
        } else {
            log.error("Неизвестный фильм передан для редактирования");
            throw new NotFoundException(String.format(FILM_NOT_FOUND_MESSAGE, film.getId()));
        }
        log.info("Изменён фильм: " + film);
        return film;
    }

    public Film addLikeToFilm(Long filmId, Long userId) {
        if (!filmStorage.checkFilmIsPresentInStorage(filmId)) {
            throw new NotFoundException(String.format(FILM_NOT_FOUND_MESSAGE, filmId));
        }
        filmStorage.addLikeToFilmInStorage(filmId, userId);
        Film film = filmStorage.getFilmByIdFromStorage(filmId);
        return film;
    }

    public Film removeLikeFromFilm(Long filmId, Long userId) {
        if (!filmStorage.checkFilmIsPresentInStorage(filmId)) {
            throw new NotFoundException(String.format(FILM_NOT_FOUND_MESSAGE, filmId));
        }
        Film film = filmStorage.getFilmByIdFromStorage(filmId);
        film.getLikes().remove(userId);
        return film;
    }

    public List<Film> getPopularFilmsByLikes(Long count) {
        count = Objects.isNull(count) ? 10L : count;
        return filmStorage.getAllFilmsFromStorage()
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
        return filmStorage.getMpaByIdFromStorage(mpaId);
    }

    public List<Rating> getAllMpa() {
        return filmStorage.getAllMpa();
    }

    public Genre getGenreById(int genreId) {
        return filmStorage.getGenreByIdFromStorage(genreId);
    }

    public List<Genre> getAllGenres() {
        return filmStorage.getAllGenresFromStorage();
    }

    private Long getNewFilmId() {
        this.filmCounter++;
        return filmCounter;
    }
}
