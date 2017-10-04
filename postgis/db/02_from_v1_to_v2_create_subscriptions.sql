CREATE TABLE graphs.subscription_groups
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

select graphs.db_schema_changed(2, '02_from_v1_to_v2_create_subscriptions.sql');