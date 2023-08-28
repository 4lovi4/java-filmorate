package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Film {
    @EqualsAndHashCode.Exclude
    @Nullable
    private Long id;
    private final String name;
    private String description;
    private final LocalDate releaseDate;
    private Integer duration;
}
