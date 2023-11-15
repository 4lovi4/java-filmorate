CREATE TABLE IF NOT EXISTS users (
id INT PRIMARY KEY auto_increment,
email VARCHAR(128) NOT NULL UNIQUE,
login VARCHAR(128) NOT NULL,
name VARCHAR(128),
birthday DATE);

CREATE TABLE IF NOT EXISTS friends (
user_id INT REFERENCES users(id) ON DELETE CASCADE,
friend_id INT REFERENCES users(id) ON DELETE CASCADE,
approved BOOLEAN DEFAULT FALSE,
CONSTRAINT friends_relation_uq UNIQUE(user_id, friend_id),
CONSTRAINT user_friend_not_eq CHECK (user_id <> friend_id));

CREATE TABLE IF NOT EXISTS genres (
id INT PRIMARY KEY,
genre VARCHAR(64));

CREATE TABLE IF NOT EXISTS ratings (
id INT PRIMARY KEY,
rating VARCHAR(5));

CREATE TABLE IF NOT EXISTS films (
id INT PRIMARY KEY auto_increment,
name VARCHAR(128) NOT NULL,
description VARCHAR(200),
release_date DATE,
duration INT,
rating_id INT REFERENCES ratings(id) ON DELETE CASCADE);

CREATE TABLE IF NOT EXISTS likes (
film_id INT REFERENCES films(id) ON DELETE CASCADE,
user_id INT REFERENCES users(id) ON DELETE CASCADE,
CONSTRAINT film_user_uq UNIQUE(film_id, user_id)
);

CREATE TABLE IF NOT EXISTS films_genres (
film_id INT REFERENCES films(id) ON DELETE CASCADE,
genre_id INT REFERENCES genres(id) ON DELETE CASCADE,
CONSTRAINT film_genre_uq UNIQUE(film_id, genre_id));

DELETE FROM FRIENDS;
DELETE FROM FILMS_GENRES;
DELETE FROM LIKES;
DELETE FROM FILMS;
ALTER TABLE FILMS ALTER COLUMN ID RESTART WITH 1;
DELETE FROM USERS;
ALTER TABLE USERS ALTER COLUMN ID RESTART WITH 1;
DELETE FROM GENRES;
DELETE FROM RATINGS;

INSERT INTO GENRES (id, genre)
VALUES (1, 'Комедия'),
(2, 'Драма'),
(3, 'Мультфильм'),
(4, 'Триллер'),
(5, 'Документальный'),
(6, 'Боевик');

INSERT INTO RATINGS (id, rating)
VALUES (1, 'G'),
(2, 'PG'),
(3, 'PG-13'),
(4, 'R'),
(5, 'NC-17');

