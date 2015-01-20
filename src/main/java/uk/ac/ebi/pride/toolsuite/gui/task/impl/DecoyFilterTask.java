package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.bushe.swing.event.EventBus;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.report.RemovalReportMessage;
import uk.ac.ebi.pride.toolsuite.gui.component.startup.ControllerContentPane;
import uk.ac.ebi.pride.toolsuite.gui.component.table.filter.DecoyAccessionTableFilter;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideTableHeader;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProteinTableHeader;
import uk.ac.ebi.pride.toolsuite.gui.component.table.sorter.NumberTableRowSorter;
import uk.ac.ebi.pride.toolsuite.gui.component.table.sorttreetable.ProteinSortableTreeTable;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.event.SummaryReportEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.filter.DecoyAccessionFilter;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.regex.Pattern;

/**
 * Task to set decoy filter
 *
 * User: rwang
 * Date: 16/09/2011
 * Time: 10:27
 */
public class DecoyFilterTask extends TaskAdapter<Void, Void>{
    private static final String TASK_NAME = "Calculating decoy ratio";
    private static final String TASK_DESCRIPTION = "Calculating decoy ratio for both protein and peptide";

    private DataAccessController controller;
    private DecoyAccessionFilter filter;

    public DecoyFilterTask(DataAccessController controller, DecoyAccessionFilter filter) {
        this.controller = controller;
        this.filter = filter;

        this.setName(TASK_NAME);
        this.setDescription(TASK_DESCRIPTION);
    }

    @Override
    protected Void doInBackground() throws Exception {
        PrideInspectorContext appContext = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        // remove previous decoy ratio
        EventBus.publish(new SummaryReportEvent(this, controller, new RemovalReportMessage(Pattern.compile(".*decoy.*"))));
        // get content pane
        ControllerContentPane contentPane = (ControllerContentPane) appContext.getDataContentPane(controller);
        // protein tab
        JTable table = contentPane.getProteinTabPane().getIdentificationPane().getIdentificationTable();
        String protAccColName = ProteinTableHeader.PROTEIN_ACCESSION.getHeader();
        int index = getAccessionColumnIndex(table.getModel(), protAccColName);
        setRowFilter(table, new DecoyAccessionTableFilter(filter, index));
        // protein decoy ratio

        // peptide tab
        table = contentPane.getPeptideTabPane().getPeptidePane().getPeptideTable();
        protAccColName = PeptideTableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
        index = getAccessionColumnIndex(table.getModel(), protAccColName);
        setRowFilter(table, new DecoyAccessionTableFilter(filter, index));

        // quant tab
        if (contentPane.isQuantTabEnabled()) {
            table = contentPane.getQuantTabPane().getQuantProteinSelectionPane().getQuantProteinTable();
            protAccColName = ProteinTableHeader.PROTEIN_ACCESSION.getHeader();
            index = getAccessionColumnIndex(table.getModel(), protAccColName);
            setRowFilter(table, new DecoyAccessionTableFilter(filter, index));
        }

        return null;
    }

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
     * Set the row filter
     *
     * @param rowFilter a given row filter
     */
    private void setRowFilter(JTable table, RowFilter rowFilter) {
        if (table instanceof ProteinSortableTreeTable) {
            ((ProteinSortableTreeTable) table).setRowFilter(rowFilter);
        } else {
            // get table model
            TableModel tableModel = table.getModel();
            RowSorter rowSorter = table.getRowSorter();
            if (rowSorter == null || !(rowSorter instanceof TableRowSorter)) {
                rowSorter = new NumberTableRowSorter(tableModel);
                table.setRowSorter(rowSorter);
            }
            ((TableRowSorter) rowSorter).setRowFilter(rowFilter);
        }
    }
}
