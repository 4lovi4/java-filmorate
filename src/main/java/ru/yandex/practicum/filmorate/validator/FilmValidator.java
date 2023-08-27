package ru.yandex.practicum.filmorate.validator;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Slf4j
@Component
@NoArgsConstructor
public class FilmValidator {

    private static final LocalDate oldestReleaseDate = LocalDate
            .parse("1895-12-28", DateTimeFormatter.ISO_LOCAL_DATE);

    public void validate(Film film) {
        if (Objects.isNull(film.getName()) || film.getName().isBlank()) {
            log.error("Поле name пустое");
            throw new ValidationException("Поле name не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            log.error("Длина описания фильма в поле description больше 200 символов");
            throw new ValidationException("Длина описания фильма в поле description не может быть больше 200 символов");
        }

        if (film.getReleaseDate().isBefore(oldestReleaseDate)) {
            log.error("Дата релиза фильма раньше предельной даты в истории кинематографа " +
                    oldestReleaseDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            throw new ValidationException("Дата выпуска фильма старше " +
                    oldestReleaseDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма duration = " + film.getDuration() +
                    " должна быть > 0");
            throw new ValidationException("Продолжительность фильма duration должна быть положительной");
        }
    }
}
