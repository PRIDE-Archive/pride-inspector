package uk.ac.ebi.pride.toolsuite.gui.event;

import org.bushe.swing.event.AbstractEventServiceEvent;

/**
 * Event to trigger showing a welcome pane
 *
 * User: rwang
 * Date: 01/06/11
 * Time: 11:10
 */
public class ShowWelcomePaneEvent extends AbstractEventServiceEvent {

    /**
     * Default constructor
     *
     * @param source the source of the event
     */
    public ShowWelcomePaneEvent(Object source) {
        super(source);
    }
}
