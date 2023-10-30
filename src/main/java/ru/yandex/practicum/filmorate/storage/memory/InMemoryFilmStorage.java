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
    public List<Film> getAllFilmsFromStorage() {
        return new ArrayList<>(filmsInMemory.values());
    }

    @Override
    public Film getFilmByIdFromStorage(Long filmId) {
        return filmsInMemory.get(filmId);
    }

    @Override
    public Long addFilmToStorage(Long filmId, Film film) {
        filmsInMemory.put(filmId, film);
        return filmId;
    }

    @Override
    public boolean checkFilmIsPresentInStorage(Long filmId, Film film) {
        return filmsInMemory.containsKey(filmId) ||
                filmsInMemory.containsValue(film);
    }

    @Override
    public boolean checkFilmIsPresentInStorage(Long filmId) {
        return filmsInMemory.containsKey(filmId);
    }

    @Override
    public boolean deleteFilmFromStorage(Long filmId, Film film) {
        return filmsInMemory.remove(filmId, film);
    }

    @Override
    public int deleteFilmFromStorage(Long filmId) {
        return Objects.isNull(filmsInMemory.remove(filmId)) ? 0 : 1 ;
    }

    @Override
    public void addLikeToFilmInStorage(Long filmId, Long userId) {
        filmsInMemory.get(filmId).getLikes().add(userId);
    }

    @Override
    public Genre getGenreByIdFromStorage(int genreId) {
        return Genre.valueOfId(genreId);
    }

    @Override
    public List<Genre> getAllGenresFromStorage() {
        return List.of(Genre.values());
    }

    @Override
    public Rating getMpaByIdFromStorage(int mpaId) {
        return Rating.valueOfId(mpaId);
    }

    @Override
    public List<Rating> getAllMpa() {
        return List.of(Rating.values());
    }

    public Long getLastFilmIdFromStorage() {
        return filmsInMemory.isEmpty() ? 0L :
                filmsInMemory
                        .keySet()
                        .stream()
                        .max(Long::compare)
                        .get();
    }
}
