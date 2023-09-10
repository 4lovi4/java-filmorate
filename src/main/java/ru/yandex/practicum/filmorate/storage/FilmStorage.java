package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilms();

    void addFilm(Long filmId, Film film);

    boolean checkFilmIsPresent(Long filmId, Film film);

    boolean deleteFilm(Long filmId, Film film);

    Long getLastFilmId();
}
