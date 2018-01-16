/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.sunsetwx.internal.json;

import java.util.Date;

/**
 * The {@link QualityFeature} class is used to parse the SunsetWx JSON message returned by the SunsetWx API.
 *
 * @author Mark Hilbush - Initial contribution
 */
public class QualityProperties {
    private String type;
    private String quality;
    private double quality_percent;
    private double quality_value;
    private double real_humidity;
    private double high_clouds;
    private double vertical_vel;
    private double pressure_tend;
    private Date last_updated;
    private Date imported_at;
    private Date valid_at;
    private String source;
    private double distance;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public double getQualityPercent() {
        return quality_percent;
    }

    public void setQualityPercent(double quality_percent) {
        this.quality_percent = quality_percent;
    }

    public double getQualityValue() {
        return quality_value;
    }

    public void setQualityValue(double quality_value) {
        this.quality_value = quality_value;
    }

    public double getRealHumidity() {
        return real_humidity;
    }

    public void setRealHumidity(double real_humidity) {
        this.real_humidity = real_humidity;
    }

    public double getHighClouds() {
        return high_clouds;
    }

    public void setHighClouds(double high_clouds) {
        this.high_clouds = high_clouds;
    }

    public double getVerticalVel() {
        return vertical_vel;
    }

    public void setVerticalVel(double vertical_vel) {
        this.vertical_vel = vertical_vel;
    }

    public double getPressureTend() {
        return pressure_tend;
    }

    public void setPressureTend(double pressure_tend) {
        this.pressure_tend = pressure_tend;
    }

    public Date getLastUpdated() {
        return last_updated;
    }

    public void setLastUpdated(Date last_updated) {
        this.last_updated = last_updated;
    }

    public Date getImportedAt() {
        return imported_at;
    }

    public void setImportedAt(Date imported_at) {
        this.imported_at = imported_at;
    }

    public Date getValidAt() {
        return valid_at;
    }

    public void setValidAt(Date valid_at) {
        this.valid_at = valid_at;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
