/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.sharptv.internal;

import static org.openhab.binding.sharptv.SharpTVBindingConstants.SUPPORTED_THING_TYPES_UIDS;

import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.openhab.binding.sharptv.handler.SharpTVHandler;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link SharpTVHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Mark Hilbush - Initial contribution
 */
@Component(service = ThingHandlerFactory.class, configurationPid = "binding.sharptv", configurationPolicy = ConfigurationPolicy.OPTIONAL)
public class SharpTVHandlerFactory extends BaseThingHandlerFactory {
    private Logger logger = LoggerFactory.getLogger(SharpTVHandlerFactory.class);

    @Override
    protected void activate(ComponentContext componentContext) {
        super.activate(componentContext);
        logger.info("SharpTV binding v{}", bundleContext.getBundle().getVersion());
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {
        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (SUPPORTED_THING_TYPES_UIDS.contains(thingTypeUID)) {
            return new SharpTVHandler(thing);
        }
        return null;
    }
}
