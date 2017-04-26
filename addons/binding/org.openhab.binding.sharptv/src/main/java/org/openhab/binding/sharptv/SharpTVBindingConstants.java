/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.sharptv;

import java.util.Collections;
import java.util.Set;

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link SharpTVBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Mark Hilbush - Initial contribution
 */
public class SharpTVBindingConstants {
    public static final String BINDING_ID = "sharptv";

    public static final ThingTypeUID THING_TYPE_SHARP_TV = new ThingTypeUID(BINDING_ID, "sharptv");

    // List of all Thing Type UIDs
    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_SHARP_TV);

    // List of all Channel ids
    public static final String CHANNEL_POWER = "power";
    public static final String CHANNEL_VOLUME = "volume";
    public static final String CHANNEL_MUTE = "mute";
    public static final String CHANNEL_CHANNEL = "channel";
    public static final String CHANNEL_INPUT = "input";
    public static final String CHANNEL_AVMODE = "av-mode";
    public static final String CHANNEL_SLEEP_TIMER = "sleep-timer";
    public static final String CHANNEL_DISABLE_ECO = "disable-eco";
}
