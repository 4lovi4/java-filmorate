package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = GenreEnumDeserializer.class)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Genre {
    COMEDY(1, "Комедия"),
    DRAMA(2, "Драма"),
    CARTOON(3,"Мультфильм"),
    THRILLER(4,"Триллер"),
    DOC(5,"Документальный"),
    ACTION(6,"Боевик");

    @JsonProperty("id")
    public final int genreId;
    @JsonProperty("name")
    public final String genreName;


    Genre(int genreId, String name) {
        this.genreId = genreId;
        this.genreName = name;
    }

    public static Genre valueOfName(String genreName) {
        for (Genre g : values()) {
            if (g.genreName.equals(genreName)) {
                return g;
            }
        }
        return null;
    }

    public static Genre valueOfId(int genreId) {
        for (Genre g : values()) {
            if (g.genreId == genreId) {
                return g;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.genreName;
    }
}
