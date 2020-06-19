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
package at.srfg.graphium.converter.commons.service.impl;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import at.srfg.graphium.ioutils.FileTransferUtils;
import at.srfg.graphium.model.config.IImportConfig;

/**
 * @author mwimmer
 *
 */
public abstract class AbstractImporterService {

    private static Logger log = Logger.getLogger(AbstractImporterService.class);

    protected String downloadFile = null;
    
    protected void download(IImportConfig config) throws IOException {
    	URL url = new URL(config.getInputFile());
    	
    	String outputFilename = createDownloadFilename(config);
    	
    	File downloadedFile = new File(outputFilename);
    	
    	if (downloadFile == null && (!downloadedFile.exists() || config.isForceDownload())) {
    		FileTransferUtils fileDownloader = new FileTransferUtils();
	    	long bytes = fileDownloader.download(url, outputFilename);
	    	
	    	if (bytes <= 0) {
	    		throw new RuntimeException("No file downloaded!");
	    	}
    	}
	}

    protected String createDownloadFilename(IImportConfig config) {
		String filename = FilenameUtils.getName(config.getInputFile());
		String outputDirectory = config.getDownloadDir();
		if (outputDirectory == null) {
			outputDirectory = config.getOutputDir();
		}
		return FilenameUtils.concat(outputDirectory, filename);
	}

    protected void importGraphFile(IImportConfig config, String outputFileName) {
		FileTransferUtils fileTransferHelper = new FileTransferUtils();
		try {
			fileTransferHelper.uploadZipped(new URL(config.getImportUrl()), new File(outputFileName));
		} catch (MalformedURLException e) {
			log.error("upload failed", e);
		}
	}

    protected void cleanup(IImportConfig config, String convertedFileName) {
		if (downloadFile != null && !config.isKeepDownloadFile()) {
			File file = new File(downloadFile);
			if (file.exists()) {
				file.delete();
			}
			file = new File(downloadFile + ".zip");
			if (file.exists()) {
				file.delete();
			}
		}
		
		if (!config.isKeepConvertedFile()) {
			File file = new File(convertedFileName);
			if (file.exists()) {
				file.delete();
			}
		}
	}

}
