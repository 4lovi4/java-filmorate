CREATE TABLE IF NOT EXISTS users (
id INT PRIMARY KEY auto_increment,
email VARCHAR(128) NOT NULL UNIQUE,
login VARCHAR(128) NOT NULL,
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

