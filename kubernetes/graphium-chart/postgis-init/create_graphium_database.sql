CREATE EXTENSION IF NOT EXISTS hstore;
CREATE EXTENSION IF NOT EXISTS postgis;

CREATE SCHEMA IF NOT EXISTS graphs AUTHORIZATION graphium;

-- Table: graphs.schema_versioning

-- DROP TABLE graphs.schema_versioning;
CREATE TABLE graphs.schema_versioning
(
  version integer NOT NULL,
  script_name character varying(255) NOT NULL,
  module_name character varying(255) NOT NULL DEFAULT 'graphium-core' ,
  execution_date timestamp with time zone NOT NULL DEFAULT now(),
  executor character varying(255) NOT NULL DEFAULT "current_user"(),
  CONSTRAINT graphs_schema_versioning_pk PRIMARY KEY (module_name, version)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE graphs.schema_versioning
  OWNER TO graphium;

--DROP FUNCTION graphs.graphium_version(module char);
CREATE FUNCTION graphs.graphium_version(module char)
  RETURNS integer AS $$
	SELECT max(version) AS version FROM
	    graphs.schema_versioning
		WHERE module_name = module
$$
LANGUAGE SQL;
ALTER FUNCTION graphs.graphium_version(char)
  OWNER TO graphium;
  
--DROP FUNCTION graphs.graphium_version();
CREATE FUNCTION graphs.graphium_version()
  RETURNS integer AS $$
	SELECT graphs.graphium_version('graphium-core');
$$
LANGUAGE SQL;

--DROP FUNCTION graphs.db_schema_changed(version integer, script_name char, module char)
CREATE FUNCTION graphs.db_schema_changed(version integer, script_name char, module_name char)
  RETURNS void AS $$
  INSERT INTO graphs.schema_versioning (version, script_name, module_name) VALUES (version, script_name, module_name);
$$
LANGUAGE SQL;


--DROP FUNCTION graphs.db_schema_changed(version integer, script_name char)
CREATE FUNCTION graphs.db_schema_changed(version integer, script_name char)
  RETURNS void AS $$
	SELECT graphs.db_schema_changed(version, script_name, 'graphium-core');
$$
LANGUAGE SQL;


CREATE TABLE graphs.accesses
(
  id smallint NOT NULL,
  "type" character varying(512),
  CONSTRAINT graphs_accesses_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE graphs.accesses OWNER TO graphium;

CREATE TABLE graphs.waysegments
(
  id bigint NOT NULL,
  geometry geometry,
  geometry_900913 geometry,
  name character varying(512),
  maxspeed_tow smallint NOT NULL,
  maxspeed_bkw smallint NOT NULL,
  speed_calc_tow smallint NOT NULL,
  speed_calc_bkw smallint NOT NULL,
  lanes_tow smallint NOT NULL DEFAULT 1,
  lanes_bkw smallint NOT NULL DEFAULT 1,
  frc smallint NOT NULL,
  formofway decimal(3),
  streettype character varying(512),
  way_id bigint NOT NULL,
  startnode_id bigint NOT NULL,
  startnode_index integer NOT NULL,
  endnode_id bigint NOT NULL,
  endnode_index integer NOT NULL,
  access_tow smallint[],
  access_bkw smallint[],
  tunnel boolean NOT NULL DEFAULT false,
  bridge boolean NOT NULL DEFAULT false,
  urban boolean,
  "timestamp" timestamp without time zone NOT NULL,
  tags hstore,
  CONSTRAINT graphs_waysegments_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE graphs.waysegments OWNER TO graphium;

CREATE TABLE graphs.waysegment_connections
(
  node_id bigint NOT NULL,
  from_segment_id bigint NOT NULL,
  to_segment_id bigint NOT NULL,
  access smallint[] NOT NULL,
  CONSTRAINT graphs_waysegment_connections_pk PRIMARY KEY (node_id, from_segment_id, to_segment_id),
  CONSTRAINT graphs_waysegment_connections_from_segment_id_fk FOREIGN KEY (from_segment_id)
      REFERENCES graphs.waysegments (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT graphs_waysegment_connections_to_segment_id_fk FOREIGN KEY (to_segment_id)
      REFERENCES graphs.waysegments (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE graphs.waysegment_connections OWNER TO graphium;

CREATE TABLE graphs.waygraphs
(
  id bigserial,
  name character varying(255) NOT NULL,
  CONSTRAINT waygraphs_pk PRIMARY KEY (id),
  CONSTRAINT waygraphs_name_unq UNIQUE (name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE graphs.waygraphs OWNER TO graphium;

CREATE TABLE graphs.sources
(
  id serial NOT NULL,
  name character varying(255) NOT NULL,
  CONSTRAINT graphs_sources_pk PRIMARY KEY (id),
  CONSTRAINT graphs_sources_unq UNIQUE (name)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE graphs.sources OWNER TO graphium;

CREATE TABLE graphs.waygraphmetadata
(
  id serial NOT NULL,
  graph_id bigint NOT NULL,
  graphname character varying(255) NOT NULL,
  version character varying(255) NOT NULL,
  origin_graphname character varying(255) NOT NULL,
  origin_version character varying(255) NOT NULL,
  state character varying(16) NOT NULL,
  valid_from timestamp without time zone NOT NULL,
  valid_to timestamp without time zone,
  covered_area geometry NOT NULL,
  segments_count integer NOT NULL DEFAULT 0,
  connections_count integer NOT NULL DEFAULT 0,
  accesstypes smallint[],
  tags hstore,
  source_id integer NOT NULL,
  "type" character varying(255),
  description character varying(255),
  creation_timestamp timestamp without time zone NOT NULL,
  storage_timestamp timestamp without time zone NOT NULL,
  creator character varying(255) NOT NULL,
  origin_url character varying(255),
  CONSTRAINT graphs_waygraphmetadata_pk PRIMARY KEY (id),
  CONSTRAINT graphs_waygraphmetadata_waygraphs_id_fk FOREIGN KEY (graph_id)
      REFERENCES graphs.waygraphs (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT graphs_waygraphmetadata_waygraphs_name_fk FOREIGN KEY (graphname)
      REFERENCES graphs.waygraphs (name) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT graphs_waygraphmetainfo_sources_fk FOREIGN KEY (source_id)
      REFERENCES graphs.sources (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT graphs_waygraphmetadata_unq UNIQUE (graphname, version)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE graphs.waygraphmetadata OWNER TO graphium;

CREATE TABLE graphs.xinfo
(
  segment_id bigint NOT NULL,
  direction_tow boolean
)
WITH (
  OIDS=FALSE
);
ALTER TABLE graphs.xinfo OWNER TO graphium;

CREATE TABLE graphs.default_xinfo
(
  segment_id bigint NOT NULL,
  direction_tow boolean,
  graphversion_id bigint,
  tags hstore
)
WITH (
  OIDS=FALSE
);
ALTER TABLE graphs.default_xinfo OWNER TO graphium;

INSERT INTO graphs.accesses ("type", id) VALUES ('PEDESTRIAN', 1);
INSERT INTO graphs.accesses ("type", id) VALUES ('BIKE', 2);
INSERT INTO graphs.accesses ("type", id) VALUES ('PRIVATE_CAR', 3);
INSERT INTO graphs.accesses ("type", id) VALUES ('PUBLIC_BUS', 4);
INSERT INTO graphs.accesses ("type", id) VALUES ('RAILWAY', 5);
INSERT INTO graphs.accesses ("type", id) VALUES ('TRAM', 6);
INSERT INTO graphs.accesses ("type", id) VALUES ('SUBWAY', 7);
INSERT INTO graphs.accesses ("type", id) VALUES ('FERRY_BOAT', 8);
INSERT INTO graphs.accesses ("type", id) VALUES ('HIGH_OCCUPATION_CAR', 9);
INSERT INTO graphs.accesses ("type", id) VALUES ('TRUCK', 10);
INSERT INTO graphs.accesses ("type", id) VALUES ('TAXI', 11);
INSERT INTO graphs.accesses ("type", id) VALUES ('EMERGENCY_VEHICLE', 12);
INSERT INTO graphs.accesses ("type", id) VALUES ('MOTOR_COACH', 13);
INSERT INTO graphs.accesses ("type", id) VALUES ('TROLLY_BUS', 14);
INSERT INTO graphs.accesses ("type", id) VALUES ('MOTORCYCLE', 15);
INSERT INTO graphs.accesses ("type", id) VALUES ('RACK_RAILWAY', 16);
INSERT INTO graphs.accesses ("type", id) VALUES ('CABLE_RAILWAY', 17);
INSERT INTO graphs.accesses ("type", id) VALUES ('CAR_FERRY', 18);
INSERT INTO graphs.accesses ("type", id) VALUES ('CAMPER', 19);
INSERT INTO graphs.accesses ("type", id) VALUES ('COMBUSTIBLES', 20);
INSERT INTO graphs.accesses ("type", id) VALUES ('HAZARDOUS_TO_WATER', 21);
INSERT INTO graphs.accesses ("type", id) VALUES ('GARBAGE_COLLECTION_VEHICLE', 22);

select graphs.db_schema_changed(1, '01_to_v1_create_base_model.sql');CREATE TABLE graphs.subscription_groups
(
  id serial NOT NULL,
  name character varying(255) NOT NULL,
  graph_id bigint NOT NULL,
  CONSTRAINT groups_pk PRIMARY KEY (id),
  CONSTRAINT groups_unq UNIQUE (name),
  CONSTRAINT groups_waygraphs_fk FOREIGN KEY (graph_id)
    REFERENCES graphs.waygraphs (id) MATCH SIMPLE
	ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE graphs.subscription_groups OWNER TO graphium;

CREATE TABLE graphs.subscriptions
(
  servername character varying(255) NOT NULL,
  viewname character varying(255) NOT NULL,
  group_id integer,
  url character varying(512) NOT NULL,
  "user" character varying(255),
  "password" character varying(255),
  "timestamp" timestamp with time zone DEFAULT now(),
  CONSTRAINT graphs_subscriptions_pk PRIMARY KEY (servername, viewname),
  CONSTRAINT graphs_subscriptions_groups_fk FOREIGN KEY (group_id)
      REFERENCES graphs.subscription_groups (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION

)
WITH (
  OIDS=FALSE
);
ALTER TABLE graphs.subscriptions OWNER TO graphium;

-- ONLY USED BY CENTRAL GRAPH SERVER --
CREATE TABLE graphs.graph_import_states
(
  servername character varying(255) NOT NULL,
  viewname character varying(255) NOT NULL,
  version character varying(255) NOT NULL,
  state character varying(16) NOT NULL,
  "timestamp" timestamp with time zone, 
  CONSTRAINT graph_import_states_pk PRIMARY KEY (servername, viewname, version),
  CONSTRAINT graph_import_states_subscriptions_fk FOREIGN KEY (servername, viewname)
      REFERENCES graphs.subscriptions (servername, viewname) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE graphs.graph_import_states OWNER TO graphium;

select graphs.db_schema_changed(2, '02_from_v1_to_v2_create_subscriptions.sql');CREATE TABLE graphs.waygraph_view_metadata
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

ALTER TABLE graphs.waysegments
  DROP COLUMN geometry_900913;
ALTER TABLE graphs.waysegments
  ADD COLUMN length real;
-- TODO: migrate data, set length to st_length

UPDATE graphs.waysegments set length = ST_length(geometry,true);

  
select graphs.db_schema_changed(4, '04_from_v3_to_v4_remove_geom900913_add_length.sql');ALTER TABLE graphs.waysegments
  ADD COLUMN graphversion_id integer;
ALTER TABLE graphs.waysegment_connections
  ADD COLUMN graphversion_id integer;
  
select graphs.db_schema_changed(5, '05_from_v4_to_v5_add_graphversion_id.sql');ALTER TABLE graphs.waygraph_view_metadata
  DROP COLUMN filter;
ALTER TABLE graphs.waygraph_view_metadata
  ADD COLUMN dbviewname character varying(255);
ALTER TABLE graphs.waygraph_view_metadata
  ADD COLUMN waysegments_included boolean;
  
select graphs.db_schema_changed(6, '06_from_v5_to_v6_update_view_definition.sql');ALTER TABLE graphs.graph_import_states DROP CONSTRAINT graph_import_states_subscriptions_fk;

ALTER TABLE graphs.graph_import_states
  ADD CONSTRAINT graph_import_states_subscriptions_fk FOREIGN KEY (servername, viewname)
      REFERENCES graphs.subscriptions (servername, viewname) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE CASCADE;

select graphs.db_schema_changed(7, '07_from_v6_to_v7_correct_subscription_fk_constraints.sql');

CREATE FUNCTION f_current_graphversion_immutable(in_graph text, in_version text)
  RETURNS integer AS
$func$
	select id as graphversion_id from graphs.waygraphmetadata
        where graph_id = (select graph_id as graph_id from graphs.waygraph_view_metadata  where viewname = in_graph)
        and
        case when 'null' != in_version
            THEN version = in_version
            ELSE state = 'ACTIVE'

        END
        order by valid_from desc limit 1
$func$  LANGUAGE sql IMMUTABLE;
ALTER FUNCTION f_current_graphversion_immutable(text, text)
  OWNER TO graphium;