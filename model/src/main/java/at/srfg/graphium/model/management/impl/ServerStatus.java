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
package at.srfg.graphium.model.management.impl;

import java.util.concurrent.atomic.AtomicInteger;

import at.srfg.graphium.model.management.IServerStatus;

/**
 * @author mwimmer
 *
 */
public class ServerStatus implements IServerStatus {

	private String serverName;
	private String uri;
	private AtomicInteger runningImports = new AtomicInteger();
	private int maxConcurrentImports = 1;
	
	public ServerStatus() {}
	
	public ServerStatus(String serverName, String uri) {
		super();
		this.serverName = serverName;
		this.uri = uri;
	}
	public ServerStatus(String serverName, String uri, int maxConcurrentImports) {
		super();
		this.serverName = serverName;
		this.uri = uri;
		this.maxConcurrentImports = maxConcurrentImports;
	}

	@Override
	public String getServerName() {
		return serverName;
	}
	@Override
	public String getUri() {
		return uri;
	}
	@Override
	public synchronized boolean canImport() {
		return runningImports.get() < maxConcurrentImports;		
	}
	@Override
	public synchronized boolean registerImport() {
		if(canImport()) {
			this.runningImports.incrementAndGet();
			return true;
		}
		return false;
	}
	@Override
	public synchronized void unregisterImport() {
		this.runningImports.decrementAndGet();
	}

	public int getMaxConcurrentImports() {
		return maxConcurrentImports;
	}

	public void setMaxConcurrentImports(int maxConcurrentImports) {
		this.maxConcurrentImports = maxConcurrentImports;
	}
	
	@Override
	public int getRunningImports() {
		return runningImports.get();
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((serverName == null) ? 0 : serverName.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServerStatus other = (ServerStatus) obj;
		if (serverName == null) {
			if (other.serverName != null)
				return false;
		} else if (!serverName.equals(other.serverName))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ServerStatus [serverName=" + serverName + ", uri=" + uri
				+ ", maxConcurrentImports=" + maxConcurrentImports
				+ ", runningImports=" + runningImports + "]";
	}
	
}