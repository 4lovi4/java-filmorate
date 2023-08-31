package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.FilmorateApplicationTests;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmServiceTest extends FilmorateApplicationTests {

    @Autowired
    private FilmService filmService;

    @Test
    @DisplayName("Получение пустого списка фильмов")
    void shouldReturnEmptyFilms() {
        List<Film> allFilms = filmService.getAllFilms();
        assertEquals(0, allFilms.size());
    }

    @Test
    @DisplayName("Получение списка фильмов")
    void shouldReturnAllFilms() {
        Film filmOne = new Film(1L, "The Thing", "science fiction horror film", LocalDate.of(1982, 6, 25), 109);
        Film filmTwo = new Film(2L, "Alien", "science fiction horror film", LocalDate.of(1979, 9, 6), 116);

        filmService.addNewFilm(filmOne);
        filmService.addNewFilm(filmTwo);

        List<Film> allFilms = filmService.getAllFilms();
        assertEquals(2, allFilms.size());
    }

    @Test
    @DisplayName("Добавление нового фильма")
    void shouldAddNewFilm() {
        Film filmOne = new Film(1L, "The Thing", "science fiction horror film", LocalDate.of(1982, 6, 25), 109);

        Film filmAdded = filmService.addNewFilm(filmOne);

        assertEquals(filmOne, filmAdded);

        List<Film> allFilms = filmService.getAllFilms();
        assertEquals(1, allFilms.size());
    }

    @Test
    @DisplayName("Редактирование добавленного фильма")
    void shouldUpdateFilm() {
        Film filmOne = new Film(1L, "The Thing", "science fiction horror film", LocalDate.of(1982, 6, 25), 109);

        filmService.addNewFilm(filmOne);

        Film filmTwo = new Film(1L, "The Thing 2011", "a direct prequel to the 1982 film", LocalDate.of(2011, 10, 6), 103);

        Film filmUpdate = filmService.updateFilm(filmTwo);

        assertEquals(filmTwo, filmUpdate);
        List<Film> allFilms = filmService.getAllFilms();
        assertEquals(1, allFilms.size());
        assertEquals(filmTwo, allFilms.get(0));
    }

    @Test
    @DisplayName("Исключение: при добавлении уже есть такой фильм в сервисе")
    void shouldThrowExceptionOnRepeatAdd() {
        Film filmOne = new Film(1L, "The Thing", "science fiction horror film", LocalDate.of(1982, 6, 25), 109);
        filmService.addNewFilm(filmOne);
        assertThrows(InstanceAlreadyExistsException.class, () -> filmService.addNewFilm(filmOne));
    }

    @Test
    @DisplayName("Исключение: name при изменении нет такого фильма в сервисе")
    void shouldThrowExceptionOnUnknownFilm() {
        Film filmOne = new Film(1L, "The Thing", "science fiction horror film", LocalDate.of(1982, 6, 25), 109);
        assertThrows(NotFoundException.class, () -> filmService.updateFilm(filmOne));
    }
}
