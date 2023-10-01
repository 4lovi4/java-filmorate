package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component("dataBaseFilmStorage")
public class DataBaseFilmStorage implements FilmStorage {

    private final JdbcTemplate filmTemplate;

    public DataBaseFilmStorage(JdbcTemplate jdbcTemplate) {
        this.filmTemplate = jdbcTemplate;
    }

    private Film mapFilm(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        Integer duration = rs.getInt("duration");
        String rating = rs.getObject("rating", String.class);
        return new Film(id, name, description, releaseDate, duration, rating);
    }

    private Set<Long> getLikesByFilmId(Long filmId) throws DataAccessException {
        String sql = "select l.user_id from likes l where l.film_id = ?";
        return filmTemplate
                .query(sql, (rs, rowNumber) -> rs.getLong("user_id"), filmId)
                .stream()
                .collect(Collectors.toSet());
    }

    private Set<Genre> getGenresByFilmId(Long filmId) throws DataAccessException {
        String sql = "select g.genre from genres g join films_genres fg " +
                "on g.id = fg.genre_id where fg.film_id = ?";
        return filmTemplate.query(sql,
                (rs, rowNumber) -> Genre.valueOfGenre(rs.getString("genre")), filmId)
                .stream()
                .collect(Collectors.toSet());
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "select f.*, r.rating left from films f join ratings r on f.rating_id = r.id";
        List<Film> films = filmTemplate.query(sql, (rs, rowNum) -> mapFilm(rs));
        for (Film f : films) {
            f.setGenres(getGenresByFilmId(f.getId()));
            f.setLikes(getLikesByFilmId(f.getId()));
        }
        return films;
    }

    @Override
    public Film getFilmById(Long filmId) {
        String sql = "select f.*, r.rating  from films f left join ratings r on f.rating_id = r.id where f.id = ?";
        Film film = filmTemplate.queryForObject(sql, (rs, rowNum) -> mapFilm(rs), filmId);
        film.setGenres(getGenresByFilmId(film.getId()));
        film.setLikes(getLikesByFilmId(film.getId()));
        return null;
    }

    @Override
    public boolean checkFilmIsPresent(Long filmId, Film film) {
        String sqlById = "select f.*, r.rating  from films f left join ratings r on f.rating_id = r.id where f.id = ?";
        String sqlByFields = "select * from films f where f.name = ? and f.description = ? and f.release_date = ? and f.duration = ?";
        List<Film> filmsById = filmTemplate.query(sqlById, (rs, rowNum) -> mapFilm(rs), filmId);
        List<Film> filmsByFields = filmTemplate.query(sqlById,
                (rs, rowNum) -> mapFilm(rs),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration());
        return !filmsById.isEmpty() || !filmsByFields.isEmpty();
    }

    @Override
    public boolean checkFilmIsPresent(Long filmId) {
        String sqlById = "select f.*, r.rating  from films f left join ratings r on f.rating_id = r.id where f.id = ?";
        List<Film> filmsById = filmTemplate.query(sqlById, (rs, rowNum) -> mapFilm(rs), filmId);
        return filmsById.isEmpty();
    }

    @Override
    public Long addFilm(Long filmId, Film film) {
        Long filmIdAdded = null;
        String sqlWoId = "insert into films (name, description, release_date, duration, rating_id)\n" +
                "values(?, ?, ?, ?,\n" +
                "(select id from ratings where rating = ?)";
        String sqlWithId = "insert into films (id, name, description, release_date, duration, rating_id)\n" +
                "values(?, ?, ?, ?, ?,\n" +
                "(select id from ratings where rating = ?)";
        if (Objects.isNull(filmId)) {
            filmTemplate.update(sqlWoId, film.getName(), film.getDescription(),
                    film.getReleaseDate(), film.getDuration(), film.getRating().rating);
            filmIdAdded = getLastFilmId();
        }
        else {
            filmTemplate.update(sqlWithId, filmId, film.getName(), film.getDescription(), film.getReleaseDate(),
                    film.getDuration(), film.getRating().rating);
            filmIdAdded = filmId;
        }
        return filmIdAdded;
    }

    @Override
    public boolean deleteFilm(Long filmId, Film film) {
        String sql = "delete from films where id = ? and name = ? and description = ?\n" +
                "and release_date = ? and duration = ?";
        return filmTemplate.update(sql, filmId, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration()) > 0;
    }

    @Override
    public Film deleteFilm(Long filmId) {
        String sql = "delete from films where id = ?";
        filmTemplate.update(sql, filmId);
        return null;
    }

    @Override
    public Long getLastFilmId() {
        String sql = "select id from films order by id desc limit 1";
        return filmTemplate.queryForObject(sql, Long.class);
    }
}
