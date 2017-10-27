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
package at.srfg.graphium.osmimport.service;

import java.util.Date;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.osmimport.model.IImportConfig;
import at.srfg.graphium.osmimport.model.impl.ImportConfig;
import at.srfg.graphium.osmimport.service.impl.OsmImporterServiceImpl;

/**
 * @author mwimmer
 */
public class TestOsmImporterServiceImpl {

	private static Logger log = LoggerFactory.getLogger(TestOsmImporterServiceImpl.class);
	
	// OSM Land
	private String country = "liechtenstein";
	
	// Wo liegt das OSM-File?
//	private String osmFileName = "C:/development/osm/" + country + "-latest.osm.pbf";
//	private String osmFileName = "D:/development/project_data/osm/" + country + "-latest.osm.pbf";
	private String osmFileName = "C:/development/Graphserver/osm/" + country + "-latest.osm.pbf";

//	private String osmFileName = "C:/development/Graphserver/osm/austria-latest.osm.pbf";
//	private String osmFileName = "C:/development/Graphserver/osm/liechtenstein-latest.osm.pbf";
//	private String osmFileName = "D:/development/project_data/osm/germany-latest.osm.pbf";
//	private String osmFileName = "D:/development/project_data/osm/liechtenstein-latest.osm.pbf";
//	private String osmFileName = "D:/development/project_data/osm/austria-latest.osm.pbf";
	
	//private String osmFileName = "/development/project_data/graphium/testdata/osm/20160708/liechtenstein-latest.osm.pbf";
	//private String osmFileName = "/development/project_data/graphium/testdata/osm/20160708/austria-latest.osm.pbf";
//	private String osmFileName = "/development/project_data/graphium/testdata/osm/20160708/germany-latest.osm.pbf";
	
	// Wohin soll das JSON geschrieben werden?
//	private String outputDirectory = "D:/development/project_data/graphserver/osm2graphium/json";
	private String outputDirectory = "C:/development/Graphserver/working_data/osm2graphium/json/";

//	private String boundsFile = "C:/development/Graphserver/working_data/osm2graphium/config/salzburg.poly";

	// Welche geografischen Einschränkungen gibt es?
//	private String boundsFile = "src/test/resources/dachi.poly";
//	private String boundsFile = "src/test/resources/salzburg.poly";
	private String boundsFile = null;
	
    private int queueSize = 20000;
    private int workerThreads = 3;

	private OsmImporterServiceImpl importService;
	
	@Test
	public void testImportOsm() {
		importService = new OsmImporterServiceImpl();

		IImportConfig config = ImportConfig.getConfig("osm-" + country, "test", osmFileName)
											.outPutDir(outputDirectory)
											.boundsFile(boundsFile)
											.validFrom(new Date())
											.queueSize(queueSize)
											.workerThreads(workerThreads)
											.highwayList("motorway", "motorway_link", "trunk", "trunk_link", "primary", "primary_link", 
														 "secondary", "secondary_link", "tertiary", "tertiary_link");
		try {
			importService.importOsm(config);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}