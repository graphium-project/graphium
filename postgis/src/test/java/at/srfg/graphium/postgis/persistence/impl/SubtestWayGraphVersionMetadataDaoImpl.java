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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import at.srfg.graphium.HelpMethods;
import at.srfg.graphium.ITestGraphiumPostgis;
import at.srfg.graphium.core.persistence.ISourceDao;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.ISource;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;

/**
 * @author mwimmer
 *
 */

public class SubtestWayGraphVersionMetadataDaoImpl implements ITestGraphiumPostgis{

	@Autowired
	private IWayGraphVersionMetadataDao dao;
	@Autowired
	private ISourceDao sourceDao;
    @Value("${db.graphName}")
    String graphName;
    @Value("${db.graphVersion}")
    String version;
    @Value("#{new java.text.SimpleDateFormat(\"${db.dateFormat}\").parse(\"${db.testDateString}\")}") //using spEL
    Date now;
    @Value("#{${db.tags}}") //assign hashmap using spEL
    Map<String, String> tags;
	@Value("${db.sourceName}")
	String sourceName;

	private static Logger log = LoggerFactory.getLogger(SubtestWayGraphVersionMetadataDaoImpl.class);

	@Transactional(readOnly=false)
	public void testGraphVersionMetadataDao() {
		List<String> graphNames = dao.getGraphs();
		for (String name : graphNames) {
			log.info("Waygraph '" + name + "'");
		}
		
		if (dao.checkIfGraphExists(graphName)) {
			log.info("Waygraph '" + graphName + "' existiert bereits");
		} else {
			log.info("Waygraph '" + graphName + "' existiert noch nicht");
			dao.saveGraph(graphName);
			log.info("Waygraph '" + graphName + "' wurde gespeichert");
		}
		
		IWayGraph wayGraph = dao.getGraph(graphName);
		ISource source = sourceDao.getSource(sourceName);
		
		IWayGraphVersionMetadata metadata;
		log.info("\nWayGraphVersionMetadata wird erzeugt...");

		Set<Access> accessTypes = new HashSet<Access>();
		accessTypes.add(Access.PRIVATE_CAR);
		metadata = dao.newWayGraphVersionMetadata(0, wayGraph.getId(), graphName, version, 
				graphName, "1.0_orig", State.INITIAL, now, null, HelpMethods.getBoundsAustria(), 1000, 2000, accessTypes, tags, source,
				"Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org");

		log.info("\nWayGraphVersionMetadata wird gespeichert...");
		dao.saveGraphVersion(metadata);

		log.info("\nLese WayGraphVersionMetadata mit graphname = '" + graphName + "' und version = '" + version + "'...");
		metadata = dao.getWayGraphVersionMetadata(graphName, version);
		log.info(metadata.toString());

		log.info("\nLese WayGraphVersionMetadata mit ID=" + metadata.getId() + "...");
		metadata = dao.getWayGraphVersionMetadata(metadata.getId());
		log.info(metadata.toString());
		
		List<IWayGraphVersionMetadata> metadataList;
		log.info("\nLese WayGraphVersionMetadata-List mit graphname = '" + graphName + "...");
		metadataList = dao.getWayGraphVersionMetadataList(graphName);
		for (IWayGraphVersionMetadata md : metadataList) {
			log.info(md.toString());
		}

		log.info("\nLese WayGraphVersionMetadata-List mit graphname = '" + graphName + " und zusätzlichen Filtern...");
		metadataList = dao.getWayGraphVersionMetadataList(graphName, State.INITIAL, now, null, null);
		for (IWayGraphVersionMetadata md : metadataList) {
			log.info(md.toString());
		}

		log.info("\nLese WayGraphVersionMetadata-List mit orig_graphname = '" + graphName + "...");
		metadataList = dao.getWayGraphVersionMetadataListForOriginGraphname(graphName);
		for (IWayGraphVersionMetadata md : metadataList) {
			log.info(md.toString());
		}

		log.info("\nAktiviere WayGraphVersionMetadata mit graphname = '" + graphName + "' und version = '" + version + "'...");
		dao.setGraphVersionState(graphName, version, State.ACTIVE);

		log.info("\nLösche WayGraphVersionMetadata mit graphname = '" + graphName + "' und version = '" + version + "'...");
		dao.setGraphVersionState(graphName, version, State.DELETED);

		log.info("\nLese WayGraphVersionMetadata mit ID=" + metadata.getId() + "...");
		metadata = dao.getWayGraphVersionMetadata(metadata.getId());
		log.info(metadata.toString());
	}

	@Transactional(readOnly=false)
	public void testGraphVersionMetadataDaoForSynchronizationAPI() {
		String newerVersion = dao.checkNewerVersionAvailable(graphName, version);
		log.info("\nFür Graph '" + graphName + "' und Version '" + version + "' ist " + newerVersion + " neue Version verfügbar");
		newerVersion = dao.checkNewerVersionAvailable(graphName, version);
		log.info("\nFür Graph '" + graphName + "' und Version '" + version + "' ist " + newerVersion + " neue Version verfügbar");
	}

	@Override
	public void run() {
		testGraphVersionMetadataDao();
		testGraphVersionMetadataDaoForSynchronizationAPI();
	}
}