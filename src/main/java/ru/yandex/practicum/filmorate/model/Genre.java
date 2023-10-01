package ru.yandex.practicum.filmorate.model;

public enum Genre {
    COMEDY("Комедия"),
    DRAMA("Драма"),
    CARTOON("Мультфильм"),
    THRILLER("Триллер"),
    DOC("Документальный"),
    ACTION("Боевик"),
    MILITARY("Военный"),
    DETECTIVE("Детектив"),
    FICTION("Фантастика");

    public final String genre;

    Genre(String genre) {
        this.genre = genre;
    }

    public static Genre valueOfGenre(String genre) {
        for (Genre g : values()) {
            if (g.genre.equals(genre)) {
                return g;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.genre;
    }
}
