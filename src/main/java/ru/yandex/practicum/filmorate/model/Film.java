package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class Film {
    @EqualsAndHashCode.Exclude
    @Nullable
    private Long id;
    @EqualsAndHashCode.Include
    private final String name;
    @EqualsAndHashCode.Include
    private String description;
    @EqualsAndHashCode.Include
    private final LocalDate releaseDate;
    @EqualsAndHashCode.Include
    private Integer duration;
}
