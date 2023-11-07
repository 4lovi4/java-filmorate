package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAllUsersFromStorage();

    Long addUserToStorage(Long userId, User user);

    int updateUserInStorage(User user);

    User getUserByIdFromStorage(Long userId);

    boolean deleteUserFromStorage(Long userId, User user);

    int deleteUserFromStorage(Long userId);

    boolean checkUserIsPresentInStorage(Long userId, User user);

    boolean checkUserIsPresentInStorage(Long userId);

    Long getLastUserIdFromStorage();
}
