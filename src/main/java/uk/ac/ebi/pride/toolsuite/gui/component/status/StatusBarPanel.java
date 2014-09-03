package uk.ac.ebi.pride.toolsuite.gui.component.status;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: rwang
 * Date: 17-Feb-2010
 * Time: 08:53:05
 */
public abstract class StatusBarPanel extends JPanel implements PropertyChangeListener {

    private int panelWidth;
    private boolean isFixedWidth;

    public StatusBarPanel() {
        this(-1, false);
    }

    public StatusBarPanel(int width, boolean fixed) {
        super();
        this.panelWidth = width < -1 ? -1 : width;
        this.isFixedWidth = fixed;
    }

    public int getPanelWidth() {
        return panelWidth;
    }

    public void setPanelWidth(int panelWidth) {
        this.panelWidth = panelWidth;
    }

    public boolean isFixedWidth() {
        return isFixedWidth;
    }

    public void setFixedWidth(boolean fixedWidth) {
        isFixedWidth = fixedWidth;
    }

    @Override
    public abstract void propertyChange(PropertyChangeEvent evt);

}
