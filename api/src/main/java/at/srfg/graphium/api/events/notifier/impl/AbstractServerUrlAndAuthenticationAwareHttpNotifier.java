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
package at.srfg.graphium.api.events.notifier.impl;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.ServletContextAware;

public class AbstractServerUrlAndAuthenticationAwareHttpNotifier implements
		ServletContextAware {

	protected Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

	private String serverRootUrl;
	private Map<String, UsernamePasswordCredentials> credentials = new HashMap<String, UsernamePasswordCredentials>();;

	public String getBase64Authentication(String url) {
		UsernamePasswordCredentials credentials = getAuthentication(url);
		String base64Creds = null;
		if(credentials != null) {
			String plainCreds = credentials.getUserName() + ":" + credentials.getPassword();
			byte[] plainCredsBytes = plainCreds.getBytes();
			byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
			base64Creds = new String(base64CredsBytes);			
		}
		return base64Creds;
	}
	
	public UsernamePasswordCredentials getAuthentication(String url) {
		if (credentials != null) {
			UsernamePasswordCredentials serviceAuth = credentials.get(url);
			if (serviceAuth == null) {
				log.info("no auth found for " + url);
			}
			return serviceAuth;
		} else {
			log.warn("not credentials map set, returning null!");
			return null;
		}
	}

	protected String getSourceString() {
		return serverRootUrl;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.serverRootUrl = servletContext.getContextPath();
	}

	public String getServerRootUrl() {
		return serverRootUrl;
	}

	public Map<String, UsernamePasswordCredentials> getCredentials() {
		return credentials;
	}

	public void setCredentials(
			Map<String, UsernamePasswordCredentials> credentials) {
		this.credentials = credentials;
	}

}
