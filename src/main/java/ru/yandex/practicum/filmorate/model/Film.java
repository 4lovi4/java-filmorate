package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

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
    private final LocalDate releaseDate;
    @Positive(message = "в поле duration должно быть положительное число")
    private Integer duration;
}
