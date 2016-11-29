package uk.ac.ebi.pride.toolsuite.gui.event;

import org.bushe.swing.event.AbstractEventServiceEvent;


/**
 * This event is used to pass a protein, peptide or dbsequence.
 * @author ypriverol
 */
public class ProteinGroupChartEvent extends AbstractEventServiceEvent {

    public enum Type {PROTEIN,
        PEPTIDE,
        PSM,
        PEPTIDE_EDGE,
        PSM_EDGE
    }

    private Type type;

    Object source;

    Object destiny;

    public ProteinGroupChartEvent(Object source, Object destiny,Type type) {
        super(source);
        this.type = type;
        this.destiny = destiny;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public Object getController() {
        return destiny;
    }

    public void setDestiny(Object destiny) {
        this.destiny = destiny;
    }
}
