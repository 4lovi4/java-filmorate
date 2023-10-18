package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> filmsInMemory;

    public InMemoryFilmStorage() {
        this.filmsInMemory = new HashMap<>();
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(filmsInMemory.values());
    }

    @Override
    public Film getFilmById(Long filmId) {
        return filmsInMemory.get(filmId);
    }

    @Override
    public Long addFilm(Long filmId, Film film) {
        filmsInMemory.put(filmId, film);
        return filmId;
    }

    @Override
    public boolean checkFilmIsPresent(Long filmId, Film film) {
        return filmsInMemory.containsKey(filmId) ||
                filmsInMemory.containsValue(film);
    }

    @Override
    public boolean checkFilmIsPresent(Long filmId) {
        return filmsInMemory.containsKey(filmId);
    }

    @Override
    public boolean deleteFilm(Long filmId, Film film) {
        return filmsInMemory.remove(filmId, film);
    }

    @Override
    public int deleteFilm(Long filmId) {
        return Objects.isNull(filmsInMemory.remove(filmId)) ? 0 : 1 ;
    }

    @Override
    public Genre getGenreById(int genreId) {
        return Genre.valueOfId(genreId);
    }

    @Override
    public List<Genre> getAllGenres() {
        return List.of(Genre.values());
    }

    @Override
    public Rating getMpaById(int mpaId) {
        return Rating.valueOfId(mpaId);
    }

    @Override
    public List<Rating> getAllMpa() {
        return List.of(Rating.values());
    }

    public Long getLastFilmId() {
        return filmsInMemory.isEmpty() ? 0L :
                filmsInMemory
                        .keySet()
                        .stream()
                        .max(Long::compare)
                        .get();
    }
}
