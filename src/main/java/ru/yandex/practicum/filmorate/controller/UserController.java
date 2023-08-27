package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ModelAndView onValidationError(HttpServletRequest request, Exception exception) {
        log.error("Request: " + request.getRequestURL() + " raised " + exception);
        ModelAndView errorResponse = new ModelAndView();
        errorResponse.addObject("exception", exception);
        errorResponse.addObject("url", request.getRequestURL());
        errorResponse.setViewName("error");
        return errorResponse;
    }
}
