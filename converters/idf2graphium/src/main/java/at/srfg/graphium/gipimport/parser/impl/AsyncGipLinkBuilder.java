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
package at.srfg.graphium.gipimport.parser.impl;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.srfg.graphium.gipimport.model.IGipLink;

/**
 * Created by shennebe on 14.12.2015.
 */
public class AsyncGipLinkBuilder implements Supplier<IGipLink> {

    private Logger log = LoggerFactory.getLogger(AsyncGipLinkBuilder.class);

    private final IGipLink gipLink;
    private final int[] linkCoordsX;
    private final int[] linkCoordsY;

    public AsyncGipLinkBuilder(final IGipLink gipLink,
                               final int[] linkCoordsX,
                               final int[] linkCoordsY) {
        this.gipLink = gipLink;
        this.linkCoordsX = linkCoordsX;
        this.linkCoordsY = linkCoordsY;
    }

    @Override
    public IGipLink get() {
        return buildLink();
    }

    protected IGipLink buildLink() {
        IGipLink link = this.gipLink;

        if(link == null) {
            return null;
        }

        try {
            this.linkCoordsX[0] = link.getCoordinatesX()[0];
            this.linkCoordsY[0] = link.getCoordinatesY()[0];
        } catch (NullPointerException e) {

            // should not occur, but who knows...?
            long linkId = link.getId();
            if (link.getCoordinatesX() == null || link.getCoordinatesY() == null) {
                log.error("link geometry is null - linkId = " + linkId);
            } else if (link.getCoordinatesX()[0] == 0 || link.getCoordinatesY()[0] == 0) {
                log.error("link from Node is null = " + linkId);
            } else if (link.getCoordinatesX()[1] == 0 || link.getCoordinatesY()[1] == 0) {
                log.error("link to Node is null - linkId = " + linkId);
            }
            return null;
        }

        // last coordinate
        try {
            this.linkCoordsX[this.linkCoordsX.length - 1] = link.getCoordinatesX()[1];
            this.linkCoordsY[this.linkCoordsY.length - 1] = link.getCoordinatesY()[1];
        } catch (NullPointerException e) {

            long linkId = link.getId();
            // should not occur, but who knows...?
            if (link.getCoordinatesX() == null || link.getCoordinatesY() == null) {
                log.error("link geometry is null - linkId = " + linkId);
            } else if (link.getCoordinatesX()[0] == 0 || link.getCoordinatesY()[0] == 0) {
                log.error("link from Node is null = " + linkId);
            } else if (link.getCoordinatesX()[1] == 0 || link.getCoordinatesY()[1] == 0) {
                log.error("link to Node is null - linkId = " + linkId);
            }
            return null;
        }

        link.setCoordinatesX(this.linkCoordsX);
        link.setCoordinatesY(this.linkCoordsY);

        // build enqueue Segmentation Task
        return link;
    }
}
