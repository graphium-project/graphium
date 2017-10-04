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

/**
 * Thrown when a graph should be written (created) but a graph with the given name already exists
 * 
 * @author <a href="mailto:andreas.wagner@salzburgresearch.at">Andreas Wagner</a>
 *
 */
public class GraphAlreadyExistException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GraphAlreadyExistException(String msg) {
		super(msg);
	}
	
	public GraphAlreadyExistException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
