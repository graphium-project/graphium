package at.srfg.graphium.api.client.impl;

import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import at.srfg.graphium.api.client.IGraphSegmentClient;
import at.srfg.graphium.model.IBaseWaySegment;

public class GenericGraphSegmentClientImpl<T extends IBaseWaySegment> extends AbstractGraphiumApiClient
		implements IGraphSegmentClient<T> {

	@PostConstruct
	public void setup() {
		super.setup();
	}
	
	@Override
	public List<T> getSegments(String graphName, String graphVersion, Set<Long> ids) {
		// TODO Auto-generated method stub
		return null;
	}

}
