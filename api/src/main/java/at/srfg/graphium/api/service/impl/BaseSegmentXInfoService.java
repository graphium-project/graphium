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

import at.srfg.graphium.api.exceptions.ValidationException;
import at.srfg.graphium.api.service.IBaseSegmentXInfoService;
import at.srfg.graphium.core.exception.GraphImportException;
import at.srfg.graphium.core.exception.GraphNotExistsException;
import at.srfg.graphium.core.exception.GraphStorageException;
import at.srfg.graphium.core.persistence.IBaseSegmentReadDao;
import at.srfg.graphium.core.persistence.IBaseSegmentWriteDao;
import at.srfg.graphium.io.adapter.exception.XInfoNotSupportedException;
import at.srfg.graphium.io.exception.WaySegmentSerializationException;
import at.srfg.graphium.io.inputformat.IQueuingSegmentInputFormat;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormatFactory;
import at.srfg.graphium.io.producer.IBaseSegmentProducer;
import at.srfg.graphium.io.producer.impl.BaseSegmentProducerImpl;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.impl.AbstractXInfoModelTypeAware;
import at.srfg.graphium.model.management.IServerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

/**
 * @author anwagner
 *
 */
public class BaseSegmentXInfoService implements IBaseSegmentXInfoService<IBaseSegment> {

	private static Logger log = LoggerFactory.getLogger(BaseSegmentXInfoService.class);

    private IBaseSegmentWriteDao writeDao;

    private IBaseSegmentReadDao readDao;

    private IQueuingSegmentInputFormat<IBaseSegment> inputFormat;

    private ISegmentOutputFormatFactory<IBaseSegment> outputFormatFactory;

    private IServerStatus serverStatus;

    private int queueSize;

    private int batchSize;

    @Autowired(required = false)
    private List<AbstractXInfoModelTypeAware> registeredXInfosList;


    @Override
	public void streamBaseSegmentXInfos(String graph, String version, OutputStream outputStream, String... types)
            throws XInfoNotSupportedException, GraphNotExistsException, WaySegmentSerializationException {
        ISegmentOutputFormat<IBaseSegment> segmentOutputFormat = this.outputFormatFactory.getSegmentOutputFormat(outputStream);
        readDao.streamBaseSegmentXInfos(graph,version,segmentOutputFormat,types);
        segmentOutputFormat.close();
	}

	@Override
	public void streamBaseConnectionXInfos(String graph, String version, OutputStream outputStream, String... types)
            throws XInfoNotSupportedException, GraphNotExistsException, WaySegmentSerializationException {
        ISegmentOutputFormat<IBaseSegment> segmentOutputFormat = this.outputFormatFactory.getSegmentOutputFormat(outputStream);
        readDao.streamBaseConnectionXInfos(graph,version,segmentOutputFormat,types);
        segmentOutputFormat.close();
	}

    @Override
    public void streamBaseSegmentXInfos(String graph, String version, InputStream inputStream)
            throws XInfoNotSupportedException, GraphImportException, GraphNotExistsException, GraphStorageException, ValidationException {
        this.streamBaseXInfos(graph,version,null,inputStream,true);
    }

    @Override
    public void streamBaseSegmentXInfos(String graph, String version, String excludedXInfos, InputStream inputStream)
            throws XInfoNotSupportedException, GraphImportException, GraphNotExistsException, GraphStorageException, ValidationException {
        this.streamBaseXInfos(graph,version,excludedXInfos,inputStream,true);
    }

    @Override
    public void streamBaseConnectionXInfos(String graph, String version, InputStream inputStream)
            throws XInfoNotSupportedException, GraphImportException, GraphStorageException, GraphNotExistsException, ValidationException {
        this.streamBaseXInfos(graph,version,null,inputStream,false);
    }

    @Override
    public void streamBaseConnectionXInfos(String graph, String version, String excludedXInfos, InputStream inputStream)
            throws XInfoNotSupportedException, GraphImportException, GraphStorageException, GraphNotExistsException, ValidationException {
        this.streamBaseXInfos(graph,version,excludedXInfos,inputStream,false);
    }

    private void streamBaseXInfos(String graph, String version, String excludedXInfos, InputStream inputStream, boolean isSegmentXInfo)
            throws XInfoNotSupportedException, GraphImportException, GraphStorageException, GraphNotExistsException, ValidationException {
        //First checkk if already another import is running. The singleton serverStatus has to be injected therefore
        if (!serverStatus.registerImport()) {
            throw new GraphImportException("Sorry, system is busy, a graph import is currently executed");
        }
        IBaseSegmentProducer<IBaseSegment> producer = null;
        try {
            //TODO create list for excludedXInfos
            List<String> excludedXInfosList;
            if (excludedXInfos != null) {
                excludedXInfosList = Arrays.asList(excludedXInfos.split(","));
            }else{
                excludedXInfosList = new ArrayList<String>();
            }
            //verify xinfos
            List<String> types;
            if (registeredXInfosList == null) {
                types = new ArrayList<>();
                log.warn("registeredXInfosList: null");
            }else{
                types = registeredXInfosList.stream().map(AbstractXInfoModelTypeAware::getResponsibleType).distinct().collect(Collectors.toList());
                //System.out.println("registeredXInfosList: " + String.join(", ", types));
            }
            for (String xinfo : excludedXInfosList) {
                boolean found = false;
                for (String  registeredXInfoType : types) {
                    if (registeredXInfoType.equals(xinfo)){
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    throw new ValidationException("XInfo-type: " +  xinfo + " is not registered!");
                }
            }

            BlockingQueue<IBaseSegment> segmentsQueue;

            segmentsQueue = new ArrayBlockingQueue<>(queueSize);

            producer = new BaseSegmentProducerImpl<>(inputFormat, inputStream, segmentsQueue);

            Thread producerThread = new Thread(producer, "basesegment-xinfo-parser-thread");
            producerThread.start();

            List<IBaseSegment> segments = new ArrayList<>();
            while (producerThread.isAlive() || !segmentsQueue.isEmpty()) {
                if (!segmentsQueue.isEmpty()) {
                    segments.add(segmentsQueue.poll());
                }
                if (segments.size() >= this.batchSize) {
                    this.writeSegments(segments,graph,version, excludedXInfosList, isSegmentXInfo);
                    segments.clear();
                }
            }
            this.writeSegments(segments,graph,version, excludedXInfosList, isSegmentXInfo);
        } finally {
            serverStatus.unregisterImport();
            if (producer != null && producer.getException() != null) {
                throw new GraphImportException("Graph could not be imported",producer.getException());
            }
        }
    }

    private void writeSegments(List<IBaseSegment> segments, String graph, String version, List<String> excludedXInfosList,  boolean isSegmentXInfo) throws GraphStorageException, GraphNotExistsException {
        if (isSegmentXInfo) {
            writeDao.saveSegmentXInfos(segments, graph, version, excludedXInfosList);
        }
        else writeDao.saveConnectionXInfos(segments, graph, version, excludedXInfosList);
    }

    @Override
	public void deleteBaseSegmentXInfos(String graph, String version, String... types) throws XInfoNotSupportedException, GraphNotExistsException, GraphStorageException {
        writeDao.deleteSegmentXInfos(graph,version,types);
    }

    @Override
    public void deleteConnectionXInfos(String graph, String version, String... types) throws XInfoNotSupportedException, GraphNotExistsException, GraphStorageException {
        writeDao.deleteConnectionXInfos(graph,version, types);
    }


    public IServerStatus getServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(IServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public IQueuingSegmentInputFormat<IBaseSegment> getInputFormat() {
        return inputFormat;
    }

    public void setInputFormat(IQueuingSegmentInputFormat<IBaseSegment> inputFormat) {
        this.inputFormat = inputFormat;
    }

    public IBaseSegmentWriteDao getWriteDao() {
        return writeDao;
    }

    public void setWriteDao(IBaseSegmentWriteDao writeDao) {
        this.writeDao = writeDao;
    }

    public IBaseSegmentReadDao getReadDao() {
        return readDao;
    }

    public void setReadDao(IBaseSegmentReadDao readDao) {
        this.readDao = readDao;
    }

    public ISegmentOutputFormatFactory<IBaseSegment> getOutputFormatFactory() {
        return outputFormatFactory;
    }

    public void setOutputFormatFactory(ISegmentOutputFormatFactory<IBaseSegment> outputFormatFactory) {
        this.outputFormatFactory = outputFormatFactory;
    }
}
