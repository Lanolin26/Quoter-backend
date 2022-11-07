alter table quote_from_entity
    rename to quote_source_entity;

alter table quote_from_type_entity
    rename to quote_source_type_entity;

alter table quote_source_entity
    alter column FROM_NAME rename to source_name;

alter table quote_entity
    alter column from_name_id rename to source_id;
