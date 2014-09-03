package uk.ac.ebi.pride.toolsuite.gui.event;

import org.bushe.swing.event.AbstractEventServiceEvent;

import java.util.Collection;

/**
 * Event to trigger when a data source has been removed
 *
 * User: rwang
 * Date: 27/05/11
 * Time: 11:24
 */
public class RemoveDataSourceEvent<T> extends AbstractEventServiceEvent {
    private Collection<T> oldDataSources;
    private Collection<T> newDataSources;

    /**
     * Default constructor
     *
     * @param source the source of the event
     * @param oldDataSources    old collection of data sources
     * @param newDataSources    new collection of data sources
     */
    public RemoveDataSourceEvent(Object source,
                                 Collection<T> oldDataSources,
                                 Collection<T> newDataSources) {
        super(source);
        setOldDataSources(oldDataSources);
        setNewDataSources(newDataSources);
    }

    public Collection<T> getOldDataSources() {
        return oldDataSources;
    }

    public void setOldDataSources(Collection<T> oldDataSources) {
        this.oldDataSources = oldDataSources;
    }

    public Collection<T> getNewDataSources() {
        return newDataSources;
    }

    public void setNewDataSources(Collection<T> newDataSources) {
        this.newDataSources = newDataSources;
    }
}
