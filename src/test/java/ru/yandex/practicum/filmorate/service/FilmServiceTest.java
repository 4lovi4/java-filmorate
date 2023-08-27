package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class FilmServiceTest {

    @Autowired private FilmService filmService;

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

        filmOne.setId(2L);
        filmOne.setDescription("a direct prequel to the 1982 film");
        filmOne.setDuration(103);

        Film filmUpdate = filmService.updateFilm(filmOne);

        assertEquals(filmOne, filmUpdate);

        List<Film> allFilms = filmService.getAllFilms();
        assertEquals(1, allFilms.size());
    }

    @Test
    @DisplayName("Исключение: при добавлении уже есть такой фильм в сервисе")
    void shouldThrowExceptionOnRepeatAdd() {
        Film filmOne = new Film(1L, "The Thing", "science fiction horror film", LocalDate.of(1982, 6, 25), 109);
        filmService.addNewFilm(filmOne);
        assertThrows(ValidationException.class, () -> filmService.addNewFilm(filmOne));
    }

    @ParameterizedTest
    @DisplayName("Исключение: name при добавлении пустое")
    @ValueSource(strings = {"", "   "})
    void shouldThrowExceptionOnBlankName(String name) {
        Film filmOne = new Film(1L, name, "science fiction horror film", LocalDate.of(1982, 6, 25), 109);
        assertThrows(ValidationException.class, () -> filmService.addNewFilm(filmOne));
    }

    @Test
    @DisplayName("Исключение: description при добавлении более 200 символов")
    void shouldThrowValidationExceptionOnDescriptionLarger200chars() {
        Film filmOne = new Film(1L, "The Thing", "science fiction horror film", LocalDate.of(1982, 6, 25), 109);
        String description = "Production began in the mid-1970s as a faithful adaptation of the novella, following 1951's The Thing from Another World. The Thing went through several directors and writers, each with different ideas on how to approach the story. Filming lasted roughly twelve weeks, beginning in August 1981, and took place on refrigerated sets in Los Angeles as well as in Juneau, Alaska, and Stewart, British Columbia. Of the film's $15 million budget, $1.5 million was spent on Rob Bottin's creature effects, a mixture of chemicals, food products, rubber, and mechanical parts turned by his large team into an alien capable of taking on any form.";
        filmOne.setDescription(description);
        assertTrue(filmOne.getDescription().length() > 200);
        assertThrows(ValidationException.class, () -> filmService.addNewFilm(filmOne));
    }

    @Test
    @DisplayName("Исключение: releaseDate при добавлении не может быть старше 1895-12-28")
    void shouldThrowValidationExceptionOnReleaseDateTooOld() {
        Film filmOne = new Film(1L, "The Thing", "science fiction horror film", LocalDate.of(1882, 6, 25), 109);
        assertThrows(ValidationException.class, () -> filmService.addNewFilm(filmOne));
    }

    @ParameterizedTest
    @DisplayName("Исключение: duration при добавлении должно быть больше 0")
    @ValueSource(ints = {0, -100})
    void shouldThrowValidationExceptionOnReleaseDateTooOld(int duration) {
        Film filmOne = new Film(1L, "The Thing", "science fiction horror film", LocalDate.of(1982, 6, 25), duration);
        assertThrows(ValidationException.class, () -> filmService.addNewFilm(filmOne));
    }


    @Test
    @DisplayName("Исключение: name при изменении нет такого фильма в сервисе")
    void shouldThrowExceptionOnUnknownFilm() {
        Film filmOne = new Film(1L, "The Thing", "science fiction horror film", LocalDate.of(1982, 6, 25), 109);
        assertThrows(ValidationException.class, () -> filmService.updateFilm(filmOne));
    }

    @ParameterizedTest
    @DisplayName("Исключение: name при изменении пустое")
    @ValueSource(strings = {"", "   "})
    void shouldThrowExceptionOnBlankNameUpdate(String name) {
        Film filmOne = new Film(1L, "The Thing", "science fiction horror film", LocalDate.of(1982, 6, 25), 109);
        filmService.addNewFilm(filmOne);
        Film filmTwo = new Film(1L, name, "science fiction horror film", LocalDate.of(1982, 6, 25), 109);
        assertThrows(ValidationException.class, () -> filmService.updateFilm(filmTwo));
    }

    @Test
    @DisplayName("Исключение: description при изменении более 200 символов")
    void shouldThrowValidationExceptionOnDescriptionLarger200charsUpdate() {
        Film filmOne = new Film(1L, "The Thing", "science fiction horror film", LocalDate.of(1982, 6, 25), 109);
        filmService.addNewFilm(filmOne);
        String description = "Production began in the mid-1970s as a faithful adaptation of the novella, following 1951's The Thing from Another World. The Thing went through several directors and writers, each with different ideas on how to approach the story. Filming lasted roughly twelve weeks, beginning in August 1981, and took place on refrigerated sets in Los Angeles as well as in Juneau, Alaska, and Stewart, British Columbia. Of the film's $15 million budget, $1.5 million was spent on Rob Bottin's creature effects, a mixture of chemicals, food products, rubber, and mechanical parts turned by his large team into an alien capable of taking on any form.";
        filmOne.setDescription(description);
        assertTrue(filmOne.getDescription().length() > 200);
        assertThrows(ValidationException.class, () -> filmService.updateFilm(filmOne));
    }

    @Test
    @DisplayName("Исключение: releaseDate при изменении не может быть старше 1895-12-28")
    void shouldThrowValidationExceptionOnReleaseDateTooOldUpdate() {
        Film filmOne = new Film(1L, "The Thing", "science fiction horror film", LocalDate.of(1982, 6, 25), 109);
        filmService.addNewFilm(filmOne);
        Film filmTwo = new Film(1L, "The Thing", "science fiction horror film", LocalDate.of(1882, 6, 25), 109);
        assertThrows(ValidationException.class, () -> filmService.updateFilm(filmTwo));
    }

    @ParameterizedTest
    @DisplayName("Исключение: duration при изменении должно быть больше 0")
    @ValueSource(ints = {0, -100})
    void shouldThrowValidationExceptionOnDurationUpdate(int duration) {
        Film filmOne = new Film(1L, "The Thing", "science fiction horror film", LocalDate.of(1982, 6, 25), 102);
        filmService.addNewFilm(filmOne);
        Film filmTwo = new Film(1L, "The Thing", "science fiction horror film", LocalDate.of(1982, 6, 25), duration);
        assertThrows(ValidationException.class, () -> filmService.updateFilm(filmTwo));
    }
}
