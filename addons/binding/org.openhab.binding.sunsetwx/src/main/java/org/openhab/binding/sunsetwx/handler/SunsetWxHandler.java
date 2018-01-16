/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.sunsetwx.handler;

import static org.openhab.binding.sunsetwx.SunsetWxBindingConstants.*;

import java.math.BigDecimal;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.smarthome.core.common.AbstractUID;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.openhab.binding.sunsetwx.SunsetWxBindingConstants.ModelType;
import org.openhab.binding.sunsetwx.internal.SunsetWxSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SunsetWxHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Mark Hilbush - Initial contribution
 */
public class SunsetWxHandler extends BaseThingHandler {
    private Logger logger = LoggerFactory.getLogger(SunsetWxHandler.class);

    private SunsetWxSession session;

    private ModelType modelType;
    private String configCoordinates;
    private int configLimit;
    private double configRadius;
    private String configLocation;

    private int updateFrequency;
    private ScheduledFuture<?> updateQualityJob;

    public SunsetWxHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing sunsetwx handler for {}", getThing().getUID());

        initializeType();
        initializeCoordinates();
        initializeLimit();
        initializeRadius();
        initializeLocation();
        initializeUpdateFrequency();

        session = new SunsetWxSession(this);

        scheduleUpdateQualityJob();

        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void dispose() {
        logger.debug("Disposing sunsetwx handler for {}", getThing().getUID());
        cancelUpdateQualityJob();
        session = null;
    }

    private void initializeType() {
        String[] segments = thing.getUID().toString().split(AbstractUID.SEPARATOR);
        if (segments.length >= 2) {
            modelType = ModelType.valueOf(segments[1].toUpperCase());
            logger.debug("Initializing {} with modelType={}", getThing().getUID(), modelType);
        } else {
            modelType = ModelType.SUNSET;
            logger.debug("Initializing {} with default modelType={}", getThing().getUID(), modelType);
        }
    }

    private void initializeCoordinates() {
        Object o = thing.getConfiguration().get(THING_PROPERTY_GEOLOCATION);
        if (o != null && o instanceof String) {
            configCoordinates = ((String) o).replaceAll("\\s+", "");
            logger.debug("Initializing {} with coordinates={}", getThing().getUID(), configCoordinates);
        } else {
            configCoordinates = DEFAULT_COORDINATES;
            logger.debug("Initializing {} with default coordinates={}", getThing().getUID(), configCoordinates);
        }
    }

    private void initializeLimit() {
        Object o = thing.getConfiguration().get(THING_PROPERTY_LIMIT);
        if (o != null && o instanceof BigDecimal) {
            configLimit = ((BigDecimal) o).intValue();
            logger.debug("Initializing {} with limit={}", getThing().getUID(), configLimit);
        } else {
            configLimit = DEFAULT_LIMIT;
            logger.debug("Initializing {} with default limit={}", getThing().getUID(), configLimit);
        }
    }

    private void initializeRadius() {
        Object o = thing.getConfiguration().get(THING_PROPERTY_RADIUS);
        if (o != null && o instanceof BigDecimal) {
            configRadius = ((BigDecimal) o).doubleValue();
            logger.debug("Initializing {} with radius={} km", getThing().getUID(), configRadius);
        } else {
            configRadius = DEFAULT_RADIUS;
            logger.debug("Initializing {} with default radius={} km", getThing().getUID(), configRadius);
        }
    }

    private void initializeLocation() {
        Object o = thing.getConfiguration().get(THING_PROPERTY_LOCATION);
        if (o != null && o instanceof String) {
            configLocation = ((String) o).replaceAll("\\s+", "");
            logger.debug("Initializing {} with location={}", thing.getUID().toString(), configLocation);
        } else {
            configCoordinates = DEFAULT_LOCATION;
            logger.debug("Initializing {} with default location={}", getThing().getUID(), configLocation);
        }
    }

    private void initializeUpdateFrequency() {
        Object o = thing.getConfiguration().get(THING_PROPERTY_UPDATE_FREQUENCY);
        if (o != null && o instanceof BigDecimal) {
            updateFrequency = ((BigDecimal) o).intValue();
            logger.debug("Initializing {} with updateFrequency={} minutes", getThing().getUID(), updateFrequency);
        } else {
            updateFrequency = DEFAULT_UPDATE_FREQUENCY;
            logger.debug("Initializing {} with default updateFrequency={} minutes", getThing().getUID(),
                    updateFrequency);
        }
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command == RefreshType.REFRESH) {
            return;
        }

        if (channelUID.getId().equals(CHANNEL_GET_QUALITY)) {
            logger.debug("Handle command {} on channel {} for {}", command.toString(), CHANNEL_GET_QUALITY,
                    getThing().getUID());
            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    session.sunsetWxPublishQuality();
                }
            }, 0, TimeUnit.SECONDS);
        }
    }

    @Override // Just exposing this for ChromecastStatusUpdater.
    public void updateState(String channelID, State state) {
        super.updateState(channelID, state);
    }

    @Override // Just exposing this for ChromecastStatusUpdater.
    public void updateStatus(ThingStatus status) {
        super.updateStatus(status);
    }

    @Override // Just exposing this for ChromecastStatusUpdater.
    public void updateStatus(ThingStatus status, ThingStatusDetail statusDetail, String description) {
        super.updateStatus(status, statusDetail, description);
    }

    @Override // Just exposing this for ChromecastStatusUpdater.
    public boolean isLinked(String channelId) {
        return super.isLinked(channelId);
    }

    public ModelType getModelType() {
        return modelType;
    }

    public String getCoordinates() {
        return configCoordinates;
    }

    public int getLimit() {
        return configLimit;
    }

    public double getRadius() {
        return configRadius;
    }

    public String getLocation() {
        return configLocation;
    }

    private void scheduleUpdateQualityJob() {
        logger.debug("Scheduling update {} quality job every {} minutes for {}", modelType, updateFrequency,
                getThing().getUID());
        cancelUpdateQualityJob();
        updateQualityJob = scheduler.scheduleWithFixedDelay(() -> {
            logger.debug("Executing scheduled job to update channels for {}", getThing().getUID());
            session.sunsetWxPublishQuality();
        }, 5L, updateFrequency * 60, TimeUnit.SECONDS);
    }

    private void cancelUpdateQualityJob() {
        if (updateQualityJob != null) {
            logger.debug("Canceling update {} quality job for {}", modelType, getThing().getUID());
            updateQualityJob.cancel(true);
            updateQualityJob = null;
        }
    }

}
