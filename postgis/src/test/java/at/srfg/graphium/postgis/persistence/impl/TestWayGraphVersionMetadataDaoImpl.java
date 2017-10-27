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
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import at.srfg.graphium.core.exception.GraphAlreadyExistException;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;

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
public class TestWayGraphVersionMetadataDaoImpl extends AbstractTestWayGraphWriteDao {

	@Autowired
	private IWayGraphVersionMetadataDao dao;
	
	@Test
	@Transactional(readOnly=false)
	@Rollback(true)
	public void testGraphVersionMetadataDao() throws GraphAlreadyExistException, GraphNotExistsException {
		String graphName = "testgraph";
		String version = "1_0";
		Date now = new Date();

		IWayGraphVersionMetadata metadata = writeTestMetadata(graphName, version, State.INITIAL);
		
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