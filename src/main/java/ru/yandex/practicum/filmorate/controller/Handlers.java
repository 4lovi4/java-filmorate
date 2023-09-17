package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.yandex.practicum.filmorate.service.InstanceAlreadyExistsException;
import ru.yandex.practicum.filmorate.service.NotFoundException;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class Handlers {

    private static final String ERROR_LOG_TEMPLATE = "Request: %s raised %s";
    private static final String ERROR_PAYLOAD_TEMPLATE = "{\"path\": \"%s\", \"error\": \"%s\"}";
    private static final String HEADER_KEY = "Content-Type";
    private static final String HEADER_VALUE = "application/json";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleValidationError(HttpServletRequest request, Exception exception) {
        log.error(String.format(ERROR_LOG_TEMPLATE, request.getRequestURL(), exception));
        String payload = String.format(ERROR_PAYLOAD_TEMPLATE, request.getRequestURL(), "Ошибка валидации при запросе!");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_KEY, HEADER_VALUE);
        return new ResponseEntity<>(payload, headers, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    ResponseEntity<String> handleNotFoundError(HttpServletRequest request, Exception exception) {
        log.error(String.format(ERROR_LOG_TEMPLATE, request.getRequestURL(), exception));
        String payload = String.format(ERROR_PAYLOAD_TEMPLATE, request.getRequestURL(), "Объект не найден на сервере!");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_KEY, HEADER_VALUE);
        return new ResponseEntity<>(payload, headers, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InstanceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<String> handleAlreadyExistsException(HttpServletRequest request, Exception exception) {
        log.error(String.format(ERROR_LOG_TEMPLATE, request.getRequestURL(), exception));
        String payload = String.format(ERROR_PAYLOAD_TEMPLATE, request.getRequestURL(), "Объект уже существует на сервере!");
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_KEY, HEADER_VALUE);
        return new ResponseEntity<>(payload, headers, HttpStatus.NOT_FOUND);
    }
}
