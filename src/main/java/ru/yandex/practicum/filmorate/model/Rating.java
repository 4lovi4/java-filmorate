package ru.yandex.practicum.filmorate.model;

public enum Rating {
    G("G"),
    PG("PG"),
    PG_13("PG-13"),
    R("R"),
    NC_17("NC-17");

    public final String rating;

    Rating(String rating) {
        this.rating = rating;
    }

    public static Rating valueOfRating(String rating) {
        for (Rating r : values()) {
            if (r.rating.equals(rating)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.rating;
    }
}
