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
package at.srfg.graphium.gipimport.model.impl;


import at.srfg.graphium.gipimport.model.IGipLink;
import at.srfg.graphium.gipimport.model.IGipLinkGeom;
import at.srfg.graphium.gipimport.model.IGipModelFactory;
import at.srfg.graphium.gipimport.model.IGipNode;
import at.srfg.graphium.gipimport.model.IGipTurnEdge;

public class GipModelFactory implements IGipModelFactory {

	@Override
	public IGipLink newLink() {
		return new GipLinkImpl();
	}

	@Override
	public IGipLinkGeom newLinkGeom() {
		return new GipLinkGeomImpl();
	}

	@Override
	public IGipNode newNode() {
		return new GipNodeImpl();
	}

	@Override
	public IGipTurnEdge newTurnEdge() {
		return new GipTurnEdgeImpl();
	}
	
}
