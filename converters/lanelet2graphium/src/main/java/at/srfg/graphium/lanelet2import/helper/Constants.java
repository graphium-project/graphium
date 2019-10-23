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
package at.srfg.graphium.lanelet2import.helper;

/**
 * @author mwimmer
 *
 */
public class Constants {
	
	public final static String TYPE_LANELET = "lanelet";
	public final static int SRID = 4326;
	public final static String TAG_LANE_CHANGE = "laneChange";

	public final static String LANELET_TYPE_LINE_THIN = "line_thin";
	public final static String LANELET_TYPE_LINE_THICK = "line_thick";
	public final static String LANELET_TYPE_CURBSTONE = "curbstone";
	public final static String LANELET_TYPE_VIRTUAL = "virtual";
	public final static String LANELET_TYPE_ROAD_BOARDER = "road_boarder";

	public final static String LANELET_SUBTYPE_SOLID = "solid";
	public final static String LANELET_SUBTYPE_DASHED = "dashed";
	public final static String LANELET_SUBTYPE_DASHED_SOLID = "dashed_solid";
	public final static String LANELET_SUBTYPE_SOLID_DASHED = "solid_dashed";
	public final static String LANELET_SUBTYPE_HIGH = "high";
	public final static String LANELET_SUBTYPE_LOW = "low";

}
