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
package at.srfg.graphium.gipimport.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import at.srfg.graphium.converter.commons.service.impl.AbstractImporterService;
import at.srfg.graphium.gipimport.model.IDFMetadata;
import at.srfg.graphium.gipimport.model.IImportConfigIdf;
import at.srfg.graphium.gipimport.parser.IGipParser;
import at.srfg.graphium.gipimport.parser.impl.GipParserImpl;
import at.srfg.graphium.gipimport.producer.IGipLinkProducer;
import at.srfg.graphium.gipimport.producer.impl.GipLinkProducerImpl;
import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.io.adapter.ISegmentAdapter;
import at.srfg.graphium.io.adapter.IXInfoDTOAdapter;
import at.srfg.graphium.io.adapter.impl.BaseSegment2SegmentDTOAdapter;
import at.srfg.graphium.io.adapter.impl.DefaultSegmentXInfoAdapter;
import at.srfg.graphium.io.adapter.impl.GraphVersionMetadata2GraphVersionMetadataDTOAdapter;
import at.srfg.graphium.io.adapter.impl.WaySegment2SegmentDTOAdapter;
import at.srfg.graphium.io.adapter.registry.ISegmentAdapterRegistry;
import at.srfg.graphium.io.adapter.registry.IXinfoAdapterRegistry;
import at.srfg.graphium.io.adapter.registry.impl.SegmentAdapterRegistryImpl;
import at.srfg.graphium.io.adapter.registry.impl.SegmentXInfoAdapterRegistry;
import at.srfg.graphium.io.csv.ICsvXInfoFactory;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.dto.IGraphVersionMetadataDTO;
import at.srfg.graphium.io.dto.ISegmentXInfoDTO;
import at.srfg.graphium.io.dto.IWaySegmentDTO;
import at.srfg.graphium.io.dto.IXInfoDTO;
import at.srfg.graphium.io.dto.impl.BaseSegmentDTOImpl;
import at.srfg.graphium.io.dto.impl.DefaultSegmentXInfoDTO;
import at.srfg.graphium.io.outputformat.IBaseGraphOutputFormatFactory;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormat;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormatFactory;
import at.srfg.graphium.io.outputformat.IWayGraphOutputFormat;
import at.srfg.graphium.io.outputformat.IWayGraphOutputFormatFactory;
import at.srfg.graphium.io.outputformat.impl.jackson.GenericJacksonSegmentOutputFormatFactoryImpl;
import at.srfg.graphium.io.outputformat.impl.jackson.GenericJacksonWayGraphOutputFormatFactoryImpl;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.IXInfo;
import at.srfg.graphium.model.impl.BaseSegment;
import at.srfg.graphium.model.impl.BaseSegmentGraphModelFactory;
import at.srfg.graphium.model.impl.DefaultSegmentXInfo;
import at.srfg.graphium.model.impl.WayGraphModelFactory;
import at.srfg.graphium.model.impl.WayGraphVersionMetadata;
import at.srfg.graphium.model.management.impl.Source;
import at.srfg.graphium.pixelcuts.adapter.impl.PixelCutAdapter;
import at.srfg.graphium.pixelcuts.dto.impl.PixelCutDTO;
import at.srfg.graphium.pixelcuts.model.impl.PixelCut;

/**
 * Service class that starts the import process. This class is meant as entry-point for the stand-a-lone and
 * embedded version.
 * </p>
 * It is splittet in a Producer and Consumer. The Consumer takes all gip Links and serialiazes them to the
 * {@link IWayGraphOutputFormatFactory}.
 */
public class GipImporterService<T extends IBaseSegment, D extends IBaseSegmentDTO> extends AbstractImporterService {

    private static Logger log = Logger.getLogger(GipImporterService.class);

    public GipImporterService() {}

    private IWayGraphVersionMetadata getVersionMetadata(IImportConfigIdf config, IDFMetadata idfMetaData) {
        IWayGraphVersionMetadata metadata = new WayGraphVersionMetadata();
        metadata.setGraphName(config.getGraphName());
        metadata.setVersion(config.getVersion());
        metadata.setOriginGraphName(config.getOriginalGraphName());
        metadata.setOriginVersion(config.getOriginalVersion());
        metadata.setValidFrom(config.getValidFrom());
        metadata.setValidTo(config.getValidTo());
        metadata.setSource(new Source(1, "GIP"));
        metadata.setSegmentsCount(-1);
        metadata.setConnectionsCount(-1);
        metadata.getTags().put("originalGraphVersion", idfMetaData.getDbName());
        return metadata;
    }

    protected ISegmentOutputFormat<T> getSegmentOutputFormat(
    		IImportConfigIdf config, OutputStream outStream) throws IOException {    	
    	ISegmentAdapterRegistry<D, T> adapterRegistry = 
    			new SegmentAdapterRegistryImpl<D, T>();
    	
    	WaySegment2SegmentDTOAdapter<IWaySegmentDTO, IWaySegment> waySegmentAdapter = 
    			new WaySegment2SegmentDTOAdapter<IWaySegmentDTO, IWaySegment>();
    	
    	BaseSegment2SegmentDTOAdapter<IBaseSegmentDTO, IBaseSegment> baseSegmentAdapter = 
    			new BaseSegment2SegmentDTOAdapter<IBaseSegmentDTO, IBaseSegment>
    				(BaseSegment.class, BaseSegmentDTOImpl.class);
    	
    	List<ISegmentAdapter<D, T>> adapters =
    			new ArrayList<ISegmentAdapter<D,T>>();
    	
    	IXInfoDTOAdapter<PixelCut, PixelCutDTO> pixelCutAdapter = new PixelCutAdapter();
    	IXInfoDTOAdapter<DefaultSegmentXInfo,DefaultSegmentXInfoDTO> defaultXInfoAdapter = new DefaultSegmentXInfoAdapter();
    	List<IXInfoDTOAdapter<? extends IXInfo, ? extends IXInfoDTO>> xInfoAdapters = new ArrayList<>();
    	xInfoAdapters.add(pixelCutAdapter);
    	xInfoAdapters.add(defaultXInfoAdapter);
    	
		// create optional XInfo adapter for CSV files - has to be from type ICsvXInfoFactory
    	if (config.getCsvConfig() != null) {
    		for (Entry<Object, Object> entry : config.getCsvConfig().entrySet()) {
    			String className = (String) entry.getValue();
    			
    			try {
					Class<?> factoryClass = Class.forName(className);
					@SuppressWarnings("unchecked")
					ICsvXInfoFactory<ISegmentXInfo, ISegmentXInfoDTO> factory = (ICsvXInfoFactory<ISegmentXInfo, ISegmentXInfoDTO>) factoryClass.newInstance();
					xInfoAdapters.add(factory);
					
    			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
    				throw new RuntimeException(e);
    			}
    		}
    	}
    	
    	IXinfoAdapterRegistry<ISegmentXInfo, ISegmentXInfoDTO> segmentXInfoAdapterRegistry = 
    			new SegmentXInfoAdapterRegistry(xInfoAdapters);
    	waySegmentAdapter.setSegmentXInfoAdapterRegistry(segmentXInfoAdapterRegistry);    	
    	baseSegmentAdapter.setSegmentXInfoAdapterRegistry(segmentXInfoAdapterRegistry);
    	
    	adapters.add((ISegmentAdapter<D, T>) waySegmentAdapter);
    	adapters.add((ISegmentAdapter<D, T>) baseSegmentAdapter);
    	adapterRegistry.setAdapters(adapters);
    	
    	ISegmentOutputFormatFactory<T> segmentOutputFormatFactory = 
    			new GenericJacksonSegmentOutputFormatFactoryImpl<T>(adapterRegistry);
    	
    	//     	
    	ISegmentOutputFormat<T> outFormat;
    	if(config.isImportGip()) {
    		IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> adapter = 
        			new GraphVersionMetadata2GraphVersionMetadataDTOAdapter();
    		IBaseGraphOutputFormatFactory<T> outputFormatFactory = 
        			new GenericJacksonWayGraphOutputFormatFactoryImpl(segmentOutputFormatFactory,adapter);
    		outFormat = outputFormatFactory.getWayGraphOutputFormat(outStream);
		}
		else {
			outFormat = segmentOutputFormatFactory.getSegmentOutputFormat(outStream);
		}
    	return outFormat;
    }
    /**
     * Imports GIP into File with the given Config. This file can also be used as Singleton. It Uses a new
     * parser instance for every new producer.
     */
    public void importGip(IImportConfigIdf config) throws Exception {
    	 
        IGipLinkProducer<T> producer;
        IGipParser<T> gipParser;
    	if(config.isImportGip()) {
    		  gipParser = new GipParserImpl(new WayGraphModelFactory());
    	}
    	else {
    		  gipParser = new GipParserImpl(new BaseSegmentGraphModelFactory());
    	}
      
        producer = new GipLinkProducerImpl<T>(gipParser);
            
    	FileOutputStream stream = null;
    	ISegmentOutputFormat<T> outputFormat = null;
    	
    	String fileName = createOutputFileName(config);
    	
        try {
            log.info("GIP import job started");
            
            String inputFile = config.getInputFile();
            
            // if input file is comes from an URL => download first
            if (inputFile.startsWith("http")) {
            	// download + create pathname regarding download directory
            	inputFile = createDownloadFilename(config);
            	download(config);
            	downloadFile = inputFile;
            }

            IDFMetadata idfMetaData = gipParser.parseHeader(inputFile);
            
            if(!config.isImportGip()) {
            	fileName = fileName + "_xinfos_only";
            }
            
            fileName += ".json";
            
            stream = new FileOutputStream(fileName);
            outputFormat = getSegmentOutputFormat(config, stream);

            BlockingQueue<T> queue = new ArrayBlockingQueue<>(config.getQueueSize());

            if(config.isImportGip() && outputFormat instanceof IWayGraphOutputFormat) {
            	((IWayGraphOutputFormat)outputFormat).serialize(this.getVersionMetadata(config, idfMetaData));
            }

            Thread producerThread = producer.produceLinks(queue, config, idfMetaData);

            T current;

            while (producerThread.isAlive() && !producer.isReady()) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    log.error(e);
                }
            }
            int count = 0;
            while (producerThread.isAlive() || !queue.isEmpty()) {
                try {
                    current = queue.poll(500, TimeUnit.MILLISECONDS);
                    // TODO: bitte nur für Pixel Cut Berechnung berücksichtigen!
                    if (current != null) {
//                    	&&
//                            (config.isEnableSmallConnections() || (current.getFrc() != FuncRoadClass.MOTORWAY_FREEWAY_OR_OTHER_MAJOR_MOTORWAY || current.getLength() > 3.5))) {
                        count++;
                        if (count % 100000 == 0) {
                            log.info("Elements Queued: " + count);
                        }
                        try {
                    		 outputFormat.serialize(current);                           
                        } catch (Exception e) {
                        	log.error(e.getMessage(), e);
                        }
                    }
                } catch (InterruptedException e) {
                    log.warn("exception while polling BlockingQueue");
                }
            }

        } catch (Exception th) {
            throw th;
        } finally {
        	if (outputFormat != null) {
        		try {
        			outputFormat.close();
				} catch (Exception e) {
					log.warn(e);
				}
        	}      
        	if (stream != null) {
        		try {
					stream.close();
				} catch (Exception e) {
					log.warn(e);
				}
        	}
        }

        if (config.getImportUrl() != null) {
        	importGraphFile(config, fileName);
        }
        
        cleanup(config, fileName);
        
        log.info("IDF to Graphium conversion finished");
    }
    
	private String createOutputFileName(IImportConfigIdf config) {
		return config.getOutputDir() + "/" + config.getGraphName() + "_" + config.getVersion();
	}

}
