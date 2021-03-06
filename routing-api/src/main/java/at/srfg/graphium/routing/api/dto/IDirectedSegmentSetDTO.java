/**
 * Copyright © 2020 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.routing.api.dto;

import java.util.List;

public interface IDirectedSegmentSetDTO {

	public long getId();

	public void setId(long id);

	public Double getStartCoordX();

	public void setStartCoordX(Double startCoordX);

	public Double getStartCoordY();

	public void setStartCoordY(Double startCoordY);

	public Double getEndCoordX();

	public void setEndCoordX(Double endCoordX);

	public Double getEndCoordY();

	public void setEndCoordY(Double endCoordY);

	public List<IDirectedSegmentDTO> getSegments();

	public void setSegments(List<IDirectedSegmentDTO> segments);

}
