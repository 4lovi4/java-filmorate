package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.sql.RowSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    private Set<Long> getFriendsByUserId(Long userId) {
        String sql = "select user_id from friends f where f.user_id = ? and approved = true";
        return new HashSet<>();
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "select from * from users u";
        return new ArrayList<>();
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
    public int deleteUser(Long userId) {
        return 0;
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
