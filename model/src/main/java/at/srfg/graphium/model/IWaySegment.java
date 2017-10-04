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
package at.srfg.graphium.model;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IWaySegment extends IBaseWaySegment {

	short getMaxSpeedTow();

	void setMaxSpeedTow(short maxSpeedTow);

	short getMaxSpeedBkw();

	void setMaxSpeedBkw(short maxSpeedBkw);

	Short getSpeedCalcTow();

	void setSpeedCalcTow(Short speedCalcTow);

	Short getSpeedCalcBkw();

	void setSpeedCalcBkw(Short speedCalcBkw);

	short getLanesTow();

	void setLanesTow(short lanesTow);

	short getLanesBkw();

	void setLanesBkw(short lanesBkw);

	FuncRoadClass getFrc();

	void setFrc(FuncRoadClass frc);

	FormOfWay getFormOfWay();

	void setFormOfWay(FormOfWay formOfWay);

	Set<Access> getAccessTow();

	void setAccessTow(Set<Access> accessTow);

	Set<Access> getAccessBkw();

	void setAccessBkw(Set<Access> accessBkw);

	Boolean isTunnel();

	void setTunnel(Boolean tunnel);

	Boolean isBridge();

	void setBridge(Boolean bridge);

	Boolean isUrban();

	void setUrban(Boolean urban);

	Date getTimestamp();

	void setTimestamp(Date timestamp);
	
	/**
	 * @param directionTow true if duration in digitalization direction is needed
	 * @return duration in seconds; if calulated speed is set duration will be calculated using this, otherwise max speed will be used
	 */
	public int getDuration(boolean directionTow);

	/**
	 * @param directionTow true if duration in digitalization direction is needed
	 * @return duration in seconds; duration will be calculated using max speed
	 */
	public int getMinDuration(boolean directionTow);

	public OneWay isOneway();

	List<IWaySegmentConnection> getStartNodeCons();

	List<IWaySegmentConnection> getEndNodeCons();

	void setStartNodeCons(List<IWaySegmentConnection> startNodeCons);

	void setEndNodeCons(List<IWaySegmentConnection> endNodeCons);
	
}