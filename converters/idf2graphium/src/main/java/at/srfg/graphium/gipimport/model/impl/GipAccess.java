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
package at.srfg.graphium.gipimport.model.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mwimmer
 *
 */
public enum GipAccess {

	PEDESTRIAN (1, 1),
	BIKE (2, 2),
	PRIVATE_CAR (3, 4),
	PUBLIC_BUS (4, 8),
	RAILWAY (5, 16),
	TRAM (6, 32),
	SUBWAY (7, 64),
	FERRY_BOAT (8, 128),
	HIGH_OCCUPATION_CAR (9, 256),
	TRUCK (10, 512),
	TAXI (11, 1024),
	EMERGENCY_VEHICLE (12, 2048),
	MOTOR_COACH (13, 4096),
	TROLLY_BUS (14, 8192),
	MOTORCYCLE (15, 16384),
	RACK_RAILWAY (16, 32768),
	CABLE_RAILWAY (17, 65536),
	CAR_FERRY (18, 131072),
	CAMPER (19, 262144),
	COMBUSTIBLES (20, 524288),
	HAZARDOUS_TO_WATER (21, 1048576),
	GARBAGE_COLLECTION_VEHICLE (22, 2097152);
	
	
	private int bitNr;
	private int value;
	
	GipAccess(int bitNr, int value) {
		this.bitNr = bitNr;
		this.value = value;
	}

	public int getBitNr() {
		return bitNr;
	}

	public int getValue() {
		return value;
	}
	
	public static List<GipAccess> getAccessTypes(int access) {
		List<GipAccess> accessTypes = new ArrayList<GipAccess>();
		
		// convert integer to bit[]
		int count = GipAccess.values().length;
		boolean[] bits = new boolean[count];
		int decimalNumber = access;
		int index = 0; //count - 1;
		
		while (decimalNumber != 0) { 
            // extract last digit in binary representation 
            // and add it to binaryNumber 
            bits[index] = (decimalNumber % 2) == 1; 

            // cut last digit in binary representation 
            decimalNumber /= 2; 
            
            //index--;
            index++;
		}
		
		// list types with access
		GipAccess[] types = GipAccess.values();
		for (int i= 0; i<count; i++) {
			if (bits[i]) {
				accessTypes.add(types[i]);
			}
		}
		
		return accessTypes;
	}
	
}
