CREATE TABLE graphs.hdareas
(
    id bigint NOT NULL,
    geometry geometry,
    type character varying(128),
    tags hstore,
    graphversion_id integer,
    -- required?
    "timestamp" timestamp without time zone NOT NULL,
    CONSTRAINT graphs_hdareas_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE graphs.hdareas OWNER TO graphium;

CREATE TABLE graphs.hdinfra_and_signs
(
    id bigint NOT NULL,
    geometry geometry,
    type character varying(128),
    tags hstore,
    graphversion_id integer,
    -- required?
    "timestamp" timestamp without time zone NOT NULL,
    CONSTRAINT graphs_hdinfra_and_sign_pk PRIMARY KEY (id)
)
    WITH (
        OIDS=FALSE
        );
ALTER TABLE graphs.hdinfra_and_signs OWNER TO graphium;

select graphs.db_schema_changed(10, '010_from_v9_to_10_add_hd_area_infrandsign_tables.sql');