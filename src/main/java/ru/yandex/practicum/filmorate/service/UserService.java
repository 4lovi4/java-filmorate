package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import java.util.*;

@Service
@Slf4j
public class UserService {

    private final HashMap<Long, User> users;

    private Long userCount;

    @Autowired
    UserValidator userValidator;

    public UserService() {
        this.users = new HashMap<>();
        this.userCount = 0L;
    }

    public UserService(HashMap<Long, User> users) {
        this.users = users;
        this.userCount = (long) users.keySet().size();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User addNewUser(User user) {
        userValidator.validate(user);
        Long currentId = userCount;
        if (!users.containsKey(user.getId()) || !users.values().contains(user)) {
            if (Objects.isNull(user.getId())) {
                userCount++;
                currentId = userCount;
                user.setId(currentId);
            } else {
                currentId = user.getId();
            }
            users.put(currentId, user);
        } else {
            log.error("Пользователь уже добавлен в сервисе");
            throw new ValidationException("Пользователь уже добавлен");
        }
        return user;
    }

    public User updateUser(User user) {
        userValidator.validate(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            log.error("Передан неизвестный пользователь для редактирования");
            throw new NotFoundException("Неизвестный пользователь");
        }
        return user;
    }
}
