package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAllUsers();

    void addUser(Long userId, User user);

    User getUserById(Long userId);

    boolean deleteUser(Long userId, User user);

    boolean checkUserIsPresent(Long userId, User user);

    boolean checkUserIsPresent(Long userId);

    Long getLastUserId();
}
