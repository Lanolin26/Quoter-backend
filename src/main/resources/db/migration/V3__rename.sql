alter table quote_from_entity
    rename to quote_source_entity;

alter table quote_from_type_entity
    rename to quote_source_type_entity;

alter table quote_source_entity
    rename column from_name to source_name;

alter table quote_entity
    rename column from_name_id to source_id;
