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
package at.srfg.graphium.pixelcuts.service.impl;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by shennebe on 29.07.2015.
 */
public class TestGipImporterAzimuth {

    @Test
    public void testAzimuth() {
        //1. Quadrant azimuth = 45°
        double azimuth1 = RenderingCalculatorImpl.getAzimuth(1,1, 2,2);
        Assert.assertEquals(Math.toDegrees(azimuth1), 45d, 0.000001);

        //1. Quadrant azimuth = 45°
        double azimuth12 = RenderingCalculatorImpl.getAzimuth(1,1, 2,3);
        Assert.assertEquals(Math.toDegrees(azimuth12), 26.56505117707799,0.000001);

        //1. Quadrant azimuth = 45°
        double azimuth13 = RenderingCalculatorImpl.getAzimuth(1,1,3,2);
        Assert.assertEquals(Math.toDegrees(azimuth13), 63.43494882292201,0.000001);

        //2. Quadrant azimuth = 135°
        double azimuth2 = RenderingCalculatorImpl.getAzimuth(1,1,2,0);
        Assert.assertEquals(Math.toDegrees(azimuth2), 135d,0.000001);

        //2. Quadrant azimuth = 116°
        double azimuth21 = RenderingCalculatorImpl.getAzimuth(1,1,3,0);
        Assert.assertEquals(Math.toDegrees(azimuth21), 116.56505117707799,0.000001);

        //2. Quadrant azimuth = 153°
        double azimuth22 = RenderingCalculatorImpl.getAzimuth(1,1,2,-1);
        Assert.assertEquals(Math.toDegrees(azimuth22), 153.43494882292202,0.000001);

        //3. Quadrant azimuth = 225°
        double azimuth3 = RenderingCalculatorImpl.getAzimuth(1,1,0,0);
        Assert.assertEquals(Math.toDegrees(azimuth3), 225d,0.000001);

        //3. Quadrant azimuth = 206°
        double azimuth31 = RenderingCalculatorImpl.getAzimuth(1,1,0,-1);
        Assert.assertEquals(Math.toDegrees(azimuth31), 206.56505117707798,0.000001);

        //3. Quadrant azimuth = 206°
        double azimuth32 = RenderingCalculatorImpl.getAzimuth(1,1,-1,0);
        Assert.assertEquals(Math.toDegrees(azimuth32), 243.43494882292202,0.000001);

        //4. Quadrant azimuth = 315
        double azimuth4 = RenderingCalculatorImpl.getAzimuth(1,1,0,2);
        Assert.assertEquals(Math.toDegrees(azimuth4), 315d,0.000001);

        //4. Quadrant azimuth = 296
        double azimuth41 = RenderingCalculatorImpl.getAzimuth(1,1,-1,2);
        Assert.assertEquals(Math.toDegrees(azimuth41), 296.565051177078,0.000001);

        //4. Quadrant azimuth = 333
        double azimuth42 = RenderingCalculatorImpl.getAzimuth(1,1,0,3);
        Assert.assertEquals(Math.toDegrees(azimuth42), 333.434948822922,0.000001);
    }

    @Test
    public void testReduceFactor() {
        //From 1 Quadrant to 4 Quadrant
        double[] startAzimuths = {45,135,225,315};
        double[] targets = {0.0,0.13165,0.26794,0.41421,0.57735,0.76732,1.00000,
                1.30322,1.73205,2.4142,3.7320,7.5957,Double.MAX_VALUE,
                -7.5957,-3.73205,-2.4142,-1.73205,-1.30322,-1.0000,
                -0.76732,-0.57735,-0.41421,-0.267949,-0.13165,0.0};
        for (double startAzimuth : startAzimuths) {
            double targetAzimuth = startAzimuth;
            for (int i = 0; i <= (360/15); i++) {
                double reduceFactor = RenderingCalculatorImpl.calculateReduceFactor(Math.toRadians(startAzimuth), Math.toRadians(targetAzimuth));
                System.out.println("Start Azimuth: " + startAzimuth + " Target Azimuth " + targetAzimuth + " Reduce Factor " + reduceFactor);
                if (i == 12) {
                    //Here this is infinity
                } else if (i == 0 || i == 24) {
                    Assert.assertTrue(Math.round(reduceFactor * 100000) == Math.round(targets[i] * 100000));
                }
                targetAzimuth = (targetAzimuth + 15)%360;
            }
        }
    }

}
