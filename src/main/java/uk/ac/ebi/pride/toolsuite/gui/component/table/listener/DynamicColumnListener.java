package uk.ac.ebi.pride.toolsuite.gui.component.table.listener;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.TableColumnExt;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * DynamicColumnListener listens to adding new columns
 * <p/>
 * This listener needs to couple with table.setAutoCreateColumnsFromModel(false)
 * If auto creation is true, then all the previous set renderer, sorters will be lost
 * <p/>
 * User: rwang
 * Date: 06/07/2011
 * Time: 09:46
 */
public class DynamicColumnListener implements TableModelListener {
    private JTable table;
    private int columnCounts;

    public DynamicColumnListener(JTable table) {
        this.table = table;
        this.columnCounts = table.getColumnCount();
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        // detect event type
        if (e.getType() == TableModelEvent.UPDATE) {
            // new columns has been added
            TableModel model = (TableModel) e.getSource();
            int newColumnCounts = model.getColumnCount();
            if (newColumnCounts > columnCounts) {

                for (int i = columnCounts; i < newColumnCounts; i++) {
                    // create a new column based on the last column
                    TableColumn column = table instanceof JXTable ? new TableColumnExt(i) : new TableColumn(i);
                    column.setHeaderValue(model.getColumnName(i));

                    // add the new column
                    TableColumnModel columnModel = table.getColumnModel();
                    columnModel.addColumn(column);
                }

                // set the new column counts
                columnCounts = newColumnCounts;
            } else {
                // reset column names
                int count = model.getColumnCount();
                for (int i = 0; i < count; i++) {
                    int viewColumn = table.convertColumnIndexToView(i);
                    if (viewColumn >= 0) {
                        TableColumn column = table.getColumnModel().getColumn(viewColumn);
                        column.setHeaderValue(model.getColumnName(i));
                    }
                }
                table.getTableHeader().repaint();
            }
        }
    }

    public JTable getTable() {
        return table;
    }

    public void setTable(JTable table) {
        this.table = table;
    }

    public int getColumnCounts() {
        return columnCounts;
    }

    public void setColumnCounts(int columnCounts) {
        this.columnCounts = columnCounts;
    }
}
