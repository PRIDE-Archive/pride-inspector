package uk.ac.ebi.pride.toolsuite.gui.event;

import org.bushe.swing.event.AbstractEventServiceEvent;

/**
 * Event to notify a reference sample change
 *
 * @author rwang
 * Date: 18/08/2011
 * Time: 12:15
 */
public class ReferenceSampleChangeEvent extends AbstractEventServiceEvent {

    private int referenceSampleIndex;

    public ReferenceSampleChangeEvent(Object source, int refSampleIndex) {
        super(source);
        this.referenceSampleIndex = refSampleIndex;
    }

    public int getReferenceSampleIndex() {
        return referenceSampleIndex;
    }

    public void setReferenceSampleIndex(int referenceSampleIndex) {
        this.referenceSampleIndex = referenceSampleIndex;
    }
}
