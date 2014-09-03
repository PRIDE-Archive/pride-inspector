package uk.ac.ebi.pride.toolsuite.gui.component.table.listener;

import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.ShowParamDialogTask;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Mouse listener to show param groups in a dialog for protein and peptide identification
 *
 * User: rwang
 * Date: 16/09/2011
 * Time: 16:25
 */
public class ShowParamsMouseListener extends MouseAdapter{
    private DataAccessController controller;
    private JTable table;
    private String columnHeader;

    public ShowParamsMouseListener(DataAccessController controller, JTable table, String columnHeader) {
        this.controller = controller;
        this.table = table;
        this.columnHeader = columnHeader;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int col = table.columnAtPoint(new Point(e.getX(), e.getY()));
        String header = table.getColumnName(col);
        if (header.equals(columnHeader)) {
            int row = table.rowAtPoint(new Point(e.getX(), e.getY()));
            TableModel tableModel = table.getModel();
            Object val = tableModel.getValueAt(table.convertRowIndexToModel(row), table.convertColumnIndexToModel(col));
            if (val != null) {
                String valStr = val.toString();
                String protId = null;
                String peptideId = null;
                if (valStr.contains(Constants.COMMA)) {
                    String[] parts = valStr.split(Constants.COMMA);
                    protId = parts[0];
                    peptideId = parts[1];
                } else {
                    protId = valStr;
                }

                ShowParamDialogTask task = new ShowParamDialogTask(controller, protId, peptideId);
                TaskUtil.startBackgroundTask(task);
            }
        }
    }
}
