package uk.ac.ebi.pride.toolsuite.gui.component.report;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.event.SummaryReportEvent;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * List model for report list
 * <p/>
 * User: rwang
 * Date: 07/06/11
 * Time: 15:14
 */
public class ReportListModel extends DefaultListModel {

    private DataAccessController source;


    public ReportListModel(DataAccessController source) {
        this.source = source;
        // enable annotation
        AnnotationProcessor.process(this);
    }

    @EventSubscriber(eventClass = SummaryReportEvent.class)
    public void onSummaryReportEvent(SummaryReportEvent evt) {
        DataAccessController controller = evt.getDataSource();
        if (source == controller) {
            ReportMessage msg = evt.getMessage();
            if (msg instanceof SummaryReportMessage) {
                addElement(msg);
            } else if (msg instanceof RemovalReportMessage) {
                int size = size();
                // get the message to be removed
                Pattern pattern = ((RemovalReportMessage) msg).getPattern();
                List<SummaryReportMessage> messageToRemove = new ArrayList<SummaryReportMessage>();
                for (int i = 0; i < size; i++) {
                    SummaryReportMessage currMsg = (SummaryReportMessage)getElementAt(i);
                    String message = currMsg.getMessage();
                    Matcher m = pattern.matcher(message);
                    if (m.matches()) {
                        messageToRemove.add(currMsg);
                    }
                }

                // remove message
                for (SummaryReportMessage reportMessage : messageToRemove) {
                    removeElement(reportMessage);
                }
            }
        }
    }
}
