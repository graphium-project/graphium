/**
 * Copyright © 2017 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.srfg.graphium.postgis.persistence.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;
import at.srfg.graphium.model.management.impl.Source;

import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @author mwimmer
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/application-context-graphium-postgis_test.xml",
		"classpath:/application-context-graphium-core.xml",
		"classpath:application-context-graphium-postgis.xml",
		"classpath:application-context-graphium-postgis-datasource.xml",
		"classpath:application-context-graphium-postgis-aliasing.xml"})
public class TestWayGraphVersionMetadataDaoImpl {

	@Autowired
	private IWayGraphVersionMetadataDao dao;
	
	@Test
	@Transactional(readOnly=false)
	@Rollback(true)
	public void testGraphVersionMetadataDao() {
		String graphName = "testgraph";
		String version = "1.0";
		Date now = new Date();
		Map<String, String> tags = new HashMap<String, String>();
		tags.put("einSinnlosesTag", "holladrio");
		
		List<String> graphNames = dao.getGraphs();
		for (String name : graphNames) {
			System.out.println("Waygraph '" + name + "'");
		}
		
		if (dao.checkIfGraphExists(graphName)) {
			System.out.println("Waygraph '" + graphName + "' existiert bereits");
		} else {
			System.out.println("Waygraph '" + graphName + "' existiert noch nicht");
			dao.saveGraph(graphName);
			System.out.println("Waygraph '" + graphName + "' wurde gespeichert");
		}
		
		IWayGraphVersionMetadata metadata;
		System.out.println("\nWayGraphVersionMetadata wird erzeugt...");
		Set<Access> accessTypes = new HashSet<Access>();
		accessTypes.add(Access.PRIVATE_CAR);
		metadata = dao.newWayGraphVersionMetadata(0, 1, graphName, version, 
				graphName, "1.0_orig", State.INITIAL, now, null, getBoundsAustria(), 1000, 2000, accessTypes, tags, new Source(1, ""), 
				"Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org");
		
		System.out.println("\nWayGraphVersionMetadata wird gespeichert...");
		dao.saveGraphVersion(metadata);
		
		System.out.println("\nLese WayGraphVersionMetadata mit graphname = '" + graphName + "' und version = '" + version + "'...");
		metadata = dao.getWayGraphVersionMetadata(graphName, version);
		System.out.println(metadata);
		
		System.out.println("\nLese WayGraphVersionMetadata mit ID=" + metadata.getId() + "...");
		metadata = dao.getWayGraphVersionMetadata(metadata.getId());
		System.out.println(metadata);
		
		List<IWayGraphVersionMetadata> metadataList;
		System.out.println("\nLese WayGraphVersionMetadata-List mit graphname = '" + graphName + "...");
		metadataList = dao.getWayGraphVersionMetadataList(graphName);
		for (IWayGraphVersionMetadata md : metadataList) {
			System.out.println(md);
		}
		
		System.out.println("\nLese WayGraphVersionMetadata-List mit graphname = '" + graphName + " und zusätzlichen Filtern...");
		metadataList = dao.getWayGraphVersionMetadataList(graphName, State.INITIAL, now, null, null);
		for (IWayGraphVersionMetadata md : metadataList) {
			System.out.println(md);
		}
		
		System.out.println("\nLese WayGraphVersionMetadata-List mit orig_graphname = '" + graphName + "...");
		metadataList = dao.getWayGraphVersionMetadataListForOriginGraphname(graphName);
		for (IWayGraphVersionMetadata md : metadataList) {
			System.out.println(md);
		}
		
		System.out.println("\nAktiviere WayGraphVersionMetadata mit graphname = '" + graphName + "' und version = '" + version + "'...");
		dao.setGraphVersionState(graphName, version, State.ACTIVE);
		
		System.out.println("\nLösche WayGraphVersionMetadata mit graphname = '" + graphName + "' und version = '" + version + "'...");
		dao.setGraphVersionState(graphName, version, State.DELETED);
		
		System.out.println("\nLese WayGraphVersionMetadata mit ID=" + metadata.getId() + "...");
		metadata = dao.getWayGraphVersionMetadata(metadata.getId());
		System.out.println(metadata);

	}

	private Polygon getBoundsAustria() {
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

	@Test
	@Transactional(readOnly=false)
	@Rollback(true)
	public void testGraphVersionMetadataDaoForSynchronizationAPI() {
		String graphName = "gip_at_frc_0_4";
		String version = "15_02_150401";
		String newerVersion = dao.checkNewerVersionAvailable(graphName, version);
		System.out.println("\nFür Graph '" + graphName + "' und Version '" + version + "' ist " +
							newerVersion + " neue Version verfügbar");
		
		version = "15_02_150402";
		newerVersion = dao.checkNewerVersionAvailable(graphName, version);
		System.out.println("\nFür Graph '" + graphName + "' und Version '" + version + "' ist " +
							newerVersion + " neue Version verfügbar");
		
	}
}