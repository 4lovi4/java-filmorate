package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component("dataBaseUserStorage")
public class DataBaseUserStorage implements UserStorage {
    private JdbcTemplate userTemplate;

    public DataBaseUserStorage(JdbcTemplate jdbcTemplate) {
        this.userTemplate = jdbcTemplate;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        return new User(id, email, login, name, birthday);
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "select from * from users u";
        return null;
    }

    @Override
    public User getUserById(Long userId) {
        return null;
    }

    @Override
    public boolean deleteUser(Long userId, User user) {
        return false;
    }

    @Override
    public User deleteUser(Long userId) {
        return null;
    }

    @Override
    public boolean checkUserIsPresent(Long userId, User user) {
        return false;
    }

    @Override
    public boolean checkUserIsPresent(Long userId) {
        return false;
    }

    @Override
    public Long getLastUserId() {
        String sql = "select id from users order by id desc limit 1";
        return userTemplate.queryForObject(sql, Long.class);
    }

    @Override
    public Long addUser(Long userId, User user) {
        return null;
    }
}
