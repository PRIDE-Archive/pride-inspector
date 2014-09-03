package uk.ac.ebi.pride.toolsuite.gui.event.container;

import org.bushe.swing.event.AbstractEventServiceEvent;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;

/**
 * Event to trigger when a peptide is selected
 *
 * User: rwang
 * Date: 10/06/11
 * Time: 11:33
 */
public class PSMEvent extends AbstractEventServiceEvent {

    private final Comparable identificationId;
    private final Comparable peptideId;
    private final DataAccessController controller;

    /**
     * Default constructor
     *
     * @param source the source of the event
     * @param controller    data access controller
     * @param identId   identification id
     * @param pepId peptide id
     */
    public PSMEvent(Object source, DataAccessController controller, Comparable identId, Comparable pepId) {
        super(source);
        this.controller = controller;
        this.identificationId = identId;
        this.peptideId = pepId;
    }

    public Comparable getIdentificationId() {
        return identificationId;
    }

    public Comparable getPeptideId() {
        return peptideId;
    }

    public DataAccessController getController() {
        return controller;
    }
}
