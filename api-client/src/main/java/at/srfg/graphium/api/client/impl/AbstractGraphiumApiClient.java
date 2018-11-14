package at.srfg.graphium.api.client.impl;

import java.util.Observable;

import javax.annotation.PostConstruct;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractGraphiumApiClient extends Observable  {

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
		if(this.externalGraphserverApiUrl == null) {
			throw new IllegalArgumentException("externalGraphserverApiUrl not set!");
		}
		else {
			externalGraphserverApiUrl += externalGraphserverApiUrl.endsWith("/") ? "" : "/"; 			
		}
	}
	
	protected String getGraphResolvedUrl(String subPath, String graphName) {
		return externalGraphserverApiUrl + subPath.replace("{graph}", graphName); 
	}

	protected CloseableHttpClient createDefaultHttpClient() {
		
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(maxConnections);
		cm.setDefaultMaxPerRoute(maxConnections);
		
	   Builder config = RequestConfig.custom()
				.setConnectionRequestTimeout(connectionRequestTimeout)
				.setConnectTimeout(connectTimeout)
				.setSocketTimeout(socketTimeout);

       // TODO: Set Credentials
		CloseableHttpClient httpClient = HttpClients.custom()
		        .setConnectionManager(cm).setDefaultRequestConfig(config.build())
		        .build();
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
