package ru.yandex.practicum.filmorate.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class FilmReleaseDateValidator implements ConstraintValidator<ReleaseDateConstraint, LocalDate> {

    private static final LocalDate oldestReleaseDate = LocalDate
            .parse("1895-12-28", DateTimeFormatter.ISO_LOCAL_DATE);

    @Override
    public boolean isValid(LocalDate releaseDate, ConstraintValidatorContext ctx) {
        return (!Objects.isNull(releaseDate)) && (releaseDate.isAfter(oldestReleaseDate));
    }
}
