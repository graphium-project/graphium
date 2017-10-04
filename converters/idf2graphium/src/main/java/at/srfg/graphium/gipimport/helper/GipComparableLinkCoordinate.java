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
package at.srfg.graphium.gipimport.helper;

public class GipComparableLinkCoordinate implements Comparable<GipComparableLinkCoordinate> {

	private long linkId;
	private int count;
	private int x;
	private int y;
	
	@Override
	public int compareTo(GipComparableLinkCoordinate lc) {
		if (count < lc.getCount()) {
			return -1;
		} else if (count > lc.getCount()) {
			return 1;
		} else {
			return 0;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof GipComparableLinkCoordinate) {
			GipComparableLinkCoordinate other = (GipComparableLinkCoordinate)obj;
			if (other.getLinkId() == linkId &&
				other.getCount() == count &&
				other.getX() == x &&
				other.getY() == y) {
				return true;
			}
		}
		return false;
	}

	public long getLinkId() {
		return linkId;
	}

	public void setLinkId(long linkId) {
		this.linkId = linkId;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
