package uk.ac.ebi.pride.toolsuite.gui.event.container;

import org.bushe.swing.event.AbstractEventServiceEvent;

/**
 * This event indicates a load next batch request.
 *
 * User: rwang
 * Date: 13/06/11
 * Time: 11:01
 */
public class LoadBatchEvent extends AbstractEventServiceEvent {
    public enum Type {NEXT, ALL}

    private Type type;

    /**
     * Default constructor
     *
     * @param source the source of the event
     * @param type the type of load batch
     */
    public LoadBatchEvent(Object source, Type type) {
        super(source);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
