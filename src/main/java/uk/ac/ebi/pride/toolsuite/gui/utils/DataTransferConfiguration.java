package uk.ac.ebi.pride.toolsuite.gui.utils;

/**
 * Represent a data transfer configuration
 *
 * @author Rui Wang
 * @version $Id$
 */
public class DataTransferConfiguration {



    private final DataTransferProtocol protocol;
    private final String host;
    private final DataTransferPort[] port;

    public DataTransferConfiguration(DataTransferProtocol protocol, String host, DataTransferPort ... ports) {
        this.protocol = protocol;
        this.host = host;
        this.port = ports;
    }

    public DataTransferProtocol getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public DataTransferPort[] getPort() {
        return port;
    }
}
