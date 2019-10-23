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
package at.srfg.graphium.lanelet2import.model;

import java.util.Date;

/**
 * @author mwimmer
 *
 */
public interface IImportConfig {
    IImportConfig outPutDir(String outputDirectory);

    IImportConfig queueSize(int queueSize);

    IImportConfig workerThreads(int workerThreads);
    
    String getGraphName();

    String getVersion();

    String getInputFile();

    String getOutputDir();
    
    int getQueueSize();

    int getWorkerThreads();

	IImportConfig validTo(Date validTo);

	IImportConfig validFrom(Date validFrom);

	Date getValidFrom();

	Date getValidTo();
}