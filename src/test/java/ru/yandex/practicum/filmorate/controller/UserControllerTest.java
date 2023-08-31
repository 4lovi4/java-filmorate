package ru.yandex.practicum.filmorate.controller;

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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.FilmorateApplicationTests;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@AutoConfigureMockMvc
class UserControllerTest extends FilmorateApplicationTests {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Value("${server.port}")
    String serverPort;

    private final String serverAddress = "http://localhost";

    private final String endpoint = "/users";

    @Test
    @DisplayName("При запросе всех пользователей возвращается пустой список")
    void shouldReturnEmptyAllUsers() throws Exception {
        Mockito.doReturn(new ArrayList<Film>()).when(userService).getAllUsers();
        mockMvc
                .perform(MockMvcRequestBuilders.get(serverAddress + ":" + serverPort + endpoint))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }

    @Test
    @DisplayName("При запросе всех пользователей вернётся список из одного пользователя")
    void shouldReturnOneUser() throws Exception {
        User userOne = new User(1L, "abc@mail", "abc", "John Doe", LocalDate.of(1990, 5, 1));
        ArrayList<User> allUsers = new ArrayList<>();
        allUsers.add(userOne);

        String usersPayload = mapper.writeValueAsString(allUsers);

        Mockito.doReturn(allUsers).when(userService).getAllUsers();

        mockMvc
                .perform(MockMvcRequestBuilders.get(serverAddress + ":" + serverPort + endpoint)
                        .accept("application/json")
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().string(usersPayload));
    }

    @Test
    @DisplayName("Добавление нового пользователя")
    void shouldAddNewUser() throws Exception {
        User userOne = new User(1L, "abc@mail", "dododo", "John Doe", LocalDate.of(1990, 5, 1));

        String userOnePayload = mapper.writeValueAsString(userOne);

        Mockito.doReturn(userOne).when(userService).addNewUser(userOne);

        mockMvc
                .perform(MockMvcRequestBuilders.post(serverAddress + ":" + serverPort + endpoint)
                        .content(userOnePayload)
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andExpect(content().string(userOnePayload));
    }

    @Test
    @DisplayName("Изменение добавленного пользователя")
    void shouldUpdateUser() throws Exception {
        User userOne = new User(1L, "bbb@ya.is", "b", "Ivan Drago", LocalDate.of(1991, 1, 1));

        String filmOnePayload = mapper.writeValueAsString(userOne);

        Mockito.doReturn(userOne).when(userService).updateUser(userOne);

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
    @DisplayName("Валидация поля email не пустое или содержит символ '@' при добавлении пользователя")
    @ValueSource(strings = {"", " ", "somedogpochta.is"})
    void shouldInvalidateWrongEmailCreate(String email) throws Exception {
        User userOne = new User(1L, email, "abc", "Boris", LocalDate.of(1988, 6, 25));
        String userOnePayload = mapper.writeValueAsString(userOne);

        mockMvc
                .perform(MockMvcRequestBuilders.post(serverAddress + ":" + serverPort + endpoint)
                        .content(userOnePayload)
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value(serverAddress + ":" + serverPort + endpoint))
                .andExpect(jsonPath("$.error").value("Ошибка валидации при запросе!"));
    }

    @Test
    @DisplayName("Валидация поля email != null при добавлении пользователя")
    void shouldInvalidateNullEmailCreate() throws Exception {
        User userOne = new User(1L, null, "abc", "Boris", LocalDate.of(1988, 6, 25));
        String userOnePayload = mapper.writeValueAsString(userOne);

        mockMvc
                .perform(MockMvcRequestBuilders.post(serverAddress + ":" + serverPort + endpoint)
                        .content(userOnePayload)
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value(serverAddress + ":" + serverPort + endpoint))
                .andExpect(jsonPath("$.error").value("Ошибка валидации при запросе!"));
    }

    @ParameterizedTest
    @DisplayName("Валидация поля login: не пустое или не содержит пробелы при добавлении пользователя")
    @ValueSource(strings = {"", " ", "login    1", "best login"})
    void shouldThrowExceptionOnWrongLoginCreate(String login) throws Exception {
        User userOne = new User(1L, "boris@razor.bum", login, "Boris", LocalDate.of(1988, 6, 25));
        String userOnePayload = mapper.writeValueAsString(userOne);

        mockMvc
                .perform(MockMvcRequestBuilders.post(serverAddress + ":" + serverPort + endpoint)
                        .content(userOnePayload)
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value(serverAddress + ":" + serverPort + endpoint))
                .andExpect(jsonPath("$.error").value("Ошибка валидации при запросе!"));
    }

    @Test
    @DisplayName("Валидация поля login != null при добавлении пользователя")
    void shouldInvalidateNullLoginCreate() throws Exception {
        User userOne = new User(1L, "dev@null.is", null, "Boris", LocalDate.of(1988, 6, 25));
        String userOnePayload = mapper.writeValueAsString(userOne);

        mockMvc
                .perform(MockMvcRequestBuilders.post(serverAddress + ":" + serverPort + endpoint)
                        .content(userOnePayload)
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value(serverAddress + ":" + serverPort + endpoint))
                .andExpect(jsonPath("$.error").value("Ошибка валидации при запросе!"));
    }

    @Test
    @DisplayName("Валидация поля birthday дата рождения не в будущем при добавлении пользователя")
    void shouldThrowExceptionOnFutureBirthday() throws Exception {
        User userOne = new User(1L, "abc@ya.is", "abc", "Boris", LocalDate.of(2088, 6, 25));
        String userOnePayload = mapper.writeValueAsString(userOne);

        mockMvc
                .perform(MockMvcRequestBuilders.post(serverAddress + ":" + serverPort + endpoint)
                        .content(userOnePayload)
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value(serverAddress + ":" + serverPort + endpoint))
                .andExpect(jsonPath("$.error").value("Ошибка валидации при запросе!"));
    }


    @ParameterizedTest
    @DisplayName("Валидация поля email при изменении пользователя не пустое и содержит символ '@'")
    @ValueSource(strings = {"", " ", "somedogpochta.is"})
    void shouldThrowExceptionOnWrongEmailUpdate(String email) throws Exception {
        User userOne = new User(1L, email, "abc", "Boris", LocalDate.of(1988, 6, 25));
        String userOnePayload = mapper.writeValueAsString(userOne);

        mockMvc
                .perform(MockMvcRequestBuilders.put(serverAddress + ":" + serverPort + endpoint)
                        .content(userOnePayload)
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value(serverAddress + ":" + serverPort + endpoint))
                .andExpect(jsonPath("$.error").value("Ошибка валидации при запросе!"));
    }

    @Test
    @DisplayName("Валидация поля email != null при изменении пользователя")
    void shouldInvalidateNullEmailUpdate() throws Exception {
        User userOne = new User(1L, null, "abc", "Boris", LocalDate.of(1988, 6, 25));
        String userOnePayload = mapper.writeValueAsString(userOne);

        mockMvc
                .perform(MockMvcRequestBuilders.put(serverAddress + ":" + serverPort + endpoint)
                        .content(userOnePayload)
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value(serverAddress + ":" + serverPort + endpoint))
                .andExpect(jsonPath("$.error").value("Ошибка валидации при запросе!"));
    }

    @ParameterizedTest
    @DisplayName("Валидация поле login не пустое и не содержит пробелы при изменении пользователя")
    @ValueSource(strings = {"", " ", "login    1", "best login"})
    void shouldThrowExceptionOnWrongLoginUpdate(String login) throws Exception {
        User userOne = new User(1L, "boris@razor.bum", login, "Boris", LocalDate.of(1988, 6, 25));
        String userOnePayload = mapper.writeValueAsString(userOne);

        mockMvc
                .perform(MockMvcRequestBuilders.put(serverAddress + ":" + serverPort + endpoint)
                        .content(userOnePayload)
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value(serverAddress + ":" + serverPort + endpoint))
                .andExpect(jsonPath("$.error").value("Ошибка валидации при запросе!"));
    }

    @Test
    @DisplayName("Валидация поля login != null при изменении пользователя")
    void shouldInvalidateNullLoginUpdate() throws Exception {
        User userOne = new User(1L, "dev@null.is", null, "Boris", LocalDate.of(1988, 6, 25));
        String userOnePayload = mapper.writeValueAsString(userOne);

        mockMvc
                .perform(MockMvcRequestBuilders.put(serverAddress + ":" + serverPort + endpoint)
                        .content(userOnePayload)
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value(serverAddress + ":" + serverPort + endpoint))
                .andExpect(jsonPath("$.error").value("Ошибка валидации при запросе!"));
    }

    @Test
    @DisplayName("Валидация поля birthday дата рождения не в будущем при изменении пользователя")
    void shouldThrowExceptionOnFutureBirthdayUpdate() throws Exception {
        User userOne = new User(1L, "abc@ya.is", "abc", "Boris", LocalDate.of(2088, 6, 25));
        String userOnePayload = mapper.writeValueAsString(userOne);

        mockMvc
                .perform(MockMvcRequestBuilders.put(serverAddress + ":" + serverPort + endpoint)
                        .content(userOnePayload)
                        .contentType("application/json")
                        .characterEncoding("UTF-8")
                        .accept("application/json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.path").value(serverAddress + ":" + serverPort + endpoint))
                .andExpect(jsonPath("$.error").value("Ошибка валидации при запросе!"));
    }
}
