package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    void getAllUsers();

    void addUser(User user);

    void updateUser(User user);
}
