package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    @EqualsAndHashCode.Exclude
    @Nullable
    private Long id;
    private final String email;
    private String login;
    private String name;
    private LocalDate birthday;
}
