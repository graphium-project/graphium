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
package at.srfg.graphium.gipimport.parser.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.zip.ZipInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;

import at.srfg.graphium.gipimport.helper.GeoHelper;
import at.srfg.graphium.gipimport.helper.GipLinkFilter;
import at.srfg.graphium.gipimport.helper.ParserHelper;
import at.srfg.graphium.gipimport.model.IDFMetadata;
import at.srfg.graphium.gipimport.model.IGipLink;
import at.srfg.graphium.gipimport.model.IGipModelFactory;
import at.srfg.graphium.gipimport.model.IGipNode;
import at.srfg.graphium.gipimport.model.IGipTurnEdge;
import at.srfg.graphium.gipimport.model.IImportConfig;
import at.srfg.graphium.gipimport.model.impl.GipModelFactory;
import at.srfg.graphium.gipimport.model.impl.IDFMetadataImpl;
import at.srfg.graphium.gipimport.parser.IGipParser;
import at.srfg.graphium.gipimport.parser.IGipSectionParser;
import at.srfg.graphium.gipimport.xinfo.CsvAdapterService;
import at.srfg.graphium.model.Access;
import at.srfg.graphium.model.FormOfWay;
import at.srfg.graphium.model.FuncRoadClass;
import at.srfg.graphium.model.IBaseGraphModelFactory;
import at.srfg.graphium.model.IBaseSegment;
import at.srfg.graphium.model.IDefaultSegmentXInfo;
import at.srfg.graphium.model.ISegmentXInfo;
import at.srfg.graphium.model.IWaySegment;
import at.srfg.graphium.model.IWaySegmentConnection;
import at.srfg.graphium.model.impl.DefaultSegmentXInfo;
import at.srfg.graphium.model.impl.WaySegmentConnection;
import at.srfg.graphium.pixelcuts.model.IPixelCut;
import at.srfg.graphium.pixelcuts.model.ISegment;
import at.srfg.graphium.pixelcuts.model.impl.SegmentImpl;
import at.srfg.graphium.pixelcuts.service.IRenderingCalculator;
import at.srfg.graphium.pixelcuts.service.impl.RenderingCalculatorImpl;
import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.procedure.TLongObjectProcedure;
import gnu.trove.set.TLongSet;

public class GipParserImpl<T extends IBaseSegment> implements IGipParser<T> {

	private static Logger log = LoggerFactory.getLogger(GipParserImpl.class);

	private IGipModelFactory modelFactory;	
	private IBaseGraphModelFactory<T> segmentFactory;
	
	private String phase;

	private boolean active;

	private ImportStatistics statistics = new ImportStatistics();
	
	/**
	 * Defines end of parsing process using the phase's name (including!).
	 */
	private static final int SRID = 4326;

	private Date beginParseDate;
	private boolean finished;

	public GipParserImpl(IBaseGraphModelFactory<T> segmentFactory) {
		this.modelFactory = new GipModelFactory();
		this.segmentFactory = segmentFactory;
		this.phase = IGipParser.PHASE_UNDEFINED;
		this.active = false;
	}

	@Override
	public void parseGip(BlockingQueue<T> queue,
			 IImportConfig config,
			 IDFMetadata metadata) {
		this.active = true;
		statistics.resetInstance();

		IGipSectionParser<TLongObjectMap<IGipNode>> nodesParser = new GipNodeSectionParserImpl(this);
		IGipSectionParser<TLongObjectMap<IGipLink>> linkParser = new GipLinkSectionParser(this,
				nodesParser,config,statistics);
		IGipSectionParser<TLongSet> linkCoordinatesParser = new GipLinkCoordinatesParser(this,
				linkParser);
		IGipSectionParser<TLongObjectMap<List<IGipTurnEdge>>> turnEdgeParser = new GipLinkTurnEdgesParser(
				this,config,nodesParser,linkParser,linkCoordinatesParser
		);
		IGipSectionParser<TLongObjectMap<Map<String, Object>>> linkUseParser = new GipLinkUsesParser(this);
		
		this.beginParseDate = new Date();

		if (metadata.getFileName() == null) {
			log.error("no filename given!");
			return;
		}

		BufferedReader file;
		printUsedMemory("Before Parsing");
		try {
			String encoding = metadata.getCharset().displayName();
			InputStream inputStream;
			if (metadata.getFileName().endsWith(".zip")) {
				ZipInputStream zStream = new ZipInputStream(new FileInputStream(metadata.getFileName()));
				zStream.getNextEntry();
				inputStream = zStream;
			} else {
				inputStream = new FileInputStream(metadata.getFileName());
			}
			file = new BufferedReader(new InputStreamReader(inputStream, encoding));
			String line = file.readLine();
			
			while (line != null) {
			
				if (this.phase.equals(PHASE_UNDEFINED)) {
					if (line.startsWith("tbl;")) {
						this.phase = selectPhase(line);
					}
				}

				switch (this.phase) {
					case IGipSectionParser.PHASE_NODE:
						line = nodesParser.parseSection(file);
						break;
					case IGipSectionParser.PHASE_LINK:
						line = linkParser.parseSection(file);
						break;
					case IGipSectionParser.PHASE_LINKCOORDINATES:
						line = linkCoordinatesParser.parseSection(file);

						log.info("Valid Gip Links " + linkCoordinatesParser.getResult().size());
						break;
					case IGipSectionParser.PHASE_TURNEDGE:
						line = turnEdgeParser.parseSection(file);

						if (!config.isExtractBusLaneInfo()) {
							finished = true;
						}
						break;
					case IGipSectionParser.PHASE_LINKUSE:
						line = linkUseParser.parseSection(file);

						if (config.isExtractBusLaneInfo()) {
							finished = true;
						}
						break;
					default:
						line = file.readLine();
						break;
				}
				
			}

			TLongSet linksToEnqueue = linkCoordinatesParser.getResult();
			TLongObjectMap<IGipLink> links = linkParser.getResult();
			TLongObjectMap<List<IGipTurnEdge>> turnEdges = turnEdgeParser.getResult();
			TLongObjectMap<List<ISegmentXInfo>> optionalXInfos = new TLongObjectHashMap<>();
			
//			TLongObjectMap<IPixelCut> renderingResultPerSegment = null;
			TLongObjectMap<Map<String, Object>> buslaneMap = linkUseParser.getResult();
			TLongObjectMap<Map<String, Object>> defaultTags = new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<>());
			defaultTags.putAll(buslaneMap);

			if (config.isCalculatePixelCut()) {
//				renderingResultPerSegment = this.calculatePixelCutOffset(config,linksToEnqueue,links);
				TLongObjectMap<IPixelCut> pixelCuts = this.calculatePixelCutOffset(config,linksToEnqueue,links);
				if (pixelCuts != null) {
					for (Long segmentId : pixelCuts.keys()) {
						addToXInfos(optionalXInfos, segmentId, Collections.singletonList(pixelCuts.get(segmentId)));
					}
				}
			}
			
			if (config.getCsvConfig() != null) {
				// read optional CSV files and adapt into XInfo objects
				if (config.getCsvEncodingName() != null) {
					encoding = config.getCsvEncodingName();
				}
				TLongObjectMap<List<ISegmentXInfo>> xinfoList = this.adaptCsvFiles(config.getCsvConfig(), encoding);
				if (xinfoList != null) {
					for (Long segmentId : xinfoList.keys()) {
						addToXInfos(optionalXInfos, segmentId, xinfoList.get(segmentId));
					}
				}
			}

			if (finished) {
				enqueueSegments(linksToEnqueue, links, turnEdges, optionalXInfos, defaultTags, queue, config);
			}
			
			log.info("segmentation tasks enqueued: " + statistics.getNrOfSegmentTasks());
			
			log.info("BBox: " + ((GipNodeSectionParserImpl)nodesParser).getMinX() + ", " + ((GipNodeSectionParserImpl)nodesParser).getMinY()
					+ " - " + ((GipNodeSectionParserImpl)nodesParser).getMaxX() + ", " +((GipNodeSectionParserImpl)nodesParser).getMaxY());
			
			statistics.printValidationResults();

			this.active = false;
			
		} catch (IOException e) {
			log.error(e.toString());
		}

	}

	private TLongObjectMap<List<ISegmentXInfo>> adaptCsvFiles(Properties csvConfig, String encoding) {
		TLongObjectMap<List<ISegmentXInfo>> xinfoList = null;
		for (Entry<Object, Object> entry : csvConfig.entrySet()) {
			String fileName = (String) entry.getKey();
			String className = (String) entry.getValue();

			log.info("Adapting CSV file " + fileName + "...");

			CsvAdapterService csvadapter = new CsvAdapterService();
			TLongObjectMap<List<ISegmentXInfo>> csvXinfoMap = csvadapter.adaptCsvFile(fileName, className, encoding);
			if (xinfoList == null) {
				xinfoList = csvXinfoMap;
			} else {
				for (long segmentId : csvXinfoMap.keys()) {
					if (!xinfoList.containsKey(segmentId)) {
						xinfoList.put(segmentId, new ArrayList<>());
					}
					xinfoList.get(segmentId).addAll(csvXinfoMap.get(segmentId));
				}
			}
			
			log.info("Adapting CSV file " + fileName + "finished");

		}
		return xinfoList;
	}

	private void addToXInfos(TLongObjectMap<List<ISegmentXInfo>> optionalXInfos, long segmentId, List<ISegmentXInfo> xinfoList) {
		if (!optionalXInfos.containsKey(segmentId)) {
			optionalXInfos.put(segmentId, new ArrayList<>());
		}
		optionalXInfos.get(segmentId).addAll(xinfoList);
	}

	private TLongObjectMap<IPixelCut> calculatePixelCutOffset(IImportConfig config,
																  TLongSet linkIdsEnqueued,
																  TLongObjectMap<IGipLink> links){
		log.info("phase pixel cut calculation ....");
		TLongObjectMap<IPixelCut> renderingResultPerSegment = new TLongObjectHashMap<>();
		final TLongObjectHashMap<TLongArrayList> nodeLinksConnectionTable = new TLongObjectHashMap<>();
		log.info("Link Ids Queued size: " + linkIdsEnqueued.size());
		GipLinkFilter filter = new GipLinkFilter(config.getMaxFrc(),config.getMinFrc(),config.getIncludedGipIds(),
				config.getExcludedGipIds(),config.isEnableSmallConnections());
		TLongSet filteredLinks = filter.filter(links);
		linkIdsEnqueued.forEach(value -> {
			//enqued Gip Links that after they have been filtered.
			if (filteredLinks.contains(value)) {
				IGipLink link = links.get(value);
				if (link != null) {
					if (link.getFromNodeId() != 0) {
						if (!nodeLinksConnectionTable.containsKey(link.getFromNodeId())) {
							nodeLinksConnectionTable.put(link.getFromNodeId(), new TLongArrayList());
						}
						nodeLinksConnectionTable.get(link.getFromNodeId()).add(link.getId());
					}
					if (link.getToNodeId() != 0) {
						if (!nodeLinksConnectionTable.containsKey(link.getToNodeId())) {
							nodeLinksConnectionTable.put(link.getToNodeId(), new TLongArrayList());
						}
						nodeLinksConnectionTable.get(link.getToNodeId()).add(link.getId());
					}
				}
			}
			return true;
		});

		log.info("Filtered Links Size " + filteredLinks.size());
		renderingResultPerSegment = new TLongObjectHashMap<>();
		IRenderingCalculator renderingCalculator = new RenderingCalculatorImpl();
		renderingCalculator.calculateAllReduceFactors(nodeLinksConnectionTable, adaptSegments(links), filteredLinks, renderingResultPerSegment);
		log.info("Rendering result size " + renderingResultPerSegment.size());
		return renderingResultPerSegment;
	}

	private TLongObjectMap<ISegment> adaptSegments(TLongObjectMap<IGipLink> links) {
		TLongObjectMap<ISegment> segments = new TLongObjectHashMap<>(links.size());
		for (Long id : links.keys()) {
			IGipLink link = links.get(id);
			segments.put(id, new SegmentImpl(link.getId(), link.getFromNodeId(), link.getToNodeId(), link.getCoordinatesX(), link.getCoordinatesY(), link.getFuncRoadClassValue()));
		}
		return segments;
	}

	protected void enqueueSegments(final TLongSet linksToEnqueue,
								   final TLongObjectMap<IGipLink> links,
								   final TLongObjectMap<List<IGipTurnEdge>> turnEdges,
								   //final TLongObjectMap<IPixelCut> rendering,
								   TLongObjectMap<List<ISegmentXInfo>> optionalXInfos,
								   final TLongObjectMap<Map<String, Object>> defaultTags,
								   final BlockingQueue<T> queue,
								   final IImportConfig config) {
//		if (rendering != null) {
//			log.info("Rendering Table" + rendering.size());
//		}
		
		if (config.isImportGip()) {
			TLongObjectMap<TLongList> fullNodeConnections = null;
			
			if (config.isEnableFullConnectivity()) {
				fullNodeConnections = new TLongObjectHashMap<>();
				for (long linkId : linksToEnqueue.toArray()) {
					IGipLink link = links.get(linkId);
					if (!fullNodeConnections.containsKey(link.getFromNodeId())) {
						fullNodeConnections.put(link.getFromNodeId(), new TLongArrayList());
					}
					fullNodeConnections.get(link.getFromNodeId()).add(linkId);
					
					if (!fullNodeConnections.containsKey(link.getToNodeId())) {
						fullNodeConnections.put(link.getToNodeId(), new TLongArrayList());
					}
					fullNodeConnections.get(link.getToNodeId()).add(linkId);
				}
			}
			
			for (long linkId : linksToEnqueue.toArray()) {
				IGipLink link = links.get(linkId);
				Coordinate[] coordinates = new Coordinate[link.getCoordinatesX().length];
				for (int i = 0; i < link.getCoordinatesX().length; i++) {
					coordinates[i] = new Coordinate(((double)link.getCoordinatesX()[i] / (double)GeoHelper.COORDINATE_MULTIPLIER),
							((double)link.getCoordinatesY()[i] / (double)GeoHelper.COORDINATE_MULTIPLIER));
				}
				LineString lineString = GeoHelper.createLineString(coordinates, SRID);
				// intersects link the given BBox?
				if (config.getBounds() != null && !lineString.intersects(config.getBounds())) {
					statistics.increaseNrOfNotIntersectingLinks();
					return;
				}
	
				// validation
				validateLink(link);
				
				statistics.increaseNrOfSegmentTasks();
	
				String segmentName = link.getName1();
				if (!link.getName1().equals("") && !link.getName2().equals("")) {
					segmentName += ", ";
				}
				segmentName += link.getName2();
				
				T segment = segmentFactory.newSegment();
				segment.setId(link.getId());
				IWaySegment waySeg = (IWaySegment) segment;
				waySeg.setGeometry(lineString);
				waySeg.setLength(link.getLength());
				waySeg.setName(segmentName);
				waySeg.setMaxSpeedTow(link.getSpeedTow());
				waySeg.setMaxSpeedBkw(link.getSpeedBkw());
				waySeg.setSpeedCalcTow(link.getSpeedTow());
				waySeg.setSpeedCalcBkw(link.getSpeedBkw());
				waySeg.setLanesTow(link.getLanesTow());
				waySeg.setLanesBkw(link.getLanesBkw());
				waySeg.setFrc(FuncRoadClass.getFuncRoadClassForValue(link.getFuncRoadClassValue()));
				waySeg.setFormOfWay(FormOfWay.getFormOfWayForValue(link.getFormOfWay()));
				waySeg.setWayId(link.getId());
				waySeg.setStartNodeId(link.getFromNodeId());
				waySeg.setStartNodeIndex(0);
				waySeg.setEndNodeId(link.getToNodeId());
				waySeg.setEndNodeIndex(link.getCoordinatesX().length);
				waySeg.setAccessTow(ParserHelper.adaptAccess(link.getAccessTow()));
				waySeg.setAccessBkw(ParserHelper.adaptAccess(link.getAccessBkw()));
				waySeg.setTunnel(link.isTunnel());
				waySeg.setBridge(link.isBridge());
				waySeg.setUrban(link.isUrban());
				waySeg.setTimestamp(beginParseDate);
				
//				if (rendering != null) {
//					IPixelCut renderingInfo = null;
//					renderingInfo = rendering.get(link.getId());
//					segment.addXInfo(renderingInfo);
//				}
				
				if (optionalXInfos != null) {
					List<ISegmentXInfo> xinfoList = optionalXInfos.get(link.getId());
					if (xinfoList != null && !xinfoList.isEmpty()) {
						segment.addXInfo(xinfoList);
					}
				}
				
				if (defaultTags != null && defaultTags.containsKey(link.getId())) {
					Map<String, Object> tags = defaultTags.get(link.getId());
					IDefaultSegmentXInfo xinfo = new DefaultSegmentXInfo();
					xinfo.setSegmentId(link.getId());
					xinfo.setValues(tags);
					segment.addXInfo(xinfo);
				}
				
				doUpdateConnections(waySeg, turnEdges);
				
				if (config.isEnableFullConnectivity()) {
					doUpdateFullConnections(waySeg, fullNodeConnections);
				}
				
				try {
					queue.put(segment);
				} catch (InterruptedException e) {
					log.error(e.toString());
				}
				
			}
		}
		// only if rendering is present and no gip import done
		else if (optionalXInfos != null) {
			
			optionalXInfos.forEachEntry( new TLongObjectProcedure<List<ISegmentXInfo>>() {

				@Override
				public boolean execute(long key, List<ISegmentXInfo> xinfoList) {
					T segment = segmentFactory.newSegment();
					segment.setId(key);
					segment.addXInfo(xinfoList);
					try {
						queue.put(segment);
					} catch (InterruptedException e) {
						log.error(e.toString());
					}
					
					return true;
				}
			});
//		// only if rendering is present and no gip import done
//		else if (rendering != null) {
//			
//			rendering.forEachEntry( new TLongObjectProcedure<IPixelCut>() {
//
//				@Override
//				public boolean execute(long key, IPixelCut pixelCut) {
//					T segment = segmentFactory.newSegment();
//					segment.setId(key);
//					segment.addXInfo(pixelCut);
//					try {
//						queue.put(segment);
//					} catch (InterruptedException e) {
//						log.error(e.toString());
//					}
//					
//					return true;
//				}
//			});
			
		}
	}
	
	private void doUpdateFullConnections(IWaySegment waySeg, TLongObjectMap<TLongList> fullNodeConnections) {
		Set<Access> accesses = new HashSet<>();
		accesses.add(Access.NONE);
		Set<IWaySegmentConnection> cons = new HashSet<>();
		TLongList startNodeSegments = fullNodeConnections.get(waySeg.getStartNodeId());
		TLongIterator it = startNodeSegments.iterator();
		while (it.hasNext()) {
			Long connectedSegId = it.next();
			if (waySeg.getId() != connectedSegId &&
				!hasConnection(waySeg.getStartNodeCons(), connectedSegId)) {
				cons.add(new WaySegmentConnection(waySeg.getStartNodeId(), waySeg.getId(),
						connectedSegId, accesses));
			}
		}
		
		TLongList endNodeSegments = fullNodeConnections.get(waySeg.getEndNodeId());
		it = endNodeSegments.iterator();
		while (it.hasNext()) {
			Long connectedSegId = it.next();
			if (waySeg.getId() != connectedSegId &&
				!hasConnection(waySeg.getEndNodeCons(), connectedSegId)) {
				cons.add(new WaySegmentConnection(waySeg.getEndNodeId(), waySeg.getId(),
						connectedSegId, accesses));
			}
		}

		if (!cons.isEmpty()) {
			waySeg.addCons(new ArrayList<>(cons));
		}
		
	}

	private boolean hasConnection(List<IWaySegmentConnection> cons, Long segId) {
		for (IWaySegmentConnection con : cons) {
			if (con.getToSegmentId() == segId) {
				return true;
			}
		}
		return false;
	}

	protected void doUpdateConnections(IWaySegment current,
									   TLongObjectMap<List<IGipTurnEdge>> turnEdges) {
		Set<IWaySegmentConnection> startNodeCons = new HashSet<>();
		Set<IWaySegmentConnection> endNodeCons = new HashSet<>();

		if (turnEdges.containsKey(current.getWayId())) {
			for (IGipTurnEdge turnEdge : turnEdges.get(current.getWayId())) {
				if (turnEdge.getViaNodeId() == current.getStartNodeId()) {
					startNodeCons.add(new WaySegmentConnection(turnEdge.getViaNodeId(), turnEdge.getFromLinkId(),
							turnEdge.getToLinkId(), ParserHelper.adaptAccess(turnEdge.getVehicleType())));
					
				} else if (turnEdge.getViaNodeId() == current.getEndNodeId()) {
					endNodeCons.add(new WaySegmentConnection(turnEdge.getViaNodeId(), turnEdge.getFromLinkId(),
							turnEdge.getToLinkId(), ParserHelper.adaptAccess(turnEdge.getVehicleType())));
				
				} else {
					log.error("ViaNode of TurnEdge is wheter start node nor end node!");
				}
			}
		}
		
		if (!startNodeCons.isEmpty()) {
			current.setStartNodeCons(new ArrayList<>(startNodeCons));
		}
		if (!endNodeCons.isEmpty()) {
			current.setEndNodeCons(new ArrayList<>(endNodeCons));
		}
		
	}


	protected String selectPhase(String line) {
		String[] lineParts = line.split(";");
		switch (lineParts[1]) {
			case "Node":
				return IGipSectionParser.PHASE_NODE;
			case "Link":
				return IGipSectionParser.PHASE_LINK;
			case "LinkCoordinate":
				return IGipSectionParser.PHASE_LINKCOORDINATES;
			case "TurnEdge":
				return IGipSectionParser.PHASE_TURNEDGE;
			case "LinkUse":
				return IGipSectionParser.PHASE_LINKUSE;
		}
		return PHASE_UNDEFINED;
	}
	
	protected void validateLink(IGipLink link) {
		// links with oneway = -1
		if (link.getOneway() < 0) {
			statistics.increaseNrOfOnewayInvalid();
			
			Set<Access> accessesTow = ParserHelper.adaptAccess(link.getAccessTow());
			if (accessesTow.isEmpty()) {
				statistics.increaseInvalidAccessTow(ImportStatistics.NO_ACCESS_ENTRY);
			} else {
				for (Access acc : accessesTow) {
					statistics.increaseInvalidAccessTow(acc.name());
				}
			}

			Set<Access> accessesBkw = ParserHelper.adaptAccess(link.getAccessBkw());
			if (accessesTow.isEmpty()) {
				statistics.increaseInvalidAccessBkw(ImportStatistics.NO_ACCESS_ENTRY);
			} else {
				for (Access acc : accessesBkw) {
					statistics.increaseInvalidAccessBkw(acc.name());
				}
			}
		} else 
			
			// oneway links
			if (link.getOneway() == 0 || link.getOneway() == 1) {
			statistics.increaseNrOfOneways();
		}
		
		// used access types
		Set<Access> accessesTow = ParserHelper.adaptAccess(link.getAccessTow());
		if (accessesTow.isEmpty()) {
			statistics.increaseAccessTow(ImportStatistics.NO_ACCESS_ENTRY);
		} else {
			for (Access acc : accessesTow) {
				statistics.increaseAccessTow(acc.name());
			}
		}

		Set<Access> accessesBkw = ParserHelper.adaptAccess(link.getAccessBkw());
		if (accessesTow.isEmpty()) {
			statistics.increaseAccessBkw(ImportStatistics.NO_ACCESS_ENTRY);
		} else {
			for (Access acc : accessesBkw) {
				statistics.increaseAccessBkw(acc.name());
			}
		}

		statistics.increaseLevel(link.getLevel());

		
		// lanes = 0.5
		if (link.getLanesTow() == 0.5) {
			statistics.increaseNrOfHalfLanesTow();
		}
		if (link.getLanesBkw() == 0.5) {
			statistics.increaseNrOfHalfLanesBkw();
		}
		
	}


	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public String getPhase() {
		return phase;
	}

	@Override
	public IGipModelFactory getModelFactory() {
		return this.modelFactory;
	}

	public boolean isReady() {
		return finished;
	}

	@Override
	public IDFMetadata parseHeader(String filename) throws IOException {
		IDFMetadata idfMetadata = null;
		InputStream inputStream = null;
		BufferedReader file = null;
		try {
			if (filename.endsWith(".zip")) {
				ZipInputStream zStream = new ZipInputStream(new FileInputStream(filename));
				zStream.getNextEntry();
				inputStream = zStream;
			} else {
				inputStream = new FileInputStream(filename);
			}
			file = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String line = file.readLine();
			idfMetadata = new IDFMetadataImpl(filename);
			while (line != null && (!line.startsWith("eoh;") || line.startsWith("tbl;"))) {
				if (line.startsWith("mod;")) {
					try {
						DateFormat format = new SimpleDateFormat("dd.MM.yyyy;hh:mm:ss");
						String date = line.substring(4);
						idfMetadata.setModificationDate(format.parse(date));
					} catch (ParseException e) {
						log.warn("Date String " + line.substring(4) + "could not be parsed");
					}
				} else if (line.startsWith("usr;")) {
					idfMetadata.setUsername(line.substring(4));
				} else if (line.startsWith("cpt;")) {
					idfMetadata.setCpt(line.substring(4));
				} else if (line.startsWith("exe;")) {
					idfMetadata.setExe(line.substring(4));
				} else if (line.startsWith("lib;")) {
					int lib = Integer.parseInt(line.substring(4));
					idfMetadata.setLib(lib);
				} else if (line.startsWith("chs;")) {
					String encoding = line.substring(4);
					if (encoding.equalsIgnoreCase("ISO_LATIN_1") || encoding.equalsIgnoreCase("ISO-LATIN-1")) {
						idfMetadata.setCharset(Charset.forName("ISO-8859-1"));
					} else if (encoding.equalsIgnoreCase("ISO646-US")) {
						idfMetadata.setCharset(Charset.forName("US-ASCII"));
					} else {
						idfMetadata.setCharset(Charset.forName(encoding));
					}
				} else if (line.startsWith("dss;")) {
					idfMetadata.setDataSet(line.substring(4));
				} else if (line.startsWith("dbn;")) {
					idfMetadata.setDbName(line.substring(4));
				} else if (line.startsWith("uid;")) {
					idfMetadata.setUid(Integer.parseInt(line.substring(4)));
				} else if (line.startsWith("cid;")) {
					idfMetadata.setCid(Integer.parseInt(line.substring(4)));
				} else if (line.startsWith("typ;")) {
					idfMetadata.setTyp(line.charAt(4));
				}
				line = file.readLine();
			}
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (file != null) {
				file.close();
			}
		}
		return idfMetadata;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public static void printUsedMemory(String info) {


		log.info(info);
		int mb = 1024*1024;

		//Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();

		log.info("##### Heap utilization statistics [MB] #####");

		//Print used memory
		log.info("Used Memory:"
				+ (runtime.totalMemory() - runtime.freeMemory()) / mb);

		//Print free memory
		log.info("Free Memory:"
				+ runtime.freeMemory() / mb);

		//Print total available memory
		log.info("Total Memory:" + runtime.totalMemory() / mb);

		//Print Maximum available memory
		log.info("Max Memory:" + runtime.maxMemory() / mb);
	}

}