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
package at.srfg.graphium.model.management;

/**
 * @author mwimmer
 *
 */
public interface IGraphStatus {

	public abstract String getGraph();

	public abstract void setGraph(String graph);

	public abstract String getOriginalGraph();

	public abstract void setOriginalGraph(String originalGraph);

	public abstract String getVersionLastImported();

	public abstract void setVersionLastImported(String versionLastImported);

	public abstract String getVersionCurrentlyActive();

	public abstract void setVersionCurrentlyActive(String versionCurrentlyActive);

}