package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component("dataBaseUserStorage")
public class DataBaseUserStorage implements UserStorage {
    private JdbcTemplate userTemplate;

    private static final String SQL_USER_BY_ID = "select u.* from users u where u.id = ?";

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
        return new HashSet<>(userTemplate.queryForList(sql, Long.class, userId));
    }

    @Override
    public List<User> getAllUsersFromStorage() {
        String sql = "select * from users";
        return userTemplate.query(sql, (rs, rowNum) -> mapUser(rs));
    }

    @Override
    public User getUserByIdFromStorage(Long userId) {
        return userTemplate.queryForObject(SQL_USER_BY_ID, (rs, rowNum) -> mapUser(rs), userId);
    }

    @Override
    public int updateUserInStorage(User user) {
        String sql = "update users set " +
                "email = ?, " +
                "login = ?, " +
                "birthday = ?, " +
                "name = ? " +
                "where id = ?";
        return userTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getName(),
                user.getId());
    }

    @Override
    public boolean deleteUserFromStorage(Long userId, User user) {
        String sql = "delete from users where id = ? and email = ? " +
                "and login = ? and name = ? and birthday = ?";
        return userTemplate.update(sql,
                userId,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()) > 0;
    }

    @Override
    public int deleteUserFromStorage(Long userId) {
        String sql = "delete from users where id = ?";
        return userTemplate.update(sql, userId);
    }

    @Override
    public boolean checkUserIsPresentInStorage(Long userId, User user) {
        String sqlByUserFields = "select * from users u " +
                "where u.email = ? and u.login = ? and u.birthday = ? and u.name = name";
        List<User> usersById = userTemplate.query(SQL_USER_BY_ID, (rs, rowNum) -> mapUser(rs), userId);
        List<User> usersByFields = userTemplate.query(sqlByUserFields, (rs, rowNum) -> mapUser(rs),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getName());
        return (!usersById.isEmpty() || !usersByFields.isEmpty());
    }

    @Override
    public boolean checkUserIsPresentInStorage(Long userId) {
        List<User> users = userTemplate.query(SQL_USER_BY_ID, (rs, rowNum) -> mapUser(rs), userId);
        return !users.isEmpty();
    }

    @Override
    public Long getLastUserIdFromStorage() {
        String sql = "select id from users order by id desc limit 1";
        return userTemplate.queryForObject(sql, Long.class);
    }

    @Override
    public Long addUserToStorage(Long userId, User user) {
        Long userIdAdded;
        String sqlWoId = "insert into users (email, login, birthday, name) \n" +
                "values(?, ?, ?, ?)";
        String sqlWithId = "insert int users (id, email, login, birthday, name) \n" +
                "values(?, ?, ?, ?, ?)";
        if (Objects.isNull(userId)) {
            userTemplate.update(sqlWoId, user.getEmail(), user.getLogin(), user.getBirthday(), user.getName());
            userIdAdded = getLastUserIdFromStorage();
        }
        else {
            userTemplate.update(sqlWithId, userId, user.getEmail(), user.getLogin(), user.getBirthday(), user.getBirthday(), user.getName());
            userIdAdded = userId;
        }
        return userIdAdded;
    }
}
