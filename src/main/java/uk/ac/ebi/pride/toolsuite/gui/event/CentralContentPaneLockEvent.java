package uk.ac.ebi.pride.toolsuite.gui.event;

import org.bushe.swing.event.AbstractEventServiceEvent;

/**
 * Event to set the lock state of the central content pane
 *
 * User: rwang
 * Date: 01/06/11
 * Time: 13:30
 */
public class CentralContentPaneLockEvent extends AbstractEventServiceEvent {

    public enum Status {LOCK, UNLOCK}

    private Status status;

    /**
     * Default constructor
     *
     * @param source the source of the event
     * @param status status of the lock
     */
    public CentralContentPaneLockEvent(Object source, Status status) {
        super(source);
        setStatus(status);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
