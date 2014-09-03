package uk.ac.ebi.pride.toolsuite.gui.component.balloontip;

import net.java.balloontip.BalloonTip;

import javax.swing.*;

/**
 * @author ypriverol
 * @author rwang
 */
public class TaskBalloon extends BalloonTip{

    String message = "PRIDE Message";
    String link    = "http://www.ebi.ac.uk/archive";
    final static int TOOLTIP_HIDE_DELAY = 300;   // 0.3s
    final static int TOOLTIP_SHOW_DELAY = 1000;  // 1.0s

    public TaskBalloon(JComponent attachedComponent, String text, String message, String link) {
        super(attachedComponent, text);
        this.message = message;
        this.link = link;
    }
}
