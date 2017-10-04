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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.TeeInputStream;
import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.vividsolutions.jts.geom.Polygon;

import at.srfg.graphium.api.service.IGraphService;
import at.srfg.graphium.core.exception.GraphAlreadyExistException;
import at.srfg.graphium.core.exception.GraphImportException;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.helper.GraphVersionHelper;
import at.srfg.graphium.core.persistence.IXInfoDaoRegistry;
import at.srfg.graphium.core.service.IGraphReadService;
import at.srfg.graphium.core.service.IGraphVersionImportService;
import at.srfg.graphium.core.service.IGraphVersionMetadataService;
import at.srfg.graphium.core.service.IGraphWriteService;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.outputformat.IWayGraphOutputFormat;
import at.srfg.graphium.io.outputformat.IWayGraphOutputFormatFactory;
import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.model.IConnectionXInfo;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.IWayGraphVersionMetadata;

/**
 * @author mwimmer
 *
 */
public class GraphServiceImpl<T extends IBaseWaySegment> implements IGraphService<T> {

	private static Logger log = LoggerFactory.getLogger(GraphServiceImpl.class);
	
	private IGraphReadService<T> graphReadService;
	private IGraphWriteService<T> graphWriteService;
	private IGraphVersionMetadataService metadataService;
	private IGraphVersionImportService<T> importService;
	private IWayGraphOutputFormatFactory<T> graphOutputFormatFactory;
	private IXInfoDaoRegistry<ISegmentXInfo, IConnectionXInfo> xInfoDaoRegistry;
	
	private boolean cacheGraphFiles = false;
	
	private String graphFileUploadDirectory;

	/**
	 *
 	 * @param metadata
	 * @param outputStream
	 * @throws IOException
	 * @throws WaySegmentSerializationException
	 */
	@Override
	@Transactional
	public void streamGraphVersion(IWayGraphVersionMetadata metadata, OutputStream outputStream)
			throws IOException, WaySegmentSerializationException {

		String fileName = createFileName(metadata.getGraphName(), metadata.getVersion());

		// look for JSON file on file system
		boolean streamFile = cacheGraphFiles;
		
		if (streamFile) {
			streamFile = streamFile(fileName, outputStream);
		}

		if (!streamFile) {
			// if no JSON file found stream from database
			log.info("start streaming of graph " + metadata.getGraphName() + " and version " + metadata.getVersion());

			OutputStream outStr = null;
			OutputStream fileOutputStream = null;
			
			if (cacheGraphFiles) {
				// write JSON to file
				fileOutputStream = writeFile(fileName, outputStream);
				outStr = fileOutputStream;
			} else {
				outStr = outputStream;
			}

			IWayGraphOutputFormat<T> graphOutputFormat = graphOutputFormatFactory.getWayGraphOutputFormat(outStr);
			try {
				this.streamStreetSegments(graphOutputFormat, null, metadata.getGraphName(), metadata.getVersion());

			} catch (Exception e) {
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
					} catch (IOException e1) {
					}
					fileOutputStream = null;
					deleteFile(fileName);
				}
				log.error("error during serialization: ", e);
				throw new WaySegmentSerializationException(e.getMessage(), e.getCause());

			} finally {
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
					} catch (IOException e) {
						log.error("Writing JSON file failed", e);
					}
				}
			}

			log.info("streaming of graph " + metadata.getGraphName() + " and version " + metadata.getVersion() + " finished");
		}

	}

	@Override
	@Transactional
	public void streamGraphVersion(IWayGraphVersionMetadata metadata, OutputStream outputStream, Set<Long> ids)
			throws IOException, WaySegmentSerializationException, GraphNotExistsException {
		if (ids != null) {
			log.info("start streaming of " + ids.size() + " segments of graph " + metadata.getGraphName() + " and version " + metadata.getVersion());
			IWayGraphOutputFormat<T> graphOutputFormat = graphOutputFormatFactory.getWayGraphOutputFormat(outputStream);
			this.streamStreetSegments(graphOutputFormat, metadata.getGraphName(), metadata.getVersion(), ids);
			log.info("streaming of graph " + metadata.getGraphName() + " and version " + metadata.getVersion() + " finished");
		}
	}

	/**
	 * @param fileName
	 */
	private void deleteFile(String fileName) {
		FileUtils.deleteQuietly(new File(graphFileUploadDirectory + fileName));
	}

	private OutputStream writeFile(String fileName, OutputStream outputStream) {
		TeeOutputStream teeOutputStream = null;
		BufferedOutputStream fileOutputStream = null;
		try {

			// check if file in directory already exists and delete it or throw exception
			File graphVersionFile = new File(graphFileUploadDirectory + fileName);

			if (!graphVersionFile.exists()) {
				// save file in graph version upload folder
				fileOutputStream = new BufferedOutputStream(new FileOutputStream(graphVersionFile));
				teeOutputStream = new TeeOutputStream(outputStream, fileOutputStream);
			}

		} catch (FileNotFoundException e) {
			log.error("Could not create File", e);
		}

		return teeOutputStream;
	}

	/**
	 *
	 * @param fileName
	 * @param outputStream
	 * @return
	 */
	private boolean streamFile(String fileName, OutputStream outputStream) {

		File jsonFile = FileUtils.getFile(graphFileUploadDirectory, fileName);
		if (jsonFile.exists()) {
			log.info("start streaming of graph file " + fileName);

			byte[] buf = new byte[8192];
			InputStream is = null;
			try {
				is = new FileInputStream(jsonFile);
				int c = 0;

				while ((c = is.read(buf, 0, buf.length)) > 0) {
					outputStream.write(buf, 0, c);
					outputStream.flush();
				}

			} catch (FileNotFoundException e) {
				log.error("file not found", e);
			} catch (IOException e) {
				log.error("io error accessing file", e);
			} finally {
				try {
					is.close();
				} catch (IOException e) {
					log.error("io error during streaming", e);
				}
				try {
					outputStream.close();
				} catch (IOException e) {
					log.error("io error during stream close", e);
				}
			}

			log.info("streaming of graph file " + fileName + " finished");

			return true;
		} else {
			return false;
		}
	}


	@Override
	@Transactional
	public IWayGraphVersionMetadata importGraph(String graphName, String version, boolean overrideIfExists, MultipartFile file)
			throws IOException, GraphAlreadyExistException, GraphImportException {

		String fileName = createFileName(graphName, version);

		log.info("Import of graph version " + fileName + " from file " + file.getOriginalFilename() + " started...");

		if (!file.isEmpty()) {
			
			//Compressed inputStream
			InputStream fileIn;
			if (file.getOriginalFilename().endsWith(".zip")) {
				ZipInputStream zip = new ZipInputStream(file.getInputStream());
				ZipEntry entry = zip.getNextEntry();
				fileIn = zip;
			} else {
				fileIn = file.getInputStream();
			}

			if (saveFile()) {
				TeeInputStream teeInputStream = null;
				BufferedOutputStream fileOutputStream = null;
				try {
	
					// check if file in directory already exists and delete it or throw exception
					File graphVersionFile = new File(graphFileUploadDirectory + fileName);
	
					if (!overrideIfExists && graphVersionFile.exists()) {
						throw new FileAlreadyExistsException("Import failed - file '" + fileName + "' already exists, but overrideIfExists has been true!");
					}
	
					// save file in graph version upload folder
					fileOutputStream =
							new BufferedOutputStream(new FileOutputStream(graphVersionFile));
					teeInputStream = new TeeInputStream(fileIn, fileOutputStream);
	
					importService.importGraphVersion(graphName, version, teeInputStream, overrideIfExists);
				} finally {
					if (teeInputStream != null) {
						teeInputStream.close();
					}
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
				}
			} else {
				try {
					importService.importGraphVersion(graphName, version, fileIn, overrideIfExists);
				} finally {
					if (fileIn != null) {
						fileIn.close();
					}
				}
			}
		} else {
			throw new IllegalArgumentException("Import failed - file " + file.getOriginalFilename() + " was empty");
		}
		return metadataService.getWayGraphVersionMetadata(graphName,version);
	}

	private boolean saveFile() {
		// File can be saved only if caching is explicitly enabled and there are no XInfo DAOs registered. This is because only graph files without XInfo should be cached and
		// passed to servers (publishing).
		return cacheGraphFiles && xInfoDaoRegistry.getAllSegmentXInfoDaos().isEmpty() && xInfoDaoRegistry.getAllConnectionXInfoDaos().isEmpty();
	}

	@Override
	public void streamStreetSegments(
			IWayGraphOutputFormat<T> outputFormat, Polygon bounds,
			String graphName, String version)
			throws WaySegmentSerializationException, GraphNotExistsException {
		
			// TODO: Hier sollen die View-Metadaten und dann die Graph-Metadaten gelesen werden!
			// Daher müssen hier je nach View-Filter die Metadaten generiert werden. Zudem muss im Metadaten-Objekt der 
			// Original-Graphname und -Version mit dem zugrunde liegenden Graphen belegt werden!
		
			IWayGraphVersionMetadata metadata = metadataService.getWayGraphVersionMetadata(graphName, version);
			
			outputFormat.serialize(metadata);
//			graphReadService.streamStreetSegments(outputFormat.getSegmentOutputFormat(), bounds, graphName, version);
			graphReadService.streamStreetSegments(outputFormat, bounds, graphName, version);
			outputFormat.close();
		
	}

	@Override
	public void streamStreetSegments(
			IWayGraphOutputFormat<T> outputFormat,
			String graphName, String version, Set<Long> ids)
			throws WaySegmentSerializationException, GraphNotExistsException {
		
			IWayGraphVersionMetadata metadata = metadataService.getWayGraphVersionMetadata(graphName, version);
			
			outputFormat.serialize(metadata);
//			graphReadService.streamStreetSegments(outputFormat.getSegmentOutputFormat(), ids, graphName, version);
			graphReadService.streamStreetSegments(outputFormat, ids, graphName, version);
			outputFormat.close();
		
	}

	@Override
	public void deleteGraphVersion(String graphName, String version, boolean keepMetadata) throws GraphNotExistsException {
		log.info("Deleting graph " + graphName + " in version " + version + " - metadata will be " + (keepMetadata ? "set to state DELETED (soft delete)" : "deleted"));
		graphWriteService.deleteGraphVersion(graphName, version, keepMetadata);
		log.info("Graph " + graphName + " in version " + version + " deleted");
	}

	/**
	 * @param graphName
	 * @param version
	 * @return
	 */
	private String createFileName(String graphName, String version) {
		return GraphVersionHelper.createGraphVersionName(graphName, version) + ".json";
	}

	public IGraphReadService<T> getGraphReadService() {
		return graphReadService;
	}

	public void setGraphReadService(IGraphReadService<T> graphReadService) {
		this.graphReadService = graphReadService;
	}

	public IGraphVersionMetadataService getMetadataService() {
		return metadataService;
	}

	public void setMetadataService(IGraphVersionMetadataService metadataService) {
		this.metadataService = metadataService;
	}

	public IGraphWriteService<T> getGraphWriteService() {
		return graphWriteService;
	}

	public void setGraphWriteService(IGraphWriteService<T> graphWriteService) {
		this.graphWriteService = graphWriteService;
	}

	public String getGraphFileUploadDirectory() {
		return graphFileUploadDirectory;
	}

	public void setGraphFileUploadDirectory(String graphFileUploadDirectory) {
		this.graphFileUploadDirectory = graphFileUploadDirectory;
		if (!this.graphFileUploadDirectory.endsWith("/")) {
			this.graphFileUploadDirectory += "/";
		}
	}

	public IGraphVersionImportService<T> getImportService() {
		return importService;
	}

	public void setImportService(IGraphVersionImportService<T> importService) {
		this.importService = importService;
	}

	public IWayGraphOutputFormatFactory<T> getGraphOutputFormatFactory() {
		return graphOutputFormatFactory;
	}

	public void setGraphOutputFormatFactory(
			IWayGraphOutputFormatFactory<T> graphOutputFormatFactory) {
		this.graphOutputFormatFactory = graphOutputFormatFactory;
	}

	public IXInfoDaoRegistry<ISegmentXInfo, IConnectionXInfo> getxInfoDaoRegistry() {
		return xInfoDaoRegistry;
	}

	public void setxInfoDaoRegistry(IXInfoDaoRegistry<ISegmentXInfo, IConnectionXInfo> xInfoDaoRegistry) {
		this.xInfoDaoRegistry = xInfoDaoRegistry;
	}

	public boolean isCacheGraphFiles() {
		return cacheGraphFiles;
	}

	public void setCacheGraphFiles(boolean cacheGraphFiles) {
		this.cacheGraphFiles = cacheGraphFiles;
	}
	
}