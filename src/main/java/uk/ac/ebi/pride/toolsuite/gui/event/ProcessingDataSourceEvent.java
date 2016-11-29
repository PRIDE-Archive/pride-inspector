package uk.ac.ebi.pride.toolsuite.gui.event;

import org.bushe.swing.event.AbstractEventServiceEvent;

/**
 * Created with IntelliJ IDEA.
 * User: ypriverol
 * Date: 4/30/14
 * Time: 4:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessingDataSourceEvent <T> extends AbstractEventServiceEvent {

    public enum Status {
        IDENTIFICATION_READING,
        CHART_GENERATION,
        SPECTRA_READING,
        INIT_LOADING
    }

    private T DataSource;

    private Status status;

    /**
     * Default constructor
     *
     * @param source the source of the event
     * @param status    the status of the event
     */
    public ProcessingDataSourceEvent(Object source, Status status, T controller) {
        super(source);
        setStatus(status);
        setDataSource(controller);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public T getDataSource() {
        return DataSource;
    }

    public void setDataSource(T dataSource) {
        DataSource = dataSource;
    }
}
