package uk.ac.ebi.pride.toolsuite.gui.event;

import org.bushe.swing.event.AbstractEventServiceEvent;

/**
 * Indicates a database search event has been triggered, this will result showing the database search pane
 *
 * User: rwang
 * Date: 27/05/11
 * Time: 15:03
 */
public class DatabaseSearchEvent<T> extends AbstractEventServiceEvent{
    public static enum Status {SHOW,
                               START,
                               RESULT,
                               COMPLETE,
                               HIDE}

    private Status status;
    private T result;

    public DatabaseSearchEvent(Status status) {
        this(null, status, null);
    }

    public DatabaseSearchEvent(Object source, Status status) {
        this(source, status, null);
    }

    /**
     * Default constructor
     *
     * @param source the source of the event
     * @param status status of the event
     * @param result search result
     */
    public DatabaseSearchEvent(Object source, Status status, T result) {
        super(source);
        setStatus(status);
        setResult(result);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
