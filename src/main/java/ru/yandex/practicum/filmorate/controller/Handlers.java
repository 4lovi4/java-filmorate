package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.service.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ValidationException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class Handlers {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity handleValidationError(HttpServletRequest request, Exception exception) {
        log.error("Request: " + request.getRequestURL() + " raised " + exception);
        String payload = "{\"path\":" + "\"" + request.getRequestURL() + "\"" + ",\"error\":" + "\"" + exception + "\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        ResponseEntity errorResponse = new ResponseEntity<>(payload, headers, HttpStatus.BAD_REQUEST);
        return errorResponse;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity handleNotFoundError(HttpServletRequest request, Exception exception) {
        log.error("Request: " + request.getRequestURL() + " raised " + exception);
        String payload = "{\"path\":" + "\"" + request.getRequestURL() + "\"" + ",\"error\":" + "\"" + exception + "\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        ResponseEntity errorResponse = new ResponseEntity<>(payload, headers, HttpStatus.NOT_FOUND);
        return errorResponse;
    }
}
