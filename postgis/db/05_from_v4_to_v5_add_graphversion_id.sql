ALTER TABLE graphs.waysegments
  ADD COLUMN graphversion_id integer;
ALTER TABLE graphs.waysegment_connections
  ADD COLUMN graphversion_id integer;
  
select graphs.db_schema_changed(5, '05_from_v4_to_v5_add_graphversion_id.sql');