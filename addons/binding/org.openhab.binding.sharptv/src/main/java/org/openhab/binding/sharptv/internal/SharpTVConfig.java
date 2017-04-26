/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.sharptv.internal;

import org.apache.commons.lang.StringUtils;

/**
 * The {@link SharpTVConfig} is responsible for storing the SharpTV thing configuration.
 *
 * @author Mark Hilbush - Initial contribution
 */
public class SharpTVConfig {
    private String ipAddress;
    private int port;
    private String user;
    private String password;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isValid() {
        if (!StringUtils.isNotBlank(ipAddress)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SharpTVConfig{ipAddress=" + ipAddress + ", port=" + port + ", user=" + user + ", password=XXXX" + "}";
    }
}
