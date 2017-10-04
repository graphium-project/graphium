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
package at.srfg.graphium.core.exception;

public class GraphNotExistsException extends Exception {

	private String graphName;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5134351542694726581L;

	public GraphNotExistsException(String message, String graphName) {
		super(message);
		this.graphName = graphName;
	}
	
	public GraphNotExistsException(String message, String graphName, Throwable cause) {
		super(message, cause);
		this.graphName = graphName;
	}

	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}

	public String getGraphName() {
		return graphName;
	}
}