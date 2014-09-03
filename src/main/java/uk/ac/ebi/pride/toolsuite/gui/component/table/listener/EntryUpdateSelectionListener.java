package uk.ac.ebi.pride.toolsuite.gui.component.table.listener;

import uk.ac.ebi.pride.toolsuite.gui.EDTUtils;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

/**
 * This listener set the default selection on a table.
 * <p/>
 * User: rwang
 * Date: 28-Jul-2010
 * Time: 14:55:57
 */
public class EntryUpdateSelectionListener implements TableModelListener {
    private final JTable table;

    public EntryUpdateSelectionListener(JTable table) {
        this.table = table;
    }

    public void tableChanged(TableModelEvent e) {
        int firstRow = e.getFirstRow();
        int selectedRow = table.getSelectedRow();
        // if first row exists and no previously selected row
        if (firstRow == 0 && selectedRow < 0) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    table.getSelectionModel().setSelectionInterval(0, 0);
                }
            };

            EDTUtils.invokeLater(run);
        }
    }
}
