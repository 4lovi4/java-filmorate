package ru.yandex.practicum.filmorate.validator;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
@Component
@NoArgsConstructor
public class UserValidator {

    public void validate(User user) {

        if (user.getName().isBlank() || user.getName() == null) {
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

        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения birthday: " + user.getBirthday() +
                    " в будущем");
            throw new ValidationException("Дата рождения пользователя не может быть в будущем");
        }
    }
}
