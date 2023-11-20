package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User findUserById(@PathVariable("id") Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public User addNewUser(@RequestBody @Valid User user) {
        return userService.addNewUser(user);
    }

    @PutMapping
    public User updateUser(@RequestBody @Valid User user) {
        return userService.updateUser(user);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriendToUser(@PathVariable("id") Long userId,
                                @PathVariable("friendId") Long friendId) {
        userService.addUserToFriends(userId, friendId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriendFromUser(@PathVariable("id") Long userId,
                                     @PathVariable("friendId") Long friendId) {
        userService.deleteUserFromFriends(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findUserFriends(@PathVariable("id") Long userId) {
        return userService.getAllUserFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriendsForUsers(@PathVariable("id") Long userId,
                                                @PathVariable("otherId") Long otherUserId) {
        return userService.getCommonFriendsForUsers(userId, otherUserId);
    }

}
