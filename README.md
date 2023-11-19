# java-filmorate
Template repository for Filmorate project.

![ER Diagram filmorate DB](https://4.downloader.disk.yandex.ru/preview/33ce2f7826945936f11dbb9b429bb5d604c42999fa22b1b6ab112f4f86247947/inf/qdJo1q1Qxxr9t3vYxCqGWcYK_z0OpCJjjOVVrE_3si0aDRpvSj6GvBEYtTpB-HmfncJ-veJCShGtbXxYVXC5RA%3D%3D?uid=82823884&filename=erd1.png&disposition=inline&hash=&limit=0&content_type=image%2Fpng&owner_uid=82823884&tknv=v2&size=3456x1824)

Примеры запросов на добавление/редактирование данных в БД

```roomsql
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


SELECT U.* FROM USERS u;
SELECT * FROM FRIENDS f;
SELECT f.* FROM FRIENDS f WHERE f.user_id = 1;


SELECT g.GENRE FROM GENRES g 
JOIN FILMS_GENRES fg 
ON g.ID = fg.GENRE_ID 
WHERE fg.FILM_ID = 0;

SELECT f.*, r.RATING  FROM FILMS f LEFT 
JOIN RATINGS r ON f.RATING_ID = r.ID WHERE f.ID = 3;

SELECT * FROM LIKES l;

select f.*, r.rating from films f LEFT join ratings r on f.rating_id = r.id;

select g.genre from genres g 
join films_genres fg on g.id = fg.genre_id 
where fg.film_id = 2;
```