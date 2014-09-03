package uk.ac.ebi.pride.toolsuite.gui.utils;

import java.awt.*;

/**
 * Utility class to handle colour
 *
 * @author Rui Wang
 * @version $Id$
 */
public class ColourUtil {
    private ColourUtil(){}

    public static final Color ALTER_ROW_COLOUR = new Color(229, 232, 236);
    public static final Color ROW_SELECTION_BACKGROUND = new Color(193, 210, 238);
    public static final Color ROW_SELECTION_FOREGROUND = Color.black;
    public static final Color TEXT_FIELD_WARNING_COLOUR = new Color(255, 255, 153);
    public static final Color TEXT_FIELD_NORMAL_COLOUR = Color.white;
    public static final Color HYPERLINK_COLOUR = new Color(98, 146, 179);

    /**
     * Returns a color whose brightness has been scaled by the provided factor.
     * @param color The input color.
     * @param factor The scale factor.
     * @return The rescaled color.
     */
    public static Color getRescaledColor(Color color, float factor) {
        float hsbVals[] = Color.RGBtoHSB(
                color.getRed(), color.getGreen(),
                color.getBlue(), null);
        return Color.getHSBColor(
                hsbVals[0], hsbVals[1], factor * hsbVals[2]);
    }

}
