/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.sharptv.internal.discovery;

import static org.openhab.binding.sharptv.SharpTVBindingConstants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.smarthome.config.discovery.DiscoveryResult;
import org.eclipse.smarthome.config.discovery.DiscoveryResultBuilder;
import org.eclipse.smarthome.config.discovery.upnp.UpnpDiscoveryParticipant;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.ThingUID;
import org.jupnp.model.meta.DeviceDetails;
import org.jupnp.model.meta.RemoteDevice;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SharpTVDiscoveryParticipant} is responsible for discovering new Sharp TVs on the network.
 *
 * @author Mark Hilbush - Initial contribution
 */
@Component(service = UpnpDiscoveryParticipant.class, immediate = true)
public class SharpTVDiscoveryParticipant implements UpnpDiscoveryParticipant {
    private final Logger logger = LoggerFactory.getLogger(SharpTVDiscoveryParticipant.class);

    private Set<ThingTypeUID> supportedThingTypes = SUPPORTED_THING_TYPES_UIDS;

    @Override
    public Set<ThingTypeUID> getSupportedThingTypeUIDs() {
        return supportedThingTypes;
    }

    @Override
    public DiscoveryResult createResult(RemoteDevice device) {
        ThingUID uid = getThingUID(device);
        if (uid != null) {
            DeviceDetails details = device.getDetails();
            Map<String, Object> properties = new HashMap<>(3);
            properties.put("ipAddress", device.getIdentity().getDescriptorURL().getHost());
            properties.put("friendlyName", details.getFriendlyName());

            logger.debug("Adding inbox entry for Sharp TV at IP {}", device.getIdentity().getDescriptorURL().getHost());

            return DiscoveryResultBuilder.create(uid).withProperties(properties).withLabel(details.getFriendlyName())
                    .build();
        }
        return null;
    }

    @Override
    public ThingUID getThingUID(RemoteDevice device) {
        DeviceDetails details = device.getDetails();
        String friendlyName = details.getFriendlyName().toUpperCase();

        logger.debug("Discovered a uPnP device {} at {}", friendlyName, device.getIdentity().getDescriptorURL());

        if (friendlyName != null && isSharpTV(friendlyName)) {
            logger.debug("Device is a Sharp TV named {} with the following details", friendlyName);
            logger.debug("  device.identity.toString={}", device.getIdentity().toString());
            logger.debug("  ---------------------------------------------");

            logger.debug("  device.displayDetails={}", device.getDisplayString());
            logger.debug("  device.embeddedDevices={}", device.getEmbeddedDevices().toString());
            logger.debug("  ---------------------------------------------");

            logger.debug("  device.identity.localAddress={}", device.getIdentity().getDiscoveredOnLocalAddress());
            logger.debug("  device.identity.interfaceMacAddress={}", device.getIdentity().getInterfaceMacAddress());
            logger.debug("  device.identity.descriptorUrl={}", device.getIdentity().getDescriptorURL());
            logger.debug("  device.identity.descriptorUrl={}", device.getIdentity().getWakeOnLANBytes());
            logger.debug("  device.identity.udn={}", device.getIdentity().getUdn().getIdentifierString());
            logger.debug("  ---------------------------------------------");
            logger.debug("  device.root.services={}", device.getRoot().getServices().toString());
            logger.debug("  ---------------------------------------------");

            logger.debug("  device.details.friendlyName={}", details.getFriendlyName());
            logger.debug("  device.details.serialNumber={}", details.getSerialNumber());
            logger.debug("  device.details.secProductCaps={}", details.getSecProductCaps());
            logger.debug("  device.details.baseURL={}", details.getBaseURL());
            logger.debug("  device.details.dlnaCaps={}", details.getDlnaCaps());
            logger.debug("  device.details.dlnaDocs={}", details.getDlnaDocs().toString());
            logger.debug("  ---------------------------------------------");

            logger.debug("  device.details.model.name={}", details.getModelDetails().getModelName());
            logger.debug("  device.details.model.description={}", details.getModelDetails().getModelDescription());
            logger.debug("  device.details.model.number={}", details.getModelDetails().getModelNumber());
            logger.debug("  device.details.model.uri={}", details.getModelDetails().getModelURI());
            logger.debug("  ---------------------------------------------");

            logger.debug("  device.details.manufacturer.name={}", details.getManufacturerDetails().getManufacturer());
            logger.debug("  device.details.manufacturer.uri={}", details.getManufacturerDetails().getManufacturerURI());
            logger.debug("  ---------------------------------------------");

            return new ThingUID(THING_TYPE_SHARP_TV, device.getIdentity().getUdn().getIdentifierString());
        }
        return null;
    }

    private boolean isSharpTV(String friendlyName) {
        if (friendlyName.contains("SHARP")) {
            return true;
        } else if (friendlyName.contains("AQUOS")) {
            return true;
        }
        return false;
    }
}
