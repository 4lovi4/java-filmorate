package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;
import ru.yandex.practicum.filmorate.validator.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {
    @EqualsAndHashCode.Exclude
    @Nullable
    private Long id;
    @NotBlank(message = "name не может быть пустым")
    private final String name;
    @Length(max = 200, message = "описание в поле description не больше 200 символов")
    private String description;
    @ReleaseDateConstraint
    private final LocalDate releaseDate;
    @Positive(message = "в поле duration должно быть положительное число")
    private final int duration;
    @EqualsAndHashCode.Exclude
    private Set<Long> likes;
    @EqualsAndHashCode.Exclude
    private Set<Genre> genres;
    @EqualsAndHashCode.Exclude
    private Rating mpa;

    public Film() {
        this.name = "1";
        this.releaseDate = LocalDate
                .parse("1895-12-28", DateTimeFormatter.ISO_LOCAL_DATE);
        this.duration = 1;
        this.likes = new HashSet<>();
        this.genres = new HashSet<>();
    }

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = new HashSet<>();
        this.genres = new HashSet<>();
    }

    public Film(Long id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = new HashSet<>();
        this.genres = new HashSet<>();
    }

    public Film(Long id, String name, String description, LocalDate releaseDate, int duration, Rating mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.likes = new HashSet<>();
        this.genres = new HashSet<>();
    }
}
