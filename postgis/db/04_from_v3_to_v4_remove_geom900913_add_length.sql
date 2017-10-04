

ALTER TABLE graphs.waysegments
  DROP COLUMN geometry_900913;
ALTER TABLE graphs.waysegments
  ADD COLUMN length real;
-- TODO: migrate data, set length to st_length

UPDATE graphs.waysegments set length = ST_length(geometry,true);

  
select graphs.db_schema_changed(4, '04_from_v3_to_v4_remove_geom900913_add_length.sql');