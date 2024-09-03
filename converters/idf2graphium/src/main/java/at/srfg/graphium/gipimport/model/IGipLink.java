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
package at.srfg.graphium.gipimport.model;

import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.FuncRoadClass;

public interface IGipLink {

	long getId();
	void setId(long id);
	
	long getEdgeId();
	void setEdgeId(long edgeId);
	
	String getName1();
	void setName1(String name);
	
	String getName2();
	void setName2(String name);


	short getSpeedTow();
	void setSpeedTow(short speed);
	
	short getSpeedBkw();
	void setSpeedBkw(short speed);

	long getAccessTow();
	void setAccessTow(long access);
	boolean isAccessTow(Access access);
	
	long getAccessBkw();
	void setAccessBkw(long access);
	boolean isAccessBkw(Access access);

	float getLength();
	void setLength(float length);
	
	FuncRoadClass getFuncRoadClass();
//	void setFuncRoadClass(FuncRoadClass funcRoadClass);
	short getFuncRoadClassValue();
	void setFuncRoadClassValue(short funcRoadClass);

	short getFormOfWay();
	void setFormOfWay(short formOfWay);

	short getLanesTow();
	void setLanesTow(short lanes);
	
	short getLanesBkw();
	void setLanesBkw(short lanes);

	byte getUTurn();
	void setUTurn(byte uTurn);
	
	byte getOneway();
	void setOneway(byte oneway);
	
	int getLevel();
	void setLevel(int level);
	
	boolean isBridge();
	void setBridge(boolean bridge);
	
	boolean isTunnel();
	void setTunnel(boolean tunnel);
	
	boolean isUrban();
	void setUrban(boolean urban);

	boolean isValid();

	void setValid(boolean valid);

	long getFromNodeId();

	void setFromNodeId(long fromNodeId);

	long getToNodeId();

	void setToNodeId(long toNodeId);


	int[] getCoordinatesX();

	void setCoordinatesX(int[] coordinatesX);

	int[] getCoordinatesY();

	void setCoordinatesY(int[] coordinatesY);

	float getMaxWidth();

	void setMaxWidth(float maxWidth);

    String getObjectId();

	void setObjectId(String objectId);

	String getName3();

	void setName3(String name3);

	IGipLink clone() throws CloneNotSupportedException;
}