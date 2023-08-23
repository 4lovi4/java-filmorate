package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final HashSet<User> users = new HashSet<>();

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<User>(users);
    }

    @PostMapping
    public User addNewUser(User user) {
        validateUser(user);
        users.add(user);
        return user;
    }

    @PutMapping
    public User updateUser(User user) {
        validateUser(user);
        if (users.contains(user)) {
            users.remove(user);
            users.add(user);
        }
        else {
            users.add(user);
        }
        return user;
    }

    private void validateUser(User user) {

        LocalDate dateNow = LocalDate.now();

        if (user.getName().isEmpty()) {
            log.error("Поле name пустое");
            throw new ValidationException("Поле name не может быть пустым");
        }
        else if (user.getName().matches("^[A-Za-z0-9+_.-]+@(.+)\\.(.+)$")) {
            log.error("Поле email: " + user.getName() + " не соответствует паттерну \".*@.*\"");
            throw new ValidationException("Ошибка валидации email");
        }

        if (user.getLogin().isEmpty()) {
            log.error("Поле login пустое");
            throw new ValidationException("Поле login не может быть пустым");
        }
        else if (user.getLogin().matches("^.*\\s+.*$")) {
            log.error("В поле login: " + user.getLogin() + " есть символ пробела");
            throw new ValidationException("Поле login не должно содержать пробелы");
        }

        if (user.getBirthday().isAfter(dateNow)) {
            log.error("Дата рождения birthday: " + user.getBirthday() +
                    " в будущем");
            throw new ValidationException("Дата рождения пользователя не может быть в будущем");
        }
    }
}
