package uk.ac.ebi.pride.toolsuite.gui.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;

/**
 * Test a port on remote server is reachable or not
 *
 * @author Rui Wang
 * @version $Id$
 */
public final class RemotePortTester {
    private static final Logger logger = LoggerFactory.getLogger(RemotePortTester.class);

    /**
     * Test a remote TCP port
     *
     * @param host            host name
     * @param port            tcp port
     * @param timeOutInSecond timeout in second
     * @return true means connection is possible
     */
    public static boolean testTCP(String host, int port, int timeOutInSecond) {
        boolean connactable = false;

        Socket s = null;

        try {
            s = new Socket();
            s.setReuseAddress(true);
            SocketAddress sa = new InetSocketAddress(host, port);
            s.connect(sa, timeOutInSecond * 1000);
        } catch (IOException e) {
            String reason = "TCP socket test exception for host: " + host + " on port " + port;
            logger.error(reason, e);
        } finally {
            if (s != null) {
                connactable = s.isConnected();

                try {
                    s.close();
                } catch (IOException e) {
                    //do nothing
                }
            }
        }

        return connactable;
    }


    /**
     * Test a remote UDP port
     *
     * @param host            host name
     * @param port            udp port
     * @return true means connection is possible
     */
    public static boolean testUDP(String host, int port) {

        DatagramSocket s = null;

        try {
            InetAddress group = InetAddress.getByName(host);
            s = new DatagramSocket();
            String msg = "Hello";
            DatagramPacket hi = new DatagramPacket(msg.getBytes(), msg.length(), group, port);
            s.send(hi);

            return true;

        } catch (IOException e) {
            String reason = "UDP socket test exception for host: " + host + " on port " + port;
            logger.error(reason, e);
        } finally {
            if (s != null) {
                s.close();
            }
        }

        return false;
    }
}
