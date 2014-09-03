package uk.ac.ebi.pride.toolsuite.gui.component.status;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * User: rwang
 * Date: 17-Feb-2010
 * Time: 08:39:10
 */
public class StatusBar extends JToolBar {

    private StatusBarPanel[] panels;

    public StatusBar(StatusBarPanel... panels) {
        this.setFloatable(false);
        this.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 1));
        this.setBorder(BorderFactory.createEtchedBorder());
        this.panels = panels;
        for (StatusBarPanel panel : panels) {
            this.add(new Separator(new Dimension(1, 20)));
            this.add(panel);
        }
    }

    public StatusBarPanel[] getPanels() {
        return panels == null ? null : Arrays.copyOf(panels, panels.length);
    }

    public void setPanels(StatusBarPanel[] panels) {
        this.panels = (panels == null ? null : Arrays.copyOf(panels, panels.length));
    }
}
