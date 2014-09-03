package uk.ac.ebi.pride.toolsuite.gui.event;

import org.bushe.swing.event.AbstractEventServiceEvent;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.component.report.ReportMessage;

/**
 * Event to trigger when a new summary report item is created
 *
 * User: rwang
 * Date: 07/06/11
 * Time: 13:41
 */
public class SummaryReportEvent extends AbstractEventServiceEvent {

    private DataAccessController dataSource;
    private ReportMessage message;

    /**
     * Default constructor
     *
     * @param source the source of the event
     * @param controller    data access controller
     * @param msg   report message
     */
    public SummaryReportEvent(Object source, DataAccessController controller, ReportMessage msg) {
        super(source);
        this.dataSource = controller;
        this.message = msg;
    }

    public DataAccessController getDataSource() {
        return dataSource;
    }

    public ReportMessage getMessage() {
        return message;
    }
}
