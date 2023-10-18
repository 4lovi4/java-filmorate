package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> usersInMemory;

    public InMemoryUserStorage() {
        this.usersInMemory = new HashMap<>();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(usersInMemory.values());
    }

    public User getUserById(Long userId) {
        return usersInMemory.get(userId);
    }

    public Long addUser(Long userId, User user) {
        usersInMemory.put(userId, user);
        return userId;
    }

    public boolean deleteUser(Long userId, User user) {
        return usersInMemory.remove(userId, user);
    }

    public int deleteUser(Long userId) {
        return Objects.isNull(usersInMemory.remove(userId)) ? 0 : 1;
    }

    public boolean checkUserIsPresent(Long userId, User user) {
        return usersInMemory.containsKey(userId) ||
                usersInMemory.containsValue(user);
    }

    public boolean checkUserIsPresent(Long userId) {
        return usersInMemory.containsKey(userId);
    }

    public Long getLastUserId() {
        return usersInMemory.isEmpty() ? 0L :
                usersInMemory
                        .keySet()
                        .stream()
                        .max(Long::compareTo)
                        .get();
    }
}
