begin transaction;
INSERT INTO user_entity
VALUES (1, 'admin', 'Administrator', '1234567', NULL);
commit transaction;

begin transaction;
INSERT INTO user_entity_roles
VALUES (1, 'ADMIN');
commit transaction;

begin transaction;
INSERT INTO quote_source_type_entity
VALUES (1, 'Книга'),
(2, 'Аниме'),
(3, 'Фильм'),
(4, 'Сериал'),
(5, 'Мультфильм'),
(6, 'Unknown Type');
commit transaction;
