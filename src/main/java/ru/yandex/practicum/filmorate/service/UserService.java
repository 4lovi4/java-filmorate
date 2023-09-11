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

    private final Map<Long, User> users;

    private Long userCount;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
        this.users = new HashMap<>();
        this.userCount = this.userStorage.getLastUserId();
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

    public void addUserToFriends(Long userId, Long friendId) {
        if (userStorage.checkUserIsPresent(userId)) {
            throw new NotFoundException(String
                    .format("Пользователь id = %d не найден",
                            userId));
        }
        if (userStorage.checkUserIsPresent(friendId)) {
            throw  new NotFoundException(String
                    .format("Друг id = %d не найден",
                            userId));
        }
        User user = userStorage.getUserById(userId);
        user.getFriends().add(friendId);
    }

    public void deleteUserFromFriends(Long userId, Long friendId) {
        if (!userStorage.checkUserIsPresent(userId)) {
            throw new NotFoundException(String
                    .format("Пользователь id = %d не найден",
                            userId));
        }
        if (!userStorage.checkUserIsPresent(friendId)) {
            throw  new NotFoundException(String
                    .format("Друг id = %d  не найден",
                            userId));
        }
        User user = userStorage.getUserById(userId);
        user.getFriends().remove(friendId);
    }

    public List<User> getAllUserFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        if (Objects.isNull(user)) {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));
        }
        return (user
                .getFriends()
                .stream()
                .map(u -> userStorage.getUserById(u))
                .collect(Collectors.toList()));
    }
}
