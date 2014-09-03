package uk.ac.ebi.pride.toolsuite.gui.event.container;

import org.bushe.swing.event.AbstractEventServiceEvent;

import javax.swing.*;

/**
 * Event to expand a given panel
 *
 * User: rwang
 * Date: 09/06/11
 * Time: 12:00
 */
public class ExpandPanelEvent extends AbstractEventServiceEvent {

    private JComponent panelToExpand;

    /**
     * Default constructor
     *
     * @param source the source of the event
     * @param panelToExpand panel to be expanded
     */
    public ExpandPanelEvent(Object source, JComponent panelToExpand) {
        super(source);
        this.panelToExpand = panelToExpand;
    }

    public JComponent getPanelToExpand() {
        return panelToExpand;
    }

    public void setPanelToExpand(JComponent panelToExpand) {
        this.panelToExpand = panelToExpand;
    }
}
