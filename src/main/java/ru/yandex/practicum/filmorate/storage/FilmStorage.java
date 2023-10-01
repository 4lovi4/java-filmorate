package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film getFilmById(Long filmId);

    boolean checkFilmIsPresent(Long filmId, Film film);

    Long addFilm(Long filmId, Film film);

    boolean checkFilmIsPresent(Long filmId);

    boolean deleteFilm(Long filmId, Film film);

    Film deleteFilm(Long filmId);

    Long getLastFilmId();
}
