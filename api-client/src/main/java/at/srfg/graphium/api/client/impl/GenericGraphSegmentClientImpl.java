package at.srfg.graphium.api.client.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import at.srfg.graphium.api.client.IGraphSegmentClient;
import at.srfg.graphium.api.client.exception.GraphNotFoundException;
import at.srfg.graphium.api.client.exception.GraphiumServerAccessException;
import at.srfg.graphium.io.exception.WaySegmentDeserializationException;
import at.srfg.graphium.io.inputformat.ISegmentInputFormat;
import at.srfg.graphium.model.IBaseWaySegment;

public class GenericGraphSegmentClientImpl<T extends IBaseWaySegment> extends AbstractGraphiumApiClient<List<T>>
		implements IGraphSegmentClient<T> {

	protected static final String GRAPH_READ_SEGMENTS_LIST = "segments/graphs/{graph}/versions/{version}";
				
	private ISegmentInputFormat<T> inputFormat;

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
