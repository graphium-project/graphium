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

public enum FuncRoadClass {

	NOT_APPLICABLE ((short)-1),
	MOTORWAY_FREEWAY_OR_OTHER_MAJOR_MOTORWAY ((short)0),
	MAJOR_ROAD_LESS_IMORTANT_THAN_MOTORWAY ((short)1),
	OTHER_MAJOR_ROAD ((short)2),
	SECONDARY_ROAD ((short)3),
	LOCAL_CONNECTING_ROAD ((short)4),
	LOCAL_ROAD_OF_HIGH_IMPORTANCE ((short)5),
	LOCAL_ROAD ((short)6),
	LOCAL_ROAD_OF_MINOR_IMPORTANCE ((short)7),
	SONSTIGE_STRASSEN ((short)8),
	RAD_FUSSWEG ((short)10),
	WIRTSCHAFTSWEG ((short)11),
	SONSTIGER_WEG ((short)12),
	BAHNTRASSE_HOCHRANGIG ((short)20),
	BAHNTRASSE_NIEDERRANGIG((short)21),
	BAHNTRASSE_ANSCHLUSSBAHN((short)22),
	STRASSENBAHNGLEIS ((short)24),
	U_BAHN_TRASSE ((short)25),
	FAEHRE ((short)31),
	TREPPE ((short)45),
	ROLLTREPPE ((short)46),
	AUFZUG ((short)47),
	RAMPE ((short)48),
	BETRIEBSUMKEHR((short)98),
	BETRIEBSWEG((short)99),
	FUSSWEG_OHNE_ANZEIGE ((short)101),
	FUSSWEGPASSAGE ((short)102),
	SEILBAHN_UND_SONSTIGE ((short)103),
	SONDERELEMENT ((short)104), //TODO: exisitert laut aktueller GIP nicht mehr
	ALMAUFSCHLIESSUNGSWEG ((short)105),
	FORSTAUFSCHLIESSUNGSWEG ((short)106),
	GEBAEUDEZUFAHRTEN ((short)107),
	GUETERWEG((short)108), //TODO: exisitert laut aktueller GIP nicht mehr
	FRIEDHOFSWEG((short)115),
	SINGLETRAIL((short)200),
	SHARED_TRAIL((short)201),
	WANDERWEG((short)300);
	
	private short value;
	
	FuncRoadClass (short value) {
		this.value = value;
	}

	public short getValue() {
		return value;
	}
	
	public static FuncRoadClass getFuncRoadClassForValue(short value) {
		for (FuncRoadClass frc : values()) {
			if (frc.getValue() == value) {
				return frc;
			}
		}
		return NOT_APPLICABLE;
	}
	
}
