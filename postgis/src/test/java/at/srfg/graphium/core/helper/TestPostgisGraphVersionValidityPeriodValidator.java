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
package at.srfg.graphium.core.helper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTReader;

import at.srfg.graphium.core.persistence.ISourceDao;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.ISource;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;
import at.srfg.graphium.model.management.impl.Source;

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
public class TestPostgisGraphVersionValidityPeriodValidator {

	@Autowired
	private GraphVersionValidityPeriodValidator validator;
	
	@Autowired
	private IWayGraphVersionMetadataDao dao;
	
	@Autowired
	private ISourceDao sourceDao;
	
	@Test
	@Transactional(readOnly=false)
	@Rollback(value=true)
	public void testValidateValidityPeriod() throws ParseException {
		initialize();
	}
	
	private void initialize() throws ParseException {
		int sourceId = 815;
		ISource source = new Source(sourceId, "neue Source");
		sourceDao.save(source);

		String graphName = "testgraph";
//		String version = "1.0";
		Date now = new Date();
		Map<String, String> tags = new HashMap<String, String>();
		tags.put("einSinnlosesTag", "holladrio");
		
		List<String> graphNames = dao.getGraphs();
		
		if (!dao.checkIfGraphExists(graphName)) {
			dao.saveGraph(graphName);
		}
		
		IWayGraph graph = dao.getGraph(graphName);
		long graphId = graph.getId();
		
		IWayGraphVersionMetadata metadata;
		Set<Access> accessTypes = new HashSet<Access>();
		accessTypes.add(Access.PRIVATE_CAR);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		// march version
		metadata = dao.newWayGraphVersionMetadata(0, graphId, graphName, "1.0", 
				graphName, "1.0_orig", State.INITIAL, df.parse("2017-03-01"), df.parse("2017-03-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source, 
				"Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org");
		dao.saveGraphVersion(metadata);
		
		// april version
		IWayGraphVersionMetadata metadataToUpdate = dao.newWayGraphVersionMetadata(1, graphId, graphName, "2.0", 
				graphName, "1.0_orig", State.INITIAL, df.parse("2017-04-01"), df.parse("2017-04-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source, 
				"Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org");
		dao.saveGraphVersion(metadataToUpdate);
		
		// may version
		metadata = dao.newWayGraphVersionMetadata(2, graphId, graphName, "3.0", 
				graphName, "1.0_orig", State.INITIAL, df.parse("2017-05-01"), df.parse("2017-05-31"), getBoundsAustria(), 1000, 2000, accessTypes, tags, source, 
				"Graph für Tests", "keine Beschreibung...", now, now, "ich", "http://0815.echt.org");
		dao.saveGraphVersion(metadata);
		
		List<IWayGraphVersionMetadata> versions = dao.getWayGraphVersionMetadataList(graphName);
		for (IWayGraphVersionMetadata version : versions) {
			System.out.println("Version '" + version.getVersion() + "'");
		}
		
		List<String> messages = validator.validateValidityPeriod(metadataToUpdate);
		Assert.assertNull(messages);
		
		metadataToUpdate.setValidFrom(df.parse("2017-03-25"));
		messages = validator.validateValidityPeriod(metadataToUpdate);
		Assert.assertNotNull(messages);
		Assert.assertEquals("validFrom overlaps with existing version 1.0", messages.get(0));
		
		metadataToUpdate.setValidTo(df.parse("2017-05-03"));
		messages = validator.validateValidityPeriod(metadataToUpdate);
		Assert.assertNotNull(messages);
		Assert.assertEquals("validFrom overlaps with existing version 1.0", messages.get(0));
		Assert.assertEquals("validTo overlaps with existing version 3.0", messages.get(1));

		metadataToUpdate.setValidFrom(df.parse("2017-04-01"));
//		metadataToUpdate.setValidTo(df.parse("2017-05-03"));
		messages = validator.validateValidityPeriod(metadataToUpdate);
		Assert.assertNotNull(messages);
		Assert.assertEquals("validTo overlaps with existing version 3.0", messages.get(0));
		
	}
	
	private Polygon getBoundsAustria() {
		WKTReader reader = new WKTReader();
		String poly = "POLYGON((9.5282778 46.3704647,9.5282778 49.023522,17.1625438 49.023522,17.1625438 46.3704647,9.5282778 46.3704647))";
		Polygon bounds = null;
		try {
			bounds = (Polygon) reader.read(poly);
			bounds.setSRID(4326);
		} catch (com.vividsolutions.jts.io.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bounds;
	}

}
