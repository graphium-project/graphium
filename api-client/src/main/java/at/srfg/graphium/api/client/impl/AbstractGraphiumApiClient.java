package at.srfg.graphium.api.client.impl;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;

import javax.annotation.PostConstruct;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import at.srfg.graphium.api.client.exception.GraphNotFoundException;
import at.srfg.graphium.api.client.exception.GraphiumServerAccessException;

public abstract class AbstractGraphiumApiClient<T> extends Observable {

	protected Logger log = LoggerFactory.getLogger(getClass());

	protected String externalGraphserverApiUrl = null;
	protected int connectionRequestTimeout = 5000;
	protected int connectTimeout = 5000;
	protected int socketTimeout = 5000;
	protected int maxConnections = 5;
	protected CloseableHttpClient httpClient;
	protected ObjectMapper objectMapper;

	@PostConstruct
	public void setup() {
		if (this.httpClient == null) {
			this.httpClient = createDefaultHttpClient();
		}
		objectMapper = new ObjectMapper();
		checkAndCleanServerUrl();
	}

	private void checkAndCleanServerUrl() {
		if (this.externalGraphserverApiUrl == null) {
			throw new IllegalArgumentException("externalGraphserverApiUrl not set!");
		} else {
			externalGraphserverApiUrl += externalGraphserverApiUrl.endsWith("/") ? "" : "/";
		}
	}

	protected String resolveUrlTemplates(String subPath, Map<String, String> templateValues) {
		String resolved = subPath;
		for (Entry<String, String> templateValue : templateValues.entrySet()) {
			resolved = resolved.replace(templateValue.getKey(), templateValue.getValue());
		}
		return resolved;
	}

	protected T doHttpRequest(String uri, String graphName, IGraphApiResponseProcessingCallback<T> responseExtractor)
			throws GraphNotFoundException, GraphiumServerAccessException {

		CloseableHttpResponse response = null;
		try {
			HttpGet httpget = new HttpGet(uri);

			response = this.httpClient.execute(httpget);
			HttpEntity resEntity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200 && resEntity != null) {
				return responseExtractor.processResponse(resEntity);
			} else if (response.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
				throw new GraphNotFoundException(graphName);
			} else {
				throw new GraphiumServerAccessException(response.getStatusLine().getStatusCode());
			}
		} catch (IOException | UnsupportedOperationException e) {
			log.error("Error while reading current graph version ID", e);
			throw new GraphiumServerAccessException(e);
		} finally {
			try {
				if (response != null) {
					response.close();
				}
			} catch (IOException e) {
				log.error("Error while reading current graph version ID", e);
				throw new GraphiumServerAccessException(e);
			}
		}
	}

	protected CloseableHttpClient createDefaultHttpClient() {

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(maxConnections);
		cm.setDefaultMaxPerRoute(maxConnections);

		Builder config = RequestConfig.custom().setConnectionRequestTimeout(connectionRequestTimeout)
				.setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout);

		// TODO: Set Credentials
		CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(cm)
				.setDefaultRequestConfig(config.build()).build();
		return httpClient;
	}

	public String getExternalGraphserverApiUrl() {
		return externalGraphserverApiUrl;
	}

	public void setExternalGraphserverApiUrl(String externalGraphserverApiUrl) {
		this.externalGraphserverApiUrl = externalGraphserverApiUrl;
	}

	public int getConnectionRequestTimeout() {
		return connectionRequestTimeout;
	}

	public void setConnectionRequestTimeout(int connectionRequestTimeout) {
		this.connectionRequestTimeout = connectionRequestTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getSocketTimeout() {
		return socketTimeout;
	}

	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}

	public int getMaxConnections() {
		return maxConnections;
	}

	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public void setHttpClient(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
	}

}
