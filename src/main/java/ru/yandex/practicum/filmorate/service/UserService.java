package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    private Long userCounter;

    public static final String USER_NOT_FOUND_MESSAGE = "Пользователь id = %d не найден";

    @Autowired
    public UserService(@Qualifier("dataBaseUserStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
        this.userCounter = this.userStorage.getLastUserIdFromStorage();
    }

    private void checkUserName(User user) {
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsersFromStorage();
    }

    public User getUserById(Long userId) {
        User user = userStorage.getUserByIdFromStorage(userId);
        if (Objects.isNull(user)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId));
        }
        return user;
    }

    public User addNewUser(User user) {
        log.info("Запрос на добавление пользователя: " + user);
        checkUserName(user);
        if (!userStorage.checkUserIsPresentInStorage(user.getId(), user)) {
            if (Objects.isNull(user.getId())) {
                user.setId(getNewUserId());
            }
            userStorage.addUserToStorage(user.getId(), user);
        } else {
            log.error("Пользователь уже добавлен в сервисе");
            throw new InstanceAlreadyExistsException("Пользователь уже добавлен");
        }
        log.info("Добавлен пользователь: " + user);
        return user;
    }

    public User updateUser(User user) {
        log.info("Запрос на изменение пользователя: " + user);
        checkUserName(user);
        if (userStorage.checkUserIsPresentInStorage(user.getId())) {
            userStorage.updateUserInStorage(user);
        } else {
            log.error("Передан неизвестный пользователь для редактирования");
            throw new NotFoundException(String.format(USER_NOT_FOUND_MESSAGE, user.getId()));
        }
        log.info("Изменён пользователь: " + user);
        return user;
    }

    public void addUserToFriends(Long userId, Long friendId) {
        if (!userStorage.checkUserIsPresentInStorage(userId)) {
            throw new NotFoundException(String
                    .format(USER_NOT_FOUND_MESSAGE, userId));
        }
        if (!userStorage.checkUserIsPresentInStorage(friendId)) {
            throw  new NotFoundException(String
                    .format(USER_NOT_FOUND_MESSAGE, userId));
        }
        User user = userStorage.getUserByIdFromStorage(userId);
        User friend = userStorage.getUserByIdFromStorage(friendId);
        user.getFriends().add(friendId);
        userStorage.updateUserInStorage(user);
    }

    public void deleteUserFromFriends(Long userId, Long friendId) {
        if (!userStorage.checkUserIsPresentInStorage(userId)) {
            throw new NotFoundException(String
                    .format(USER_NOT_FOUND_MESSAGE, userId));
        }
        if (!userStorage.checkUserIsPresentInStorage(friendId)) {
            throw  new NotFoundException(String
                    .format(USER_NOT_FOUND_MESSAGE, userId));
        }
        User user = userStorage.getUserByIdFromStorage(userId);
        User friend = userStorage.getUserByIdFromStorage(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getAllUserFriends(Long userId) {
        User user = userStorage.getUserByIdFromStorage(userId);
        if (Objects.isNull(user)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND_MESSAGE, userId));
        }
        return (user
                .getFriends()
                .stream()
                .map(userStorage::getUserByIdFromStorage)
                .collect(Collectors.toList()));
    }

    public List<User> getCommonFriendsForUsers(Long userId, Long otherUserId) {
        if (!userStorage.checkUserIsPresentInStorage(userId)) {
            throw new NotFoundException(String
                    .format(USER_NOT_FOUND_MESSAGE, userId));
        }
        if (!userStorage.checkUserIsPresentInStorage(otherUserId)) {
            throw  new NotFoundException(String
                    .format(USER_NOT_FOUND_MESSAGE, userId));
        }

        User user = userStorage.getUserByIdFromStorage(userId);
        User otherUser = userStorage.getUserByIdFromStorage(otherUserId);

        return user.getFriends()
                .stream()
                .filter(friendId -> otherUser
                        .getFriends()
                        .contains(friendId)
                ).map(userStorage::getUserByIdFromStorage
                ).collect(Collectors.toList());
    }

    public boolean isUserPresent(Long userId) {
        return userStorage.checkUserIsPresentInStorage(userId);
    }

    private Long getNewUserId() {
        this.userCounter++;
        return userCounter;
    }
}
