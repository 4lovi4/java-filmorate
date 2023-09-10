package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {

    private HashMap<Long, User> usersInMemory;

    public InMemoryUserStorage() {
        this.usersInMemory = new HashMap<>();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(usersInMemory.values());
    }

    public User getUserById(Long userId) {
        return usersInMemory.get(userId);
    }

    public void addUser(Long userId, User user) {
        usersInMemory.put(userId, user);
    }

    public boolean deleteUser(Long userId, User user) {
        return usersInMemory.remove(userId, user);
    }

    public boolean checkUserIsPresent(Long userId, User user) {
        return usersInMemory.containsKey(userId) &&
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
