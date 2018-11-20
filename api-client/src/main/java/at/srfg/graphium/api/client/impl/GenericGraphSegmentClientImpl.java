package at.srfg.graphium.api.client.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import at.srfg.graphium.api.client.IGraphSegmentClient;
import at.srfg.graphium.api.client.exception.GraphNotFoundException;
import at.srfg.graphium.api.client.exception.GraphiumServerAccessException;
import at.srfg.graphium.io.adapter.impl.AbstractSegmentDTOAdapter;
import at.srfg.graphium.io.adapter.impl.DefaultConnectionXInfoAdapter;
import at.srfg.graphium.io.adapter.impl.DefaultSegmentXInfoAdapter;
import at.srfg.graphium.io.adapter.impl.WaySegment2SegmentDTOAdapter;
import at.srfg.graphium.io.adapter.registry.ISegmentAdapterRegistry;
import at.srfg.graphium.io.adapter.registry.impl.GenericXInfoAdapterRegistry;
import at.srfg.graphium.io.adapter.registry.impl.SegmentAdapterRegistryImpl;
import at.srfg.graphium.io.adapter.registry.impl.SegmentXInfoAdapterRegistry;
import at.srfg.graphium.io.dto.IBaseSegmentDTO;
import at.srfg.graphium.io.dto.IWaySegmentDTO;
import at.srfg.graphium.io.exception.WaySegmentDeserializationException;
import at.srfg.graphium.io.inputformat.ISegmentInputFormat;
import at.srfg.graphium.io.inputformat.impl.jackson.GenericQueuingJacksonSegmentInputFormat;
import at.srfg.graphium.model.IBaseWaySegment;
import at.srfg.graphium.model.IWaySegment;

public class GenericGraphSegmentClientImpl<T extends IBaseWaySegment> extends AbstractGraphiumApiClient<List<T>>
		implements IGraphSegmentClient<T> {

	protected static final String GRAPH_READ_SEGMENTS_LIST = "segments/graphs/{graph}/versions/{version}";
				
	private ISegmentInputFormat<T> inputFormat;

	public GenericGraphSegmentClientImpl() {
		createDefaultInputFormat();
	}
	
	public GenericGraphSegmentClientImpl(String externalGraphserverApiUrl) {
		createDefaultInputFormat();
		this.externalGraphserverApiUrl = externalGraphserverApiUrl;
	}

	public GenericGraphSegmentClientImpl(ISegmentInputFormat<T>  inputFormat) {
		this.inputFormat = inputFormat;
	}
	
	public GenericGraphSegmentClientImpl(ISegmentInputFormat<T>  inputFormat, 
			String externalGraphserverApiUrl) {
		this.inputFormat = inputFormat;
		this.externalGraphserverApiUrl = externalGraphserverApiUrl;
	}
	
	@PostConstruct
	public void setup() {
		super.setup();
		if(this.inputFormat == null) {
			createDefaultInputFormat();
		}
	}
	
	protected void createDefaultInputFormat() {
		DefaultConnectionXInfoAdapter defaultConnectionXInfoAdatper = new DefaultConnectionXInfoAdapter();
        DefaultSegmentXInfoAdapter defaultSegmentXInfoAdapter = new DefaultSegmentXInfoAdapter();
        
		 // TODO: fix generics
//      List<IXInfoDTOAdapter<IConnectionXInfo, IConnectionXInfoDTO>> xInfoConnectionAdapters = new ArrayList<>();
        List xInfoConnectionAdapters = new ArrayList<>();
        xInfoConnectionAdapters.add(defaultConnectionXInfoAdatper);

//      IXinfoAdapterRegistry<? extends IConnectionXInfo,? extends IConnectionXInfoDTO> adapterConnectionXInfoRegistry =
//		new GenericXInfoAdapterRegistry<>(Collections.singletonList(defaultConnectionXInfoAdatper));
        GenericXInfoAdapterRegistry adapterConnectionXInfoRegistry = 
        		new GenericXInfoAdapterRegistry<>(Collections.singletonList(defaultConnectionXInfoAdatper));
        
        List xInfoSegmentAdapters = new ArrayList<>();
        xInfoSegmentAdapters.add(defaultSegmentXInfoAdapter);
        GenericXInfoAdapterRegistry adapterSegmentXInfoRegsitry = new SegmentXInfoAdapterRegistry<>(xInfoSegmentAdapters);
        
        AbstractSegmentDTOAdapter<IWaySegmentDTO, IWaySegment> adapterWay = new WaySegment2SegmentDTOAdapter<>();
        adapterWay.setConnectionXInfoAdapterRegistry(adapterConnectionXInfoRegistry);
        adapterWay.setSegmentXInfoAdapterRegistry(adapterSegmentXInfoRegsitry);

        ISegmentAdapterRegistry<IBaseSegmentDTO, T> adapterRegistry = new SegmentAdapterRegistryImpl<>();

        List adapterList = new ArrayList<>();
        adapterList.add(adapterWay);

        adapterRegistry.setAdapters(adapterList);		
		this.inputFormat = new GenericQueuingJacksonSegmentInputFormat<T>(adapterRegistry);
	}
	
	@Override
	public List<T> getSegments(String graphName, String graphVersion, Set<Long> ids) throws GraphNotFoundException, GraphiumServerAccessException {
		Map<String, String> requestParams = new HashMap<>();
		requestParams.put("{graph}", graphName);
		requestParams.put("{version}", graphVersion);
		
		String uri = externalGraphserverApiUrl + resolveUrlTemplates(GRAPH_READ_SEGMENTS_LIST, requestParams);
		uri += "?ids=" + ids.stream().map(e -> ""+ e).collect(Collectors.joining(","));
		return doHttpRequest(uri, graphName,
				httpEntity -> {
					try {
						return inputFormat.deserialize(httpEntity.getContent());
					}  catch (WaySegmentDeserializationException e) {
						log.error("error deserializing waysegments", e);
					} 			
					// TODO: exception up?
					return null;
				});
	}
}
