/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.sunsetwx.internal.json;

import java.util.List;

/**
 * The {@link FeatureContainer} class is used to parse the SunsetWx JSON message returned by the SunsetWx API.
 *
 * @author Mark Hilbush - Initial contribution
 */
public class FeatureContainer {
    private String type;
    public List<QualityFeature> features;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
