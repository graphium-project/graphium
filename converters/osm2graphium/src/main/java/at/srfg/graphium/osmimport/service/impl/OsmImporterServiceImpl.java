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
package at.srfg.graphium.osmimport.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.openstreetmap.osmosis.areafilter.v0_6.PolygonFilter;
import org.openstreetmap.osmosis.core.domain.v0_6.Relation;
import org.openstreetmap.osmosis.core.filter.common.IdTrackerType;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.core.task.v0_6.SinkSource;
import org.openstreetmap.osmosis.pbf2.v0_6.PbfReader;
import org.openstreetmap.osmosis.tagfilter.v0_6.TagFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.io.adapter.ISegmentAdapter;
import at.srfg.graphium.io.adapter.impl.GraphVersionMetadata2GraphVersionMetadataDTOAdapter;
import at.srfg.graphium.io.adapter.impl.WaySegment2SegmentDTOAdapter;
import at.srfg.graphium.io.adapter.registry.ISegmentAdapterRegistry;
import at.srfg.graphium.io.adapter.registry.impl.SegmentAdapterRegistryImpl;
import at.srfg.graphium.io.dto.IGraphVersionMetadataDTO;
import at.srfg.graphium.io.dto.IWaySegmentDTO;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormatFactory;
import at.srfg.graphium.io.outputformat.IWayGraphOutputFormat;
import at.srfg.graphium.io.outputformat.IWayGraphOutputFormatFactory;
import at.srfg.graphium.io.outputformat.impl.jackson.GenericJacksonSegmentOutputFormatFactoryImpl;
import at.srfg.graphium.io.outputformat.impl.jackson.GenericJacksonWayGraphOutputFormatFactoryImpl;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.impl.WayGraphVersionMetadata;
import at.srfg.graphium.model.management.impl.Source;
import at.srfg.graphium.osmimport.model.IImportConfig;
import at.srfg.graphium.osmimport.model.impl.NodeCoord;
import at.srfg.graphium.osmimport.reader.pbf.RelationSink;
import at.srfg.graphium.osmimport.reader.pbf.SegmentationNodesSink;
import at.srfg.graphium.osmimport.reader.pbf.SegmentationWaySink;
import at.srfg.graphium.osmimport.reader.pbf.WayRef;
import at.srfg.graphium.osmimport.reader.pbf.WaySink;
import gnu.trove.map.hash.TLongObjectHashMap;

/**
 * @author mwimmer
 *
 */
public class OsmImporterServiceImpl {

	private static Logger log = LoggerFactory.getLogger(OsmImporterServiceImpl.class);
	
    private IWayGraphOutputFormatFactory<IWaySegment> outputFormatFactory;
    
    public OsmImporterServiceImpl() {
    	
    	IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> adapter = 
    			new GraphVersionMetadata2GraphVersionMetadataDTOAdapter();
    	ISegmentAdapterRegistry<IWaySegmentDTO, IWaySegment> adapterRegistry = 
    			new SegmentAdapterRegistryImpl<IWaySegmentDTO, IWaySegment>();
    	
    	WaySegment2SegmentDTOAdapter<IWaySegmentDTO, IWaySegment> waySegmentAdapter = 
    			new WaySegment2SegmentDTOAdapter<IWaySegmentDTO, IWaySegment>();
    	List<ISegmentAdapter<IWaySegmentDTO, IWaySegment>> adapters =
    			new ArrayList<ISegmentAdapter<IWaySegmentDTO,IWaySegment>>();
    	adapters.add(waySegmentAdapter);
    	adapterRegistry.setAdapters(adapters);
    	
    	ISegmentOutputFormatFactory<IWaySegment> segmentOutputFormatFactory = 
    			new GenericJacksonSegmentOutputFormatFactoryImpl<IWaySegment>(adapterRegistry);
    	
    	this.outputFormatFactory = 
    			new GenericJacksonWayGraphOutputFormatFactoryImpl<IWaySegment>(segmentOutputFormatFactory,adapter);
    }
	
	public void importOsm(IImportConfig config) throws Exception {
        log.info("Start converting OSM file for graph " + config.getGraphName() + " in version " + config.getVersion() + "...");

        // read OSM model and identify end nodes and nodes for segmentation task
        log.info("Start reading PBF for identifiying segmentation nodes...");
        long startTime = System.currentTimeMillis();

        SinkSource wayTagFilter = createWayTagFilter(config);
        SinkSource relationTagFilter = createRelationTagFilter();

        TLongObjectHashMap<List<WayRef>> wayRefs = new TLongObjectHashMap<>();
        
        SegmentationNodesSink segmentationNodesSink = new SegmentationNodesSink(wayRefs);
        readOsm(segmentationNodesSink, wayTagFilter, config, false);
        
        log.info("Reading PBF for identifiying segmentation nodes took " + (System.currentTimeMillis() - startTime) + "ms");

        // TODO: RelationSink in SegmentationNodesSink integrieren => geht leider ned, weil beide Sinks unterschiedliche TagFilter verwenden, diese können
        //		 leider nicht kombiniert werden!
        
        // => speichert alle Relations, deren Members (alle) auf Kandidaten (gefilterte Ways) referenzieren
        log.info("Start reading PBF for collecting relations...");
        startTime = System.currentTimeMillis();

        RelationSink relationSink = new RelationSink(wayRefs);
        readOsm(relationSink, relationTagFilter, config, false);
        TLongObjectHashMap<List<Relation>> relations = relationSink.getWayRelations();
        
        log.info(relationSink.getWayRelations().size() + " relations found");
        log.info("Reading PBF for for collecting relations took " + (System.currentTimeMillis() - startTime) + "ms");
        
    	FileOutputStream stream = null;
        IWayGraphOutputFormat<IWaySegment> outputFormat = null;
       
        try {
	        int wayCount = 0;
			stream = new FileOutputStream(config.getOutputDir() + "/" + config.getGraphName() + "_" + config.getVersion() + ".json");
	        outputFormat = outputFormatFactory.getWayGraphOutputFormat(stream);
	        outputFormat.serialize(this.getVersionMetadata(config));
	
	
	        // read OSM model again to build segmentated IWaySegment objects
	        log.info("Start reading PBF for segmenting ways...");
	        startTime = System.currentTimeMillis();
	
	        ArrayBlockingQueue<IWaySegment> waysQueue = new ArrayBlockingQueue<>(config.getQueueSize());
	        SegmentationWaySink segmentationWaySink = new SegmentationWaySink(wayRefs, relations, waysQueue);
	        Thread segmentationThread = readOsm(segmentationWaySink, wayTagFilter, config, true);

	        IWaySegment segment;
	        while (segmentationThread.isAlive() || !waysQueue.isEmpty()) {
	        	try {
					segment = waysQueue.poll(10000, TimeUnit.MILLISECONDS);
					
					if (segment != null) {
						outputFormat.serialize(segment);
						wayCount++;
					}
					
					if (wayCount > 0 && wayCount % 10000 == 0) {
						log.info(wayCount + " ways produced");
					}
					
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
	        }
			if (wayCount > 0) {
				log.info(wayCount + " ways produced");
			}

			TLongObjectHashMap<NodeCoord> nodes = segmentationWaySink.getNodes();
			
			segmentationThread = null;
			TLongObjectHashMap<List<IWaySegment>> segmentedWaySegments = segmentationWaySink.getSegmentedWaySegments();
			segmentationWaySink = null;
			
			log.info("Reading PBF for segmenting ways took " + (System.currentTimeMillis() - startTime) + "ms");

            // read OSM model again, do the segmentation task and generate IWaySegment objects
	        log.info("Start reading PBF for building non-segmented ways...");
	        startTime = System.currentTimeMillis();

	        WaySink waySink = new WaySink(wayRefs, nodes, segmentedWaySegments, relations, waysQueue);
	        Thread wayThread = readOsm(waySink, wayTagFilter, config, true);

	        // retrieve ways and process them
	        while (wayThread.isAlive() || !waysQueue.isEmpty()) {
	        	try {
					segment = waysQueue.poll(10000, TimeUnit.MILLISECONDS);
					
					if (segment != null) {
						outputFormat.serialize(segment);
						wayCount++;
					}
					
					if (wayCount > 0 && wayCount % 10000 == 0) {
						log.info(wayCount + " ways produced"); // (adaption took " + (durationAdaption / 10000) + "ms)");
					}
					
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
				}
	        }
			if (wayCount > 0) {
				log.info(wayCount + " ways produced");
			}
			wayThread = null;
			
			log.info("Reading PBF for building non-segmented ways took " + (System.currentTimeMillis() - startTime) + "ms");
	        
	        log.info("wayCount: " + wayCount);
	                
        } catch (Exception th) {
            throw th;
        } finally {
        	if (outputFormat != null) {
        		try {
        			outputFormat.close();
				} catch (Exception e) {
					log.warn(e.getMessage(), e);
				}
        	}
        	
        	if (stream != null) {
        		try {
					stream.close();
				} catch (Exception e) {
					log.warn(e.getMessage(), e);
				}
        	}
        }

        log.info("Finished converting OSM");

	}
	
	private SinkSource createWayTagFilter(IImportConfig config) {
        Set<String> keys = new HashSet<>();
        Map<String, Set<String>> keyValues = new HashMap<>();
        if (config.getHighwayList() != null && !config.getHighwayList().isEmpty()) {
			keyValues.put("highway", config.getHighwayList());
		}
		return new TagFilter("accept-way", keys, keyValues);
	}
	
	private SinkSource createRelationTagFilter() {
        Set<String> keys = new HashSet<String>();
        Map<String, Set<String>> keyValues = new HashMap<String, Set<String>>();
        Set<String> restrictionValues = new HashSet<String>();
        restrictionValues.add("restriction"); 
        keyValues.put("type", restrictionValues);
        
        TagFilter filterSink = new TagFilter("accept-relation", keys, keyValues);

        return filterSink;
	}
	
	private Thread readOsm(Sink sink, SinkSource filter, IImportConfig config, boolean async) {
        filter.setSink(sink);
        
        Sink readerSink;
        File boundsFile = null;
        if (config.getBoundsFile() != null) {
        	log.info("found polygon definition for geographic filtering");
        	// PolygonFilter -> Tagfilter -> Sink
        	boundsFile = new File(config.getBoundsFile());
        	PolygonFilter polygonFilterSink = new PolygonFilter(IdTrackerType.Dynamic, boundsFile, true, false, false, false);
        	
        	polygonFilterSink.setSink(sink);
        	filter.setSink(polygonFilterSink);
            readerSink = filter;
        } else {
        	// Tagfilter -> Sink
        	readerSink = filter;
        }
        		
        PbfReader reader = new PbfReader(new File(config.getInputFile()), config.getWorkerThreads());
        
        reader.setSink(readerSink);
        
        if (async) {
	        Thread pbfReaderThread = new Thread(reader, "PBF Reader");
	        pbfReaderThread.start();
	        return pbfReaderThread;
        } else {
        	reader.run();
        	return null;
        }
	}
	
    private IWayGraphVersionMetadata getVersionMetadata(IImportConfig config) {
        IWayGraphVersionMetadata metadata = new WayGraphVersionMetadata();
        metadata.setGraphName(config.getGraphName());
        metadata.setVersion(config.getVersion());
        metadata.setOriginGraphName(config.getOriginalGraphName());
        metadata.setOriginVersion(config.getOriginalVersion());
        metadata.setValidFrom(config.getValidFrom());
        metadata.setValidTo(config.getValidTo());
        metadata.setSource(new Source(2, "OSM"));
        metadata.setSegmentsCount(-1);
        metadata.setConnectionsCount(-1);
        return metadata;
    }

}
