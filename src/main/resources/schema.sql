


CREATE TABLE IF NOT EXISTS users (
id INT primary key,
email VARCHAR(64) not NULL,
login VARCHAR(64) not null,
birhday DATE);

drop table users;

select * from users;

create table if not exists films (
id INT primary key,
name VARCHAR(64) not NULL,
description VARCHAR(200),
release_date DATE,
duration INT,
genre_id INT,
rating_id INT);


create table if not exists friends (
user_id INT,
friend_id INT
);


create table if not exists likes (
film_id INT,
user_id INT
);

create table if not exists genres (
id INT primary key,
genre VARCHAR(64));

create table if not exists ratings (
id INT primary key,
rating VARCHAR(5));