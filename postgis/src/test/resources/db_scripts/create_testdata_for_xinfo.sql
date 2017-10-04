CREATE TABLE graphs.xinfo_test
(
  directed_id bigint,
  CONSTRAINT xinfo_test_waygraph_fk FOREIGN KEY (graph_id)
      REFERENCES graphs.waygraphs (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
INHERITS (graphs.xinfo)
WITH (
  OIDS=FALSE
);
ALTER TABLE graphs.xinfo_test
  OWNER TO graphserver;
  
WITH segments AS (
	SELECT id, true AS direction_tow FROM graphs.waysegments_gip_at_frc_0_4_15_10_151222a
	UNION
	SELECT id, false AS direction_tow FROM graphs.waysegments_gip_at_frc_0_4_15_10_151222a
)
INSERT INTO graphs.xinfo_test (segment_id, direction_tow, graph_id, directed_id)  
	SELECT id, direction_tow, 5, CASE WHEN direction_tow = true THEN id ELSE (9223372036854775807 - id) END FROM segments;
	
INSERT INTO graphs.waygraph_view_metadata (viewname, graph_id, filter, creation_timestamp) VALUES
	('gip_at_frc_0_4_xinfo_test', 5, 'SELECT wayseg.*, xit.* FROM graphs.waysegments AS wayseg LEFT OUTER JOIN graphs.xinfo_test xit ON xit.segment_id = wayseg.id LIMIT 100', now());