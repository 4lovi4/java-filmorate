package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    @EqualsAndHashCode.Exclude
    @Nullable
    private Long id;
    @NotBlank(message = "Поле email не может быть пустым")
    @Email(message = "email должен соответствовать паттерну name@domen.zone и содержать @")
    private final String email;
    @Pattern(regexp = "^\\S+$", message = "login не должен содержать пробелы")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
