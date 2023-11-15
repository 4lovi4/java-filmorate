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


INSERT INTO users (email, login, birthday, name)
VALUES ('alex@mail.il', 'alex', '2000-12-01', 'Алексей Фролович'),
('maria@mail.il', 'maria', '2001-12-02', 'Мария Простая'),
('bob@mail.il', 'bob', '2005-10-01', 'Marley'),
('alisa@mail.il', 'alisa', '2004-01-01', 'Wonder');


INSERT INTO films (name, description, release_date, duration, rating_id)
VALUES('В августе 44', 'Опергруппа капитана Алехина устраивает засаду на немецких агентов.',
'2001-01-17', 118, 2);


INSERT INTO films (name, description, release_date, duration, rating_id)
VALUES('Луна', '2029 год. Южная Корея отправляет на Луну трех космонавтов', 
'2023-07-19', 129, 3);


INSERT INTO films_genres VALUES
((SELECT ID FROM FILMS WHERE NAME = 'В августе 44'), 6),
((SELECT ID FROM FILMS WHERE NAME = 'В августе 44'), 2);

INSERT INTO films_genres VALUES
((SELECT ID FROM FILMS WHERE NAME = 'Луна'), 2),
((SELECT ID FROM FILMS WHERE NAME = 'Луна'), 4);