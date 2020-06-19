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
package at.srfg.graphium.model.config.impl;

import java.util.Date;

import at.srfg.graphium.model.config.IImportConfig;

public class ImportConfig implements IImportConfig {

    protected String graphName;
    protected String version;
    protected String originalGraphName;
    protected String originalVersion;
    protected Date validFrom;
    protected Date validTo;
    protected String inputFile;
    protected String outputDir;
    protected String downloadDir;
    protected boolean keepDownloadFile;
    protected boolean keepConvertedFile;
    protected boolean forceDownload;
    protected String importUrl;

    public static IImportConfig getConfig(String graphName, String version,
                                         String inputFile) {
        return new ImportConfig(graphName, version, inputFile);
    }

    protected ImportConfig(String graphName, String version, String inputFile) {
        this.graphName = graphName;
        this.version = version;
        this.inputFile = inputFile;
    }

    @Override
    public void setOutPutDir(String outputDirectory) {
        this.outputDir = outputDirectory;
    }

    @Override
    public void setOriginalGraphName(String originalGraphName){
        this.originalGraphName = originalGraphName;
    }

    @Override
    public void setOriginalGraphVersion(String originalVersion) {
        this.originalVersion = originalVersion;
    }

    @Override
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

    @Override
    public void setValidTo(Date validTo) {
        this.validTo = validTo;
    }

	@Override
    public String getGraphName() {
        return graphName;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getOriginalGraphName() {
        return originalGraphName;
    }

    @Override
    public String getOriginalVersion() {
        return originalVersion;
    }
    
    @Override
    public Date getValidFrom() {
    	return validFrom;
    }
    
    @Override
    public Date getValidTo() {
    	return validTo;
    }

    @Override
    public String getInputFile() {
        return inputFile;
    }

    @Override
    public String getOutputDir() {
        return outputDir;
    }
    
	@Override
	public void setDownloadDir(String downloadDir) {
		this.downloadDir = downloadDir;
	}

	@Override
	public String getDownloadDir() {
		return downloadDir;
	}

	@Override
	public void setKeepDownloadFile(boolean keepDownloadFile) {
		this.keepDownloadFile = keepDownloadFile;
	}

	@Override
	public boolean isKeepDownloadFile() {
		return keepDownloadFile;
	}

	@Override
	public void setKeepConvertedFile(boolean keepConvertedFile) {
		this.keepConvertedFile = keepConvertedFile;
	}

	@Override
	public boolean isKeepConvertedFile() {
		return keepConvertedFile;
	}

	@Override
	public void setForceDownload(boolean forceDownload) {
		this.forceDownload = forceDownload;
	}

	@Override
	public boolean isForceDownload() {
		return forceDownload;
	}

	@Override
	public String getImportUrl() {
		return importUrl;
	}

	@Override
	public void setImportUrl(String importUrl) {
		this.importUrl = importUrl;
	}

	@Override
	public String toString() {
		return "ImportConfig [graphName=" + graphName + ", version=" + version + ", originalGraphName="
				+ originalGraphName + ", originalVersion=" + originalVersion + ", validFrom=" + validFrom + ", validTo="
				+ validTo + ", inputFile=" + inputFile + ", outputDir=" + outputDir + ", downloadDir=" + downloadDir
				+ ", keepDownloadFile=" + keepDownloadFile + ", keepConvertedFile=" + keepConvertedFile
				+ ", forceDownload=" + forceDownload + ", importUrl=" + importUrl + "]";
	}

}