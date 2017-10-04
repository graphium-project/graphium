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
package at.srfg.graphium.api.service.impl;

import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.api.events.notifier.IGraphVersionImportFinishedNotifier;
import at.srfg.graphium.core.persistence.IWayGraphVersionMetadataDao;
import at.srfg.graphium.core.service.IGraphVersionImportService;
import at.srfg.graphium.model.IWayGraphVersionMetadata;

/**
 * Asynchronous graph's import service.
 * After import a listener will be notified.
 * 
 * @author mwimmer
 *
 */
public class AsyncGraphImportService implements Runnable {
	
	private static Logger log = LoggerFactory.getLogger(AsyncGraphImportService.class);

	private IGraphVersionImportService importService;
	private IGraphVersionImportFinishedNotifier importFinishedNotifier;
	private List<GraphVersionsImportInfo> importInfoList;
	private boolean compressed = false;
	private IWayGraphVersionMetadataDao metadataDao;
	
	public AsyncGraphImportService(IGraphVersionImportService importService, IGraphVersionImportFinishedNotifier importFinishedNotifier,
			IWayGraphVersionMetadataDao metadataDao, List<GraphVersionsImportInfo> importInfoList, boolean compressed) {
		super();
		this.importService = importService;
		this.importFinishedNotifier = importFinishedNotifier;
		this.importInfoList = importInfoList;
		this.metadataDao = metadataDao;
		this.compressed = compressed;
	}

	@Override
	public void run() {
		if (importInfoList != null && !importInfoList.isEmpty()) {
			log.info("Start graph import asynchronously");
			for (GraphVersionsImportInfo importInfo : importInfoList) {
				importGraphVersion(importInfo);
			}
		}
	}

	private void importGraphVersion(GraphVersionsImportInfo importInfo) {
		String version = importInfo.getVersion();
		try {
			InputStream in = importInfo.getUrl().openStream();
			if (this.compressed) {
				in = new GZIPInputStream(in);
			}
			importService.importGraphVersion(importInfo.getGraphName(), version, in, true);
			if (importInfo.getVersion() == null) {
				// read current version in case of initial import
				List<IWayGraphVersionMetadata> metadataList = metadataDao.getWayGraphVersionMetadataList(importInfo.getGraphName());
				if (metadataList != null && !metadataList.isEmpty()) {
					version = metadataList.get(0).getVersion();
				} else {
					version = "";
				}
			}
			// send ImportFinishedEvent
			sendImportFinishedEvent(importInfo.getCentralServerUrl(), importInfo.getGraphName(), version);
		} catch (Exception e) {
			log.error("error downloading graph version", e);
			// send ImportFailedEvent
			if (version == null) {
				version = "";
			}
			sendImportFailedEvent(importInfo.getCentralServerUrl(), importInfo.getGraphName(), version);
		}
	}

	/**
	 * @param graphName
	 * @param version
	 */
	private void sendImportFinishedEvent(String url, String graphName, String version) {
		if (importFinishedNotifier != null) {
			importFinishedNotifier.notifyCentralServers(url, graphName, version, false);
		}
	}

	/**
	 * @param graphName
	 * @param version
	 */
	private void sendImportFailedEvent(String url, String graphName, String version) {
		if (importFinishedNotifier != null) {
			importFinishedNotifier.notifyCentralServers(url, graphName, version, true);
		}
	}

}
