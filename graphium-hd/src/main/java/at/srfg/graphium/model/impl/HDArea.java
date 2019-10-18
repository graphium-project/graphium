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
package at.srfg.graphium.model.impl;

import com.vividsolutions.jts.geom.Geometry;

import at.srfg.graphium.model.IHDArea;

public class HDArea extends WaySegment implements IHDArea {

	private static final long serialVersionUID = 1704083353423296842L;

	private Geometry areaGeometry;
	
	public HDArea() {}
	
	public HDArea(Geometry areaGeometry) {
		super();
		this.areaGeometry = areaGeometry;
	}

	@Override
	public Geometry getAreaGeometry() {
		return areaGeometry;
	}

	@Override
	public void setAreaGeometry(Geometry area) {
		areaGeometry = area;
	}

	@Override
	public String toString() {
		return "LaneletArea [areaGeometry=" + areaGeometry + ", maxSpeedTow=" + maxSpeedTow + ", maxSpeedBkw="
				+ maxSpeedBkw + ", speedCalcTow=" + speedCalcTow + ", speedCalcBkw=" + speedCalcBkw + ", lanesTow="
				+ lanesTow + ", lanesBkw=" + lanesBkw + ", frc=" + frc + ", formOfWay=" + formOfWay + ", accessTow="
				+ accessTow + ", accessBkw=" + accessBkw + ", tunnel=" + tunnel + ", bridge=" + bridge + ", urban="
				+ urban + ", timestamp=" + timestamp + ", geometry=" + geometry + ", length=" + length + ", name="
				+ name + ", streetType=" + streetType + ", wayId=" + wayId + ", startNodeId=" + startNodeId
				+ ", startNodeIndex=" + startNodeIndex + ", endNodeId=" + endNodeId + ", endNodeIndex=" + endNodeIndex
				+ ", tags=" + tags + ", id=" + id + ", xInfo=" + xInfo + ", cons=" + cons + "]";
	}

}