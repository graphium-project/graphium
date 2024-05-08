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
package at.srfg.graphium.lanelet2import.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import at.srfg.graphium.io.adapter.IXInfoDTOAdapter;
import at.srfg.graphium.io.adapter.impl.*;
import at.srfg.graphium.io.adapter.registry.impl.SegmentXInfoAdapterRegistry;
import at.srfg.graphium.io.dto.*;
import at.srfg.graphium.io.outputformat.hd.IHdWayGraphOutputFormat;
import at.srfg.graphium.io.outputformat.hd.IHdWayGraphOutputFormatFactory;
import at.srfg.graphium.io.outputformat.hd.impl.jackson.GenericJacksonHdWayGraphOutputFormatFactoryImpl;
import at.srfg.graphium.lanelet2import.adapter.InfraAndSignsAdapter;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.hd.IHDArea;
import at.srfg.graphium.model.hd.IHDInfraAndSigns;
import org.openstreetmap.osmosis.core.task.v0_6.RunnableSource;
import org.openstreetmap.osmosis.core.task.v0_6.Sink;
import org.openstreetmap.osmosis.core.task.v0_6.SinkSource;
import org.openstreetmap.osmosis.pbf2.v0_6.PbfReader;
import org.openstreetmap.osmosis.xml.common.CompressionMethod;
import org.openstreetmap.osmosis.xml.v0_6.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.io.adapter.IAdapter;
import at.srfg.graphium.io.adapter.ISegmentAdapter;
import at.srfg.graphium.io.adapter.registry.ISegmentAdapterRegistry;
import at.srfg.graphium.io.adapter.registry.impl.SegmentAdapterRegistryImpl;
import at.srfg.graphium.io.outputformat.ISegmentOutputFormatFactory;
import at.srfg.graphium.io.outputformat.impl.jackson.GenericJacksonSegmentOutputFormatFactoryImpl;
import at.srfg.graphium.lanelet2import.adapter.AreasAdapter;
import at.srfg.graphium.lanelet2import.adapter.LaneletsAdapter;
import at.srfg.graphium.lanelet2import.connections.ConnectionsBuilder;
import at.srfg.graphium.lanelet2import.model.IImportConfig;
import at.srfg.graphium.lanelet2import.reader.EntitySink;
import at.srfg.graphium.lanelet2import.reader.LaneletContainer;
import at.srfg.graphium.model.IWayGraphVersionMetadata;
import at.srfg.graphium.model.hd.IHDWaySegment;
import at.srfg.graphium.model.impl.WayGraphVersionMetadata;
import at.srfg.graphium.model.management.impl.Source;

/**
 * @author mwimmer
 *
 */
public class LaneletImporterService {
	
	private static Logger log = LoggerFactory.getLogger(LaneletImporterService.class);

	private IHdWayGraphOutputFormatFactory<IHDWaySegment> outputFormatFactory;

    private LaneletsAdapter laneletsAdapter;
    private AreasAdapter areasAdapter;
	private InfraAndSignsAdapter infraAndSignsAdapter;
    private ConnectionsBuilder connectionsBuilder;
    
    public LaneletImporterService() {
    	
    	IAdapter<IGraphVersionMetadataDTO, IWayGraphVersionMetadata> adapter = 
    			new GraphVersionMetadata2GraphVersionMetadataDTOAdapter();
    	ISegmentAdapterRegistry<IHDWaySegmentDTO, IHDWaySegment> adapterRegistry =
                new SegmentAdapterRegistryImpl<>();
    	
    	WaySegment2SegmentDTOAdapter<IHDWaySegmentDTO, IHDWaySegment> waySegmentAdapter = 
    			new HDWaySegment2HDWaySegmentDTOAdapter<>();

		// add segment xinfo adapter registry
		SegmentXInfoAdapterRegistry<ISegmentXInfo,ISegmentXInfoDTO> segmentXInfoAdapterRegistry
				= new SegmentXInfoAdapterRegistry<>();
		// register xinfo adapter
		List<IXInfoDTOAdapter<ISegmentXInfo, ISegmentXInfoDTO>> xInfoAdapters = new ArrayList<>();
		IXInfoDTOAdapter regulationXInfoAdapter = new HDRegulatoryElementXInfoAdapter();
        xInfoAdapters.add(regulationXInfoAdapter);
		segmentXInfoAdapterRegistry.setAdapters(xInfoAdapters);
		waySegmentAdapter.setSegmentXInfoAdapterRegistry(segmentXInfoAdapterRegistry);


    	List<ISegmentAdapter<IHDWaySegmentDTO, IHDWaySegment>> adapters = new ArrayList<>();
    	adapters.add(waySegmentAdapter);
    	adapterRegistry.setAdapters(adapters);


		ISegmentAdapterRegistry<IHDAreaDTO, IHDArea> areaAdapterRegistry =
				new SegmentAdapterRegistryImpl<IHDAreaDTO, IHDArea>();

		HDArea2HDAreaDTOAdapter<IHDAreaDTO, IHDArea> areaAdapter = new HDArea2HDAreaDTOAdapter<>();
		List<ISegmentAdapter<IHDAreaDTO, IHDArea>> areaAdapters = new ArrayList<>();
		areaAdapters.add(areaAdapter);
		areaAdapterRegistry.setAdapters(areaAdapters);

		ISegmentAdapterRegistry<IHDInfraAndSignsDTO, IHDInfraAndSigns> roadInfraAdapterRegistry =
				new SegmentAdapterRegistryImpl<>();

		HDInfraAndSigns2HDInfraAndSignsDTOAdapter<IHDInfraAndSignsDTO, IHDInfraAndSigns> roadInfraAdapter =
				new HDInfraAndSigns2HDInfraAndSignsDTOAdapter<>();
		List<ISegmentAdapter<IHDInfraAndSignsDTO, IHDInfraAndSigns>> roadInfraAdapters = new ArrayList<>();
		roadInfraAdapters.add(roadInfraAdapter);
		roadInfraAdapterRegistry.setAdapters(roadInfraAdapters);

    	ISegmentOutputFormatFactory<IHDWaySegment> segmentOutputFormatFactory = 
    			new GenericJacksonSegmentOutputFormatFactoryImpl<>(adapterRegistry);

		ISegmentOutputFormatFactory<IHDArea> areaOutputFormatFactory =
				new GenericJacksonSegmentOutputFormatFactoryImpl<>(areaAdapterRegistry);

		ISegmentOutputFormatFactory<IHDInfraAndSigns> roadInfraOutputFormatFactory =
				new GenericJacksonSegmentOutputFormatFactoryImpl<>(roadInfraAdapterRegistry);


		this.outputFormatFactory =
    			new GenericJacksonHdWayGraphOutputFormatFactoryImpl<>(segmentOutputFormatFactory,
						areaOutputFormatFactory, roadInfraOutputFormatFactory, adapter);
    	
    	laneletsAdapter = new LaneletsAdapter();
    	areasAdapter = new AreasAdapter();
		infraAndSignsAdapter = new InfraAndSignsAdapter();
    	connectionsBuilder = new ConnectionsBuilder();
    }

    // TODO: asynchrone Verarbeitung umsetzen!
    
	public void importOsm(IImportConfig config) throws Exception {
        log.info("Start converting Lanelet file for graph " + config.getGraphName() + " in version " + config.getVersion() + "...");

        // read OSM model and identify end nodes and nodes for segmentation task
        log.info("Start reading PBF for identifiying segmentation nodes...");
//        long startTime = System.currentTimeMillis();

//        SinkSource wayTagFilter = createWayTagFilter(config);
//        SinkSource relationTagFilter = createRelationTagFilter();

        EntitySink entitySink = new EntitySink();
//        readOsm(segmentationNodesSink, wayTagFilter, config, false);
        readOsm(entitySink, null, config, false);

//        List<IHDRegulatoryElement> hdRegulatoryElements = adaptRegulatoryElements(entitySink);
        List<IHDWaySegment> lanelets = adaptLanelets(entitySink);
        List<IHDArea> areas = adaptAreas(entitySink);
		List<IHDInfraAndSigns> roadInfras = adaptRoadInfrastructure(entitySink);

//        collectRegulatoryElements(hdWaySegment, hdRegulatoryElements);

        log.info(lanelets.size() + " segments adapted");
        log.info(areas.size() + " areas adapted");

        // build nodeId->Lanelet map
        LaneletContainer laneletContainer = buildNodeId2LaneletMap(lanelets);
        
        // create connections
        createConnections(lanelets, laneletContainer);
        
        FileOutputStream stream = null;
		IHdWayGraphOutputFormat<IHDWaySegment> outputFormat = null;
        
        try {
			stream = new FileOutputStream(config.getOutputDir() + "/" + config.getGraphName() + "_" + config.getVersion() + ".json");
	        outputFormat = outputFormatFactory.getWayGraphOutputFormat(stream);
	        outputFormat.serialize(this.getVersionMetadata(config, lanelets.size()));
	        
	        for (IHDWaySegment hdSegment : lanelets) {
	        	outputFormat.serialize(hdSegment);
	        }
			outputFormat.finishSegments();
			for (IHDArea area : areas) {
				outputFormat.serialize(area);
			}

			outputFormat.finishAreas();
			for (IHDInfraAndSigns roadInfra : roadInfras) {
				outputFormat.serialize(roadInfra);
			}
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

        log.info("Finished converting Lanelet2 file");
        
	}
        
	private void createConnections(List<IHDWaySegment> lanelets, LaneletContainer laneletContainer) {
		for (IHDWaySegment lanelet : lanelets) {
			connectionsBuilder.build(lanelet, laneletContainer);
		}
	}

	private LaneletContainer buildNodeId2LaneletMap(List<IHDWaySegment> lanelets) {
		LaneletContainer laneletContainer = new LaneletContainer();
		for (IHDWaySegment lanelet : lanelets) {
			laneletContainer.addLanelet(lanelet);
		}
		
		return laneletContainer;
	}

	private List<IHDWaySegment> adaptLanelets(EntitySink entitySink) {
		return laneletsAdapter.adaptLanelets(entitySink.getRelations(),
											 entitySink.getWays(),
											 entitySink.getNodes());
	}

	private List<IHDArea> adaptAreas(EntitySink entitySink) {
		return areasAdapter.adapt(entitySink.getRelations(),
											 entitySink.getWays(),
											 entitySink.getNodes());
	}

	private List<IHDInfraAndSigns> adaptRoadInfrastructure(EntitySink entitySink) {
		return infraAndSignsAdapter.adapt(entitySink.getRelations(),
				entitySink.getWays(),
				entitySink.getNodes());
	}

//	private List<IHDRegulatoryElement> adaptRegulatoryElements(EntitySink entitySink) {
//		
//		return null;
//	}

//	private SinkSource createWayTagFilter(IImportConfig config) {
//        Set<String> keys = new HashSet<>();
//        Map<String, Set<String>> keyValues = new HashMap<String, Set<String>>();
//		return new TagFilter("accept-way", keys, keyValues);
//	}
//	
//	private SinkSource createRelationTagFilter() {
//        Set<String> keys = new HashSet<String>();
//        Map<String, Set<String>> keyValues = new HashMap<String, Set<String>>();
//        Set<String> restrictionValues = new HashSet<String>();
//        restrictionValues.add("restriction"); 
//        keyValues.put("type", restrictionValues);
//        return new TagFilter("accept-relation", keys, keyValues);
//	}

	private Thread readOsm(Sink sink, SinkSource filter, IImportConfig config, boolean async) {
        Sink readerSink;
        if (filter == null) {
        	readerSink = sink;
        } else {
            filter.setSink(sink);
        	readerSink = filter;
        }
        
//        File boundsFile = null;
//        if (config.getBoundsFile() != null) {
//        	log.info("found polygon definition for geographic filtering");
//        	// PolygonFilter -> Tagfilter -> Sink
//        	boundsFile = new File(config.getBoundsFile());
//        	PolygonFilter polygonFilterSink = new PolygonFilter(IdTrackerType.Dynamic, boundsFile, true, false, false, false);
//        	
//        	polygonFilterSink.setSink(sink);
//        	filter.setSink(polygonFilterSink);
//            readerSink = filter;
//        } else {
//        	// Tagfilter -> Sink
//        	readerSink = filter;
//        }
        		
        RunnableSource reader;
        if (config.getInputFile().endsWith(".pbf")) {
        	reader = new PbfReader(new File(config.getInputFile()), config.getWorkerThreads());
        } else {
        	CompressionMethod compressionMethod;
        	if (config.getInputFile().endsWith(".gz")) {
        		compressionMethod = CompressionMethod.GZip;
        	} else if (config.getInputFile().endsWith(".bz2")) {
        		compressionMethod = CompressionMethod.BZip2;
        	} else {
        		compressionMethod = CompressionMethod.None;
        	}
//        	reader = new FastXmlReader(new File(config.getInputFile()), false, compressionMethod);
        	reader = new XmlReader(new File(config.getInputFile()), false, compressionMethod);
        }
        	
        reader.setSink(readerSink);
        
        if (async) {
	        Thread readerThread = new Thread(reader, "Lanelet File Reader");
	        readerThread.start();
	        return readerThread;
        } else {
        	reader.run();
        	return null;
        }
	}
	
    private IWayGraphVersionMetadata getVersionMetadata(IImportConfig config, int segmentsCount) {
        IWayGraphVersionMetadata metadata = new WayGraphVersionMetadata();
        metadata.setGraphName(config.getGraphName());
        metadata.setVersion(config.getVersion());
        metadata.setValidFrom(config.getValidFrom());
        metadata.setValidTo(config.getValidTo());
        metadata.setSource(new Source(3, "Lanelet 2"));
        metadata.setSegmentsCount(segmentsCount);
        metadata.setConnectionsCount(-1);
        return metadata;
    }

}