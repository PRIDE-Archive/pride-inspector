package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.bushe.swing.event.EventBus;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.report.RemovalReportMessage;
import uk.ac.ebi.pride.toolsuite.gui.component.report.SummaryReportMessage;
import uk.ac.ebi.pride.toolsuite.gui.component.startup.ControllerContentPane;
import uk.ac.ebi.pride.toolsuite.gui.component.table.filter.DecoyAccessionFilter;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideTableHeader;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProteinTableHeader;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.event.SummaryReportEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.util.regex.Pattern;

/**
 * Task to calculate decoy ratio
 *
 * User: rwang
 * Date: 16/09/2011
 * Time: 10:08
 */
public class DecoyRatioTask extends TaskAdapter<Void, Void> {
    private static final String TASK_NAME = "Calculating decoy ratio";
    private static final String TASK_DESCRIPTION = "Calculating decoy ratio for both protein and peptide";

    private DataAccessController controller;
    private DecoyAccessionFilter.Type type;
    private String criteria;

    public DecoyRatioTask(DataAccessController controller, DecoyAccessionFilter.Type type, String criteria) {
        this.controller = controller;
        this.type = type;
        this.criteria = criteria.toLowerCase();

        this.setName(TASK_NAME);
        this.setDescription(TASK_DESCRIPTION);
    }

    @Override
    protected Void doInBackground() throws Exception {
        PrideInspectorContext appContext = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        // remove previous decoy ratio
        EventBus.publish(new SummaryReportEvent(this, controller, new RemovalReportMessage(Pattern.compile("Decoy.*"))));
        // get content pane
        ControllerContentPane contentPane = (ControllerContentPane) appContext.getDataContentPane(controller);
        // protein tab
        JTable table = contentPane.getProteinTabPane().getIdentificationPane().getIdentificationTable();
        String protAccColName = ProteinTableHeader.PROTEIN_ACCESSION.getHeader();
        int index = getAccessionColumnIndex(table.getModel(), protAccColName);
        // protein decoy ratio
        String proteinDecoyRatio = calculateDecoyRatio(table.getModel(), index, type, criteria);
        String proteinDecoyMsg = "Decoy Protein Hits: " + proteinDecoyRatio;
        EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(SummaryReportMessage.Type.INFO, proteinDecoyMsg, proteinDecoyMsg)));

        // peptide tab
        table = contentPane.getPeptideTabPane().getPeptidePane().getPeptideTable();
        protAccColName = PeptideTableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
        index = getAccessionColumnIndex(table.getModel(), protAccColName);
        // peptide decoy ratio
        String peptideDecoyRatio = calculateDecoyRatio(table.getModel(), index, type, criteria);
        String peptideDecoyMsg = "Decoy Peptide Hits: " + peptideDecoyRatio;
        EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(SummaryReportMessage.Type.INFO, peptideDecoyMsg, peptideDecoyMsg)));

        return null;
    }

    /**
     * Get the index of accession column
     *
     * @param tableModel     table model
     * @param protAccColName protein accession column name
     * @return int protein accession column index
     */
    private int getAccessionColumnIndex(TableModel tableModel, String protAccColName) {
        int colCnt = tableModel.getColumnCount();
        for (int i = 0; i < colCnt; i++) {
            if (tableModel.getColumnName(i).equals(protAccColName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Calculate decoy ratio
     *
     * @param tableModel table model
     * @param colIndex   protein accession column
     * @param type
     * @param criteria
     * @return String  decoy ratio
     */
    private String calculateDecoyRatio(TableModel tableModel, int colIndex,
                                       DecoyAccessionFilter.Type type, String criteria) {
        int rowCnt = tableModel.getRowCount();
        int decoyCnt = 0;

        for (int i = 0; i < rowCnt; i++) {
            String acc = ((String) tableModel.getValueAt(i, colIndex)).toLowerCase();
            if (acc != null) {
                switch (type) {
                    case PREFIX:
                        if (acc.startsWith(criteria)) {
                            decoyCnt ++;
                        }
                        break;
                    case POSTFIX:
                        if (acc.endsWith(criteria)) {
                            decoyCnt ++;
                        }
                        break;
                    case CONTAIN:
                        if (acc.contains(criteria)) {
                            decoyCnt ++;
                        }
                        break;
                }
            }
        }

        return decoyCnt + "/"+ rowCnt;
    }
}
