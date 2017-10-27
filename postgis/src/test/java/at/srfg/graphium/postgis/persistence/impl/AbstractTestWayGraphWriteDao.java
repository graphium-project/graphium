package at.srfg.graphium.postgis.persistence.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import at.srfg.graphium.core.exception.GraphAlreadyExistException;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.exception.GraphStorageException;
import at.srfg.graphium.core.persistence.ISourceDao;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.persistence.IWayGraphViewDao;
import at.srfg.graphium.core.persistence.IWayGraphWriteDao;
import at.srfg.graphium.geomutils.GeometryUtils;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.FuncRoadClass;
import at.srfg.graphium.model.ISource;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.State;
import at.srfg.graphium.model.impl.WaySegment;
import at.srfg.graphium.model.management.impl.Source;
import at.srfg.graphium.postgis.model.impl.XInfoTest;

public class AbstractTestWayGraphWriteDao {

	private static Logger log = LoggerFactory.getLogger(AbstractTestWayGraphWriteDao.class);

	@Autowired
	private ISourceDao sourceDao;
	
	@Autowired
	protected IWayGraphWriteDao<IWaySegment> writeDao;

	@Autowired
	protected IWayGraphVersionMetadataDao metadataDao;
	
	@Autowired
	protected IWayGraphViewDao viewDao;
	
	protected IWaySegment writeTestSegment(String graphName, String version) throws GraphNotExistsException {
		IWaySegment segment = new WaySegment();
		Coordinate c1 = new Coordinate(13.1, 47.1);
		Coordinate c2 = new Coordinate(13.2, 47.2);
		LineString geom = GeometryUtils.createLineString(new Coordinate[] {c1, c2}, 4326);
		segment.setId(Long.MAX_VALUE);
		segment.setGeometry(geom);
		segment.setMaxSpeedBkw((short)50);
		segment.setMaxSpeedTow((short)50);
		segment.setSpeedCalcBkw((short)50);
		segment.setSpeedCalcTow((short)50);
		segment.setLanesBkw((short)1);
		segment.setLanesTow((short)1);
		segment.setFrc(FuncRoadClass.MOTORWAY_FREEWAY_OR_OTHER_MAJOR_MOTORWAY);
		segment.setWayId(segment.getId());
		segment.setStartNodeId(1);
		segment.setStartNodeIndex(1);
		segment.setEndNodeId(2);
		segment.setEndNodeIndex(2);
		segment.setTimestamp(new Date());
		segment.setTunnel(false);
		segment.setBridge(false);
		segment.setUrban(false);
		
		XInfoTest xInfo = new XInfoTest();
		xInfo.setDirectedId(1);
		xInfo.setDirectionTow(true);
		xInfo.setGraphId(1);
		xInfo.setSegmentId(segment.getId());
		segment.addXInfo(xInfo);
		
		List<IWaySegment> segments = new ArrayList<>();
		segments.add(segment);
		
		try {
			writeDao.saveSegments(segments, graphName, version);
		} catch (GraphStorageException e) {
			log.error("error during storing segments",e);
		}

		return segment;
	}
	
	protected IWayGraphVersionMetadata writeTestMetadata(String graphName, String version, State state) throws GraphAlreadyExistException, GraphNotExistsException {
		long graphId = 0;
		Date now = new Date();
		Map<String, String> tags = new HashMap<String, String>();
		tags.put("einSinnlosesTag", "holladrio");
		
		ISource source = sourceDao.getSource("testSource");
		if (source == null) {
			source = new Source(1, "testSource");
			sourceDao.save(source);
		}
		
		List<String> graphNames = metadataDao.getGraphs();
		for (String name : graphNames) {
			log.info("Waygraph '" + name + "'");
		}
		
		if (metadataDao.checkIfGraphExists(graphName)) {
			log.info("Waygraph '" + graphName + "' already exists");
		} else {
			log.info("Waygraph '" + graphName + "' does not exist");
			metadataDao.saveGraph(graphName);
			log.info("Waygraph '" + graphName + "' saved");
		}
		IWayGraph wayGraph = metadataDao.getGraph(graphName);
		graphId = wayGraph.getId();
		
		writeDao.createGraph(graphName, version, true);
		
		IWayGraphVersionMetadata metadata;
		log.info("\nCreating WayGraphVersionMetadata...");
		Set<Access> accessTypes = new HashSet<Access>();
		accessTypes.add(Access.PRIVATE_CAR);
		metadata = metadataDao.newWayGraphVersionMetadata(0, graphId, graphName, version, 
				graphName, "1.0_orig", state, now, null, getBoundsAustria(), 1000, 2000, accessTypes, tags, source, 
				"Graph for tests", "no desc...", now, now, "ich", "http://0815.echt.org");
		
		log.info("\nSaving WayGraphVersionMetadata...");
		
		metadataDao.saveGraphVersion(metadata);
		
		log.info("\nSaving default view...");
		
		viewDao.saveDefaultView(wayGraph);
		
		return metadata;
	}

	protected Polygon getBoundsAustria() {
		WKTReader reader = new WKTReader();
		String poly = "POLYGON((9.5282778 46.3704647,9.5282778 49.023522,17.1625438 49.023522,17.1625438 46.3704647,9.5282778 46.3704647))";
		Polygon bounds = null;
		try {
			bounds = (Polygon) reader.read(poly);
			bounds.setSRID(4326);
		} catch (ParseException e) {
			//log.error("error parsing geometry of reference point");
		}
		return bounds;
	}


}
