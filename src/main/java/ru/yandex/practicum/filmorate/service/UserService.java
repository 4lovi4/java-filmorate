package ru.yandex.practicum.filmorate.service;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class UserService {

    private final HashSet<User> users;

    @Autowired
    UserValidator userValidator;

    public UserService() {
        this.users = new HashSet<>();
    }

    public UserService(HashSet<User> users) {
        this.users = users;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public User addNewUser(User user) {
        userValidator.validate(user);
        if (!users.contains(user)) {
            users.add(user);
        }
        else {
            users.remove(user);
            users.add(user);
        }
        return user;
    }

    public User updateUser(User user) {
        userValidator.validate(user);
        if (users.contains(user)) {
            users.remove(user);
            users.add(user);
        }
        else {
            users.add(user);
        }
        return user;
    }
}
