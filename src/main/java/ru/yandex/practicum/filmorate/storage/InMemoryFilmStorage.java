package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private HashMap<Long, Film> filmsInMemory;

    public InMemoryFilmStorage() {
        this.filmsInMemory = new HashMap<>();
    }

    public List<Film> getAllFilms() {
        return new ArrayList<>(filmsInMemory.values());
    }

    public Film getFilmById(Long filmId) {
        return filmsInMemory.get(filmId);
    }

    public void addFilm(Long filmId, Film film) {
        filmsInMemory.put(filmId, film);
    }

    public boolean checkFilmIsPresent(Long filmId, Film film) {
        return filmsInMemory.containsKey(filmId) ||
                filmsInMemory.containsValue(film);
    }

    public boolean checkFilmIsPresent(Long filmId) {
        return filmsInMemory.containsKey(filmId);
    }

    public boolean deleteFilm(Long filmId, Film film) {
        return filmsInMemory.remove(filmId, film);
    }

    public Film deleteFilm(Long filmId) {
        return filmsInMemory.remove(filmId);
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
