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
/**
 * (C) 2017 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.tutorial.xinfo.dto.impl;

import at.srfg.graphium.io.dto.impl.AbstractSegmentXInfoDTO;
import at.srfg.graphium.tutorial.xinfo.dto.IRoadDamageDTO;

/**
 * @author mwimmer
 */
public class RoadDamageDTOImpl extends AbstractSegmentXInfoDTO implements IRoadDamageDTO {

	/**
	 * id of way segment
	 */
	private long segmentId = 0;
	/**
	 * id of referenced graph version
	 */
	private long graphVersionId = 0;
	/**
	 * road damage object refers segment in / against geometry direction
	 */
	private boolean directionTow;
	/**
	 * start offset on segment geometry in road damage object direction
	 */
	private float startOffset = 0;
	/**
	 * end offset on segment geometry in road damage object direction
	 */
	private float endOffset = 1;
	/**
	 * type of road damage
	 */
	private String type;
	
	public RoadDamageDTOImpl() {
		super();
	}
	
	public RoadDamageDTOImpl(long segmentId, long graphVersionId, boolean directionTow, float startOffset, float endOffset, String type) {
		super();
		this.segmentId = segmentId;
		this.graphVersionId = graphVersionId;
		this.directionTow = directionTow;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
		this.type = type;
	}

	
	public long getSegmentId() {
		return segmentId;
	}

	public void setSegmentId(long segmentId) {
		this.segmentId = segmentId;
	}

	public long getGraphVersionId() {
		return graphVersionId;
	}
	
	public void setGraphVersionId(long graphVersionId) {
		this.graphVersionId = graphVersionId;
	}

	public boolean isDirectionTow() {
		return directionTow;
	}

	public void setDirectionTow(boolean directionTow) {
		this.directionTow = directionTow;
	}

	public float getStartOffset() {
		return startOffset;
	}

	public void setStartOffset(float startOffset) {
		this.startOffset = startOffset;
	}

	public float getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(float endOffset) {
		this.endOffset = endOffset;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
