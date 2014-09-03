package uk.ac.ebi.pride.toolsuite.gui.component.exception;

import uk.ac.ebi.pride.toolsuite.gui.utils.PropertyChangeHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ThrowableHandler maintains a list of exceptions has been captured by PRIDE Inspector.
 *
 * User: rwang
 * Date: 16-Nov-2010
 * Time: 11:23:12
 */
public class ThrowableHandler extends PropertyChangeHelper {
    /** This property is triggered when adding a new throwable entry */
    public static final String ADD_THROWABLE_PROP = "ADD_THROWABLE_PROP";
    
    /** This property is triggered when removing a new throwable entry */
    public static final String REMOVE_THROWABLE_PROP = "REMOVE_THROWABLE_PROP";
    /**
     * A list of current exceptions
     */
    private final List<ThrowableEntry> throwables;

    public ThrowableHandler() {
        throwables = Collections.synchronizedList(new ArrayList<ThrowableEntry>());
    }

    /**
     * Return a list of all throwables.
     *
     * @return  List<ThrowableEntry> a list of throwables.
     */
    public List<ThrowableEntry> getAllThrowables() {
        return new ArrayList<ThrowableEntry>(throwables);
    }

    /**
     * Add a new ThrowableEntry
     *
     * @param entry new ThrowableEntry
     */
    public void addThrowableEntry(ThrowableEntry entry) {
        List<ThrowableEntry> oldThrowables, newThrowables;
        synchronized (throwables) {
            oldThrowables = new ArrayList<ThrowableEntry>(throwables);
            throwables.add(entry);
            newThrowables = new ArrayList<ThrowableEntry>(throwables);
        }
        // notify
        firePropertyChange(ADD_THROWABLE_PROP, oldThrowables, newThrowables);
    }

    /**
     * Remove an Throwable entry
     *
     * @param entry throwable entyr
     */
    public void removeThrowableEntry(ThrowableEntry entry) {
        List<ThrowableEntry> oldThrowables, newThrowables;
        synchronized (throwables) {
            oldThrowables = new ArrayList<ThrowableEntry>(throwables);
            throwables.remove(entry);
            newThrowables = new ArrayList<ThrowableEntry>(throwables);
        }

        // notify
        firePropertyChange(REMOVE_THROWABLE_PROP, oldThrowables, newThrowables);
    }

    /**
     * Remove all the throwable enties.
     */
    public void removeAllThrowableEntries() {
        List<ThrowableEntry> oldThrowables, newThrowables;
        synchronized (throwables) {
            oldThrowables = new ArrayList<ThrowableEntry>(throwables);
            throwables.clear();
            newThrowables = new ArrayList<ThrowableEntry>(throwables);
        }

        // notify
        firePropertyChange(REMOVE_THROWABLE_PROP, oldThrowables, newThrowables);
    }
}
