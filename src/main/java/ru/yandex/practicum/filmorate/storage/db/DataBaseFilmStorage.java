package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.NotFoundException;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component("dataBaseFilmStorage")
public class DataBaseFilmStorage implements FilmStorage {

    private final JdbcTemplate filmTemplate;

    private static final String QUERY_LIKES_BY_FILM_ID = "select l.user_id from likes l where l.film_id = ?";
    private static final String QUERY_FILM_BY_ID = "select f.*, r.rating from films f " +
            "left join ratings r on f.rating_id = r.id where f.id = ?";
    private static final String QUERY_GENRES_BY_FILM_ID = "select g.genre from genres g " +
            "join films_genres fg on g.id = fg.genre_id where fg.film_id = ?";
    private static final String QUERY_FILM_BY_FIELDS = "select f.* from films f where " +
            "f.name = ? and " +
            "f.description = ? and " +
            "f.release_date = ? and " +
            "f.duration = ?";
    private static final String QUERY_ALL_FILMS_WITH_RATINGS = "select f.*, r.rating from films f " +
            "left join ratings r on f.rating_id = r.id;";
    private static final String INSERT_FILM_WITHOUT_ID = "insert into films " +
            "(name, description, release_date, duration, rating_id) " +
            "values(?, ?, ?, ?, (select id from ratings where rating = ?))";
    private static final String INSERT_FILM_WITH_ID = "insert into films " +
            "(id, name, description, release_date, duration, rating_id) " +
            "values(?, ?, ?, ?, ?, (select id from ratings where rating = ?))";
    private static final String UPDATE_FILM = "update films set name = ?, " +
            "description = ?, " +
            "release_date = ?, " +
            "duration = ?, " +
            "rating_id = ? " +
            "where id = ?";
    private static final String DELETE_FILM_BY_FIELDS = "delete from films where id = ? and name = ? " +
            "and description = ? and release_date = ? and duration = ?";
    private static final String DELETE_FILM_BY_ID = "delete from films where id = ?";
    private static final String QUERY_LAST_FILM_ID = "select id from films order by id desc limit 1";
    private static final String SET_LIKES_TO_FILM = "merge into likes (film_id, user_id) key (film_id, user_id) values (?, ?)";
    private static final String DELETE_LIKES_FOR_FILM = "delete from likes where film_id = ? and user_id = ?";
    private static final String QUERY_GENRE_BY_ID = "select g.genre from genres g where g.id = ?";
    private static final String QUERY_ALL_GENRES = "select g.genre from genres g";
    private static final String DELETE_FILM_GENRE_RELATION = "delete from films_genres where film_id = ?";
    private static final String INSERT_FILM_GENRE_RELATION = "insert into films_genres (film_id, genre_id) values (?, ?)";
    private static final String QUERY_MPA_BY_ID = "select r.rating from ratings r where r.id = ?";
    private static final String QUERY_ALL_MPA = "select r.rating from ratings r";

    public DataBaseFilmStorage(JdbcTemplate jdbcTemplate) {
        this.filmTemplate = jdbcTemplate;
    }

    private Film mapFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        String ratingLine = rs.getString("rating");
        if (rs.wasNull()) {
            ratingLine = "";
        }
        Rating rating = Rating.valueOfName(ratingLine);
        return new Film(id, name, description, releaseDate, duration, rating);
    }

    private Set<Long> getLikesByFilmId(Long filmId) throws DataAccessException {
        return new HashSet<>(filmTemplate
                .query(QUERY_LIKES_BY_FILM_ID, (rs, rowNumber) -> rs.getLong("user_id"), filmId));
    }

    private Genre mapGenre(ResultSet rs) throws SQLException {
        String genreName = rs.getString("genre");
        return Genre.valueOfName(genreName);
    }

    private Rating mapRating(ResultSet rs) throws SQLException {
        String ratingName = rs.getString("rating");
        return Rating.valueOfName(ratingName);
    }

    private Set<Genre> getGenresByFilmId(Long filmId) throws DataAccessException {
        return (filmTemplate.query(QUERY_GENRES_BY_FILM_ID,
                (rs, rowNumber) -> Genre.valueOfName(rs.getString("genre")), filmId))
                .stream().sorted(Comparator.comparingInt(g -> g.genreId)).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    @Override
    public List<Film> getAllFilmsFromStorage() {
        List<Film> films;
        try {
            films = filmTemplate.query(QUERY_ALL_FILMS_WITH_RATINGS, (rs, rowNum) -> mapFilm(rs));
            for (Film f : films) {
                f.setGenres(getGenresByFilmId(f.getId()));
                f.setLikes(getLikesByFilmId(f.getId()));
            }
        } catch (EmptyResultDataAccessException e) {
            films = new ArrayList<>();
        }
        return films;
    }

    @Override
    public Film getFilmByIdFromStorage(Long filmId) {
        Film film;
        try {
            film = filmTemplate.queryForObject(QUERY_FILM_BY_ID, (rs, rowNum) -> mapFilm(rs), filmId);
            Objects.requireNonNull(film).setGenres(getGenresByFilmId(film.getId()));
            Objects.requireNonNull(film).setLikes(getLikesByFilmId(film.getId()));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Фильм id = %d не найден", filmId));
        }
        return film;
    }

    @Override
    public boolean checkFilmIsPresentInStorage(Long filmId, Film film) {
        List<Film> filmsById = filmTemplate.query(QUERY_FILM_BY_ID,
                (rs, rowNum) -> mapFilm(rs), filmId);
        List<Film> filmsByFields = filmTemplate.query(QUERY_FILM_BY_FIELDS,
                (rs, rowNum) -> mapFilm(rs),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration());
        return !filmsById.isEmpty() || !filmsByFields.isEmpty();
    }

    @Override
    public boolean checkFilmIsPresentInStorage(Long filmId) {
        List<Film> filmsById = filmTemplate.query(QUERY_FILM_BY_ID, (rs, rowNum) -> mapFilm(rs), filmId);
        return !filmsById.isEmpty();
    }

    @Override
    public Long addFilmToStorage(Long filmId, Film film) {
        Long filmIdAdded;
        if (Objects.isNull(filmId)) {
            filmTemplate.update(INSERT_FILM_WITHOUT_ID, film.getName(), film.getDescription(),
                    film.getReleaseDate(), film.getDuration(), film.getMpa().ratingName);
            filmIdAdded = getLastFilmIdFromStorage();
        } else {
            filmTemplate.update(INSERT_FILM_WITH_ID, filmId, film.getName(), film.getDescription(),
                    film.getReleaseDate(), film.getDuration(), film.getMpa().ratingName);
            filmIdAdded = filmId;
        }
        setGenresForFilmInStorage(filmIdAdded, film.getGenres());
        return filmIdAdded;
    }

    @Override
    public int updateFilmInStorage(Film film) {
        setGenresForFilmInStorage(film.getId(), film.getGenres());
        return filmTemplate.update(UPDATE_FILM, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().ratingId, film.getId());
    }

    @Override
    public boolean deleteFilmFromStorage(Long filmId, Film film) {
        deleteFilmGenresFromStorage(filmId);
        return filmTemplate.update(DELETE_FILM_BY_FIELDS, filmId, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration()) > 0;
    }

    public int deleteFilmFromStorage(Long filmId) {
        deleteFilmGenresFromStorage(filmId);
        return filmTemplate.update(DELETE_FILM_BY_ID, filmId);
    }

    @Override
    public Long getLastFilmIdFromStorage() {
        return filmTemplate.query(QUERY_LAST_FILM_ID, new ResultSetExtractor<Long>() {
            @Override
            public Long extractData(ResultSet rs) throws SQLException, DataAccessException {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0L;
            }
        });
    }

    @Override
    public void addLikeToFilmInStorage(Long filmId, Long userId) {
        filmTemplate.update(SET_LIKES_TO_FILM, filmId, userId);
    }

    @Override
    public void removeLikeFromFilmInStorage(Long filmId, Long userId) {
        filmTemplate.update(DELETE_LIKES_FOR_FILM, filmId, userId);
    }

    @Override
    public Genre getGenreByIdFromStorage(int genreId) {
        Genre genre;
        try {
            genre = filmTemplate.queryForObject(QUERY_GENRE_BY_ID, (rs, rowNum) -> mapGenre(rs), genreId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Жанр id = %d не найден", genreId));
        }
        return genre;
    }

    @Override
    public List<Genre> getAllGenresFromStorage() {
        return filmTemplate.query(QUERY_ALL_GENRES, (rs, rowNum) -> mapGenre(rs));
    }

    private int deleteFilmGenresFromStorage(Long filmId) {
        return filmTemplate.update(DELETE_FILM_GENRE_RELATION, filmId);
    }

    private void setGenresForFilmInStorage(Long filmId, Set<Genre> genres) {
        deleteFilmGenresFromStorage(filmId);

        filmTemplate.batchUpdate(INSERT_FILM_GENRE_RELATION, new BatchPreparedStatementSetter() {
            final ArrayList<Integer> filmGenresId = (ArrayList<Integer>) genres
                    .stream()
                    .map(g -> g.genreId)
                    .collect(Collectors.toList());

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setInt(2, filmGenresId.get(i));
            }

            @Override
            public int getBatchSize() {
                return filmGenresId.size();
            }
        });
    }

    @Override
    public Rating getMpaByIdFromStorage(int mpaId) {
        Rating mpa;
        try {
            mpa = filmTemplate.queryForObject(QUERY_MPA_BY_ID, (rs, rowNum) -> mapRating(rs), mpaId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Рейтинг mpa id = %d не найден", mpaId));
        }
        return mpa;
    }

    @Override
    public List<Rating> getAllMpa() {
        return filmTemplate.query(QUERY_ALL_MPA, (rs, rowNum) -> mapRating(rs));
    }
}
