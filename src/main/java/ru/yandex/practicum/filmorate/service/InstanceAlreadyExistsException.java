package ru.yandex.practicum.filmorate.service;

public class InstanceAlreadyExistsException extends RuntimeException {
    public InstanceAlreadyExistsException() {
    }

    public InstanceAlreadyExistsException(String message) {
        super(message);
    }

    public InstanceAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}
