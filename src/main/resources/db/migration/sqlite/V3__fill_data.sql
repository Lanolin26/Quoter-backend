begin transaction;
INSERT INTO user_entity
VALUES (1, 'admin', 'Administrator', '1234567', NULL);

INSERT INTO user_entity_roles
VALUES (1, 'ADMIN');
commit transaction;

