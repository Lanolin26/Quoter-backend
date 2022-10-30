alter table quote_entity
    alter column "text" type text using text::text;

alter table quote_source_entity
    alter column source_name type text using source_name::text;
