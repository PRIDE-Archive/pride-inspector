package uk.ac.ebi.pride.toolsuite.gui.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

/**
 * PropertyChangeHandler provides a set of interfaces to register/remove property listeners,
 * and fire property change event.
 *
 * User: rwang
 * Date: 21-Aug-2010
 * Time: 18:02:58
 */
public interface PropertyChangeHandler extends Serializable{

    void addPropertyChangeListener(PropertyChangeListener listener);

    void addPropertyChangeListener(String propName, PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(String propName, PropertyChangeListener listener);

    void removeAllPropertyChangeListeners();

    PropertyChangeListener[] getPropertyChangeListeners();

    PropertyChangeListener[] getPropertyChangeListeners(String propName);

    void firePropertyChange(final PropertyChangeEvent event);

    void firePropertyChange(String propName, Object oldValue, Object newValue);
}
