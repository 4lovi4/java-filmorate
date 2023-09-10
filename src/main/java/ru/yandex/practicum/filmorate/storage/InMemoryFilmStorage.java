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

    public void addFilm(Long filmId, Film film) {
        filmsInMemory.put(filmId, film);
    }

    public boolean checkFilmIsPresent(Long filmId, Film film) {
        return filmsInMemory.containsKey(filmId) &&
                filmsInMemory.containsValue(film);
    }

    public boolean deleteFilm(Long filmId, Film film) {
        return filmsInMemory.remove(filmId, film);
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
