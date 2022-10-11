INSERT INTO user_entity (id, login, name, password, img)
VALUES (NEXTVAL('hibernate_sequence'), 'test-1', 'User 1', 'user-1', null),
       (NEXTVAL('hibernate_sequence'), 'test-2', 'User 2', 'user-2', null),
       (NEXTVAL('hibernate_sequence'), 'test-3', 'User 3', 'user-3', null);


INSERT INTO QUOTER_TYPE_ENTITY (id, type)
VALUES (NEXTVAL('hibernate_sequence'), 'Anime'),
       (NEXTVAL('hibernate_sequence'), 'Book'),
       (NEXTVAL('hibernate_sequence'), 'Film');

INSERT INTO quotes_entity (id, text, author_id, from_type_id, FROM_NAME)
VALUES (NEXTVAL('hibernate_sequence'), 'u1', 1, 4, 'Anim 1'),
       (NEXTVAL('hibernate_sequence'), 'u2', 2, 5, 'Book 1'),
       (NEXTVAL('hibernate_sequence'), 'u3', 3, 4, 'Anim 2'),
       (NEXTVAL('hibernate_sequence'), 'u4', 1, 5, 'Book 2'),
       (NEXTVAL('hibernate_sequence'), 'u5', 2, 4, 'Anim 3'),
       (NEXTVAL('hibernate_sequence'), 'u6', 3, 5, 'Book 4');