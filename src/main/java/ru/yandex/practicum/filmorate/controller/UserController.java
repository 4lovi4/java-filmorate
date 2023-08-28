package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.HashSet;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final HashSet<User> users = new HashSet<>();

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> findAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User addNewUser(@RequestBody User user) {
        User resultUser;
        resultUser = userService.addNewUser(user);
        return resultUser;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }
}
