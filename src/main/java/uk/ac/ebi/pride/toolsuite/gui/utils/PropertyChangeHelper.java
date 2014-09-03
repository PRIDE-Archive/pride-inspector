package uk.ac.ebi.pride.toolsuite.gui.utils;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * This is a adapter class on PropertyChangeSupport with improved thread support
 * User: rwang
 * Date: 21-Jan-2010
 * Time: 11:44:29
 */
public class PropertyChangeHelper implements PropertyChangeHandler {

    private final PropertyChangeSupport supporter;

    public PropertyChangeHelper() {
        supporter = new ImprovedPropertyChangerSupport(this);
    }

    public PropertyChangeHelper(Object source) {
        supporter = new ImprovedPropertyChangerSupport(source);
    }

    /**
     * Check whether property change listener exists
     *
     * @param listener property change listener
     * @return boolean  true if exists
     */
    public boolean hasPropertyChangeListener(PropertyChangeListener listener) {
        for (PropertyChangeListener propertyChangeListener : supporter.getPropertyChangeListeners()) {
            if (propertyChangeListener.equals(listener)) {
                return true;
            }
        }
        return false;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        supporter.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propName, PropertyChangeListener listener) {
        supporter.addPropertyChangeListener(propName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        supporter.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propName, PropertyChangeListener listener) {
        supporter.removePropertyChangeListener(propName, listener);
    }

    public void removeAllPropertyChangeListeners() {
        PropertyChangeListener[] listeners = supporter.getPropertyChangeListeners();
        for (PropertyChangeListener listener : listeners) {
            removePropertyChangeListener(listener);
        }
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return supporter.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propName) {
        return supporter.getPropertyChangeListeners(propName);
    }

    public void firePropertyChange(final PropertyChangeEvent event) {
        supporter.firePropertyChange(event);
    }

    public void firePropertyChange(String propName, Object oldValue, Object newValue) {
        supporter.firePropertyChange(propName, oldValue, newValue);
    }

    private class ImprovedPropertyChangerSupport extends PropertyChangeSupport {
        public ImprovedPropertyChangerSupport(Object o) {
            super(o);
        }

        @Override
        public void firePropertyChange(final PropertyChangeEvent event) {
            if (SwingUtilities.isEventDispatchThread()) {
                super.firePropertyChange(event);
            } else {
                Runnable eventDispatcher = new Runnable() {
                    public void run() {
                        firePropertyChange(event);
                    }
                };
                EventQueue.invokeLater(eventDispatcher);
            }
        }
    }
}
