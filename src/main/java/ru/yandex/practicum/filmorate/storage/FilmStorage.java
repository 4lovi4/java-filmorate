package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    void getAllFilms();

    void addFilm(Film film);

    void updateFilm(Film film);
}
