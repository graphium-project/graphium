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
 * (C) 2016 Salzburg Research Forschungsgesellschaft m.b.H.
 *
 * All rights reserved.
 *
 */
package at.srfg.graphium.api.service.impl;

import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.api.events.notifier.IGraphVersionImportFinishedNotifier;
import at.srfg.graphium.api.service.IDownloadGraphService;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.service.IGraphVersionImportService;
import at.srfg.graphium.model.IBaseWaySegment;

/**
 * @author User
 */
public class DownloadGraphServiceImpl<T extends IBaseWaySegment> implements IDownloadGraphService {

	private static Logger log = LoggerFactory.getLogger(DownloadGraphServiceImpl.class);
	
	private IGraphVersionImportService<T> importService;
	private IGraphVersionImportFinishedNotifier importFinishedNotifier;
	private IWayGraphVersionMetadataDao metadataDao;

	/**
	 * @param getGraphVersionUrl URL path of getGraphVersion API on central graph server
	 * @param centralServerUrl central graph server URL
	 * @param graphName name of graph to download
	 * @param version version of graph to download
	 * @return boolean if start of async graph download succeeded
	 */
	public boolean downloadGraphVersion(List<GraphVersionsImportInfo> importInfoList) {
		if (importInfoList != null && !importInfoList.isEmpty()) {
			try {
				for (GraphVersionsImportInfo importInfo : importInfoList) {

					String version = importInfo.getVersion();
					String urlString = importInfo.getUrl().toString().replace("{graph}", importInfo.getGraphName());
					if (version != null) {
						urlString = urlString.replace("{version}", version);
					} else {
						urlString = urlString.replace("{version}", "current");
					}
					URL url = new URL(urlString);
					importInfo.setUrl(url);
				}
				
				new Thread(new AsyncGraphImportService<T>(importService, importFinishedNotifier, metadataDao, importInfoList, false)).start();
			} catch (Exception e) {
				log.error("error downloading graph version", e);
				return false;
			}
		}
		return true;
	}

	public IGraphVersionImportService<T> getImportService() {
		return importService;
	}

	public void setImportService(IGraphVersionImportService<T> importService) {
		this.importService = importService;
	}

	public IGraphVersionImportFinishedNotifier getImportFinishedNotifier() {
		return importFinishedNotifier;
	}

	public void setImportFinishedNotifier(IGraphVersionImportFinishedNotifier importFinishedNotifier) {
		this.importFinishedNotifier = importFinishedNotifier;
	}

	public IWayGraphVersionMetadataDao getMetadataDao() {
		return metadataDao;
	}

	public void setMetadataDao(IWayGraphVersionMetadataDao metadataDao) {
		this.metadataDao = metadataDao;
	}
	
}