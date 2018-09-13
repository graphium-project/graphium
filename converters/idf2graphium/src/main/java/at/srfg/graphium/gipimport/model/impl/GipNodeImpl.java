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

import at.srfg.graphium.gipimport.model.IGipNode;

public class GipNodeImpl implements IGipNode, Serializable {

	private static final long serialVersionUID = 3531323005879065775L;

	private long id;
	private long virtualLinkId;
	private boolean virtual;
	private int coordinateX;
	private int coordinateY;
	

	@Override
	public long getId() {
		return id;
	}

	@Override
	public long getVirtualLinkId() {
		return virtualLinkId;
	}

	@Override
	public boolean isVirtual() {
		return virtual;
	}


	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public void setVirtual(boolean virtual) {
		this.virtual = virtual;
	}

	@Override
	public void setVirtualLinkId(long virtualLinkId) {
		this.virtualLinkId = virtualLinkId;
	}

	@Override
	public int getCoordinateX() {
		return coordinateX;
	}

	@Override
	public void setCoordinateX(int coordinateX) {
		this.coordinateX = coordinateX;
	}

	@Override
	public int getCoordinateY() {
		return coordinateY;
	}

	@Override
	public void setCoordinateY(int coordinateY) {
		this.coordinateY = coordinateY;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GipNodeImpl gipNode = (GipNodeImpl) o;

		if (id != gipNode.id) return false;
		if (virtualLinkId != gipNode.virtualLinkId) return false;
		if (virtual != gipNode.virtual) return false;
		if (coordinateX != gipNode.coordinateX) return false;
		return coordinateY == gipNode.coordinateY;

	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + (int) (virtualLinkId ^ (virtualLinkId >>> 32));
		result = 31 * result + (virtual ? 1 : 0);
		result = 31 * result + coordinateX;
		result = 31 * result + coordinateY;
		return result;
	}

	@Override
	public String toString() {
		return "GipNodeImpl{" +
				"id=" + id +
				", virtualLinkId=" + virtualLinkId +
				", virtual=" + virtual +
				", coordinateX=" + coordinateX +
				", coordinateY=" + coordinateY +
				'}';
	}
}
