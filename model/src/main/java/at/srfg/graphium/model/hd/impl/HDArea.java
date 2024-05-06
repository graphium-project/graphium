/**
 * Copyright © 2019 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package at.srfg.graphium.model.hd.impl;

import at.srfg.graphium.model.impl.BaseSegment;
import com.vividsolutions.jts.geom.Geometry;

import at.srfg.graphium.model.hd.IHDArea;

import java.util.Map;

public class HDArea extends BaseSegment implements IHDArea {

    private static final long serialVersionUID = 1704083353423296842L;

    private Geometry geometry;
    private String type;
    protected Map<String, String> tags;

    public HDArea() {
    }

    public HDArea(long id, Geometry geometry, String type, Map<String, String> tags) {
        super();
        this.id = id;
        this.type = type;
        this.tags = tags;
        this.geometry = geometry;
    }

    @Override
    public Geometry getGeometry() {
        return geometry;
    }

    @Override
    public void setGeometry(Geometry area) {
        geometry = area;
    }

    @Override
    public Map<String, String> getTags() {
        return tags;
    }

    @Override
    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "LaneletArea [geometry=" + geometry + ", id=" + id + ", xInfo=" + xInfo + ", cons=" + cons + "]";
		/*return "LaneletArea [areaGeometry=" + areaGeometry + ", maxSpeedTow=" + maxSpeedTow + ", maxSpeedBkw="
				+ maxSpeedBkw + ", speedCalcTow=" + speedCalcTow + ", speedCalcBkw=" + speedCalcBkw + ", lanesTow="
				+ lanesTow + ", lanesBkw=" + lanesBkw + ", frc=" + frc + ", formOfWay=" + formOfWay + ", accessTow="
				+ accessTow + ", accessBkw=" + accessBkw + ", tunnel=" + tunnel + ", bridge=" + bridge + ", urban="
				+ urban + ", timestamp=" + timestamp + ", geometry=" + geometry + ", length=" + length + ", name="
				+ name + ", streetType=" + streetType + ", wayId=" + wayId + ", startNodeId=" + startNodeId
				+ ", startNodeIndex=" + startNodeIndex + ", endNodeId=" + endNodeId + ", endNodeIndex=" + endNodeIndex
				+ ", tags=" + tags + ", id=" + id + ", xInfo=" + xInfo + ", cons=" + cons + "]";*/
    }

}