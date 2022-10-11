create sequence hibernate_sequence start with 1 increment by 1;

create table quoter_type_entity
(
    id   integer not null,
    type varchar(255),
    primary key (id)
);

create table quotes_entity
(
    id           integer not null,
    from_name    varchar(255),
    text         varchar(255),
    author_id    integer,
    from_type_id integer,
    primary key (id)
);

create table user_entity
(
    id       integer not null,
    img      varchar(255),
    login    varchar(255),
    name     varchar(255),
    password varchar(255),
    primary key (id)
);

alter table quotes_entity
    add constraint FKcrguf79fg0gsq94ouxw8jwkct foreign key (author_id) references user_entity;

alter table quotes_entity
    add constraint FKq825k3tda3mlxf6mwnodi9l1f foreign key (from_type_id) references quoter_type_entity;