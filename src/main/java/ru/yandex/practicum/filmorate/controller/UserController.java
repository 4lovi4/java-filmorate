package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final HashSet<User> users = new HashSet<>();

    @Autowired
    UserService userService;

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

    @ExceptionHandler(ValidationException.class)
    ResponseEntity onValidationError(HttpServletRequest request, Exception exception) {
        log.error("Request: " + request.getRequestURL() + " raised " + exception);
        String payload = "{\"path\":" + "\"" + request.getRequestURL() + "\"" + ",\"error\":" + "\"" + exception + "\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        ResponseEntity errorResponse = new ResponseEntity<>(payload, headers, HttpStatus.BAD_REQUEST);
        return errorResponse;
    }
}
