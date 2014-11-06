package uk.ac.ebi.pride.toolsuite.gui.component.utils;

/**
 * Created by ilias on 02/05/14 for.detectOS project
 */
public final class OSDetector {
    public static final String os = System.getProperty("os.name").toLowerCase();
    public static final String arch = System.getProperty("os.arch");

    public enum OS {
        LINUX_64,
        LINUX_32,
        MAC,
        WINDOWS,
        UNSUPPORTED
    }

    public static OS getOS() {
        if (os.contains("linux")) {
            if (arch.contains("amd64")) {
                return OS.LINUX_64;
            } else {
                return OS.LINUX_32;
            }
        } else if (os.contains("mac")) {
            return OS.MAC;
        } else if (os.contains("win")) {
            return OS.WINDOWS;
        } else {
            return OS.UNSUPPORTED;
        }
    }
}
