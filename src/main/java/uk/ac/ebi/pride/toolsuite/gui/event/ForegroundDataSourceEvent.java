package uk.ac.ebi.pride.toolsuite.gui.event;

import org.bushe.swing.event.AbstractEventServiceEvent;

/**
 * Event triggered when a new foreground data source is set.
 *
 * User: rwang
 * Date: 27/05/11
 * Time: 11:30
 */
public class ForegroundDataSourceEvent<T> extends AbstractEventServiceEvent {
    public static enum Status {DUMMY, DUMMY_TO_DATA, DATA, EMPTY}

    private T oldForegroundDataSource, newForegroundDataSource;

    private Status status;

    private String message;

    /**
     * Default constructor
     *
     * @param source the source of the event
     * @param status    the status of the event
     * @param oldForeground previous foreground data source
     * @param newForeground new foreground data source
     */
    public ForegroundDataSourceEvent(Object source, Status status, String message,
                                     T oldForeground, T newForeground) {
        super(source);
        setStatus(status);
        setOldForegroundDataSource(oldForeground);
        setNewForegroundDataSource(newForeground);
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public T getOldForegroundDataSource() {
        return oldForegroundDataSource;
    }

    public void setOldForegroundDataSource(T oldForegroundDataSource) {
        this.oldForegroundDataSource = oldForegroundDataSource;
    }

    public T getNewForegroundDataSource() {
        return newForegroundDataSource;
    }

    public void setNewForegroundDataSource(T newForegroundDataSource) {
        this.newForegroundDataSource = newForegroundDataSource;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
