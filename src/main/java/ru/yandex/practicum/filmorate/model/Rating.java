package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = RatingEnumDeserializer.class)
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Rating {
    G(1, "G"),
    PG(2, "PG"),
    PG_13(3, "PG-13"),
    R(4, "R"),
    NC_17(5, "NC-17");

    @JsonProperty("id")
    public final int ratingId;
    @JsonProperty("name")
    @JsonIgnore
    public final String ratingName;

    Rating(int id) {
        this.ratingId = id;
        this.ratingName = valueOfId(id).ratingName;
    }

    Rating(int ratingId, String ratingName) {
        this.ratingId = ratingId;
        this.ratingName = ratingName;
    }

    public static Rating valueOfName(String ratingName) {
        for (Rating r : values()) {
            if (r.ratingName.equals(ratingName)) {
                return r;
            }
        }
        return null;
    }

    public static Rating valueOfId(int ratingId) {
        for (Rating r : values()) {
            if (r.ratingId == ratingId) {
                return r;
            }
        }
        return null;
    }

    public static Rating valueOfIdName(int ratingId, String ratingName) {
        for (Rating r : values()) {
            if (r.ratingId == ratingId && r.ratingName.equals(ratingName)) {
                return r;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.ratingName;
    }
}
