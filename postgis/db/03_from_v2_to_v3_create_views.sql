CREATE TABLE graphs.waygraph_view_metadata
(
  viewname character varying(255) NOT NULL,
  graph_id bigint NOT NULL,
  filter character varying(2048),
  covered_area geometry,
  segments_count integer,
  connections_count integer,
  tags hstore,
  creation_timestamp timestamp with time zone DEFAULT now(),
  CONSTRAINT graphs_waygraph_view_metadata_pk PRIMARY KEY (viewname),
  CONSTRAINT graphs_waygraph_view_metadata_waygraphs_fk FOREIGN KEY (graph_id)
      REFERENCES graphs.waygraphs (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE graphs.waygraph_view_metadata OWNER TO graphium;

select graphs.db_schema_changed(3, '03_from_v2_to_v3_create_views.sql');