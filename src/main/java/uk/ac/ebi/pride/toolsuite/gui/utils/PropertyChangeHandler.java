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

    public void addPropertyChangeListener(PropertyChangeListener listener);

    public void addPropertyChangeListener(String propName, PropertyChangeListener listener);

    public void removePropertyChangeListener(PropertyChangeListener listener);

    public void removePropertyChangeListener(String propName, PropertyChangeListener listener);

    public void removeAllPropertyChangeListeners();

    public PropertyChangeListener[] getPropertyChangeListeners();

    public PropertyChangeListener[] getPropertyChangeListeners(String propName);

    public void firePropertyChange(final PropertyChangeEvent event);

    public void firePropertyChange(String propName, Object oldValue, Object newValue);
}
