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
package at.srfg.graphium.core.observer.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.helper.GraphVersionHelper;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.service.impl.GraphWriteServiceImpl;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.State;

/**
 * Observe changes of graph version metadata state; in case of activation of a new graph version all old active versions of the same graph
 * will be deleted until the maximum number of active versions (if defined) for this graph has been reached.
 * 
 * @author mwimmer
 *
 */
public class GraphVersionCapacityManager extends AbstractGraphVersionStateModifiedObserver {

	private static Logger log = LoggerFactory.getLogger(GraphVersionCapacityManager.class);
	
	protected GraphWriteServiceImpl graphWriteService;
	protected IWayGraphVersionMetadataDao metadataDao;
	protected Map<String, Integer> graphVersionQuantity = new HashMap<>();
	protected Properties properties;
	protected boolean keepMetadata;

	@Override
	public void update(Observable observable, Object metadataObj) {
		if (metadataObj != null) {
			if (metadataObj instanceof IWayGraphVersionMetadata) {
				IWayGraphVersionMetadata metadata = (IWayGraphVersionMetadata) metadataObj;
				if (((IWayGraphVersionMetadata) metadata).getState().equals(State.ACTIVE)) {
					Integer quantity = graphVersionQuantity.get(metadata.getGraphName());
					if (quantity != null && quantity > 0) {
						// check number of saved versions
						List<IWayGraphVersionMetadata> metadataList = metadataDao.getWayGraphVersionMetadataList(metadata.getGraphName(), State.ACTIVE, null, null, null);
						if (metadataList.size() >= quantity) { // currently activating graph version has not saved its state to ACTIVE!						
							// delete oldest versions
							log.info("maximum number of versions for graph " + metadata.getGraphName() + " exceeded");
							metadataList.sort((IWayGraphVersionMetadata m1, IWayGraphVersionMetadata m2)->m1.getValidFrom().compareTo(m2.getValidFrom()));
							for (int i=0; i<=(metadataList.size() - quantity); i++) {
								IWayGraphVersionMetadata oldMetadata = metadataList.get(i);
								if (!oldMetadata.getVersion().equals(metadata.getVersion())) { // Only delete old versions; already activated version coult be
																							   // could be activated again!
									try {
										log.info("deleting graph " + metadata.getGraphName() + " in version " + oldMetadata.getVersion());
										graphWriteService.deleteGraphVersion(oldMetadata.getGraphName(), oldMetadata.getVersion(), keepMetadata);
									} catch (GraphNotExistsException e) {
										log.error("Could not delete graph version " + GraphVersionHelper.createGraphVersionName(oldMetadata.getGraphName(), oldMetadata.getVersion()), e);
									}
								} else {
									log.info("ignore already activated version " + metadata.getVersion() + " for graph " + metadata.getGraphName() + " from deletion");
								}
							}
						}
					}
					
				}
			}
		}
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
		if (properties != null) {
			graphVersionQuantity = new HashMap<>();
			for (Entry<Object, Object> entry : properties.entrySet()) {
				graphVersionQuantity.put((String)entry.getKey(), Integer.parseInt((String)entry.getValue()));
			}
		}
	}

	public boolean isKeepMetadata() {
		return keepMetadata;
	}

	public void setKeepMetadata(boolean keepMetadata) {
		this.keepMetadata = keepMetadata;
	}

	public GraphWriteServiceImpl getGraphWriteService() {
		return graphWriteService;
	}

	public void setGraphWriteService(GraphWriteServiceImpl graphWriteService) {
		this.graphWriteService = graphWriteService;
	}

	public IWayGraphVersionMetadataDao getMetadataDao() {
		return metadataDao;
	}

	public void setMetadataDao(IWayGraphVersionMetadataDao metadataDao) {
		this.metadataDao = metadataDao;
	}

}
