package ru.yandex.practicum.filmorate.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.yandex.practicum.filmorate.FilmorateApplicationTests;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceTest extends FilmorateApplicationTests {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("Получение пустого списка пользователей")
    void shouldReturnEmptyUsers() {
        List<User> allUsers = userService.getAllUsers();
        assertEquals(0, allUsers.size());
    }

    @Test
    @DisplayName("Получение списка пользователей")
    void shouldReturnAllFilms() {
        User userOne = new User(1L, "abc@ya.is", "abc", "Boris", LocalDate.of(1988, 6, 25));
        User userTwo = new User(2L, "xyz@ya.is", "xyz", "Razor", LocalDate.of(1999, 9, 6));

        userService.addNewUser(userOne);
        userService.addNewUser(userTwo);

        List<User> allUsers = userService.getAllUsers();
        assertEquals(2, allUsers.size());
    }

    @Test
    @DisplayName("Добавление нового пользователя")
    void shouldAddNewUser() {
        User userOne = new User(1L, "abc@ya.is", "abc", "Boris", LocalDate.of(1988, 6, 25));
        User userAdded = userService.addNewUser(userOne);
        assertEquals(userOne, userAdded);
        List<User> allUsers = userService.getAllUsers();
        assertEquals(1, allUsers.size());
    }

    @Test
    @DisplayName("Редактирование добавленного пользователя")
    void shouldUpdateUser() {
        User userOne = new User(1L, "abc@ya.is", "abc", "Boris", LocalDate.of(1988, 6, 25));
        User userTwo = new User(1L, "abc@mail.ru", "xyz", "Razor", LocalDate.of(1999, 9, 6));
        userService.addNewUser(userOne);
        User userUpdated = userService.updateUser(userTwo);
        assertEquals(userTwo, userUpdated);
        List<User> allUsers = userService.getAllUsers();
        assertEquals(1, allUsers.size());
    }

    @Test
    @DisplayName("Исключение: при добавлении уже есть такой пользователь в сервисе")
    void shouldThrowExceptionOnRepeatAdd() {
        User userOne = new User(1L, "abc@ya.is", "abc", "Boris", LocalDate.of(1988, 6, 25));
        userService.addNewUser(userOne);
        assertThrows(InstanceAlreadyExistsException.class, () -> userService.addNewUser(userOne));
    }

    @ParameterizedTest
    @DisplayName("Исключение: email при добавлении пустое или без символа '@'")
    @ValueSource(strings = {"", " ", "somedogpochta.is"})
    void shouldThrowExceptionOnWrongEmail(String email) {
        User userOne = new User(1L, email, "abc", "Boris", LocalDate.of(1988, 6, 25));
        assertThrows(MethodArgumentNotValidException.class, () -> userService.addNewUser(userOne));
    }

    @ParameterizedTest
    @DisplayName("Исключение: поле login пустое или содержит пробелы")
    @ValueSource(strings = {"", " ", "login    1", "best login"})
    void shouldThrowExceptionOnWrongLogin(String login) {
        User userOne = new User(1L, "boris@razor.bum", login, "Boris", LocalDate.of(1988, 6, 25));
        assertThrows(MethodArgumentNotValidException.class, () -> userService.addNewUser(userOne));
    }

    @ParameterizedTest
    @DisplayName("Добавление нового пользователя с пустым полем name")
    @ValueSource(strings = {"", " "})
    void shouldAddNewUserWithoutName(String name) {
        User userOne = new User(1L, "abc@ya.is", "abc", name, LocalDate.of(1988, 6, 25));
        User userAdded = userService.addNewUser(userOne);
        List<User> allUsers = userService.getAllUsers();
        assertEquals(1, allUsers.size());
        assertEquals(userOne.getLogin(), userAdded.getName());
    }

    @Test
    @DisplayName("Исключение: при добавлении пользователя дата рождения birthday в будущем")
    void shouldThrowExceptionOnFutureBirthday() {
        User userOne = new User(1L, "abc@ya.is", "abc", "Boris", LocalDate.of(2088, 6, 25));
        assertThrows(MethodArgumentNotValidException.class, () -> userService.addNewUser(userOne));
    }

    @Test
    @DisplayName("Исключение: при изменении неизвестного пользователя")
    void shouldThrowExceptionOnNonExistingUserUpdate() {
        User userOne = new User(1L, "abc@ya.is", "abc", "Boris", LocalDate.of(1988, 6, 25));
        assertThrows(NotFoundException.class, () -> userService.updateUser(userOne));
    }

    @ParameterizedTest
    @DisplayName("Исключение: email при изменении пользователя пустое или без символа '@'")
    @ValueSource(strings = {"", " ", "somedogpochta.is"})
    void shouldThrowExceptionOnWrongEmailUpdate(String email) {
        User userOne = new User(1L, "abc@ya.is", "abc", "Boris", LocalDate.of(1988, 6, 25));
        userService.addNewUser(userOne);
        User userTwo = new User(1L, email, "abc", "Boris", LocalDate.of(1988, 6, 25));
        assertThrows(MethodArgumentNotValidException.class, () -> userService.updateUser(userTwo));
    }

    @ParameterizedTest
    @DisplayName("Исключение: поле login пустое или содержит пробелы")
    @ValueSource(strings = {"", " ", "login    1", "best login"})
    void shouldThrowExceptionOnWrongLoginUpdate(String login) {
        User userOne = new User(1L, "boris@razor.bum", "nospace", "Boris", LocalDate.of(1988, 6, 25));
        userService.addNewUser(userOne);
        userOne.setLogin(login);
        assertThrows(MethodArgumentNotValidException.class, () -> userService.updateUser(userOne));
    }

    @ParameterizedTest
    @DisplayName("Изменение нового пользователя с пустым полем name")
    @ValueSource(strings = {"", " "})
    void shouldAddNewUserWithoutNameUpdate(String name) {
        User userOne = new User(1L, "abc@ya.is", "abc", "Boris", LocalDate.of(1988, 6, 25));
        userService.addNewUser(userOne);
        userOne.setName(name);
        User userUpdated = userService.updateUser(userOne);
        assertEquals(userOne.getLogin(), userUpdated.getName());
    }

    @Test
    @DisplayName("Исключение: при добавлении пользователя дата рождения birthday в будущем")
    void shouldThrowExceptionOnFutureBirthdayUpdate() {
        User userOne = new User(1L, "abc@ya.is", "abc", "Boris", LocalDate.of(1988, 6, 25));
        userService.addNewUser(userOne);
        User userTwo = new User(1L, "abc@ya.is", "abc", "Boris", LocalDate.of(2088, 6, 25));
        assertThrows(MethodArgumentNotValidException.class, () -> userService.addNewUser(userTwo));
    }

}
