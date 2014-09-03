package uk.ac.ebi.pride.toolsuite.gui.component.mzgraph;

import uk.ac.ebi.pride.utilities.data.core.Parameter;
import uk.ac.ebi.pride.utilities.data.utils.CollectionUtils;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.Collection;

/**
 * User: rwang
 * Date: 25-May-2010
 * Time: 09:56:29
 */
public class PropertyTable extends JTable {

    public PropertyTable(Collection<Parameter> params) {
        this.setModel(new PropertyTableModel(params));
        this.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    }

    private static class PropertyTableModel extends AbstractTableModel {

        public Collection<Parameter> params = null;

        private PropertyTableModel(Collection<Parameter> params) {
            this.params = params;
        }

        @Override
        public int getRowCount() {
            return params.size();
        }

        @Override
        public int getColumnCount() {
            // only two columns
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Object result = null;
            Parameter param = CollectionUtils.getElement(params, rowIndex);
            String name = param.getName();
            String value = param.getValue();
            if (value == null || "".equals(value.trim())) {
                value = name;
                name = Constants.PARAMETER;
            }
            if (columnIndex == 0) {
                result = name;
            } else if (columnIndex == 1) {
                result = value;
            }
            return result;
        }
    }
}
