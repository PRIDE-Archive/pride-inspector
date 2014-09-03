package uk.ac.ebi.pride.toolsuite.gui.utils;

import javax.swing.border.Border;
import java.lang.reflect.Constructor;

/**
 * BorderUtil helps to create
 *
 * @author Rui Wang
 * @version $Id$
 */
public class BorderUtil {

    /**
     * Create a lowered border using reflection
     * NOTE: this method is overcome the problem with nimbus look-and-feel in Java 7, LoweredBorder is package access in Java 7
     */
    public static Border createLoweredBorder() {
        JavaVersion version = JavaVersion.getVersion();
        boolean isJava7 = JavaVersion.VERSION_7.equals(version);

        Border border = null;
        try {
            Class clazz = Class.forName(isJava7 ? "javax.swing.plaf.nimbus.LoweredBorder" : "com.sun.java.swing.plaf.nimbus.LoweredBorder");
            Constructor[] c = clazz.getDeclaredConstructors();
            c[0].setAccessible(true);

            border = (Border) c[0].newInstance();
        } catch (Exception ex) {
            // do nothing
        }

        return border;
    }
}
