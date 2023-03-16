-- Создание

create table user_entity
(
    id       integer not null
        constraint user_entity_pk
            primary key autoincrement,
    login    text    not null constraint user_entity_pk2 unique,
    name     text    not null,
    password text    not null,
    img      blob
);

create table user_entity_roles
(
    user_entity_id integer not null
        constraint user_entity_roles_user_entity_id_fk
            references user_entity,
    roles          text    not null
);

create table quote_source_type_entity
(
    id   integer not null
        constraint quote_source_type_entity_pk
            primary key autoincrement,
    type text not null
);

create table quote_source_entity
(
    id          integer not null
        constraint quote_source_entity_pk
            primary key autoincrement,
    source_name text    not null,
    type_id     integer not null
        constraint quote_source_entity_quote_source_type_entity_id_fk
            references quote_source_type_entity
);

create table quote_entity
(
    id        integer not null
        constraint quote_entity_pk
            primary key autoincrement,
    "text"      text    not null,
    author_id integer not null
        constraint quote_entity_user_entity_id_fk
            references user_entity,
    source_id integer not null
        constraint quote_entity_quote_source_entity_id_fk
            references quote_source_entity
);
