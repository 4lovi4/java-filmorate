package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> usersInMemory;

    public InMemoryUserStorage() {
        this.usersInMemory = new HashMap<>();
    }

    public List<User> getAllUsersFromStorage() {
        return new ArrayList<>(usersInMemory.values());
    }

    public User getUserByIdFromStorage(Long userId) {
        return usersInMemory.get(userId);
    }

    public Long addUserToStorage(Long userId, User user) {
        usersInMemory.put(userId, user);
        return userId;
    }

    @Override
    public int updateUserInStorage(User user) {
        deleteUserFromStorage(user.getId());
        return addUserToStorage(user.getId(), user).intValue();
    }

    public boolean deleteUserFromStorage(Long userId, User user) {
        return usersInMemory.remove(userId, user);
    }

    public int deleteUserFromStorage(Long userId) {
        return Objects.isNull(usersInMemory.remove(userId)) ? 0 : 1;
    }

    public boolean checkUserIsPresentInStorage(Long userId, User user) {
        return usersInMemory.containsKey(userId) ||
                usersInMemory.containsValue(user);
    }

    public boolean checkUserIsPresentInStorage(Long userId) {
        return usersInMemory.containsKey(userId);
    }

    public Long getLastUserIdFromStorage() {
        return usersInMemory.isEmpty() ? 0L :
                usersInMemory
                        .keySet()
                        .stream()
                        .max(Long::compareTo)
                        .get();
    }
}
