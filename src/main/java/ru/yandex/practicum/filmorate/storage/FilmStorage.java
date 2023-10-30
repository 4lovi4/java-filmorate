package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilmsFromStorage();

    Film getFilmByIdFromStorage(Long filmId);

    boolean checkFilmIsPresentInStorage(Long filmId, Film film);

    Long addFilmToStorage(Long filmId, Film film);

    boolean checkFilmIsPresentInStorage(Long filmId);

    boolean deleteFilmFromStorage(Long filmId, Film film);

    int deleteFilmFromStorage(Long filmId);

    public void addLikeToFilmInStorage(Long filmId, Long userId);

    Genre getGenreByIdFromStorage(int genreId);

    List<Genre> getAllGenresFromStorage();

    Rating getMpaByIdFromStorage(int mpaId);

    List<Rating> getAllMpa();

    Long getLastFilmIdFromStorage();
}
