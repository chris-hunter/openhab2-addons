/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.sunsetwx.internal.model;

/**
 *
 * @author Mark Hilbush - Initial contribution
 */
public class RunningStats {

    private int count = 0;
    private double average = 0.0;
    private double pwrSumAvg = 0.0;
    private double stdDev = 0.0;

    /**
     * Add another value and calculate average and standard deviation
     */
    public void put(double value) {
        count++;
        average += (value - average) / count;
        pwrSumAvg += (value * value - pwrSumAvg) / count;
        stdDev = Math.sqrt((pwrSumAvg * count - count * average * average) / (count - 1));
    }

    public double getAverage() {
        return average;
    }

    public double getStandardDeviation() {
        return Double.isNaN(stdDev) ? 0.0 : stdDev;
    }

}
