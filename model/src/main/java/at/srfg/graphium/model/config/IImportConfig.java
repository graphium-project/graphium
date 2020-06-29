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
package at.srfg.graphium.model.config;

import java.util.Date;

public interface IImportConfig {
    void setOutPutDir(String outputDirectory);

//    IImportConfig highwayList(String... highwayList);
//
//    IImportConfig restrictions(String... restrictions);
//
//    IImportConfig boundsFile(String boundsFile);
//
    void setOriginalGraphName(String originalGraphName);

    void setOriginalGraphVersion(String originalVersion);

//    IImportConfig queueSize(int queueSize);
//
//    IImportConfig workerThreads(int workerThreads);
//    
//    Set<String> getHighwayList();
//
//    Set<String> getRestrictions();
//
//    String getBoundsFile();

    String getGraphName();

    String getVersion();

    String getOriginalGraphName();

    String getOriginalVersion();

    String getInputFile();

    String getOutputDir();
    
//    int getQueueSize();
//
//    int getWorkerThreads();
//    
//    OsmTagAdaptionMode getTagAdaptionMode();
//
    void setValidTo(Date validTo);

    void setValidFrom(Date validFrom);
	
//	IImportConfig tagAdaptionMode(OsmTagAdaptionMode tagAdaptionMode);
//
	Date getValidFrom();

	Date getValidTo();

	void setDownloadDir(String downloadDir);
	String getDownloadDir();

	void setKeepDownloadFile(boolean keepDownloadFile);
	boolean isKeepDownloadFile();

	void setKeepConvertedFile(boolean keepConvertedFile);
	boolean isKeepConvertedFile();

	void setForceDownload(boolean forceDownload);
	boolean isForceDownload();

	void setImportUrl(String importUrl);
	String getImportUrl();
	
}
