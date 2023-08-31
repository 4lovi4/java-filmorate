package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class UserService {

    private final HashMap<Long, User> users;

    private Long userCount;

    public UserService() {
        this.users = new HashMap<>();
        this.userCount = 0L;
    }

    public UserService(HashMap<Long, User> users) {
        this.users = users;
        this.userCount = (long) users.keySet().size();
    }

    private void checkUserName(User user) {
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User addNewUser(User user) {
        log.debug("Запрос на добавление пользователя: " + user);
        checkUserName(user);
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
            throw new InstanceAlreadyExistsException("Пользователь уже добавлен");
        }
        log.debug("Добавлен пользователь: " + user);
        return user;
    }

    public User updateUser(User user) {
        log.debug("Запрос на изменение пользователя: " + user);
        checkUserName(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
        } else {
            log.error("Передан неизвестный пользователь для редактирования");
            throw new NotFoundException("Неизвестный пользователь");
        }
        log.debug("Изменён пользователь: " + user);
        return user;
    }
}
