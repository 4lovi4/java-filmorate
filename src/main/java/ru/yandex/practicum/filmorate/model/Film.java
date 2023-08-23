package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode
public class Film {
    @EqualsAndHashCode.Exclude
    private Long id;
    @EqualsAndHashCode.Include
    private final String name;
    @EqualsAndHashCode.Exclude
    private String description;
    @EqualsAndHashCode.Include
    private final LocalDate releaseDate;
    @EqualsAndHashCode.Exclude
    private Integer duration;
}
