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
package at.srfg.graphium.io.dto;

import com.vividsolutions.jts.geom.LineString;

public interface IHDWaySegmentDTO extends IWaySegmentDTO {

	LineString getLeftBoarderGeometry();
	
	void setLeftBoarderGeometry(LineString geometry);

	long getLeftBoarderStartNodeId();
	
	void setLeftBoarderStartNodeId(long startNodeId);
	
	long getLeftBoarderEndNodeId();
	
	void setLeftBoarderEndNodeId(long endNodeId);
	
	LineString getRightBoarderGeometry();
	
	void setRightBoarderGeometry(LineString geometry);

	long getRightBoarderStartNodeId();
	
	void setRightBoarderStartNodeId(long startNodeId);
	
	long getRightBoarderEndNodeId();
	
	void setRightBoarderEndNodeId(long endNodeId);
	
}