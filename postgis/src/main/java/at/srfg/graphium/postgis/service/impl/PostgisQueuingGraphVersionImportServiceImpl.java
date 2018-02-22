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
package at.srfg.graphium.postgis.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.service.impl.QueuingGraphVersionImportServiceImpl;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.postgis.persistence.IWayGraphImportDao;

/**
 * @author mwimmer
 *
 */
public class PostgisQueuingGraphVersionImportServiceImpl<T extends IWaySegment> extends
		QueuingGraphVersionImportServiceImpl<T> {

	private static Logger log = LoggerFactory.getLogger(PostgisQueuingGraphVersionImportServiceImpl.class);
	private IWayGraphImportDao importDao;
	
	@Override
	public void postImport(IWayGraph wayGraph, String version, boolean graphVersionAlreadyExisted) {
		super.postImport(wayGraph, version, graphVersionAlreadyExisted);
		
		String graphVersionName = wayGraph.getName() + "_" + version;
		
		// Do creation always because tables get dropped if they already exist
		log.info("Creating indexes...");
		writeDao.createIndexes(wayGraph.getName(), version);
		log.info("Indexes created successfully");
		
		try {
			log.info("Creating connection constraints...");
			writeDao.createConnectionContstraints(graphVersionName);
			log.info("Connection constraints created successfully");
		} catch (GraphNotExistsException e) {
			log.error("graph " + e.getGraphName() + " doesn´t exist", e);
		}
	}

	public IWayGraphImportDao getImportDao() {
		return importDao;
	}

	public void setImportDao(IWayGraphImportDao importDao) {
		this.importDao = importDao;
	}

}