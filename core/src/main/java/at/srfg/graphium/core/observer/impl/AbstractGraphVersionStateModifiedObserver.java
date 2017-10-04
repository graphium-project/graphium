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
package at.srfg.graphium.core.observer.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import at.srfg.graphium.core.observer.IGraphVersionStateModifiedObserver;

/**
 * @author mwimmer
 *
 */
public abstract class AbstractGraphVersionStateModifiedObserver implements IGraphVersionStateModifiedObserver {

	protected List<Observable> observables = null;
	
	@Override
	public void setGraphVersionStateModifiedObservables(List<Observable> observables) {
		this.observables = new ArrayList<>();
		for (Observable observable : observables) {
			this.observables.add(observable);
			observable.addObserver(this);
		}
	}

}
