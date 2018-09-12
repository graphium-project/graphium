INSERT INTO graphs.accesses ("type", id) VALUES ('NONE', -1);

SELECT graphs.db_schema_changed(8, '08_from_v7_to_v8_add_access_type_none.sql');