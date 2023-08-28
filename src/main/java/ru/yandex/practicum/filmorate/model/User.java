package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class User {
    @EqualsAndHashCode.Exclude
    @Nullable
    private Long id;
    @EqualsAndHashCode.Include
    private final String email;
    @EqualsAndHashCode.Include
    private String login;
    @EqualsAndHashCode.Include
    private String name;
    @EqualsAndHashCode.Include
    private LocalDate birthday;
}
