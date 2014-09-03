package uk.ac.ebi.pride.toolsuite.gui.event.container;

import org.bushe.swing.event.AbstractEventServiceEvent;

/**
 * Event to trigger an export of all the spectra details
 *
 * User: rwang
 * Date: 13/06/11
 * Time: 10:42
 */
public class ExportSpectrumDetailEvent extends AbstractEventServiceEvent {

    /**
     * Default constructor
     *
     * @param source the source of the event
     */
    public ExportSpectrumDetailEvent(Object source) {
        super(source);
    }
}
