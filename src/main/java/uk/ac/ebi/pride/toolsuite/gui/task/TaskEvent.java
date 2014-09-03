package uk.ac.ebi.pride.toolsuite.gui.task;

import java.util.EventObject;

/**
 * User: rwang
 * Date: 22-Jan-2010
 * Time: 13:39:19
 */
public class TaskEvent<V> extends EventObject {

    private final V value;

    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public TaskEvent(Task source, V value) {
        super(source);
        this.value = value;
    }

    public V getValue() {
        return value;
    }
}
