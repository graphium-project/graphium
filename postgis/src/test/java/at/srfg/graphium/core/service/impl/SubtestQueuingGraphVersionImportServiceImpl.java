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
package at.srfg.graphium.core.service.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import at.srfg.graphium.ITestGraphiumPostgis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.core.exception.GraphAlreadyExistException;
import at.srfg.graphium.core.exception.GraphImportException;
import at.srfg.graphium.core.service.IGraphVersionImportService;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.impl.WayGraphVersionMetadata;
import at.srfg.graphium.model.management.impl.Source;

/**
 * @author mwimmer
 *
 */

public class TestQueuingGraphVersionImportServiceImpl implements ITestGraphiumPostgis{

	private static Logger log = LoggerFactory.getLogger(TestQueuingGraphVersionImportServiceImpl.class);

	@Value("${db.graphNameImport}")
	String graphName;
	@Value("${db.graphVersionImport}")
	String version;
	@Value("${db.originGraphNameImport}")
	String originGraphName;
	@Value("${db.originVersionImport}")
	String originVersion;
	@Value("${db.validFromDateString}")
	String validFromDateString;
	@Value("${db.dateFormat}")
	String dateFormat;
	@Value("#{new java.text.SimpleDateFormat(\"${db.dateFormat}\").parse(\"${db.validFromDateString}\")}") //using spEL
	Date validFrom;
	@Value("#{null}")
	Date validTo;
	@Value("#{${db.tags}}") //assign hashmap using spEL
	Map<String, String> tags;
	@Value("${db.sourceId}")
	int sourceId;
	@Value("${db.type}")
	String type;
	@Value("${db.description}")
	String description;
	@Value("${db.creator}")
	String creator;
	@Value("${db.originUrl}")
	String originUrl;
	@Value("${db.inputFileName}")
	String inputFileName;
	@Value("#{helper.getBoundsAustria()}") //assign return-value of helper-bean method: getBoundsAustria using spEL (the helper bean is initialized in application-context-graphium-postgis-testsuite.xml)
	Polygon coveredArea;
	@Value("#{null}")
	InputStream stream;

	@Resource(name="postgisQueuingGraphVersionImportService")
	private IGraphVersionImportService importService;

	public void testImportGraphVersion() {
		try {
			stream = new FileInputStream(inputFileName);
			
			IWayGraphVersionMetadata metadata = new WayGraphVersionMetadata();
			metadata.setGraphName(graphName);
			metadata.setVersion(version);
			metadata.setOriginGraphName(originGraphName);
			metadata.setOriginVersion(originVersion);
			metadata.setValidFrom(validFrom);
			metadata.setValidTo(validTo);
			metadata.setType(type);
			metadata.setCreator(creator);
			metadata.setCreationTimestamp(new Date());
			metadata.setCoveredArea(coveredArea);
			metadata.setSource(new Source(sourceId, "GIP"));
			metadata.setOriginUrl(originUrl);
			metadata.setDescription(description);

			importService.importGraphVersion(graphName, version, stream, true);
			
		} catch (FileNotFoundException e) {
			log.error("file not found", e);
		} catch (GraphImportException e) {
			log.error("error importing graph", e);
		} catch (GraphAlreadyExistException e) {
			log.error("error, graph already exists", e);
		}
	}

	@Override
	public void run() {
		testImportGraphVersion();
	}
}
