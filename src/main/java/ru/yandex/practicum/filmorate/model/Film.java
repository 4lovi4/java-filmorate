package ru.yandex.practicum.filmorate.model;

import lombok.*;
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
    private final Integer duration;
    @EqualsAndHashCode.Exclude
    private Set<Long> likes;

    public Film() {
        this.name = "";
        this.releaseDate = LocalDate
                .parse("1895-12-28", DateTimeFormatter.ISO_LOCAL_DATE);
        this.duration = 0;
        this.likes = new HashSet<>();
    }

    public Film(String name, String description, LocalDate releaseDate, int duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = new HashSet<>();
    }

    public Film(Long id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = new HashSet<>();
    }
}
