package uk.ac.ebi.pride.toolsuite.gui.utils;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class DataTransferPort {
    public enum Type {
        TCP, UDP
    }

    private final Type type;
    private final int port;

    public DataTransferPort(Type type, int port) {
        this.type = type;
        this.port = port;
    }

    public Type getType() {
        return type;
    }

    public int getPort() {
        return port;
    }
}
