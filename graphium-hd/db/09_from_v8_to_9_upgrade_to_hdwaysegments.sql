CREATE TABLE graphs.hdwaysegments
(
  left_boarder_geometry geometry(LINESTRING, 4326),
  left_boarder_startnode_id bigint NOT NULL,
  left_boarder_endnode_id bigint NOT NULL,
  right_boarder_geometry geometry(LINESTRING, 4326),
  right_boarder_startnode_id bigint NOT NULL,
  right_boarder_endnode_id bigint NOT NULL,
  CONSTRAINT graphs_hdwaysegments_pk PRIMARY KEY (id)
) INHERITS (graphs.waysegments)
WITH (
  OIDS=FALSE
);
ALTER TABLE graphs.hdwaysegments OWNER TO graphium;

ALTER TABLE graphs.waysegment_connections ADD COLUMN tags hstore;

select graphs.db_schema_changed(9, '09_from_v8_to_v9_update_to_hdwaysegments.sql');