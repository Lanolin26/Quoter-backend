-- Создание

create table user_entity
(
    id       integer auto_increment not null,
    login    varchar(255) not null,
    name     varchar(255) not null,
    password varchar(255) not null,
    img      varchar(255),
    constraint pk_user_entity primary key (id)
);

create table user_entity_roles (
    user_entity_id int4 not null,
    roles varchar(64)
);

alter table if exists user_entity_roles
    add constraint FK_user_entity_roles_id
        foreign key (user_entity_id)
        references user_entity;

create table quote_from_type_entity
(
    id   integer auto_increment not null,
    type varchar(255) not null,
    constraint pk_quote_from_type_entity primary key (id)
);

create table quote_from_entity
(
    id        integer auto_increment not null,
    from_name varchar(255) not null,
    type_id   int       not null,
    constraint pk_quote_from_entity primary key (id)
);

alter table quote_from_entity
    add constraint fk_quote_from_entity_on_type
        foreign key (type_id)
        references  quote_from_type_entity (id);

create table quote_entity
(
    id           integer auto_increment not null,
    text         varchar(255) not null,
    author_id    int       not null,
    from_name_id int       not null,
    constraint pk_quote_entity primary key (id)
);

alter table quote_entity
    add constraint fk_quote_entity_on_author
        foreign key (author_id)
        references user_entity (id);

alter table quote_entity
    add constraint fk_quote_entity_on_fromname
        foreign key (from_name_id)
        references quote_from_entity (id);