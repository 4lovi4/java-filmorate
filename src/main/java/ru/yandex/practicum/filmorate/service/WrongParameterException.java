package ru.yandex.practicum.filmorate.service;

public class WrongParameterException extends RuntimeException {
    public WrongParameterException() {
    }

    public WrongParameterException(String message) {
        super(message);
    }

    public WrongParameterException(String message, Throwable cause) {
        super(message, cause);
    }
}
