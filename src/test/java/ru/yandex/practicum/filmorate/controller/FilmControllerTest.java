package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.FilmorateApplicationTests;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class FilmControllerTest extends FilmorateApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    FilmService filmService;

    @Value("${server.port}")
    String serverPort;

    private final String serverAddress = "http://localhost";

    private final String endpoint = "/films";

    @Test
    @DisplayName("При запросе всех фильмов возвращается пустой список")
    void shouldReturnEmptyAllFilms() throws Exception {
        Mockito.doReturn(new ArrayList<Film>()).when(filmService).getAllFilms();
        mockMvc
                .perform(MockMvcRequestBuilders.get(serverAddress + ":" + serverPort + endpoint))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    @DisplayName("При запросе всех фильмов вернётся список из одного фильма")
    void shouldReturnOneFilm() throws Exception {
        Film filmOne = new Film(1L, "1111", "dddd", LocalDate.of(1975, 5, 1), 102);
        ArrayList<Film> allFilms = new ArrayList<>();
        allFilms.add(filmOne);

        String filmsPayload = mapper.writeValueAsString(allFilms);

        Mockito.doReturn(allFilms).when(filmService).getAllFilms();

        mockMvc
                .perform(MockMvcRequestBuilders.get(serverAddress + ":" + serverPort + endpoint)
                        .accept("application/json")
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().string(filmsPayload));
    }

    @Test
    @DisplayName("Добавление нового фильма")
    void shouldAddNewFilm() throws Exception {
        Film filmOne = new Film(1L, "1111", "dddd", LocalDate.of(1975, 5, 1), 102);

        String filmOnePayload = mapper.writeValueAsString(filmOne);

        Mockito.doReturn(filmOne).when(filmService).addNewFilm(filmOne);

        mockMvc
                .perform(MockMvcRequestBuilders.post(serverAddress + ":" + serverPort + endpoint)
                        .content(filmOnePayload)
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(filmOnePayload));
    }

    @Test
    @DisplayName("Изменение добавленного фильма")
    void shouldUpdateFilm() throws Exception {
        Film filmOne = new Film(1L, "2222", "cccc", LocalDate.of(1975, 6, 1), 105);

        String filmOnePayload = mapper.writeValueAsString(filmOne);

        Mockito.doReturn(filmOne).when(filmService).updateFilm(filmOne);

        mockMvc
                .perform(MockMvcRequestBuilders.put(serverAddress + ":" + serverPort + endpoint)
                        .content(filmOnePayload)
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(filmOnePayload));
    }

    @ParameterizedTest
    @DisplayName("Валидация поля name")
    @ValueSource(strings = {"", "   "})
    void shouldReturnErrorOnEmptyNameCreate(String name) throws Exception {
        Film filmOne = new Film(null, name, "cccc", LocalDate.of(1975, 6, 1), 105);

        String filmOnePayload = mapper.writeValueAsString(filmOne);

        mockMvc
                .perform(MockMvcRequestBuilders.put(serverAddress + ":" + serverPort + endpoint)
                        .content(filmOnePayload)
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value(serverAddress + ":" + serverPort + endpoint))
                .andExpect(jsonPath("$.error").value("Ошибка валидации при запросе!"));
    }

    @Test
    @DisplayName("Валидация поля name = null")
    void shouldReturnErrorOnNullNameCreate() throws Exception {
        Film filmOne = new Film(null, null, "cccc", LocalDate.of(1975, 6, 1), 105);

        String filmOnePayload = mapper.writeValueAsString(filmOne);

        mockMvc
                .perform(MockMvcRequestBuilders.put(serverAddress + ":" + serverPort + endpoint)
                        .content(filmOnePayload)
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value(serverAddress + ":" + serverPort + endpoint))
                .andExpect(jsonPath("$.error").value("Ошибка валидации при запросе!"));
    }

    @Test
    @DisplayName("Валидация поля description более 200 символов при добавлении")
    void shouldThrowValidationExceptionOnDescriptionLarger200chars() throws Exception {
        Film filmOne = new Film(1L, "The Thing", "science fiction horror film", LocalDate.of(1982, 6, 25), 109);
        String description = "Production began in the mid-1970s as a faithful adaptation of the novella, following 1951's The Thing from Another World. The Thing went through several directors and writers, each with different ideas on how to approach the story. Filming lasted roughly twelve weeks, beginning in August 1981, and took place on refrigerated sets in Los Angeles as well as in Juneau, Alaska, and Stewart, British Columbia. Of the film's $15 million budget, $1.5 million was spent on Rob Bottin's creature effects, a mixture of chemicals, food products, rubber, and mechanical parts turned by his large team into an alien capable of taking on any form.";
        filmOne.setDescription(description);

        String filmOnePayload = mapper.writeValueAsString(filmOne);

        mockMvc
                .perform(MockMvcRequestBuilders.put(serverAddress + ":" + serverPort + endpoint)
                        .content(filmOnePayload)
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value(serverAddress + ":" + serverPort + endpoint))
                .andExpect(jsonPath("$.error").value("Ошибка валидации при запросе!"));
    }
}
