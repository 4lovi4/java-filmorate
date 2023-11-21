package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.NotFoundException;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component("dataBaseUserStorage")
public class DataBaseUserStorage implements UserStorage {
    private final JdbcTemplate userTemplate;

    private static final String QUERY_USER_BY_ID = "select u.* from users u where u.id = ?";
    private static final String QUERY_USER_BY_FIELDS = "select u.* from users u " +
            "where u.email = ? and u.login = ? and u.birthday = ? and u.name = ?";
    private static final String QUERY_ALL_USERS = "select * from users";
    private static final String QUERY_LAST_USER_ID = "select id from users order by id desc limit 1";
    private static final String QUERY_FRIEND_BY_USER_ID = "select f.friend_id from friends f where f.user_id = ?";
    private static final String DELETE_USER_FROM_FRIENDS = "delete from friends f where f.user_id = ?";
    private static final String INSERT_USER_FRIEND_RELATION = "insert into friends (user_id, friend_id) values (?, ?)";
    private static final String INSERT_USER_WITHOUT_ID = "insert into users (email, login, birthday, name) \n" +
            "values(?, ?, ?, ?)";
    private static final String INSERT_USER_WITH_ID = "insert into users (id, email, login, birthday, name) \n" +
            "values(?, ?, ?, ?, ?)";
    private static final String UPDATE_USER = "update users set email = ?, login = ?, birthday = ?, name = ? where id = ?";
    private static final String DELETE_USER_BY_FIELDS = "delete from users where id = ? and email = ? " +
            "and login = ? and name = ? and birthday = ?";
    private static final String DELETE_USER_BY_ID = "delete from users where id = ?";
    private static final String DELETE_FRIEND_RELATION = "delete from friends where friend_id = ?";

    public DataBaseUserStorage(JdbcTemplate jdbcTemplate) {
        this.userTemplate = jdbcTemplate;
    }

    private User mapUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();

        return new User(id, email, login, name, birthday);
    }

    private Set<Long> getFriendsByUserId(Long userId) {
        return new HashSet<>(userTemplate.queryForList(QUERY_FRIEND_BY_USER_ID, Long.class, userId));
    }

    private void insertUserFriends(User user) {
        userTemplate.update(DELETE_USER_FROM_FRIENDS, user.getId());
        userTemplate.batchUpdate(INSERT_USER_FRIEND_RELATION, new BatchPreparedStatementSetter() {
            final ArrayList<Long> userFriends = new ArrayList<>(user.getFriends());

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, user.getId());
                ps.setLong(2, userFriends.get(i));
            }

            @Override
            public int getBatchSize() {
                return userFriends.size();
            }
        });
    }

    @Override
    public List<User> getAllUsersFromStorage() {
        List<User> users;
        try {
            users = userTemplate.query(QUERY_ALL_USERS, (rs, rowNum) -> mapUser(rs));
        } catch (EmptyResultDataAccessException e) {
            users = new ArrayList<>();
        }
        for (User user : users) {
            user.setFriends(getFriendsByUserId(user.getId()));
        }
        return users;
    }

    @Override
    public User getUserByIdFromStorage(Long userId) {
        User user;
        try {
            user = userTemplate.queryForObject(QUERY_USER_BY_ID, (rs, rowNum) -> mapUser(rs), userId);
            user.setFriends(getFriendsByUserId(userId));
        } catch (EmptyResultDataAccessException | NullPointerException e) {
            throw new NotFoundException(String.format("Пользователь id = %d", userId));
        }
        return user;
    }

    @Override
    public int updateUserInStorage(User user) {
        if (!user.getFriends().isEmpty()) {
            insertUserFriends(user);
        }
        return userTemplate.update(UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getName(),
                user.getId());
    }

    @Override
    public boolean deleteUserFromStorage(Long userId, User user) {
        userTemplate.update(DELETE_FRIEND_RELATION, userId);
        return userTemplate.update(DELETE_USER_BY_FIELDS,
                userId,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()) > 0;
    }

    @Override
    public int deleteUserFromStorage(Long userId) {
        userTemplate.update(DELETE_FRIEND_RELATION, userId);
        return userTemplate.update(DELETE_USER_BY_ID, userId);
    }

    @Override
    public boolean checkUserIsPresentInStorage(Long userId, User user) {
        List<User> usersById = userTemplate.query(QUERY_USER_BY_ID, (rs, rowNum) -> mapUser(rs), userId);
        List<User> usersByFields = userTemplate.query(QUERY_USER_BY_FIELDS, (rs, rowNum) -> mapUser(rs),
                user.getEmail(),
                user.getLogin(),
                user.getBirthday(),
                user.getName());
        return (!usersById.isEmpty() || !usersByFields.isEmpty());
    }

    @Override
    public boolean checkUserIsPresentInStorage(Long userId) {
        List<User> users = userTemplate.query(QUERY_USER_BY_ID, (rs, rowNum) -> mapUser(rs), userId);
        return !users.isEmpty();
    }

    @Override
    public Long getLastUserIdFromStorage() {
        Long lastUserId;
        try {
            lastUserId = userTemplate.queryForObject(QUERY_LAST_USER_ID, Long.class);
        } catch (EmptyResultDataAccessException e) {
            lastUserId = 0L;
        }
        return lastUserId;
    }

    @Override
    public Long addUserToStorage(Long userId, User user) {
        Long userIdAdded;
        if (Objects.isNull(userId)) {
            userTemplate.update(INSERT_USER_WITHOUT_ID, user.getEmail(), user.getLogin(), user.getBirthday(), user.getName());
            userIdAdded = getLastUserIdFromStorage();
        } else {
            userTemplate.update(INSERT_USER_WITH_ID, userId, user.getEmail(), user.getLogin(), user.getBirthday(), user.getName());
            userIdAdded = userId;
        }
        return userIdAdded;
    }
}
