package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film getFilmById(Long filmId);

    boolean checkFilmIsPresent(Long filmId, Film film);

    Long addFilm(Long filmId, Film film);

    boolean checkFilmIsPresent(Long filmId);

    boolean deleteFilm(Long filmId, Film film);

    int deleteFilm(Long filmId);

    Genre getGenreById(int genreId);

     List<Genre> getAllGenres();

    Rating getMpaById(int mpaId);

    List<Rating> getAllMpa();

    Long getLastFilmId();
}
