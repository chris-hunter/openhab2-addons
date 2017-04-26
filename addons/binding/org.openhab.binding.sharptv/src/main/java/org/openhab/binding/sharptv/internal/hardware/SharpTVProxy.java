/**
 * Copyright (c) 2010-2018 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.binding.sharptv.internal.hardware;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sharp TV Proxy used to control a Sharp TV using TCP
 *
 * @author Eric Thill - Original implememntation
 * @author Mark Hilbush - Adapted for openHAB 2
 */
public class SharpTVProxy {
    private Logger logger = LoggerFactory.getLogger(SharpTVProxy.class);

    private static final int BUTTON_VOL_DOWN = 32;
    private static final int BUTTON_VOL_UP = 33;

    private final String host;
    private final int port;
    private final String user;
    private final String pass;

    private static final int SOCKET_OPEN_TIMEOUT = 1500;
    private static final int SOCKET_READ_TIMEOUT = 1500;

    public SharpTVProxy(String host, int port, String user, String pass) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.pass = pass;
    }

    public void disableEco() throws IOException {
        sendCommands("RSPW2   ");
    }

    public void setPower(boolean power) throws IOException {
        if (power) {
            sendCommands("POWR1   ");
        } else {
            sendCommands("POWR0   ");
        }
    }

    public void toggleInput() throws IOException {
        sendCommands("ITGD0   ");
    }

    public void setInput(int input) throws IOException {
        if (input == 0) {
            // Input 0 is always TV on Sharp TV's and uses ITVD command
            sendCommands("ITVD0");
        } else {
            // Other inputs use IAVD command
            sendCommands("IAVD" + input);
        }
    }

    public void setAvMode(int mode) throws IOException {
        sendCommands("AVMD" + mode);
    }

    public void setVolume(int volume) throws IOException {
        sendCommands("VOLM" + volume);
    }

    public void setPositionH(int val) throws IOException {
        sendCommands("HPOS" + val);
    }

    public void setPositionV(int val) throws IOException {
        sendCommands("VPOS" + val);
    }

    public void setPositionClock(int val) throws IOException {
        sendCommands("CLCK" + val);
    }

    public void setPositionPhase(int val) throws IOException {
        sendCommands("PHSE" + val);
    }

    public void setViewMode(int mode) throws IOException {
        sendCommands("WIDE" + mode);
    }

    public void toggleMute() throws IOException {
        sendCommands("MUTE0   ");
    }

    public void setMute(boolean mute) throws IOException {
        if (mute) {
            sendCommands("MUTE1   ");
        } else {
            sendCommands("MUTE2   ");
        }
    }

    public void setSurround(int surround) throws IOException {
        sendCommands("ACSU" + surround);
    }

    public void toggleAudioSelection() throws IOException {
        sendCommands("ACHA0   ");
    }

    public void setSleepTimer(int timerMode) throws IOException {
        sendCommands("OFTM" + timerMode);
    }

    public void setChannelAnalog(int channel) throws IOException {
        sendCommands("DCCH" + channel);
    }

    public void setChannelDigitalAir(int channel) throws IOException {
        sendCommands("DA2P" + channel);
    }

    public void setChannelDigitalCableTwoPart(int u, int l) throws IOException {
        sendCommands("DC2U" + u, "DC2L" + l);
    }

    public void setChannelDigitalCableOnePart(int channel) throws IOException {
        if (channel < 10000) {
            sendCommands("DC10" + channel);
        } else {
            sendCommands("DC11" + (10000 - channel));
        }
    }

    public void channelUp() throws IOException {
        sendCommands("CHUP0   ");
    }

    public void channelDown() throws IOException {
        sendCommands("CHDW0   ");
    }

    public void toggleClosedCaption() throws IOException {
        sendCommands("CLCP0   ");
    }

    public void set3DMode(int mode) throws IOException {
        sendCommands("TDCH" + mode);
    }

    /**
     * This function is not supported on every Sharp TV Model. I believe this
     * started being supported sometime in the 2012 models. If it doesn't work
     * for you, you may have luck updating your firmware.
     *
     * @param button
     * @throws IOException
     */
    public void pressRemoteButton(int button) throws IOException {
        sendCommands("RCKY" + button);
    }

    /**
     * This function relies on press remote button functionality which is not
     * supported on every Sharp TV Model
     *
     * @throws IOException
     */
    public void volumeDown() throws IOException {
        pressRemoteButton(BUTTON_VOL_DOWN);
    }

    /**
     * This function relies on press remote button functionality which is not
     * supported on every Sharp TV Model
     *
     * @throws IOException
     */
    public void volumeUp() throws IOException {
        pressRemoteButton(BUTTON_VOL_UP);
    }

    public void sendCommands(String... commands) throws IOException {
        // Normalize commands
        for (int i = 0; i < commands.length; i++) {
            String command = commands[i];
            if (command.length() > 8) {
                commands[i] = command.substring(0, 8);
            } else if (command.length() < 8) {
                StringBuilder sb = new StringBuilder();
                sb.append(command);
                for (int j = command.length(); j < 8; j++) {
                    sb.append(" ");
                }
                commands[i] = sb.toString();
            }
        }

        Socket socket = null;
        try {
            // Create connection
            logger.debug("Opening socket to {}:{} with timeout {}", host, port, SOCKET_OPEN_TIMEOUT);
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), SOCKET_OPEN_TIMEOUT);
            socket.setSoTimeout(SOCKET_READ_TIMEOUT);
        } catch (IllegalArgumentException e) {
            logger.info("IllegalArgumentException in InetSocketAddress: {}", e.getMessage(), e);
            return;
        } catch (IOException e) {
            logger.debug("IOException opening socket to TV: {}", e.getMessage());
            socket.close();
            return;
        }
        OutputStream out = null;
        InputStream in = null;
        BufferedReader reader = null;
        try {
            String value;
            out = socket.getOutputStream();
            in = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in));

            if (StringUtils.isNotEmpty(user)) {
                logger.debug("Waiting for Username prompt");
                value = readPrompt(reader);
                logger.debug("Got: {}", value.trim());

                logger.debug("Writing user: {}", user);
                out.write((user + "\r").getBytes());
                out.flush();

                logger.debug("Waiting for Password prompt");
                value = readPrompt(reader);
                logger.debug("Got: {}", value.trim());

                logger.debug("Writing password: XXXXX");
                out.write((pass + "\r").getBytes());
                out.flush();

                logger.debug("Waiting for login success");
                value = readLine(reader);
                logger.debug("Got: {}", value.trim());
            }

            // Write commands
            for (String command : commands) {
                logger.debug("Writing command: '{}'", command);
                out.write(command.getBytes());
                // out.write("\r".getBytes());
                out.write("\r\n".getBytes());

                logger.debug("Waiting for command response");
                value = readLine(reader);
                logger.debug("Got: {}", value.trim());
            }
            out.flush();
        } catch (SocketTimeoutException e) {
            logger.debug("Timed out while waiting for data on socket: {}", e.getMessage());
        } finally {
            if (in != null) {
                logger.debug("Closing input stream");
                in.close();
            }
            if (out != null) {
                logger.debug("Closing output stream");
                out.close();
            }
            if (socket != null) {
                logger.debug("Closing socket");
                socket.close();
            }
        }
    }

    private String readLine(BufferedReader reader) throws IOException, SocketTimeoutException {
        // return readUntil(reader, '\r');
        return reader.readLine();
    }

    private String readPrompt(BufferedReader reader) throws IOException, SocketTimeoutException {
        return readUntil(reader, ':');
    }

    private String readUntil(BufferedReader reader, char c) throws IOException, SocketTimeoutException {
        StringBuilder sb = new StringBuilder();
        int val = reader.read();
        while (val != -1 && val != c) {
            sb.append((char) val);
            logger.trace("Read a character from the input stream: {}", val);
            val = reader.read();
        }
        if (val == -1) {
            throw new IOException("Unexpected end of stream");
        }
        sb.append((char) val);
        return sb.toString();
    }
}
