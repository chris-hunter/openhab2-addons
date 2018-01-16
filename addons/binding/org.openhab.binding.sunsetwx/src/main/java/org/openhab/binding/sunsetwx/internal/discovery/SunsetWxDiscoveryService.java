/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.sunsetwx.internal.discovery;

import static org.openhab.binding.sunsetwx.SunsetWxBindingConstants.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smarthome.config.discovery.AbstractDiscoveryService;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.eclipse.smarthome.io.net.http.HttpUtil;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SunsetWxDiscoveryService} tries to automatically discover the
 * geolocation based on the Internet IP address.
 *
 * @author Mark Hilbush - Initial contribution
 */
@Component(service = DiscoveryService.class, immediate = true)
public class SunsetWxDiscoveryService extends AbstractDiscoveryService {
    private final Logger logger = LoggerFactory.getLogger(SunsetWxDiscoveryService.class);

    /**
     * Creates a SunsetWxDiscoveryService with disabled autostart.
     */
    public SunsetWxDiscoveryService() {
        super(SUPPORTED_THING_TYPES_UIDS, 0, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void startScan() {
        logger.info("Starting SunsetWx discovery scan");

        String geoLocation;
        String result;
        try {
            result = HttpUtil.executeUrl("GET", "http://ip-api.com/json/?fields=lat,lon", 5000);
        } catch (IOException e) {
            result = null;
        }

        if (result != null) {
            String lat = StringUtils.trim(StringUtils.substringBetween(result, "\"lat\":", ","));
            String lon = StringUtils.trim(StringUtils.substringBetween(result, "\"lon\":", "}"));

            try {
                Double latitude = Double.parseDouble(lat);
                Double longitude = Double.parseDouble(lon);
                logger.info("Evaluated geolocation: longitude: {}, latitude: {}", longitude, latitude);

                geoLocation = String.format("%s,%s", longitude.toString(), latitude.toString());
                logger.debug("SunsetWx propGeolocation: {}", geoLocation);
            } catch (Exception ex) {
                geoLocation = DEFAULT_COORDINATES;
                logger.warn("Can't discover geolocation, using defaults of {}", geoLocation);
            }
        } else {
            geoLocation = DEFAULT_COORDINATES;
            logger.warn("Can't discover geolocation, using defaults of {}", geoLocation);
        }

        ThingUID sunriseThing = new ThingUID(THING_TYPE_SUNRISE, "local");
        ThingUID sunsetThing = new ThingUID(THING_TYPE_SUNSET, "local");

        Map<String, Object> properties = new HashMap<>();
        properties.put(THING_PROPERTY_GEOLOCATION, geoLocation);

        // Create sunrise thing
        thingDiscovered(DiscoveryResultBuilder.create(sunriseThing).withLabel(THING_SUNRISE_LABEL)
                .withProperties(properties).build());

        // Create sunset thing
        thingDiscovered(DiscoveryResultBuilder.create(sunsetThing).withLabel(THING_SUNSET_LABEL)
                .withProperties(properties).build());
    }
}
