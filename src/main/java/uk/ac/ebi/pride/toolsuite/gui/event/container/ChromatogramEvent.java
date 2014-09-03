package uk.ac.ebi.pride.toolsuite.gui.event.container;

import org.bushe.swing.event.AbstractEventServiceEvent;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;

/**
 * Event for select a chromatogram
 * User: rwang
 * Date: 10/06/11
 * Time: 16:11
 */
public class ChromatogramEvent extends AbstractEventServiceEvent {
    private DataAccessController controller;
    private Comparable chromatogramId;

    /**
     * Default constructor
     *
     * @param source the source of the event
     */
    public ChromatogramEvent(Object source, DataAccessController controller, Comparable chromaId) {
        super(source);
        this.chromatogramId = chromaId;
        this.controller = controller;
    }

    public Comparable getChromatogramId() {
        return chromatogramId;
    }

    public void setChromatogramId(Comparable chromaId) {
        this.chromatogramId = chromaId;
    }

    public DataAccessController getController() {
        return controller;
    }

    public void setController(DataAccessController controller) {
        this.controller = controller;
    }
}
