package uk.ac.ebi.pride.toolsuite.gui.utils;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.RoundedBalloonStyle;
import net.java.balloontip.utils.ToolTipUtils;

import javax.swing.*;
import java.awt.*;

/**
 * This utility class creates balloon tool tips
 *
 * @author Rui Wang
 * @version $Id$
 */
public class BalloonTipUtil {
    public static final int MESSAGE_ARC_WIDTH = 5;
    public static final int MESSAGE_ARC_HEIGHT = 5;
    public static final Color MESSAGE_BORDER_COLOR = Color.gray;
    public static final Color ERROR_MESSAGE_FILL_COLOUR = new Color(255, 204, 204);
    public static final Color SUCCESS_MESSAGE_FILL_COLOUR = new Color(64, 255, 160);
    public static final Color TOOLTIP_FILL_COLOUR = new Color(245, 217, 39);
    public static final Color NOTE_FILL_COLOUR = new Color(225, 240, 245);

    private BalloonTipUtil() {
    }

    /**
     * Show a error message to a balloon tool tip
     *
     * @param component component which the balloon tooltip associated
     * @param message   message can be html formatted
     * @return BalloonTip  warning tip
     */
    public static BalloonTip createErrorBalloonTip(JComponent component, String message) {
        return createErrorBalloonTip(component, new JLabel(message));
    }

    /**
     * Show a error message to a balloon tool tip
     *
     * @param component component which the balloon tooltip associated
     * @param contents  component which contents warning message
     * @return BalloonTip  warning tip
     */
    public static BalloonTip createErrorBalloonTip(JComponent component, JComponent contents) {
        return createBalloonTip(component, contents, ERROR_MESSAGE_FILL_COLOUR, BalloonTip.Orientation.LEFT_ABOVE, BalloonTip.AttachLocation.NORTH);
    }


    /**
     * Show a tooltip message using a balloon tooltip
     * @param component component which the tooltip belongs to
     * @param message   tooltip message
     */
    public static void createBalloonTooltip(JComponent component, String message) {
        BalloonTip tooltip = createBalloonTip(component, new JLabel(message), TOOLTIP_FILL_COLOUR, BalloonTip.Orientation.LEFT_ABOVE, BalloonTip.AttachLocation.NORTH);
        ToolTipUtils.balloonToToolTip(tooltip, 500, 3000);
    }

    /**
     * Show a success message using a balloon tooltip
     *
     * @param component component which the note attaches to
     * @param message   message to display
     */
    public static BalloonTip createSuccessBalloonTip(JComponent component, String message) {
        return createBalloonTip(component, new JLabel(message), SUCCESS_MESSAGE_FILL_COLOUR, BalloonTip.Orientation.LEFT_ABOVE, BalloonTip.AttachLocation.NORTH);
    }


    /**
     * Show a tooltip message using a balloon tooltip
     *
     * @param component component which the note attaches to
     * @param message   message to display
     */
    public static BalloonTip createBalloonNote(JComponent component, String message) {
        return createBalloonTip(component, new JLabel(message), NOTE_FILL_COLOUR, BalloonTip.Orientation.LEFT_ABOVE, BalloonTip.AttachLocation.NORTH);
    }


    public static BalloonTip createBalloonTip(JComponent component, JComponent contents, Color fillColour,
                                              BalloonTip.Orientation orientation, BalloonTip.AttachLocation attachLocation) {
        BalloonTipStyle tipStyle = new RoundedBalloonStyle(MESSAGE_ARC_WIDTH, MESSAGE_ARC_HEIGHT,
                fillColour, MESSAGE_BORDER_COLOR);
        return new BalloonTip(component, contents, tipStyle, orientation, attachLocation, 20, 10, false);
    }

}
