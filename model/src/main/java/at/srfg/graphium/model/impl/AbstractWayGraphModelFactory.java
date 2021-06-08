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
package at.srfg.graphium.model.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.IBaseWayGraphModelFactory;
import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.ISource;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.IWaySegmentConnection;
import at.srfg.graphium.model.State;

public abstract class AbstractWayGraphModelFactory<T extends IBaseWaySegment>
	extends AbstractGraphModelFactory<T>
	implements IBaseWayGraphModelFactory<T> {

	@Override
	public IWaySegmentConnection newWaySegmentConnection() {
		return new WaySegmentConnection();
	}

	@Override
	public IWaySegmentConnection newWaySegmentConnection(long nodeId, long fromSegmentId,
			long toSegmentId, Set<Access> access) {
		return new WaySegmentConnection(nodeId, fromSegmentId, toSegmentId, access);
	}

	@Override
	public IWaySegmentConnection newWaySegmentConnection(long nodeId, long fromSegmentId,
			long toSegmentId, Set<Access> access, Map<String, String> tags) {
		return new WaySegmentConnection(nodeId, fromSegmentId, toSegmentId, access, null, tags);
	}
	
	protected List<ISegmentXInfo> mergeXInfoList(Map<String, List<ISegmentXInfo>> xInfo) {
		List<ISegmentXInfo> merged = new ArrayList<ISegmentXInfo>();
		if(xInfo != null) {		
			for(List<ISegmentXInfo> xi : xInfo.values()) {
				merged.addAll(xi);
			}		
		}
		return merged;
	}
	
	@Override
	public IWayGraphVersionMetadata newWayGraphVersionMetadata() {
		return new WayGraphVersionMetadata();
	}

	@Override
	public IWayGraphVersionMetadata newWayGraphVersionMetadata(long id,
			long graphId, String graphName, String version,
			String originGraphName, String originVersion, State state,
			Date validFrom, Date validTo, Polygon coveredArea,
			int segmentsCount, int connectionsCount, Set<Access> accessTypes,
			Map<String, String> tags, ISource source, String type,
			String description, Date creationTimestamp, Date storageTimestamp,
			String creator, String originUrl) {
		return new WayGraphVersionMetadata(id, graphId, graphName, version,
				originGraphName, originVersion, state, validFrom, validTo,
				coveredArea, segmentsCount, connectionsCount, accessTypes,
				tags, source, type, description, creationTimestamp,
				storageTimestamp, creator, originUrl);
	}

	protected List<IWaySegmentConnection> adaptConns(List<IWaySegmentConnection> startNodeCons,
			   List<IWaySegmentConnection> endNodeCons) {
		List<IWaySegmentConnection> conns = new ArrayList<>();
		if (startNodeCons != null) {
		conns.addAll(startNodeCons);
		}
		if (endNodeCons != null) {
		conns.addAll(endNodeCons);
		}
		return conns;
	}


}
