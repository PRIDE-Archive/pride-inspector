package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import java.util.List;

/**
 * @author Rui Wang
 * @version $Id$
 */
public abstract class ListBasedListTableModel<T> extends ListTableModel<T> {
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object result = null;

        if (!contents.isEmpty() && rowIndex >= 0 && columnIndex >= 0) {
            List<Object> colValues = (List<Object>)contents.get(rowIndex);
            if (colValues != null) {
                result = colValues.get(columnIndex);
            }
        }

        return result;
    }
}
