/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.sunsetwx.internal.json;

/**
 * The {@link LoginSuccess} class is used to parse the SunsetWx JSON
 * message returned by the SunsetWx Login API.
 *
 * @author Mark Hilbush - Initial contribution
 */
public class LoginSuccess {
    private String message;
    private String token;
    private int token_exp_sec;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getTokenExpSec() {
        return token_exp_sec;
    }

    public void setTokenExpSec(int token_exp_sec) {
        this.token_exp_sec = token_exp_sec;
    }
}
