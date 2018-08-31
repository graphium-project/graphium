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

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import at.srfg.graphium.ITestGraphiumPostgis;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.persistence.IWayGraphViewDao;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.view.IWayGraphView;

/**
 * @author mwimmer
 *
 */

public class SubtestWayGraphViewDaoImpl implements ITestGraphiumPostgis {
	@Autowired
	private IWayGraphViewDao dao;

	@Autowired
	private IWayGraphVersionMetadataDao metaDao;

	@Value("${db.graphName}")
	String graphName;
	@Value("${db.version}")
	String version;

	private static Logger log = LoggerFactory.getLogger(SubtestWayGraphViewDaoImpl.class);

	public IWayGraph saveWayGraph(String graphName, String version) {
		if (graphName == null) {
			graphName = "osm_lu_180213";
		}
		if (version == null) {
			version = "1.0";
		}
		
		IWayGraph wayGraph = metaDao.getGraph(graphName);
		if (wayGraph != null) {
			log.info("Waygraph '" + graphName + "' existiert bereits");
		} else {
			log.info("Waygraph '" + graphName + "' existiert noch nicht");
			metaDao.saveGraph(graphName);
			wayGraph = metaDao.getGraph(graphName);
			log.info("Waygraph '" + graphName + "' wurde gespeichert");
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

	@Transactional(readOnly=false)
	public void testSaveDefaultView() {
		IWayGraphView view = saveView(graphName, version);
		Assert.assertNotNull(view);
	}

	@Override
	public void run() {
		testSaveDefaultView();
	}
}
