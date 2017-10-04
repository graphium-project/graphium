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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.ValidationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

import at.srfg.geomutils.GeometryUtils;
import at.srfg.graphium.core.exception.GraphAlreadyExistException;
import at.srfg.graphium.core.exception.GraphImportException;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.exception.GraphStorageException;
import at.srfg.graphium.core.helper.GraphVersionHelper;
import at.srfg.graphium.core.helper.GraphVersionValidityPeriodValidator;
import at.srfg.graphium.core.persistence.ISourceDao;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.persistence.IWayGraphViewDao;
import at.srfg.graphium.core.persistence.IWayGraphWriteDao;
import at.srfg.graphium.core.service.IGraphVersionImportService;
import at.srfg.graphium.io.adapter.registry.ISegmentAdapterRegistry;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.inputformat.IQueuingGraphInputFormat;
import at.srfg.graphium.io.producer.IBaseWaySegmentProducer;
import at.srfg.graphium.io.producer.impl.BaseWaySegmentProducerImpl;
import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.model.ISource;
import at.srfg.graphium.model.IWayGraph;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.IWaySegmentConnection;
import at.srfg.graphium.model.State;
import at.srfg.graphium.model.impl.WayGraph;
import at.srfg.graphium.model.management.IServerStatus;

public class QueuingGraphVersionImportServiceImpl<T extends IBaseWaySegment> implements IGraphVersionImportService<T> {

	private static Logger log = LoggerFactory.getLogger(QueuingGraphVersionImportServiceImpl.class);
	
	protected IWayGraphVersionMetadataDao metadataDao;
	protected ISourceDao sourceDao;
	protected IWayGraphWriteDao<T> writeDao;
	protected IQueuingGraphInputFormat<T> inputFormat;
	protected IServerStatus serverStatus;
	protected IWayGraphViewDao viewDao;
	protected ISegmentAdapterRegistry<? extends IBaseSegmentDTO, T> segmentAdpaterRegistry;
	protected GraphVersionValidityPeriodValidator validityPeriodValidator;

	protected int SRID = 4326;
	protected int queueSize;
	protected int batchSize;
	
	@Override
	@Transactional(readOnly=false, rollbackFor={GraphImportException.class,GraphAlreadyExistException.class})
	public void importGraphVersion(String graphName, String version,
			InputStream stream, boolean overrideIfExists) 
			throws GraphImportException, GraphAlreadyExistException {
		
		BlockingQueue<T> segmentsQueue;
		BlockingQueue<IWayGraphVersionMetadata> metadataQueue;
		
		log.info("Starting import of graph " + graphName + " in version " + version + " (override existing graph = " + overrideIfExists + ")");
		
		try {
		
			int segmentsCount = 0;
			int connectionsCount = 0;
			
			if (!serverStatus.registerImport()) {
				throw new GraphImportException("Sorry, system is busy, a graph import is currently executed");
			}
				
			// deserialize
			segmentsQueue = new ArrayBlockingQueue<T>(queueSize);
			metadataQueue = new ArrayBlockingQueue<IWayGraphVersionMetadata>(1);
			
			IBaseWaySegmentProducer<T> producer = new BaseWaySegmentProducerImpl<T>(inputFormat, stream, segmentsQueue, metadataQueue);
						
			Thread producerThread = new Thread(producer, "waysegment-parser-thread");
			producerThread.start();
						
			IWayGraphVersionMetadata metadata = null;
			while ((producerThread.isAlive() 
					|| !metadataQueue.isEmpty())
					&& metadata == null) {
				try {
					metadata = metadataQueue.poll(500, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					log.error("error during thread sleep", e);
				}
			}
			
			if (metadata == null) {
				throw new GraphImportException("Could not parse graph's input format - metadata is null!");
			}

			IWayGraph wayGraph = getOrCreateWayGraph(graphName);
			
			if (version == null) {
				version = metadata.getVersion();
			}
			
			preImport(graphName, version);

			writeDao.createGraphVersion(graphName, version, overrideIfExists, false);
			
			metadata.setGraphId(wayGraph.getId());
			int originalSegmentsCount = metadata.getSegmentsCount();
			int originalConnectionsCount = metadata.getConnectionsCount();
			
			setOrCreateSource(metadata);

			// if no graphName and/or version explicitly defined take them from JSON's metadata
			if (graphName == null) {
				graphName = metadata.getGraphName();
			}
			if (graphName == null) {
				throw new GraphImportException("graph name is null");
			}

			if (version == null) {
				version = metadata.getVersion();
			}
			if (version == null) {
				throw new GraphImportException("version is null");
			}
			
			Polygon coveredArea = metadata.getCoveredArea();
			
			setDefaultValues(metadata);
			
			IWayGraphVersionMetadata savedMetadata = null;
			savedMetadata = metadataDao.getWayGraphVersionMetadata(graphName, version);
			boolean graphVersionAlreadyExisted = savedMetadata != null;

			// save first shot of metadata
			// has to be done so further segment and xInfo DAOs could access a valid ID of the graph version's metadata
			savedMetadata = saveInitialMetadata(graphName, version, metadata, savedMetadata);
			
			// validate validity period
			List<String> errorMessages = validityPeriodValidator.validateValidityPeriod(savedMetadata);
			if (errorMessages != null) {
				String msg = StringUtils.join(errorMessages, "; ");
				log.error(msg);			
				throw new ValidationException(msg);
			}
			
			if (!graphVersionAlreadyExisted) {
				insertDefaultView(wayGraph);
			}

			// save segments via view
			List<T> segmentsToSave = new ArrayList<T>(queueSize);
			Map<Long, List<IWaySegmentConnection>> connectionIntegrityMap = new HashMap<Long, List<IWaySegmentConnection>>();
			List<IWaySegmentConnection> connectionsToSave = new ArrayList<IWaySegmentConnection>();
			List<Long> segmentIds = new ArrayList<Long>();
			String segmentType = null;
			while (producerThread.isAlive() || !segmentsQueue.isEmpty()) {
				T segment;
				try {
					segment = segmentsQueue.poll(500, TimeUnit.MILLISECONDS);
											
					if (segment != null) {
						// check which segment type is contained as model
						if(segmentType == null) {
							segmentType = segmentAdpaterRegistry.getSegmentDtoType((Class<T>) segment.getClass());
						}
												
						// calculate metadata
					/*	if (segment.getAccessTow() != null) {
							for (Access access : segment.getAccessTow()) {
								accessTypes.add(access);
							}
						}
						if (segment.getAccessBkw() != null) {
							for (Access access : segment.getAccessBkw()) {
								accessTypes.add(access);
							}
						}*/
						segmentsCount++;
						if(segment.getCons() != null) {
							connectionsCount += segment.getCons().size();
						}
						
						if (metadata.getCoveredArea() == null) {
							coveredArea = expandEnvelope(coveredArea, segment);
						}
						
						segmentsToSave.add(segment);
						segmentIds.add(segment.getId());
						addConnectionsToIntegrityList(segment, connectionIntegrityMap);
						
						if (segmentsToSave.size() == batchSize) {
							connectionsToSave = getValidConnections(connectionIntegrityMap, segmentIds);
							saveBatch(segmentsToSave, connectionsToSave, graphName, version);
							segmentsToSave.clear();
							connectionsToSave.clear();
						}
					}

				} catch (InterruptedException e) {
					log.error("error during thread sleep", e);
				}
			}
			
			// check if background worker terminated with errors
			if(producer.getException() != null) {
				throw new GraphImportException("Could not parse graph's input format - error in background producer!", 
						producer.getException());
			}
			
			if (!segmentsToSave.isEmpty()) {
				connectionsToSave = getValidConnections(connectionIntegrityMap, segmentIds);
				saveBatch(segmentsToSave, connectionsToSave, graphName, version);
				segmentsToSave.clear();
				connectionsToSave.clear();
			}
			
			if (!connectionIntegrityMap.isEmpty()) {
				int waitingConnectionsSize = 0;
				for (List<IWaySegmentConnection> conns : connectionIntegrityMap.values()) {
					waitingConnectionsSize += conns.size();
				}
				log.warn(waitingConnectionsSize + " connections are left because their toSegmentIds are not valid");			
			}
			
			// check if counts of original graph's metadata are equal to saved ones
			if ((originalSegmentsCount > 0 && originalSegmentsCount != segmentsCount) ||
				(originalConnectionsCount > 0 && originalConnectionsCount != connectionsCount)) {
				throw new GraphImportException("Import failed because of invalid import counts "
						+ "(segmentCount is " + segmentsCount +	" - should be " + originalSegmentsCount 
						+ ", connectionsCount is " + connectionsCount + ", should be " + originalConnectionsCount + "!");
			}
	
			log.info("data saved, updating metadata..");
			updateCompletedMetadata(savedMetadata, connectionsCount, coveredArea, segmentsCount, segmentType);
			log.info("metadata updated.");
			
			// TODO: Wie wird die grapharea berechnet??? Derzeit nur als Envelope. Polygon benötigt?
			// für die Postgres Version wäre vermutlich das vernünftigste in postImport ein Query zu machen das
			// in den Metadaten das Polygon mit der Concave Hull der Geometry Collection die mit collect aus allen 
			// segmenten das Graphen zusammengesammelt wurden ersetzt
			log.info("starting post processing ...");
			postImport(wayGraph, version, graphVersionAlreadyExisted);
			log.info("post processing finished.");
			
			log.info("importer finished, commiting transaction");
		} catch (Exception e) {
			handleImportError(graphName, version, e);
		
		} finally {
//			serverStatus.setCurrentImport(false);
			serverStatus.unregisterImport();
			log.info("Graph import finished");
		}
		
	}
	
	protected IWayGraphVersionMetadata saveInitialMetadata(String graphName, String version, IWayGraphVersionMetadata metadata, IWayGraphVersionMetadata savedMetadata) {
		Date now = new Date();
		boolean doInsert = true;
		
		if (savedMetadata != null) {
			// reset validity
			metadata.setValidFrom(savedMetadata.getValidFrom());
			metadata.setValidTo(null);
		}
		
		if (savedMetadata == null) {
			savedMetadata = metadataDao.newWayGraphVersionMetadata();
			savedMetadata.setCoveredArea(createMaxCoveredArea());
			doInsert = true;
		} else {
			doInsert = false;
		}
		savedMetadata.setState(State.INITIAL);
		savedMetadata.setCreationTimestamp(metadata.getCreationTimestamp());
		savedMetadata.setCreator(metadata.getCreator());
		savedMetadata.setDescription(metadata.getDescription());
		savedMetadata.setGraphId(metadata.getGraphId());
		savedMetadata.setGraphName(graphName);
		savedMetadata.setOriginGraphName(metadata.getOriginGraphName());
		savedMetadata.setOriginVersion(metadata.getOriginVersion());
		savedMetadata.setOriginUrl(metadata.getOriginUrl());
		savedMetadata.setSource(metadata.getSource());
		savedMetadata.setStorageTimestamp(now);
		savedMetadata.setTags(metadata.getTags());
		savedMetadata.setType(metadata.getType());
		savedMetadata.setValidFrom(metadata.getValidFrom());
		savedMetadata.setValidTo(metadata.getValidTo());
		savedMetadata.setVersion(version);
		
		if (doInsert) {
			metadataDao.saveGraphVersion(savedMetadata);
		} else {
			metadataDao.updateGraphVersion(savedMetadata);
		}

		return savedMetadata;
	}
	
	/**
	 * @return
	 */
	private Polygon createMaxCoveredArea() {
		return GeometryUtils.createPolygon(new Coordinate[] {new Coordinate(-180, 90),
															 new Coordinate(180, 90),
															 new Coordinate(180, -90),
															 new Coordinate(-180, -90),
															 new Coordinate(-180, 90)}, SRID);
	}

	protected void updateCompletedMetadata(IWayGraphVersionMetadata savedMetadata, int connectionsCount, 
			Polygon coveredArea, int segmentsCount, String segmentType) {
		
		savedMetadata.setConnectionsCount(connectionsCount);
		savedMetadata.setCoveredArea(coveredArea);
		savedMetadata.setSegmentsCount(segmentsCount);
					
		if(savedMetadata.getType() == null || !savedMetadata.getType().equals(segmentType)) {
			log.info("different segment type in metadata then present based on deserialisation,"
					+ " replacing metadata value with present value: " + segmentType);
			savedMetadata.setType(segmentType);
		}
		
		metadataDao.updateGraphVersion(savedMetadata);

		// post processing of stored graph. e.g. add check constraint for partitioning key 
		writeDao.postCreateGraph(savedMetadata);
		
	}

	protected String createGraphVersionName(String graphName, String version) {
		return GraphVersionHelper.createGraphVersionName(graphName, version);
	}

	protected void saveBatch(List<T> segmentsToSave, List<IWaySegmentConnection> connectionsToSave,
			String graphName, String version) throws GraphImportException, GraphNotExistsException {
		log.info("saving " + segmentsToSave.size() + " segments...");
		try {
			writeDao.saveSegments(segmentsToSave, graphName, version);
		} catch (GraphStorageException e) {
			String msg = "error saving segments: " + e.getMessage();
			log.error(msg);
			throw new GraphImportException(msg, e);
		}
		log.info("saving " + connectionsToSave.size() + " connections...");
		writeDao.saveConnections(connectionsToSave, graphName, version);
		log.info("batch saved");
	}

	/**
	 * post import process on error
	 * @throws GraphImportException 
	 */
	protected void handleImportError(String graphName, String version, Exception e) throws GraphImportException {
		log.error(e.toString(), e);
		throw new GraphImportException(e.getMessage(), e);
	}

	/**
	 * @param metadata
	 * @return
	 */
	private void setOrCreateSource(IWayGraphVersionMetadata metadata) {
		ISource source = sourceDao.getSource(metadata.getSource().getName());
		if (source == null) {
			sourceDao.save(metadata.getSource());
		} else {
			metadata.setSource(source);
		}
	}

	/**
	 * @param graphName
	 * @return
	 */
	private IWayGraph getOrCreateWayGraph(String graphName) {
		IWayGraph wayGraph = metadataDao.getGraph(graphName);
		long graphId = 0;
		if (wayGraph == null) {
			// save new graph entry
			graphId = metadataDao.saveGraph(graphName);
			wayGraph = new WayGraph(graphId, graphName);
		}
		return wayGraph;
	}

	/**
	 * @param metadata
	 */
	private void setDefaultValues(IWayGraphVersionMetadata metadata) {
		if (metadata.getValidFrom() == null) {
			metadata.setValidFrom(new Date());
		}
		if (metadata.getCreationTimestamp() == null) {
			metadata.setCreationTimestamp(new Date());
		}
		if (metadata.getCreator() == null) {
			metadata.setCreator("Importer");
		}
		if (metadata.getCreationTimestamp() == null) {
			metadata.setCreationTimestamp(new Date());
		}
		if (metadata.getOriginGraphName() == null) {
			metadata.setOriginGraphName(metadata.getGraphName());
		}
		if (metadata.getOriginVersion() == null) {
			metadata.setOriginVersion(metadata.getVersion());
		}
	}

	/**
	 * @param connectionIntegrityList
	 * @return
	 */
	private List<IWaySegmentConnection> getValidConnections(
			Map<Long, List<IWaySegmentConnection>> connectionsMap, List<Long> segmentIds) {
		List<IWaySegmentConnection> validConnections = new ArrayList<IWaySegmentConnection>();
		List<IWaySegmentConnection> connectionsList;
		for (Long segmentId : segmentIds) {
			connectionsList = connectionsMap.get(segmentId);
			if (connectionsList != null) {
				validConnections.addAll(connectionsList);
				connectionsMap.remove(segmentId);
			}
		}

		if (log.isDebugEnabled()) {
			int waitingConnectionsSize = 0;
			for (List<IWaySegmentConnection> conns : connectionsMap.values()) {
				waitingConnectionsSize += conns.size();
			}
			log.debug(validConnections.size() + " valid connections found to save (" + 
					waitingConnectionsSize + " connections wait for saving...)");
		}

		return validConnections;
	}

	/**
	 * @param segment
	 * @param connectionIntegrityList
	 */
	private void addConnectionsToIntegrityList(T segment,
			Map<Long, List<IWaySegmentConnection>> connectionIntegrityMap) {
		if(segment.getCons() != null) {
			for (IWaySegmentConnection conn : segment.getCons()) {
				if (!connectionIntegrityMap.containsKey(conn.getToSegmentId())) {
					connectionIntegrityMap.put(conn.getToSegmentId(), new ArrayList<>());
				}
				connectionIntegrityMap.get(conn.getToSegmentId()).add(conn);
			}
		}		
	}

	/**
	 * do some pre import processing
	 */
	public void preImport(String graphName, String version) {
	}

	/**
	 * do some post import processing
	 */
	public void postImport(IWayGraph wayGraph, String version, boolean graphVersionAlreadyExisted) {
	}

	/**
	 * @param graphVersionName
	 */
	private void insertDefaultView(IWayGraph wayGraph) {
		viewDao.saveDefaultView(wayGraph);
	}

	/**
	 * @param segment
	 * @return
	 */
	private Polygon expandEnvelope(Polygon coveredArea, T segment) {
		if (coveredArea == null) {
			return (Polygon) segment.getGeometry().getEnvelope();
		} else {
			Geometry g1 = coveredArea.union(segment.getGeometry().getEnvelope());
			Geometry g2 = g1.getEnvelope();
			return (Polygon) g2;
		}
	}

	public IWayGraphVersionMetadataDao getMetadataDao() {
		return metadataDao;
	}

	public void setMetadataDao(IWayGraphVersionMetadataDao metadataDao) {
		this.metadataDao = metadataDao;
	}

	public IWayGraphWriteDao<T> getWriteDao() {
		return writeDao;
	}

	public void setWriteDao(IWayGraphWriteDao<T> writeDao) {
		this.writeDao = writeDao;
	}

	public ISourceDao getSourceDao() {
		return sourceDao;
	}

	public void setSourceDao(ISourceDao sourceDao) {
		this.sourceDao = sourceDao;
	}

	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public IServerStatus getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(IServerStatus serverStatus) {
		this.serverStatus = serverStatus;
	}

	public IWayGraphViewDao getViewDao() {
		return viewDao;
	}

	public void setViewDao(IWayGraphViewDao viewDao) {
		this.viewDao = viewDao;
	}

	public IQueuingGraphInputFormat<T> getInputFormat() {
		return inputFormat;
	}

	public void setInputFormat(IQueuingGraphInputFormat<T> inputFormat) {
		this.inputFormat = inputFormat;
	}

	public ISegmentAdapterRegistry<? extends IBaseSegmentDTO, T> getSegmentAdpaterRegistry() {
		return segmentAdpaterRegistry;
	}

	public void setSegmentAdpaterRegistry(
			ISegmentAdapterRegistry<? extends IBaseSegmentDTO, T> segmentAdpaterRegistry) {
		this.segmentAdpaterRegistry = segmentAdpaterRegistry;
	}

	public GraphVersionValidityPeriodValidator getValidityPeriodValidator() {
		return validityPeriodValidator;
	}

	public void setValidityPeriodValidator(GraphVersionValidityPeriodValidator validityPeriodValidator) {
		this.validityPeriodValidator = validityPeriodValidator;
	}
	
}