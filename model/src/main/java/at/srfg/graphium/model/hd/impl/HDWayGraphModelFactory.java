/**
 * Copyright © 2019 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.model.hd.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.FormOfWay;
import at.srfg.graphium.model.FuncRoadClass;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.IWayGraphModelFactory;
import at.srfg.graphium.model.IWaySegmentConnection;
import at.srfg.graphium.model.hd.IHDWaySegment;
import at.srfg.graphium.model.impl.AbstractWayGraphModelFactory;

/**
 * @author mwimmer
 *
 */
public class HDWayGraphModelFactory extends
		AbstractWayGraphModelFactory<IHDWaySegment> implements
		IWayGraphModelFactory<IHDWaySegment> {

	@Override
	public IHDWaySegment newSegment() {
		return new HDWaySegment();
	}

	@Override
	public IHDWaySegment newSegment(long id,
			Map<String, List<ISegmentXInfo>> xInfo) {
		IHDWaySegment segment = new HDWaySegment();
		segment.setId(id);
		segment.setXInfo(mergeXInfoList(xInfo));
		return segment;
	}

	@Override
	public IHDWaySegment newSegment(long id, Map<String, List<ISegmentXInfo>> xInfo,
			List<IWaySegmentConnection> connections) {
		IHDWaySegment segment = new HDWaySegment();
		segment.setId(id);
		segment.setXInfo(mergeXInfoList(xInfo));
		segment.setCons(connections);
		return segment;		
	}

	@Override
	public IHDWaySegment newSegment(long id, LineString geometry, float length,
			String name, String streetType, long wayId, long startNodeId,
			int startNodeIndex, long endNodeId, int endNodeIndex,
			List<IWaySegmentConnection> connections) {
		IHDWaySegment segment = new HDWaySegment();
		segment.setId(id);
		segment.setGeometry(geometry);
		segment.setLength(length);
		segment.setName(name);
		segment.setStreetType(streetType);
		segment.setWayId(wayId);
		segment.setStartNodeId(startNodeId);
		segment.setStartNodeIndex(startNodeIndex);
		segment.setEndNodeId(endNodeId);
		segment.setEndNodeIndex(endNodeIndex);
		segment.setCons(connections);
		return segment;
	}
	
	@Override
	public IHDWaySegment newSegment(long id, LineString geometry, float length,
			String name, String streetType, long wayId, long startNodeId,
			int startNodeIndex, long endNodeId, int endNodeIndex,
			List<IWaySegmentConnection> connections,
			Map<String, String> tags, Map<String, List<ISegmentXInfo>> xInfo) {
		IHDWaySegment segment = new HDWaySegment();
		segment.setId(id);
		segment.setGeometry(geometry);
		segment.setLength(length);
		segment.setName(name);
		segment.setStreetType(streetType);
		segment.setWayId(wayId);
		segment.setStartNodeId(startNodeId);
		segment.setStartNodeIndex(startNodeIndex);
		segment.setEndNodeId(endNodeId);
		segment.setEndNodeIndex(endNodeIndex);
		segment.setCons(connections);
		segment.setTags(tags);
		segment.setXInfo(mergeXInfoList(xInfo));
		return segment;
	}

	@Override
	public IHDWaySegment newSegment(long id, LineString geometry,
			float length, String name, short maxSpeedTow, short maxSpeedBkw,
			Short speedCalcTow, Short speedCalcBkw, short lanesTow,
			short lanesBkw, FuncRoadClass frc, FormOfWay formOfWay, String streetType,
			long wayId, long startNodeId, int startNodeIndex, long endNodeId,
			int endNodeIndex, Set<Access> accessTow, Set<Access> accessBkw,
			Boolean tunnel, Boolean bridge, Boolean urban, Date timestamp,
			List<IWaySegmentConnection> startNodeCons,
			List<IWaySegmentConnection> endNodeCons) {
		IHDWaySegment segment =  new HDWaySegment();
		segment.setId(id);
		segment.setGeometry(geometry);
		segment.setLength(length);
		segment.setName(name);
		segment.setMaxSpeedTow(maxSpeedTow);
		segment.setMaxSpeedBkw(maxSpeedBkw);
		segment.setSpeedCalcTow(speedCalcTow);
		segment.setSpeedCalcBkw(speedCalcBkw);
		segment.setLanesTow(lanesTow);
		segment.setLanesBkw(lanesBkw);
		segment.setFrc(frc);
		segment.setFormOfWay(formOfWay);
		segment.setStreetType(streetType);
		segment.setWayId(wayId);
		segment.setStartNodeId(startNodeId);
		segment.setStartNodeIndex(startNodeIndex);
		segment.setEndNodeId(endNodeId);
		segment.setEndNodeIndex(endNodeIndex);
		segment.setAccessTow(accessTow);
		segment.setAccessBkw(accessBkw);
		segment.setTunnel(tunnel);
		segment.setBridge(bridge);
		segment.setUrban(urban);
		segment.setTimestamp(timestamp);
		segment.setCons(adaptConns(startNodeCons, endNodeCons));
		return segment;
	}
	
	@Override
	public IHDWaySegment newSegment(long id, LineString geometry,
								  float length, String name, short maxSpeedTow, short maxSpeedBkw,
								  Short speedCalcTow, Short speedCalcBkw, short lanesTow,
								  short lanesBkw, FuncRoadClass frc, FormOfWay formOfWay, String streetType,
								  long wayId, long startNodeId, int startNodeIndex, long endNodeId,
								  int endNodeIndex, Set<Access> accessTow, Set<Access> accessBkw,
								  Boolean tunnel, Boolean bridge, Boolean urban, Date timestamp,
								  List<IWaySegmentConnection> startNodeCons,
								  List<IWaySegmentConnection> endNodeCons,
								  Map<String, String> tags, Map<String, List<ISegmentXInfo>> xInfo) {
		IHDWaySegment segment = newSegment(id, geometry, length, name, maxSpeedTow, maxSpeedBkw, speedCalcTow, speedCalcBkw, lanesTow, lanesBkw, frc, formOfWay, streetType, wayId, startNodeId, startNodeIndex, endNodeId, endNodeIndex, accessTow, accessBkw, tunnel, bridge, urban, timestamp, startNodeCons, endNodeCons);
		segment.setTags(tags);
		for (List<ISegmentXInfo> xInfos : xInfo.values()) {
			segment.addXInfo(xInfos);
		}
		return segment;
	}

}
