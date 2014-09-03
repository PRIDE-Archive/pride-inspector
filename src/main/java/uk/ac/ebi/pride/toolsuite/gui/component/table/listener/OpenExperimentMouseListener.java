package uk.ac.ebi.pride.toolsuite.gui.component.table.listener;

import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.OpenPrideDatabaseTask;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * OpenExperimentMouseListener listens to any mouse click on the view button within the database search pane
 *
 *
 * <p/>
 * User: rwang
 * Date: 06/07/2011
 * Time: 10:56
 */
public class OpenExperimentMouseListener extends MouseAdapter {
    private JTable table;
    private String columnHeader;

    public OpenExperimentMouseListener(JTable table, String columnHeader) {
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
                OpenPrideDatabaseTask task = new OpenPrideDatabaseTask(val.toString());
                TaskUtil.startBackgroundTask(task);
            }
        }
    }
}
