-- In some cases / SQL queries the ID of a specific graph version has to be selected. Doing this by a subselect the query planner is not able
-- to plan correctly considering the check constraint on inherited waysegments and waysegment_connections tables. One solution can be to use
-- an immutable function returning the requested graph version ID.
-- Following example returns the ID of the latest (current) active graph version:

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