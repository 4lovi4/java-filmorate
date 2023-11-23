package ru.yandex.practicum.filmorate.storage.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.NotFoundException;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"})
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class DataBaseFilmStorageTest {
    JdbcTemplate jdbcTemplate;

    FilmStorage dataBaseFilmStorage;

    @Autowired
    DataBaseFilmStorageTest(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    Film filmOne;
    Film filmTwo;

    @BeforeEach
    public void setup() {
        filmOne = new Film("Marcipan", "sweet story fo nobody",
                LocalDate.of(1990, 12, 1), 91);
        filmOne.setMpa(Rating.R);
        filmTwo = new Film("Crocko", "story",
                LocalDate.of(1994, 12, 1), 95);
        filmTwo.setMpa(Rating.G);
        dataBaseFilmStorage = new DataBaseFilmStorage(jdbcTemplate);
    }

    @Test
    @DisplayName("Из пустой базы возвращается пустой список фильмов")
    void shouldGetEmptyFilmListFromDb() {
        List<Film> allFilms = dataBaseFilmStorage.getAllFilmsFromStorage();
        assertTrue(allFilms.isEmpty());
    }

    @Test
    @DisplayName("Из БД возвращается список всех добавленных фильмов")
    void shouldReturnAllFilmsFromDb() {
        dataBaseFilmStorage.addFilmToStorage(null, filmOne);
        dataBaseFilmStorage.addFilmToStorage(null, filmTwo);
        List<Film> allFilms = dataBaseFilmStorage.getAllFilmsFromStorage();
        assertEquals(2, allFilms.size());
        assertEquals(filmOne, allFilms.get(0));
        assertEquals(filmTwo, allFilms.get(1));
    }

    @Test
    @DisplayName("В базу добавляется новая запись фильма")
    void shouldAddNewFilmToDb() {
        Long filmIdAdded = dataBaseFilmStorage.addFilmToStorage(null, filmOne);
        assertEquals(1L, filmIdAdded);
        List<Film> allFilms = dataBaseFilmStorage.getAllFilmsFromStorage();
        assertEquals(1, allFilms.size());
        Film filmOneAdded = allFilms.get(0);
        assertEquals(1L, filmOneAdded.getId());
        assertEquals(filmOne, filmOneAdded);
    }

    @Test
    @DisplayName("Получение фильма по id из БД")
    void shouldReturnFilmById() {
        Long filmIdAdded = dataBaseFilmStorage.addFilmToStorage(null, filmOne);
        Film filmAdded = dataBaseFilmStorage.getFilmByIdFromStorage(filmIdAdded);
        assertEquals(filmOne, filmAdded);
        assertEquals(filmIdAdded, filmAdded.getId());
    }

    @Test
    @DisplayName("Исключение NotFoundException если нет фильма с заданным id")
    void shouldThrowNotFoundExceptionOnWrongFilmId() {
        Long wrongFilmId = 999L;
        assertThrows(NotFoundException.class, ()-> dataBaseFilmStorage.getFilmByIdFromStorage(wrongFilmId));
    }

    @Test
    @DisplayName("Метод проверки наличия фильма в БД должен вернуть true если фильм был предварительно добавлен")
    void shouldReturnTrueOnPresentFilm() {
        assertFalse(dataBaseFilmStorage.checkFilmIsPresentInStorage(1L, filmOne));
        Long filmIdAdded = dataBaseFilmStorage.addFilmToStorage(null, filmOne);
        assertTrue(dataBaseFilmStorage.checkFilmIsPresentInStorage(filmIdAdded));
    }

    @Test
    @DisplayName("Изменение фильма в БД")
    void shouldUpdateFilmInStorage() {
        Long filmIdAdded = dataBaseFilmStorage.addFilmToStorage(null, filmOne);
        HashSet<Long> changeLikes = new HashSet<>();
        changeLikes.add(1L);
        changeLikes.add(2L);
        changeLikes.add(3L);
        HashSet<Genre> changeGenres = new HashSet<>();
        changeGenres.add(Genre.COMEDY);
        changeGenres.add(Genre.ACTION);
        Film filmChanged = new Film(filmIdAdded, "the frog", "tale",
                LocalDate.of(1980, 10, 1), 80,
                changeLikes, changeGenres, Rating.PG);
        assertEquals(1, dataBaseFilmStorage.updateFilmInStorage(filmChanged),
                "метод updateFilmInStorage() не вернул 1");
        Film filmUpdated = dataBaseFilmStorage.getFilmByIdFromStorage(filmIdAdded);
        assertEquals(filmChanged, filmUpdated);
    }

    @Test
    @DisplayName("Удаление добавленного фильма по id из БД")
    void shouldDeleteFilm() {
        Long filmIdAdded = dataBaseFilmStorage.addFilmToStorage(null, filmOne);
        assertEquals(1, dataBaseFilmStorage.deleteFilmFromStorage(filmIdAdded),
                "deleteFilmFromStorage() не вернул 1");
        List<Film> checkedFilms = dataBaseFilmStorage.getAllFilmsFromStorage()
                .stream()
                .filter((f) -> f.getId().equals(filmIdAdded))
                .collect(Collectors.toList());
        assertTrue(checkedFilms.isEmpty());
    }

    @Test
    @DisplayName("Получение последнего id фильма добавленного в БД")
    void shouldReturnLastFilmId() {
        dataBaseFilmStorage.addFilmToStorage(null, filmOne);
        assertEquals(1, dataBaseFilmStorage.getLastFilmIdFromStorage());
    }

    @Test
    @DisplayName("Получение id фильма = 0 из пустой таблицы films")
    void shouldReturnZeroFilmId() {
        assertEquals(0, dataBaseFilmStorage.getLastFilmIdFromStorage());
    }

    @Test
    @DisplayName("Добавление лайка для фильма")
    void shouldAddLikeToFilm()  {
        Long userLikeId = 1L;
        Long filmIdAdded = dataBaseFilmStorage.addFilmToStorage(null, filmOne);
        dataBaseFilmStorage.addLikeToFilmInStorage(filmIdAdded, userLikeId);
        Film filmLiked = dataBaseFilmStorage.getFilmByIdFromStorage(filmIdAdded);
        assertEquals(1, filmLiked.getLikes().size());
        assertTrue(filmLiked.getLikes().contains(userLikeId));
    }
}
