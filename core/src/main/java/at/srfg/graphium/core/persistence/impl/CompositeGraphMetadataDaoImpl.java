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
/**
 * (C) 2013 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 * @author anwagner
 **/
package at.srfg.graphium.core.persistence.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.ISource;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;

import com.vividsolutions.jts.geom.Polygon;

public class CompositeGraphMetadataDaoImpl implements IWayGraphVersionMetadataDao {

	protected static Logger log = LoggerFactory.getLogger(CompositeGraphMetadataDaoImpl.class);
	
	protected List<IWayGraphVersionMetadataDao> writeDaos;
	protected IWayGraphVersionMetadataDao primaryDao;
	
	public CompositeGraphMetadataDaoImpl(List<IWayGraphVersionMetadataDao> writeDaos) {
		Assert.notEmpty(writeDaos, "no write daos set!");
		Assert.isTrue(writeDaos.size() < 2, "at least 2 write daos expected");
		this.writeDaos = writeDaos;
		log.info("using first write dao (" + writeDaos.get(0).getClass() + ") as primary dao for reads ");
	}
	public CompositeGraphMetadataDaoImpl(List<IWayGraphVersionMetadataDao> writeDaos, IWayGraphVersionMetadataDao primaryDao) {
		Assert.notEmpty(writeDaos, "no write daos set!");
		Assert.isTrue(writeDaos.size() >= 2, "at least 2 write daos expected");
		this.writeDaos = writeDaos;
		if (writeDaos.contains(primaryDao)) {
			this.primaryDao = primaryDao;
		}
		else {
			throw new RuntimeException("primary dao is not included in write daos list");
		}
	}
	
	@Override
	public IWayGraphVersionMetadata newWayGraphVersionMetadata() {
		return primaryDao.newWayGraphVersionMetadata();
	}

	@Override
	public IWayGraphVersionMetadata newWayGraphVersionMetadata(long id, long graphId, String graphName, String version,
			String originGraphName, String originVersion, State state, 
			Date validFrom, Date validTo, Polygon coveredArea,
			int segmentsCount, int connectionsCount, Set<Access> accessTypes,
			Map<String, String> tags, ISource source, String type,
			String description, Date creationTimestamp, Date storageTimestamp,
			String creator, String originUrl) {
		return primaryDao.newWayGraphVersionMetadata(id, graphId, graphName, version, originGraphName, originVersion, state, 
				validFrom, validTo, coveredArea, segmentsCount, connectionsCount, accessTypes, tags, source, type, description, 
				creationTimestamp, storageTimestamp, creator, originUrl);
	}
	
	@Override
	public IWayGraphVersionMetadata getWayGraphVersionMetadata(long id) {
		return primaryDao.getWayGraphVersionMetadata(id);
	}
	
	@Override
	public IWayGraphVersionMetadata getWayGraphVersionMetadata(
			String graphName, String version) {
		return primaryDao.getWayGraphVersionMetadata(graphName, version);
	}
	
	@Override
	public List<IWayGraphVersionMetadata> getWayGraphVersionMetadataList(
			String graphName) {
		return primaryDao.getWayGraphVersionMetadataList(graphName);
	}
	
	@Override
	public List<IWayGraphVersionMetadata> getWayGraphVersionMetadataListForOriginGraphname(
			String originGraphName) {
		return primaryDao.getWayGraphVersionMetadataListForOriginGraphname(originGraphName);
	}
	
	@Override
	public List<IWayGraphVersionMetadata> getWayGraphVersionMetadataList(
			String graphName, State state, Date validFrom,
			Date validTo, Set<Access> access) {
		return primaryDao.getWayGraphVersionMetadataList(graphName, state, validFrom, validTo, access);
	}
	
	@Override
	public void saveGraphVersion(IWayGraphVersionMetadata graphMetadata) {
		for (IWayGraphVersionMetadataDao dao : writeDaos) {
			dao.saveGraphVersion(graphMetadata);
		}
	}
	
	@Override
	public boolean checkIfGraphExists(String graphName) {
		return primaryDao.checkIfGraphExists(graphName);
	}

	@Override
	public long saveGraph(String graphName) {
		long id = 0;
		for (IWayGraphVersionMetadataDao dao : writeDaos) {
			id = dao.saveGraph(graphName);
		}
		return id;
	}

	@Override
	public void updateGraphVersion(IWayGraphVersionMetadata graphMetadata) {
		for (IWayGraphVersionMetadataDao dao : writeDaos) {
			dao.updateGraphVersion(graphMetadata);
		}
	}

	@Override
	public void setGraphVersionState(String graphName, String version,
			State state) {
		for (IWayGraphVersionMetadataDao dao : writeDaos) {
			dao.setGraphVersionState(graphName, version, state);
		}
	}

	@Override
	public void setValidToTimestampOfPredecessorGraphVersion(
			IWayGraphVersionMetadata graphMetadata) {
		for (IWayGraphVersionMetadataDao dao : writeDaos) {
			dao.setValidToTimestampOfPredecessorGraphVersion(graphMetadata);
		}

	}

	@Override
	public List<String> getGraphs() {
		return primaryDao.getGraphs();
	}

	@Override
	public String checkNewerVersionAvailable(String graphName, String version) {
		return primaryDao.checkNewerVersionAvailable(graphName, version);
	}

	@Override
	public IWayGraphVersionMetadata getCurrentWayGraphVersionMetadata(String graphName) {
		return primaryDao.getCurrentWayGraphVersionMetadata(graphName);
	}

	@Override
	public IWayGraph getGraph(String graphName) {
		return primaryDao.getGraph(graphName);
	}

	@Override
	public IWayGraph getGraph(long graphId) {
		return primaryDao.getGraph(graphId);
	}
	@Override
	public IWayGraphVersionMetadata getCurrentWayGraphVersionMetadataForView(String viewName) {
		return primaryDao.getCurrentWayGraphVersionMetadataForView(viewName);
	}
	@Override
	public IWayGraphVersionMetadata getWayGraphVersionMetadataForView(String viewName, String version) {
		return primaryDao.getWayGraphVersionMetadataForView(viewName, version);
	}
	@Override
	public IWayGraphVersionMetadata getCurrentWayGraphVersionMetadata(String graphName, Set<State> states) {
		return primaryDao.getCurrentWayGraphVersionMetadata(graphName, states);
	}
	@Override
	public void deleteWayGraphVersionMetadata(String graphName, String version, boolean keepMetadata) {
		for (IWayGraphVersionMetadataDao dao : writeDaos) {
			dao.deleteWayGraphVersionMetadata(graphName, version, keepMetadata);
		}

	}
}
