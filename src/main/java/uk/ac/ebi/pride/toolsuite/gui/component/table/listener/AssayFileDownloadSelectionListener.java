package uk.ac.ebi.pride.toolsuite.gui.component.table.listener;

import uk.ac.ebi.pride.toolsuite.gui.component.table.model.AssayFileDownloadTableModel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class AssayFileDownloadSelectionListener extends MouseAdapter {
    private JTable table;

    public AssayFileDownloadSelectionListener(JTable table) {
        this.table = table;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());

        if (col != 0 && row >= 0 && row < table.getRowCount()) {
            Boolean selected = (Boolean) table.getValueAt(row, 0);
            AssayFileDownloadTableModel tableModel = (AssayFileDownloadTableModel) table.getModel();
            tableModel.setValueAt(!selected, row, 0);
            table.getSelectionModel().clearSelection();
        }
    }
}

