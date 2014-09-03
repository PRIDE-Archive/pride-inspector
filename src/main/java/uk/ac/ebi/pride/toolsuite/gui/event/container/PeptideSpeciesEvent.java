package uk.ac.ebi.pride.toolsuite.gui.event.container;

import org.bushe.swing.event.AbstractEventServiceEvent;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideSpecies;

/**
 * @author ypriverol, rwang
 * @version $Id$
 */
public class PeptideSpeciesEvent extends AbstractEventServiceEvent {

    private final PeptideSpecies peptideSpecies;
    /**
     * Default constructor
     *
     * @param source the source of the event
     * @param peptideSpecies    Peptide species
     */
    public PeptideSpeciesEvent(Object source, PeptideSpecies peptideSpecies) {
        super(source);
        this.peptideSpecies = peptideSpecies;
    }

    public PeptideSpecies getPeptideSpecies() {
        return peptideSpecies;
    }
}