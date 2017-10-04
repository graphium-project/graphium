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
package at.srfg.graphium.core.service.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.core.exception.GraphAlreadyExistException;
import at.srfg.graphium.core.exception.GraphImportException;
import at.srfg.graphium.core.service.IGraphVersionImportService;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.impl.WayGraphVersionMetadata;
import at.srfg.graphium.model.management.impl.Source;

/**
 * @author mwimmer
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/application-context-graphium-core.xml",
		"classpath:application-context-graphium-postgis.xml",
		"classpath:application-context-graphium-postgis_test.xml"})
public class TestQueuingGraphVersionImportServiceImpl {

	private static Logger log = LoggerFactory.getLogger(TestQueuingGraphVersionImportServiceImpl.class);

	@Resource(name="postgisQueuingGraphVersionImportService")
	private IGraphVersionImportService importService;
	
	@Test
	public void testImportGraphVersion() {
		String graphName = "gip_at_frc_0_4";
		String version = "16_02_160415";
		String originGraphName = "gip_at";
		String originVersion = "test";
		Date validFrom = new Date();
		Date validTo = null;
		Map<String, String> tags = null;
		int sourceId = 1;
		String type = "graph";
		String description = "die GIP";
		String creator = "ich";
		String originUrl = "http://gip.at";
		String inputFileName = "C:/development/Graphserver/working_data/graphs_for_import/gip_at_frc_0_4_15_02_151120.json";
		Polygon coveredArea = null;
		InputStream stream = null;
		
		try {
			stream = new FileInputStream(inputFileName);
			
			IWayGraphVersionMetadata metadata = new WayGraphVersionMetadata();
			metadata.setGraphName(graphName);
			metadata.setVersion(version);
			metadata.setOriginGraphName(originGraphName);
			metadata.setOriginVersion(originVersion);
			metadata.setValidFrom(validFrom);
			metadata.setValidTo(validTo);
			metadata.setType(type);
			metadata.setCreator(creator);
			metadata.setCreationTimestamp(new Date());
			metadata.setCoveredArea(coveredArea);
			metadata.setSource(new Source(sourceId, "GIP"));
			metadata.setOriginUrl(originUrl);
			metadata.setDescription(description);

			importService.importGraphVersion(graphName, version, stream, true);
			
		} catch (FileNotFoundException e) {
			log.error("file not found", e);
		} catch (GraphImportException e) {
			log.error("error importing graph", e);
		} catch (GraphAlreadyExistException e) {
			log.error("error, graph already exists", e);
		}
		
	}
	
}
