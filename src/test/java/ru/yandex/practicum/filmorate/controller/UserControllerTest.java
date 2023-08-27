package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
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
}
