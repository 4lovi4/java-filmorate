package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.stream.Collectors;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Objects;

@Component("dataBaseFilmStorage")
public class DataBaseFilmStorage implements FilmStorage {

    private final JdbcTemplate filmTemplate;

    private static final String SQL_FILM_BY_ID = "select f.*, r.rating from films f left join ratings r on f.rating_id = r.id where f.id = ?";

    public DataBaseFilmStorage(JdbcTemplate jdbcTemplate) {
        this.filmTemplate = jdbcTemplate;
    }

    private Film mapFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        Rating rating;
        try {
            rating = Rating.valueOfName(rs.getString("rating"));
        }
        catch (SQLException e) {
            rating = null;
        }
        return new Film(id, name, description, releaseDate, duration, rating);
    }

    private Set<Long> getLikesByFilmId(Long filmId) throws DataAccessException {
        String sql = "select l.user_id from likes l where l.film_id = ?";
        return new HashSet<>(filmTemplate
                .query(sql, (rs, rowNumber) -> rs.getLong("user_id"), filmId));
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
        String sql = "select g.genre from genres g " +
                "join films_genres fg on g.id = fg.genre_id " +
                "where fg.film_id = ?";
        return new HashSet<>(filmTemplate.query(sql,
                (rs, rowNumber) -> Genre.valueOfName(rs.getString("genre")), filmId));
    }

    @Override
    public List<Film> getAllFilmsFromStorage() {
        String sql = "select f.*, r.rating from films f left join ratings r on f.rating_id = r.id;";
        List<Film> films;
        try {
            films = filmTemplate.query(sql, (rs, rowNum) -> mapFilm(rs));
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
            film = filmTemplate.queryForObject(SQL_FILM_BY_ID, (rs, rowNum) -> mapFilm(rs), filmId);
            Objects.requireNonNull(film).setGenres(getGenresByFilmId(film.getId()));
            Objects.requireNonNull(film).setLikes(getLikesByFilmId(film.getId()));
        }
        catch (EmptyResultDataAccessException e) {
            film = null;
        }

        return film;
    }

    @Override
    public boolean checkFilmIsPresentInStorage(Long filmId, Film film) {
        String sqlByFields = "select * from films f where f.name = ? and f.description = ? and f.release_date = ? and f.duration = ?";
        List<Film> filmsById = filmTemplate.query(SQL_FILM_BY_ID, (rs, rowNum) -> mapFilm(rs), filmId);
        List<Film> filmsByFields = filmTemplate.query(sqlByFields,
                (rs, rowNum) -> mapFilm(rs),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration());
        return !filmsById.isEmpty() || !filmsByFields.isEmpty();
    }

    @Override
    public boolean checkFilmIsPresentInStorage(Long filmId) {
        List<Film> filmsById = filmTemplate.query(SQL_FILM_BY_ID, (rs, rowNum) -> mapFilm(rs), filmId);
        return !filmsById.isEmpty();
    }

    @Override
    public Long addFilmToStorage(Long filmId, Film film) {
        Long filmIdAdded;
        String sqlWoId = "insert into films (name, description, release_date, duration, rating_id)\n" +
                "values(?, ?, ?, ?,\n" +
                "(select id from ratings where rating = ?))";
        String sqlWithId = "insert into films (id, name, description, release_date, duration, rating_id)\n" +
                "values(?, ?, ?, ?, ?,\n" +
                "(select id from ratings where rating = ?))";
        if (Objects.isNull(filmId)) {
            filmTemplate.update(sqlWoId, film.getName(), film.getDescription(),
                    film.getReleaseDate(), film.getDuration(), film.getMpa().ratingName);
            filmIdAdded = getLastFilmIdFromStorage();
        } else {
            filmTemplate.update(sqlWithId, filmId, film.getName(), film.getDescription(), film.getReleaseDate(),
                    film.getDuration(), film.getMpa().ratingName);
            filmIdAdded = filmId;
        }
        return filmIdAdded;
    }

    @Override
    public int updateFilmInStorage(Film film) {
        String sql = "update films set name = ?, " +
                "description = ?, " +
                "release_date = ?, " +
                "duration = ?, " +
                "rating_id = ? " +
                "where id = ?";
        setGenresForFilmInStorage(film.getId(), film.getGenres());
        return filmTemplate.update(sql, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().ratingId, film.getId());
    }

    @Override
    public boolean deleteFilmFromStorage(Long filmId, Film film) {
        String sql = "delete from films where id = ? and name = ? and description = ?\n" +
                "and release_date = ? and duration = ?";
        deleteFilmGenresFromStorage(filmId);
        return filmTemplate.update(sql, filmId, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration()) > 0;
    }

    public int deleteFilmFromStorage(Long filmId) {
        String sql = "delete from films where id = ?";
        deleteFilmGenresFromStorage(filmId);
        return filmTemplate.update(sql, filmId);
    }

    @Override
    public Long getLastFilmIdFromStorage() {
        String sql = "select id from films order by id desc limit 1";
        Long lastFilmId;
        try {
            lastFilmId = filmTemplate.queryForObject(sql, Long.class);
        } catch (EmptyResultDataAccessException e) {
            lastFilmId = 0L;
        }
        return lastFilmId;
    }

    @Override
    public void addLikeToFilmInStorage(Long filmId, Long userId) {
        String sql = "merge into likes (film_id, user_id) key (film_id, user_id) values (?, ?)";
        filmTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLikeFromFilmInStorage(Long filmId, Long userId) {
        String sql = "delete from likes where film_id = ? and user_id = ?";
        filmTemplate.update(sql, filmId, userId);
    }

    @Override
    public Genre getGenreByIdFromStorage(int genreId) {
        String sql = "select genre from genres g where g.id = ?";
        Genre genre;
        try {
            genre = filmTemplate.queryForObject(sql, (rs, rowNum) -> mapGenre(rs), genreId);
        }
        catch (EmptyResultDataAccessException e) {
            genre = null;
        }
        return genre;
    }

    @Override
    public List<Genre> getAllGenresFromStorage() {
        String sql = "select genre from genres g";
        return filmTemplate.query(sql, (rs, rowNum) -> mapGenre(rs));
    }

    private int deleteFilmGenresFromStorage(Long filmId) {
        String sql = "delete from films_genres where film_id = ?";
        return filmTemplate.update(sql, filmId);
    }

    private void setGenresForFilmInStorage(Long filmId, Set<Genre> genres) {
        String sql = "insert into films_genres (film_id, genre_id) values (?, ?)";
        deleteFilmGenresFromStorage(filmId);
        for (int g: genres.stream().map(genre -> genre.genreId).collect(Collectors.toSet())) {
            filmTemplate.update(sql, filmId, g);
        }
    }

    @Override
    public Rating getMpaByIdFromStorage(int mpaId) {
        String sql = "select rating from ratings r where r.id = ?";
        Rating mpa;
        try {
            mpa = filmTemplate.queryForObject(sql, (rs, rowNum) -> mapRating(rs), mpaId);
        }
         catch (EmptyResultDataAccessException e) {
            mpa = null;
         }
        return mpa;
    }

    @Override
    public List<Rating> getAllMpa() {
        String sql = "select rating from ratings r";
        return filmTemplate.query(sql, (rs, rowNum) -> mapRating(rs));
    }
}
