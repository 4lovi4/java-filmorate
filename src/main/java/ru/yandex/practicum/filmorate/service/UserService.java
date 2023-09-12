package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    private Long userCount;

    private static final String USER_NOT_FOUND_MESSAGE = "Пользователь id = %d не найден";

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
        this.userCount = this.userStorage.getLastUserId();
    }

    private void checkUserName(User user) {
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addNewUser(User user) {
        log.debug("Запрос на добавление пользователя: " + user);
        checkUserName(user);
        if (!userStorage.checkUserIsPresent(user.getId(), user)) {
            if (Objects.isNull(user.getId())) {
                userCount++;
                user.setId(userCount);
            }
            userStorage.addUser(userCount, user);
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
        if (userStorage.checkUserIsPresent(user.getId())) {
            userStorage.addUser(user.getId(), user);
        } else {
            log.error("Передан неизвестный пользователь для редактирования");
            throw new NotFoundException("Неизвестный пользователь");
        }
        log.debug("Изменён пользователь: " + user);
        return user;
    }

    public void addUserToFriends(Long userId, Long friendId) {
        if (userStorage.checkUserIsPresent(userId)) {
            throw new NotFoundException(String
                    .format(USER_NOT_FOUND_MESSAGE, userId));
        }
        if (userStorage.checkUserIsPresent(friendId)) {
            throw  new NotFoundException(String
                    .format(USER_NOT_FOUND_MESSAGE, userId));
        }
        User user = userStorage.getUserById(userId);
        user.getFriends().add(friendId);
    }

    public void deleteUserFromFriends(Long userId, Long friendId) {
        if (!userStorage.checkUserIsPresent(userId)) {
            throw new NotFoundException(String
                    .format(USER_NOT_FOUND_MESSAGE, userId));
        }
        if (!userStorage.checkUserIsPresent(friendId)) {
            throw  new NotFoundException(String
                    .format(USER_NOT_FOUND_MESSAGE, userId));
        }
        User user = userStorage.getUserById(userId);
        user.getFriends().remove(friendId);
    }

    public List<User> getAllUserFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        if (Objects.isNull(user)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId));
        }
        return (user
                .getFriends()
                .stream()
                .map(u -> userStorage.getUserById(u))
                .collect(Collectors.toList()));
    }
}
