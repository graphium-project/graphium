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
package at.srfg.graphium.lanelet2import.connections;

import java.util.Set;

import at.srfg.graphium.lanelet2import.helper.Constants;
import at.srfg.graphium.lanelet2import.reader.LaneletContainer;
import at.srfg.graphium.model.IHDWaySegment;
import at.srfg.graphium.model.IWaySegmentConnection;
import at.srfg.graphium.model.OneWay;
import at.srfg.graphium.osmimport.helper.ConnectionsHelper;

/**
 * @author mwimmer
 *
 */
public class ConnectionsBuilder {

	// TODO: Access / Restrictions werden dzt. nur von Quell-Lanelet berücksichtigt
	
	public void build(IHDWaySegment lanelet, LaneletContainer laneletContainer) {
		Set<IHDWaySegment> neighbours = laneletContainer.getNeighbours(lanelet);
		String laneChangeLeftAllowed = lanelet.getTags().get(Constants.TAG_LANE_CHANGE + ":" + Constants.LEFT);
		String laneChangeRightAllowed = lanelet.getTags().get(Constants.TAG_LANE_CHANGE + ":" + Constants.RIGHT);
		if (neighbours != null) {
			for (IHDWaySegment neighbour : neighbours) {
				if (lanelet.getId() != neighbour.getId() ) {
					if ((lanelet.getLeftBoarderStartNodeId() == neighbour.getRightBoarderStartNodeId() &&	// parallel lane
						 lanelet.getLeftBoarderEndNodeId()   == neighbour.getRightBoarderEndNodeId()) ||
						(lanelet.getLeftBoarderStartNodeId() == neighbour.getLeftBoarderEndNodeId() &&		// opposite lane
						 lanelet.getLeftBoarderEndNodeId()   == neighbour.getLeftBoarderStartNodeId())) {
						// left side parallel lanelet
						IWaySegmentConnection connection = ConnectionsHelper.createConnection(lanelet.getId(), 
								  															  neighbour.getId(),
								  															  -1L, 
																							  lanelet.getAccessTow());
						String type = Constants.CONNECTION_TYPE_CONNECTS.toString();
						if (laneChangeLeftAllowed != null && laneChangeLeftAllowed.equals("false")) {
							type = Constants.CONNECTION_TYPE_CONNECTS_FORBIDDEN.toString();
						}
						connection.addTag(Constants.CONNECTION_TYPE, type);
						connection.addTag(Constants.CONNECTION_PARALLEL, Constants.LEFT);
						
						if (lanelet.getLeftBoarderStartNodeId() == neighbour.getLeftBoarderEndNodeId()) {
							// opposite lane
							connection.addTag(Constants.CONNECTION_DIRECTION, Constants.CONNECTION_REVERSE);
						}
						
						ConnectionsHelper.addConnectionToSegment(lanelet, connection, -1L);
					} else
	
					if ((lanelet.getRightBoarderStartNodeId() == neighbour.getLeftBoarderStartNodeId() &&	// parallel lane
						 lanelet.getRightBoarderEndNodeId()   == neighbour.getLeftBoarderEndNodeId()) ||
						(lanelet.getRightBoarderStartNodeId() == neighbour.getRightBoarderEndNodeId() &&	// opposite lane
						 lanelet.getRightBoarderEndNodeId()   == neighbour.getRightBoarderStartNodeId())) {
						// right side parallel lanelet
						IWaySegmentConnection connection = ConnectionsHelper.createConnection(lanelet.getId(), 
								  															  neighbour.getId(),
								  															  -1L, 
																							  lanelet.getAccessTow());
						String type = Constants.CONNECTION_TYPE_CONNECTS.toString();
						if (laneChangeRightAllowed != null && laneChangeRightAllowed.equals("false")) {
							type = Constants.CONNECTION_TYPE_CONNECTS_FORBIDDEN.toString();
						}
						connection.addTag(Constants.CONNECTION_TYPE, type);
						connection.addTag(Constants.CONNECTION_PARALLEL, Constants.RIGHT);
						
						if (lanelet.getRightBoarderStartNodeId() == neighbour.getRightBoarderEndNodeId()) {
							// opposite lane
							connection.addTag(Constants.CONNECTION_DIRECTION, Constants.CONNECTION_REVERSE);
						}
						
						ConnectionsHelper.addConnectionToSegment(lanelet, connection, -1L);
	
					} else
	
					if (lanelet.getLeftBoarderEndNodeId() == neighbour.getLeftBoarderStartNodeId() &&
						lanelet.getRightBoarderEndNodeId() == neighbour.getRightBoarderStartNodeId()) {
						// succeeding lanelet
						IWaySegmentConnection connection = ConnectionsHelper.createConnection(lanelet.getId(), 
								  															  neighbour.getId(),
								  															  lanelet.getEndNodeId(), 
																							  lanelet.getAccessTow());
						String type = Constants.CONNECTION_TYPE_CONNECTS.toString();
						connection.addTag(Constants.CONNECTION_TYPE, type);
						ConnectionsHelper.addConnectionToSegment(lanelet, connection, lanelet.getEndNodeId());
	
					} else
	
					if (lanelet.isOneway().equals(OneWay.NO_ONEWAY)) {
						if ((neighbour.isOneway().equals(OneWay.NO_ONEWAY) &&
							 lanelet.getLeftBoarderStartNodeId()  == neighbour.getLeftBoarderEndNodeId() &&
							 lanelet.getRightBoarderStartNodeId() == neighbour.getRightBoarderEndNodeId()) ||
							(lanelet.getLeftBoarderStartNodeId()  == neighbour.getRightBoarderStartNodeId() &&	// preceding lanelet in reverse direction
							 lanelet.getRightBoarderStartNodeId() == neighbour.getLeftBoarderStartNodeId())) {
							// preceding lanelet
							IWaySegmentConnection connection = ConnectionsHelper.createConnection(lanelet.getId(), 
									  															  neighbour.getId(),
									  															  lanelet.getStartNodeId(), 
																								  lanelet.getAccessBkw());
							String type = Constants.CONNECTION_TYPE_CONNECTS.toString();
							connection.addTag(Constants.CONNECTION_TYPE, type);
							connection.addTag(Constants.CONNECTION_DIRECTION, Constants.CONNECTION_REVERSE);
							ConnectionsHelper.addConnectionToSegment(lanelet, connection, lanelet.getEndNodeId());
						}
					}
				}
			}
		}
	}
	
}