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

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.persistence.IWayGraphViewDao;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.view.IWayGraphView;

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
public class TestWayGraphViewDaoImpl {
	
	private static Logger log = LoggerFactory.getLogger(TestWayGraphViewDaoImpl.class);
	
	@Autowired
	private IWayGraphViewDao dao;

	@Autowired
	private IWayGraphVersionMetadataDao metaDao;

	public IWayGraph saveWayGraph(String graphName, String version) {
		if (graphName == null) {
			graphName = "gip_at";
		}
		if (version == null) {
			version = "1.0";
		}
		
		IWayGraph wayGraph = metaDao.getGraph(graphName);
		if (wayGraph != null) {
			System.out.println("Waygraph '" + graphName + "' existiert bereits");
		} else {
			System.out.println("Waygraph '" + graphName + "' existiert noch nicht");
			metaDao.saveGraph(graphName);
			wayGraph = metaDao.getGraph(graphName);
			System.out.println("Waygraph '" + graphName + "' wurde gespeichert");
		}
		
		return wayGraph;
	}
	
	public IWayGraphView saveView(String graphName, String version) {
		IWayGraph wayGraph = saveWayGraph(graphName, version);
		dao.saveDefaultView(wayGraph);
		
		try {
			return dao.getView(graphName);
		} catch (GraphNotExistsException e) {
			log.error("",e);
		}
		return null;
	}
	
	@Test
	@Transactional(readOnly=false)
	@Rollback(value=true)
	public void testSaveDefaultView() {
		String graphName = "osm_at";
		String version = "1";
		
		Map<String, String> tags = new HashMap<String, String>();
		tags.put("einSinnlosesTag", "holladrio");
		
		IWayGraphView view = saveView(graphName, version);

		Assert.assertNotNull(view);
		
	}

}
