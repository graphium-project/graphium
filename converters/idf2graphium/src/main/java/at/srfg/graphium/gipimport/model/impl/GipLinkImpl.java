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

import java.io.Serializable;
import java.util.Arrays;

import at.srfg.graphium.gipimport.model.IGipLink;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.FuncRoadClass;

public class GipLinkImpl implements IGipLink, Cloneable, Serializable {

	private static final long serialVersionUID = 1239758995434878308L;

	private String objectId;
	private long id;
	private String name1;
	private String name2;
	//TODO: delete
	private String name3;
	private long fromNodeId;
	private long toNodeId;
	private short speedTow;
	private short speedBkw;
	private long accessTow;
	private long accessBkw;
	private float length;
	private short formOfWay;
	private short funcRoadClass;
	private short lanesTow;
	private short lanesBkw;
	private byte uTurn;
	private boolean urban;
	private byte oneway;
	private int[] coordinatesX;
	private int[] coordinatesY;
	private int level;
	private boolean bridge;
	private boolean tunnel;
	private boolean valid;
	private long edgeId;
	private float maxWidth;
	
	@Override
	public long getAccessBkw() {
		return accessBkw;
	}
	@Override
	public long getAccessTow() {
		return accessTow;
	}

	@Override
	public FuncRoadClass getFuncRoadClass() {
		return FuncRoadClass.getFuncRoadClassForValue(funcRoadClass);
	}
	@Override
	public short getFuncRoadClassValue() {
		return funcRoadClass;
	}

	@Override
	public long getId() {
		return id;
	}
	@Override
	public float getLength() {
		return length;
	}
	@Override
	public String getName1() {
		return name1;
	}
	@Override
	public String getName2() {
		return name2;
	}
	@Override
	public byte getOneway() {
		return oneway;
	}
	@Override
	public short getSpeedBkw() {
		return speedBkw;
	}
	@Override
	public short getSpeedTow() {
		return speedTow;
	}

	@Override
	public byte getUTurn() {
		return uTurn;
	}
	@Override
	public short getLanesBkw() {
		return lanesBkw;
	}
	@Override
	public short getLanesTow() {
		return lanesTow;
	}

	// TODO: brauchen wir das?
	@Override
	public boolean isAccessBkw(Access access) {
		return false;
	}
	// TODO: brauchen wir das?
	@Override
	public boolean isAccessTow(Access access) {
		return false;
	}

	@Override
	public void setAccessBkw(long access) {
		this.accessBkw = access;
	}
	@Override
	public void setAccessTow(long access) {
		this.accessTow = access;
	}

	@Override
	public void setFuncRoadClassValue(short funcRoadClass) {
		this.funcRoadClass = funcRoadClass;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}
	@Override
	public void setLength(float length) {
		this.length = length;
	}
	@Override
	public void setName1(String name) {
		this.name1 = name;
	}
	@Override
	public void setName2(String name) {
		this.name2 = name;
	}
	@Override
	public void setOneway(byte oneway) {
		this.oneway = oneway;
	}
	@Override
	public void setSpeedBkw(short speed) {
		this.speedBkw = speed;
	}
	@Override
	public void setSpeedTow(short speed) {
		this.speedTow = speed;
	}

	@Override
	public void setUTurn(byte uturn) {
		this.uTurn = uturn;
	}
	@Override
	public void setLanesBkw(short lanes) {
		this.lanesBkw = lanes;
	}
	@Override
	public void setLanesTow(short lanes) {
		this.lanesTow = lanes;
	}
	@Override
	public int getLevel() {
		return level;
	}
	@Override
	public void setLevel(int level) {
		this.level = level;
	}
	@Override
	public boolean isBridge() {
		return bridge;
	}
	@Override
	public void setBridge(boolean bridge) {
		this.bridge = bridge;
	}
	@Override
	public boolean isTunnel() {
		return tunnel;
	}
	@Override
	public void setTunnel(boolean tunnel) {
		this.tunnel = tunnel;
	}
	@Override
	public short getFormOfWay() {
		return formOfWay;
	}
	@Override
	public void setFormOfWay(short formOfWay) {
		this.formOfWay = formOfWay;
	}
	@Override
	public boolean isUrban() {
		return urban;
	}
	@Override
	public void setUrban(boolean urban) {
		this.urban = urban;
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	@Override
	public long getFromNodeId() {
		return fromNodeId;
	}

	@Override
	public void setFromNodeId(long fromNodeId) {
		this.fromNodeId = fromNodeId;
	}

	@Override
	public long getToNodeId() {
		return toNodeId;
	}

	@Override
	public void setToNodeId(long toNodeId) {
		this.toNodeId = toNodeId;
	}

	@Override
	public int[] getCoordinatesX() {
		return coordinatesX;
	}

	@Override
	public void setCoordinatesX(int[] coordinatesX) {
		this.coordinatesX = coordinatesX;
	}

	@Override
	public int[] getCoordinatesY() {
		return coordinatesY;
	}

	@Override
	public void setCoordinatesY(int[] coordinatesY) {
		this.coordinatesY = coordinatesY;
	}

	@Override
	public long getEdgeId() {
		return edgeId;
	}
	
	@Override
	public void setEdgeId(long edgeId) {
		this.edgeId = edgeId;
	}

	@Override
	public float getMaxWidth() {
		return maxWidth;
	}

	@Override
	public void setMaxWidth(float maxWidth) {
		this.maxWidth = maxWidth;
	}

	@Override
	public String getObjectId() {
		return objectId;
	}

	@Override
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	@Override
	public String getName3() {
		return name3;
	}

	@Override
	public void setName3(String name3) {
		this.name3 = name3;
	}

	@Override
	public IGipLink clone() throws CloneNotSupportedException {
		return (IGipLink) super.clone();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GipLinkImpl gipLink = (GipLinkImpl) o;

		if (id != gipLink.id) return false;
		if (fromNodeId != gipLink.fromNodeId) return false;
		if (toNodeId != gipLink.toNodeId) return false;
		if (speedTow != gipLink.speedTow) return false;
		if (speedBkw != gipLink.speedBkw) return false;
		if (accessTow != gipLink.accessTow) return false;
		if (accessBkw != gipLink.accessBkw) return false;
		if (Float.compare(gipLink.length, length) != 0) return false;
		if (funcRoadClass != gipLink.funcRoadClass) return false;
		if (lanesTow != gipLink.lanesTow) return false;
		if (lanesBkw != gipLink.lanesBkw) return false;
		if (maxWidth != gipLink.maxWidth) return false;
		if (uTurn != gipLink.uTurn) return false;
		if (urban != gipLink.urban) return false;
		if (oneway != gipLink.oneway) return false;
		if (Float.compare(gipLink.level, level) != 0) return false;
		if (bridge != gipLink.bridge) return false;
		if (tunnel != gipLink.tunnel) return false;
		if (valid != gipLink.valid) return false;
		if (name1 != null ? !name1.equals(gipLink.name1) : gipLink.name1 != null) return false;
		if (name2 != null ? !name2.equals(gipLink.name2) : gipLink.name2 != null) return false;
		if (name3 != null ? !name3.equals(gipLink.name3) : gipLink.name3 != null) return false;
		if (objectId != null ? !objectId.equals(gipLink.objectId) : gipLink.objectId != null) return false;
		if (!Arrays.equals(coordinatesX, gipLink.coordinatesX)) return false;
		return Arrays.equals(coordinatesY, gipLink.coordinatesY);

	}

	@Override
	public int hashCode() {
		int result = Long.hashCode(id);
		result = 31 * result + (name1 != null ? name1.hashCode() : 0);
		result = 31 * result + (name2 != null ? name2.hashCode() : 0);
		result = 31 * result + (name3 != null ? name3.hashCode() : 0);
		result = 31 * result + (objectId != null ? objectId.hashCode() : 0);
		result = 31 * result + Long.hashCode(fromNodeId);
		result = 31 * result + Long.hashCode(toNodeId);
		result = 31 * result + (int) speedTow;
		result = 31 * result + (int) speedBkw;
		result = 31 * result + Long.hashCode(accessTow);
		result = 31 * result + Long.hashCode(accessBkw);
		result = 31 * result + (length != +0.0f ? Float.floatToIntBits(length) : 0);
		result = 31 * result + funcRoadClass;
		result = 31 * result + (int) lanesTow;
		result = 31 * result + (int) lanesBkw;
		result = 31 * result + (maxWidth != +0.0f ? Float.floatToIntBits(maxWidth) : 0);
		result = 31 * result + (int) uTurn;
		result = 31 * result + (urban ? 1 : 0);
		result = 31 * result + (int) oneway;
		result = 31 * result + (coordinatesX != null ? Arrays.hashCode(coordinatesX) : 0);
		result = 31 * result + (coordinatesY != null ? Arrays.hashCode(coordinatesY) : 0);
		result = 31 * result + (level != +0.0f ? Float.floatToIntBits(level) : 0);
		result = 31 * result + (bridge ? 1 : 0);
		result = 31 * result + (tunnel ? 1 : 0);
		result = 31 * result + (valid ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return "GipLinkImpl{" +
				"id=" + id +
				", name1='" + name1 + '\'' +
				", name2='" + name2 + '\'' +
				", fromNodeId=" + fromNodeId +
				", toNodeId=" + toNodeId +
				", speedTow=" + speedTow +
				", speedBkw=" + speedBkw +
				", accessTow=" + accessTow +
				", accessBkw=" + accessBkw +
				", length=" + length +
				", maxWidth=" + maxWidth +
				", funcRoadClass=" + funcRoadClass +
				", lanesTow=" + lanesTow +
				", lanesBkw=" + lanesBkw +
				", uTurn=" + uTurn +
				", urban=" + urban +
				", oneway=" + oneway +
				", coordinatesX=" + Arrays.toString(coordinatesX) +
				", coordinatesY=" + Arrays.toString(coordinatesY) +
				", level=" + level +
				", bridge=" + bridge +
				", tunnel=" + tunnel +
				", valid=" + valid +
				'}';
	}
}