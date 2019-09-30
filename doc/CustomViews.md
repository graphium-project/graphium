# Custom Views

Unforunately there is currently no comfortable way to define custom views. Custom views have to be defined via SQL in PostgreSQL.

Default-View
------------

At import of a graph version a default view will be created implicitly. The name of this default view has the pattern:

```
vw_<graphname>
```

This default view is valid for all versions of the specified graph. Within internal processing the view will be extended by selecting all data having the requested graphversion\_id, e.g.:

```
SELECT * FROM graphs.vw_osm_at WHERE graphversion_id = 1;
```

The definition of the default view looks like:

```
CREATE OR REPLACE VIEW graphs.vw_osm_at AS 
 SELECT wayseg.id,
    wayseg.graphversion_id AS wayseg_graphversion_id,
    st_asewkb(wayseg.geometry) AS wayseg_geometry,
    wayseg.length AS wayseg_length,
    wayseg.name AS wayseg_name,
    wayseg.maxspeed_tow AS wayseg_maxspeed_tow,
    wayseg.maxspeed_bkw AS wayseg_maxspeed_bkw,
    wayseg.speed_calc_tow AS wayseg_speed_calc_tow,
    wayseg.speed_calc_bkw AS wayseg_speed_calc_bkw,
    wayseg.lanes_tow AS wayseg_lanes_tow,
    wayseg.lanes_bkw AS wayseg_lanes_bkw,
    wayseg.frc AS wayseg_frc,
    wayseg.formofway AS wayseg_formofway,
    wayseg.streettype AS wayseg_streettype,
    wayseg.way_id AS wayseg_way_id,
    wayseg.startnode_id AS wayseg_startnode_id,
    wayseg.startnode_index AS wayseg_startnode_index,
    wayseg.endnode_id AS wayseg_endnode_id,
    wayseg.endnode_index AS wayseg_endnode_index,
    wayseg.access_tow::integer[] AS wayseg_access_tow,
    wayseg.access_bkw::integer[] AS wayseg_access_bkw,
    wayseg.tunnel AS wayseg_tunnel,
    wayseg.bridge AS wayseg_bridge,
    wayseg.urban AS wayseg_urban,
    wayseg."timestamp" AS wayseg_timestamp,
    wayseg.tags AS wayseg_tags,
    COALESCE(startnodesegments.startnodesegments, '{NULL}') AS startnodesegments,
	COALESCE(endnodesegments.endnodesegments, '{NULL}') AS endnodesegments
   FROM graphs.waysegments wayseg
   	LEFT OUTER JOIN
		LATERAL (select array_agg(con_start.*::character varying) AS startnodesegments,
				 con_start.graphversion_id
			  FROM graphs.waysegment_connections con_start 
			  WHERE con_start.node_id = wayseg.startnode_id 
			  AND con_start.from_segment_id = wayseg.id 
		GROUP BY con_start.from_segment_id, con_start.graphversion_id
		) AS startnodesegments
		ON startnodesegments.graphversion_id = wayseg.graphversion_id
	LEFT OUTER JOIN
		LATERAL (select array_agg(con_end.*::character varying) AS endnodesegments,
				 con_end.graphversion_id
			  FROM graphs.waysegment_connections con_end 
			  WHERE con_end.node_id = wayseg.endnode_id 
			  AND con_end.from_segment_id = wayseg.id 
		GROUP BY con_end.from_segment_id, con_end.graphversion_id
		) AS endnodesegments
		ON endnodesegments.graphversion_id = wayseg.graphversion_id;
```

Custom-View
-----------

If the default view doesn't fulfills ones needs custom views can be defined. Some conventions have to be followed:

* The view's name has to be prefixed by "vw_".
* Attribute names have to be prefixed by the one defined in associated row mappers. For default waysegment table the prefix is "wayseg_". The one and only exception is the attribute "id" of the waysegment table.
* Because of this "SELECT * ..." is forbidden.
* For JOIN-clauses the attribute *graphversion_id* of *waysegment* table can be used.
* For JOIN-clauses requiring a *graph_id* a subselect on table *waygraphmetadata* has to be defined.
* Don't use LIMIT.
* The view has to be granted correctly.

### Register custom view

Each custom view has to be registered to the Graphium server by inserting an entry in the table *waygraph_view_metadata*:

- viewname: name of the view *without* prefix "vw_"
- dbviewname: name of the view *with* prefix "vw_"
- graph_id: graph id of the parent graph

Example:

```
"osm_at";5;"";0;0;"";"2017-05-09 09:13:44.743+02";"vw_osm_at";TRUE
```

### Example restriction on functional road class:
```
CREATE OR REPLACE VIEW graphs.vw_osm_at_frc_0 AS 
SELECT wayseg.id,
    wayseg.graphversion_id AS wayseg_graphversion_id,
    st_asewkb(wayseg.geometry) AS wayseg_geometry,

 	...

    wayseg."timestamp" AS wayseg_timestamp,
    wayseg.tags AS wayseg_tags,
    COALESCE(startnodesegments.startnodesegments, '{NULL}') AS startnodesegments,
	COALESCE(endnodesegments.endnodesegments, '{NULL}') AS endnodesegments
   FROM graphs.waysegments wayseg
    LEFT OUTER JOIN
		LATERAL (select array_agg(con_start.*::character varying) AS startnodesegments,
				 con_start.graphversion_id
			  FROM graphs.waysegment_connections con_start 
			  WHERE con_start.node_id = wayseg.startnode_id 
			  AND con_start.from_segment_id = wayseg.id 
		GROUP BY con_start.from_segment_id, con_start.graphversion_id
		) AS startnodesegments
		ON startnodesegments.graphversion_id = wayseg.graphversion_id
	LEFT OUTER JOIN
		LATERAL (select array_agg(con_end.*::character varying) AS endnodesegments,
				 con_end.graphversion_id
			  FROM graphs.waysegment_connections con_end 
			  WHERE con_end.node_id = wayseg.endnode_id 
			  AND con_end.from_segment_id = wayseg.id 
		GROUP BY con_end.from_segment_id, con_end.graphversion_id
		) AS endnodesegments
		ON endnodesegments.graphversion_id = wayseg.graphversion_id 

WHERE wayseg.frc >= 0 AND wayseg.frc < 1;
```

### Example join a XInfo table:

```
CREATE OR REPLACE VIEW graphs.vw_osm_at_xinfo AS 
SELECT wayseg.id,
    wayseg.graphversion_id AS wayseg_graphversion_id,
    st_asewkb(wayseg.geometry) AS wayseg_geometry,

 	...

    wayseg."timestamp" AS wayseg_timestamp,
    wayseg.tags AS wayseg_tags,
    COALESCE(startnodesegments.startnodesegments, '{NULL}') AS startnodesegments,
	COALESCE(endnodesegments.endnodesegments, '{NULL}') AS endnodesegments,

    def.tags
   
   FROM graphs.waysegments wayseg
   	LEFT OUTER JOIN
		LATERAL (select array_agg(con_start.*::character varying) AS startnodesegments,
				 con_start.graphversion_id
			  FROM graphs.waysegment_connections con_start 
			  WHERE con_start.node_id = wayseg.startnode_id 
			  AND con_start.from_segment_id = wayseg.id 
		GROUP BY con_start.from_segment_id, con_start.graphversion_id
		) AS startnodesegments
		ON startnodesegments.graphversion_id = wayseg.graphversion_id
	LEFT OUTER JOIN
		LATERAL (select array_agg(con_end.*::character varying) AS endnodesegments,
				 con_end.graphversion_id
			  FROM graphs.waysegment_connections con_end 
			  WHERE con_end.node_id = wayseg.endnode_id 
			  AND con_end.from_segment_id = wayseg.id 
		GROUP BY con_end.from_segment_id, con_end.graphversion_id
		) AS endnodesegments
		ON endnodesegments.graphversion_id = wayseg.graphversion_id
    
    LEFT OUTER JOIN graphs.default_xinfo def ON (def.segment_id = wayseg.id);
```

### Example join a XInfo table and restrict on graphversion_id:

```
CREATE OR REPLACE VIEW graphs.vw_osm_at_xinfo AS 
SELECT wayseg.id,
    wayseg.graphversion_id AS wayseg_graphversion_id,
    st_asewkb(wayseg.geometry) AS wayseg_geometry,

 	...

    wayseg."timestamp" AS wayseg_timestamp,
    wayseg.tags AS wayseg_tags,
    COALESCE(startnodesegments.startnodesegments, '{NULL}') AS startnodesegments,
	COALESCE(endnodesegments.endnodesegments, '{NULL}') AS endnodesegments,

    def.tags
   
   FROM graphs.waysegments wayseg
   	LEFT OUTER JOIN
		LATERAL (select array_agg(con_start.*::character varying) AS startnodesegments,
				 con_start.graphversion_id
			  FROM graphs.waysegment_connections con_start 
			  WHERE con_start.node_id = wayseg.startnode_id 
			  AND con_start.from_segment_id = wayseg.id 
		GROUP BY con_start.from_segment_id, con_start.graphversion_id
		) AS startnodesegments
		ON startnodesegments.graphversion_id = wayseg.graphversion_id
	LEFT OUTER JOIN
		LATERAL (select array_agg(con_end.*::character varying) AS endnodesegments,
				 con_end.graphversion_id
			  FROM graphs.waysegment_connections con_end 
			  WHERE con_end.node_id = wayseg.endnode_id 
			  AND con_end.from_segment_id = wayseg.id 
		GROUP BY con_end.from_segment_id, con_end.graphversion_id
		) AS endnodesegments
		ON endnodesegments.graphversion_id = wayseg.graphversion_id
    
    LEFT OUTER JOIN graphs.default_xinfo def ON (def.segment_id = wayseg.id AND def.graphversion_id = wayseg.graphversion_id);
```