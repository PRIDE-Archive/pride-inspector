package uk.ac.ebi.pride.toolsuite.gui.event;

import org.bushe.swing.event.AbstractEventServiceEvent;

/**
 * ThrowableEvent is thrown when an problem has occurred within the system
 *
 * User: rwang
 * Date: 02/06/11
 * Time: 14:23
 */
public class ThrowableEvent extends AbstractEventServiceEvent {
    public static enum Type {INFO, WARNING, ERROR}

    private Type type;
    private String title;
    private Throwable err;

    /**
     * Default constructor
     *
     * @param source the source of the event
     * @param type  throwable type
     * @param title throwable title
     * @param err   throwable actually err
     */
    public ThrowableEvent(Object source, Type type, String title, Throwable err) {
        super(source);
        setType(type);
        setTitle(title);
        setErr(err);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Throwable getErr() {
        return err;
    }

    public void setErr(Throwable err) {
        this.err = err;
    }
}
