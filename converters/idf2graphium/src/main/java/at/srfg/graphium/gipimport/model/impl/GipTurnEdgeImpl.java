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

import at.srfg.graphium.gipimport.model.IGipTurnEdge;

public class GipTurnEdgeImpl implements IGipTurnEdge {

	private long id;
	private long fromLinkId;
	private long toLinkId;
	private long viaNodeId;
	private int vehicleType;


	@Override
	public long getId() {
		return id;
	}

	@Override
	public int getVehicleType() {
		return vehicleType;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public void setVehicleType(int vehicleType) {
		this.vehicleType = vehicleType;
	}

	@Override
	public long getFromLinkId() {
		return fromLinkId;
	}

	@Override
	public void setFromLinkId(long fromLinkId) {
		this.fromLinkId = fromLinkId;
	}

	@Override
	public long getToLinkId() {
		return toLinkId;
	}

	@Override
	public void setToLinkId(long toLinkId) {
		this.toLinkId = toLinkId;
	}

	@Override
	public long getViaNodeId() {
		return viaNodeId;
	}

	@Override
	public void setViaNodeId(long viaNodeId) {
		this.viaNodeId = viaNodeId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GipTurnEdgeImpl that = (GipTurnEdgeImpl) o;

		if (id != that.id) return false;
		if (fromLinkId != that.fromLinkId) return false;
		if (toLinkId != that.toLinkId) return false;
		if (viaNodeId != that.viaNodeId) return false;
		return vehicleType == that.vehicleType;

	}

	@Override
	public int hashCode() {
		int result = (int) (id ^ (id >>> 32));
		result = 31 * result + (int) (fromLinkId ^ (fromLinkId >>> 32));
		result = 31 * result + (int) (toLinkId ^ (toLinkId >>> 32));
		result = 31 * result + (int) (viaNodeId ^ (viaNodeId >>> 32));
		result = 31 * result + vehicleType;
		return result;
	}

	@Override
	public String toString() {
		return "GipTurnEdgeImpl{" +
				"id=" + id +
				", fromLinkId=" + fromLinkId +
				", toLinkId=" + toLinkId +
				", viaNodeId=" + viaNodeId +
				", vehicleType=" + vehicleType +
				'}';
	}
}
