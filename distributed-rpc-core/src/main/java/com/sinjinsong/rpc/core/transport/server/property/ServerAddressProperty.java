package com.sinjinsong.rpc.core.transport.server.property;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * @author sinjinsong
 * @date 2018/6/10
 */
public class ServerAddressProperty {
    public static String HOST;

    static {
        try {
            HOST = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public static final Integer PORT = Integer.valueOf(System.getProperty("port"));
}
