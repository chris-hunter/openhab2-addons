/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.sharptv.handler;

import static org.openhab.binding.sharptv.SharpTVBindingConstants.*;

import java.io.IOException;

import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.IncreaseDecreaseType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.openhab.binding.sharptv.internal.SharpTVConfig;
import org.openhab.binding.sharptv.internal.hardware.SharpTVProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SharpTVHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Mark Hilbush - Initial contribution
 */
public class SharpTVHandler extends BaseThingHandler {
    private Logger logger = LoggerFactory.getLogger(SharpTVHandler.class);

    private SharpTVConfig config;
    private String ipAddress;
    private int port;
    private String user;
    private String password;

    private SharpTVProxy proxy;

    private static final int MAX_VOLUME = 100;

    public SharpTVHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            return;
        }

        logger.debug("Handle command {} on {} channel", command, channelUID);
        try {
            if (channelUID.getId().equals(CHANNEL_POWER)) {
                handlePower(command);
            } else if (channelUID.getId().equals(CHANNEL_VOLUME)) {
                handleVolume(command);
            } else if (channelUID.getId().equals(CHANNEL_MUTE)) {
                handleVolumeMute(command);
            } else if (channelUID.getId().equals(CHANNEL_CHANNEL)) {
                handleChannel(command);
            } else if (channelUID.getId().equals(CHANNEL_INPUT)) {
                handleInput(command);
            } else if (channelUID.getId().equals(CHANNEL_AVMODE)) {
                handleAvMode(command);
            } else if (channelUID.getId().equals(CHANNEL_SLEEP_TIMER)) {
                handleSleepTimer(command);
            } else if (channelUID.getId().equals(CHANNEL_DISABLE_ECO)) {
                handleDisableEco(command);
            }
        } catch (IOException e) {
            logger.debug("IOException handling command: {}", command);
        }
    }

    private void handlePower(Command command) throws IOException {
        logger.debug("Handling power command: {}", command);
        if (command instanceof OnOffType) {
            if (((OnOffType) command).equals(OnOffType.ON)) {
                proxy.setPower(true);
            } else if (((OnOffType) command).equals(OnOffType.OFF)) {
                proxy.setPower(false);
            }
        }
    }

    private void handleVolume(Command command) throws IOException {
        if (command instanceof PercentType) {
            logger.debug("Handling volume command, set volume to {}", command);
            // Set value from 0 to 100 percent
            double percent = .01f * Integer.parseInt(command.toString());
            proxy.setVolume((int) (MAX_VOLUME * percent));
        } else if (command instanceof IncreaseDecreaseType) {
            logger.debug("Handling volume increase/decrease");
            if (((IncreaseDecreaseType) command).equals(IncreaseDecreaseType.INCREASE)) {
                proxy.volumeUp();
            } else if (((IncreaseDecreaseType) command).equals(IncreaseDecreaseType.DECREASE)) {
                proxy.volumeDown();
            }
        }
    }

    private void handleVolumeMute(Command command) throws IOException {
        if (command instanceof OnOffType) {
            if (((OnOffType) command).equals(OnOffType.ON)) {
                proxy.setMute(true);
            } else if (((OnOffType) command).equals(OnOffType.OFF)) {
                proxy.setMute(false);
            }
        }
    }

    private void handleChannel(Command command) throws IOException {
        if (command instanceof DecimalType) {
            logger.debug("Handling channel command, set channel to {}", command);
            proxy.setChannelAnalog(0);
        } else if (command instanceof IncreaseDecreaseType) {
            logger.debug("Handling channel increase/decrease");
            if (((IncreaseDecreaseType) command).equals(IncreaseDecreaseType.INCREASE)) {
                proxy.channelUp();
            } else if (((IncreaseDecreaseType) command).equals(IncreaseDecreaseType.DECREASE)) {
                proxy.channelDown();
            }
        }
    }

    private void handleInput(Command command) throws IOException {
        logger.debug("Handling input selection for input {}", command.toString());
        if (command instanceof DecimalType) {
            int input = Integer.parseInt(command.toString());
            if (input >= 0 && input <= 9) {
                proxy.setInput(input);
            }
        }
    }

    private void handleAvMode(Command command) throws IOException {
        logger.debug("Handling input selection for input {}", command.toString());
        if (command instanceof DecimalType) {
            int input = Integer.parseInt(command.toString());
            if (input >= 0 && input <= 999) {
                proxy.setAvMode(input);
            }
        }
    }

    private void handleSleepTimer(Command command) throws IOException {
        logger.debug("Handling sleep timer command {}", command.toString());
        if (command instanceof DecimalType) {
            int timer = Integer.parseInt(command.toString());
            if (timer >= 0 && timer <= 4) {
                proxy.setSleepTimer(timer);
            }
        }
    }

    private void handleDisableEco(Command command) throws IOException {
        logger.debug("Disabling eco mode");
        proxy.disableEco();
    }

    @Override
    public void initialize() {
        logger.debug("Handler initializing for thing {}", thingID());

        config = getConfig().as(SharpTVConfig.class);
        logger.debug("SharpTVHandler config is {}", config);

        if (!config.isValid()) {
            logger.debug("SharpTVHandler config is invalid. Check configuration of thing {}", thingID());
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.CONFIGURATION_ERROR,
                    "Invalid SharpTV config. Check configuration.");
            return;
        }
        ipAddress = config.getIpAddress();
        port = config.getPort();
        user = config.getUser();
        password = config.getPassword();

        proxy = new SharpTVProxy(ipAddress, port, user, password);

        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void dispose() {
        logger.debug("Handler disposing for thing {}", thingID());
    }

    private String thingID() {
        return thing.getUID().getId();
    }
}
