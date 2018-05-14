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

import at.srfg.graphium.core.exception.GraphAlreadyExistException;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.exception.GraphStorageException;
import at.srfg.graphium.core.persistence.ICompositeWayGraphWriteDao;
import at.srfg.graphium.core.persistence.IWayGraphWriteDao;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.IWaySegmentConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CompositeGraphWriteDaoImpl<W extends IWaySegment> implements IWayGraphWriteDao<W>, ICompositeWayGraphWriteDao<W>{

	protected static Logger log = LoggerFactory.getLogger(CompositeGraphWriteDaoImpl.class);
	
	public CompositeGraphWriteDaoImpl(List<IWayGraphWriteDao<W>> writeDaos) {
		this.writeDaos = writeDaos;
	}
	
	List<IWayGraphWriteDao<W>> writeDaos;

	@Override
	public void createGraph(String graphName, String version, boolean overrideGraphIfExsists)
			throws GraphAlreadyExistException, GraphNotExistsException {
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			dao.createGraph(graphName, version, overrideGraphIfExsists);
		}
	}
	
	@Override
	public void createGraphVersion(String graphName, String version, boolean overrideGraphIfExsists,
			boolean createConnectionConstraint)
			throws GraphAlreadyExistException, GraphNotExistsException {
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			dao.createGraphVersion(graphName, version, overrideGraphIfExsists, createConnectionConstraint);
		}
	}

	@Override
	public void createConnectionContstraints(String graphVersionName)
			throws GraphNotExistsException {
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			dao.createConnectionContstraints(graphVersionName);
		}
	}

	@Override
	public void saveSegments(List<W> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException {
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			dao.saveSegments(segments, graphName, version);
		}
	}

	@Override
	public void saveSegments(List<W> segments, String graphName, String version, List<String> excludedXInfosList) throws GraphStorageException, GraphNotExistsException {
        for (IWayGraphWriteDao<W> dao : writeDaos) {
            dao.saveSegments(segments, graphName, version, excludedXInfosList);
        }
	}

	@Override
	public long updateSegmentAttributes(List<W> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException {
		Map<String, Long> results = new HashMap<String, Long>();
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			results.put(dao.getClass().getSimpleName(), dao.updateSegmentAttributes(segments, graphName, version));
		}
		return checkResults(results);
	}

	@Override
	public long saveConnectionsOnSegments(List<W> segmentsWithConnections,
			boolean saveSegments, String graphName, String version) throws GraphStorageException, GraphNotExistsException {
		Map<String, Long> results = new HashMap<String, Long>();
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			results.put(dao.getClass().getSimpleName(), dao.saveConnectionsOnSegments(segmentsWithConnections, saveSegments, graphName, version));			
		}
		return checkResults(results);
	}

	@Override
	public long saveConnections(List<IWaySegmentConnection> connections,
			String graphName, String version) {
		Map<String, Long> results = new HashMap<String, Long>();
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			results.put(dao.getClass().getSimpleName(), dao.saveConnections(connections, graphName, version));
		}
		return checkResults(results);
	}
	
	@Override
	public void setGraphWriteDaos(List<IWayGraphWriteDao<W>> writeDaos) {
		this.writeDaos = writeDaos;
	}

	@Override
	public List<IWayGraphWriteDao<W>> getGraphWriteDaos() {		
		return writeDaos;
	}

	
	private long checkResults(Map<String, Long> results) {
		Entry<String, Long> prev = null;
		for (Entry<String, Long> result : results.entrySet()) {
			if (prev != null && prev.getValue() != result.getValue()) {
				log.warn("different results on composed daos! " +prev.getKey() + " returned " + prev.getValue() + " entries, " + 
						result.getKey() + " returned " + result.getValue()  + "!");
			}
			prev = result;			
		}
		return prev.getValue();
	}

	@Override
	public void createIndexes(String graphName, String version) {
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			dao.createIndexes(graphName, version);
		}
	}

	@Override
	public void deleteSegments(String graphName, String version) throws GraphNotExistsException {
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			dao.deleteSegments(graphName, version);
		}
	}

	@Override
	public void updateSegments(List<W> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException {
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			dao.updateSegments(segments, graphName, version);
		}
	}

	@Override
	public long updateConnections(List<W> segments, String graphName, String version) {
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			dao.updateConnections(segments, graphName, version);
		}
		return segments.size();
	}

	@Override
	public void saveConnectionXInfos(List<? extends IBaseSegment> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException {
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			dao.saveConnectionXInfos(segments, graphName, version);
		}
	}

	@Override
	public void saveConnectionXInfos(List<? extends IBaseSegment> segments, String graphName, String version, List<String> excludedXInfos) throws GraphStorageException, GraphNotExistsException {
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			dao.saveConnectionXInfos(segments, graphName, version, excludedXInfos);
		}
	}

	@Override
	public void deleteConnectionXInfos(String graphName, String version, String... types) throws GraphStorageException, GraphNotExistsException {
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			dao.deleteConnectionXInfos(graphName, version, types);
		}
	}

	@Override
	public void deleteSegmentXInfos(String graphName, String version, String... types) throws GraphStorageException, GraphNotExistsException {
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			dao.deleteSegmentXInfos(graphName, version, types);
		}
	}

	@Override
	public void saveSegmentXInfos(List<? extends IBaseSegment> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException {
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			dao.saveSegmentXInfos(segments, graphName, version);
		}
	}

	@Override
	public void saveSegmentXInfos(List<? extends IBaseSegment> segments, String graphName, String version, List<String> excludedXInfosList) throws GraphStorageException, GraphNotExistsException {
        for (IWayGraphWriteDao<W> dao : writeDaos) {
            dao.saveSegmentXInfos(segments, graphName, version, excludedXInfosList);
        }
	}

	@Override
	public void updateConnectionXInfos(List<? extends IBaseSegment> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException {
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			dao.updateConnectionXInfos(segments, graphName, version);
		}
	}

	@Override
	public void updateSegmentXInfos(List<? extends IBaseSegment> segments, String graphName, String version) throws GraphStorageException, GraphNotExistsException {
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			dao.updateSegmentXInfos(segments, graphName, version);
		}
	}

	@Override
	public void postCreateGraph(IWayGraphVersionMetadata graphVersionMeta) {
		for (IWayGraphWriteDao<W> dao : writeDaos) {
			dao.postCreateGraph(graphVersionMeta);
		}
	}
}
