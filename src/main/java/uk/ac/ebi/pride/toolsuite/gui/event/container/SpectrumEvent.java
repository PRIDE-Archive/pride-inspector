package uk.ac.ebi.pride.toolsuite.gui.event.container;

import org.bushe.swing.event.AbstractEventServiceEvent;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;

/**
 * Event triggered when a spectrum is selected
 *
 * User: rwang
 * Date: 13/06/11
 * Time: 11:21
 */
public class SpectrumEvent extends AbstractEventServiceEvent {

    private DataAccessController controller;
    private Comparable spectrumId;

    /**
     * Default constructor
     *
     * @param source the source of the event
     * @param controller    data access controller
     * @param specId    spectrum id
     */
    public SpectrumEvent(Object source, DataAccessController controller, Comparable specId) {
        super(source);
        this.controller = controller;
        this.spectrumId = specId;
    }

    public DataAccessController getController() {
        return controller;
    }

    public void setController(DataAccessController controller) {
        this.controller = controller;
    }

    public Comparable getSpectrumId() {
        return spectrumId;
    }

    public void setSpectrumId(Comparable spectrumId) {
        this.spectrumId = spectrumId;
    }
}
