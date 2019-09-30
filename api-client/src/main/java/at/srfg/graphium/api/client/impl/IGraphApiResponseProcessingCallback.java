package at.srfg.graphium.api.client.impl;

import java.io.IOException;

import org.apache.http.HttpEntity;

public interface IGraphApiResponseProcessingCallback<T> {

	public T processResponse(HttpEntity httpEntity) throws UnsupportedOperationException, IOException;
	
}
