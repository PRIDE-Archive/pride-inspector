package uk.ac.ebi.pride.toolsuite.gui.utils;

/**
 * Check the local Java version
 *
 * @author Rui Wang
 * @version $Id$
 */
public enum JavaVersion {
    VERSION_1,
    VERSION_2,
    VERSION_3,
    VERSION_4,
    VERSION_5,
    VERSION_6,
    VERSION_7,
    VERSION_UNKNOWN;

    public static JavaVersion getVersion() {
        String version = System.getProperty("java.version");
        if (version.startsWith("1.7")) {
            return VERSION_7;
        } else if (version.startsWith("1.6")) {
            return VERSION_6;
        } else if (version.startsWith("1.5")) {
            return VERSION_5;
        } else if (version.startsWith("1.4")) {
            return VERSION_4;
        } else if (version.startsWith("1.3")) {
            return VERSION_3;
        } else if (version.startsWith("1.2")) {
            return VERSION_2;
        } else if (version.startsWith("1.1")) {
            return VERSION_1;
        } else {
            return VERSION_UNKNOWN;
        }
    }
}
