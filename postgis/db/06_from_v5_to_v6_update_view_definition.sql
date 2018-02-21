ALTER TABLE graphs.waygraph_view_metadata
  DROP COLUMN filter;
ALTER TABLE graphs.waygraph_view_metadata
  ADD COLUMN dbviewname character varying(255);
ALTER TABLE graphs.waygraph_view_metadata
  ADD COLUMN waysegments_included boolean;
  
select graphs.db_schema_changed(6, '06_from_v5_to_v6_update_view_definition.sql');