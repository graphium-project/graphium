/**
 * Copyright © 2019 Salzburg Research Forschungsgesellschaft (graphium@salzburgresearch.at)
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
package at.srfg.graphium.lanelet2import.model.impl;

import java.util.Date;

import at.srfg.graphium.lanelet2import.model.IImportConfig;

public class ImportConfig implements IImportConfig {

    private String graphName;
    private String version;
    private Date validFrom;
    private Date validTo;
    private String inputFile;
    private String outputDir;
    private int queueSize;
    private int workerThreads;

    public static IImportConfig getConfig(String graphName, String version,
                                         String inputOSMFile) {
        return new ImportConfig(graphName, version, inputOSMFile);
    }

    private ImportConfig(String graphName, String version, String inputFile) {
        this.graphName = graphName;
        this.version = version;
        this.inputFile = inputFile;
    }

    @Override
    public IImportConfig outPutDir(String outputDirectory) {
        this.outputDir = outputDirectory;
        return this;
    }

    @Override
    public IImportConfig validFrom(Date validFrom) {
        this.validFrom = validFrom;
        return this;
    }

    @Override
    public IImportConfig validTo(Date validTo) {
        this.validTo = validTo;
        return this;
    }

	@Override
	public IImportConfig queueSize(int queueSize) {
		this.queueSize = queueSize;
		return this;
	}
	
	@Override
	public IImportConfig workerThreads(int workerThreads) {
		this.workerThreads = workerThreads;
		return this;
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
    public int getQueueSize() {
        return queueSize;
    }
  
    @Override
    public int getWorkerThreads() {
        return workerThreads;
    }

	@Override
	public String toString() {
		return "ImportConfig [graphName=" + graphName + ", version=" + version + ", validFrom='" + validFrom + '\''
                + ", validTo='" + validTo + '\'' + ", inputFile=" + inputFile + ", outputDir=" + outputDir
				+ ", queueSize=" + queueSize + "workerThreads=" + workerThreads + "]";
	}

}