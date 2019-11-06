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
	public final static String LANELET_TYPE_ROAD_BORDER = "road_border";

	public final static String LANELET_SUBTYPE_SOLID = "solid";
	public final static String LANELET_SUBTYPE_DASHED = "dashed";
	public final static String LANELET_SUBTYPE_DASHED_SOLID = "dashed_solid";
	public final static String LANELET_SUBTYPE_SOLID_DASHED = "solid_dashed";
	public final static String LANELET_SUBTYPE_HIGH = "high";
	public final static String LANELET_SUBTYPE_LOW = "low";
	public final static String LANELET_ONEWAY = "one_way";
	public final static String LANELET_ROAD_NAME = "road_name";
	public final static String LANELET_ROAD_SURFACE = "road_surface";
	public final static String LANELET_REGION = "region";
	public final static String LANELET_SPEED_LIMIT = "speed_limit";
	public final static String LANELET_SPEED_LIMIT_MANDATORY = "speed_limit_mandatory ";
	public final static String LANELET_PARTICIPANT = "participant";
	public final static String LANELET_VEHICLE = "vehicle";
	public final static String LANELET_VEHICLE_CAR = "vehicle:car";
	public final static String LANELET_VEHICLE_CAR_ELECTRIC = "vehicle:car:electric";
	public final static String LANELET_VEHICLE_CAR_COMBUSTION = "vehicle:car:combustion";
	public final static String LANELET_VEHICLE_BUS = "vehicle:bus";
	public final static String LANELET_VEHICLE_TRUCK = "vehicle:truck";
	public final static String LANELET_VEHICLE_MOTORCYCLE = "vehicle:motorcycle";
	public final static String LANELET_VEHICLE_TAXI = "vehicle:taxi";
	public final static String LANELET_VEHICLE_EMERGENCY = "vehicle:emergency";
	public final static String LANELET_PEDESTRIAN = "pedestrian";
	public final static String LANELET_BYCICLE = "bycicle";

	public final static String BORDER_INVERTED = "invertedBorder";
	public final static String ROAD_TYPE = "roadType";
	public final static String LOCATION = "location";
	public final static String ROAD_SURFACE = "roadSurface";
	public final static String REGION = "region";
	public final static String URBAN = "urban";
	public final static String NONURBAN = "nonurban";
	
	public final static String CONNECTION_TYPE = "connectionType";
	public final static String CONNECTION_TYPE_CONNECTS = "connects";
	public final static String CONNECTION_TYPE_CONNECTS_FORBIDDEN = "connects_forbidden";
	public final static String CONNECTION_PARALLEL = "parallel";
	public final static String CONNECTION_DIRECTION = "direction";
	public final static String CONNECTION_REVERSE = "reverse";
	public final static String CONNECTION_DIVERGING = "diverging";
	
	public final static String LEFT = "left";
	public final static String RIGHT = "right";
	
}
