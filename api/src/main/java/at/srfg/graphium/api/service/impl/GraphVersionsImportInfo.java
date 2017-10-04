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
package at.srfg.graphium.api.service.impl;

import java.net.URL;

/**
 * @author mwimmer
 */
public class GraphVersionsImportInfo {

	private String graphName;
	private String version;
	private URL url;
	private String centralServerUrl;
	
	public GraphVersionsImportInfo(String graphName, String version, URL url, String centralServerUrl) {
		super();
		this.graphName = graphName;
		this.version = version;
		this.url = url;
		this.centralServerUrl = centralServerUrl;
	}

	public String getGraphName() {
		return graphName;
	}

	public String getVersion() {
		return version;
	}

	public URL getUrl() {
		return url;
	}

	public String getCentralServerUrl() {
		return centralServerUrl;
	}

	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public void setCentralServerUrl(String centralServerUrl) {
		this.centralServerUrl = centralServerUrl;
	}
	
}
