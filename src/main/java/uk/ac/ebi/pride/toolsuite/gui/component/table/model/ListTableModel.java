package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * List based table model, it also keeps track of column name and types.
 * <p/>
 * @author rwang
 * @author ypriverol
 */
public abstract class ListTableModel<T> extends AbstractTableModel {
    protected final Map<String, String> columnNames;
    protected final List<Object> contents;

    public ListTableModel() {
        columnNames = new LinkedHashMap<>();
        contents = new ArrayList<>();
        initializeTableModel();
    }

    /**
     * This method should be implemented to columns
     * and basic rows of the table
     */
    public abstract void initializeTableModel();

    /**
     * Add new data into contents.
     *
     * @param newData should be the object returned from long running task
     * @return boolean true if the new data has been added
     */
    public abstract void addData(T newData);

    /**
     * Add an extra column to table model
     *
     * @param columnName column string name
     * @param toolTip    column tool tips
     */
    public void addColumn(String columnName, String toolTip) {
        columnNames.put(columnName, toolTip);
        fireTableStructureChanged();
    }

    /**
     * Remove all the columns
     */
    public void removeAllColumns() {
        columnNames.clear();
    }

    /**
     * Add an extra row of data to table model
     *
     * @param content a list of data objects
     */
    public void addRow(Object content) {
        contents.add(content);
    }

    public void removeRow(Object content){
        contents.remove(content);
    }

    public int getRowPostion( Object content){
        return contents.indexOf(content);
    }

    /**
     * Add the row data using a given row number
     *
     * @param rowNum row number
     * @return  Object    row data
     */
    public Object getRow(int rowNum) {
        int rowCnt = contents.size();

        if (rowNum >= 0 && rowNum <= rowCnt) {
            return contents.get(rowNum);
        }

        return  null;
    }

    public void removeAllRows() {
        int rowCnt = contents.size();
        if (rowCnt > 0) {
            contents.clear();
            fireTableRowsDeleted(0, rowCnt - 1);
        }
    }

    @Override
    public int getRowCount() {
        return contents.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    /**
     * Get the index of the column
     *
     * @param header column title
     * @return int  index of the column in int
     */
    public int getColumnIndex(String header) {
        int index = -1;

        List<Map.Entry<String, String>> entries = new LinkedList<>(columnNames.entrySet());

        for (Map.Entry<String, String> entry : entries) {
            if (entry.getKey().equals(header)) {
                index = entries.indexOf(entry);
            }
        }

        return index;
    }

    public String getColumnName(int index) {
        String columnName = null;

        List<Map.Entry<String, String>> entries = new LinkedList<>(columnNames.entrySet());
        Map.Entry<String, String> entry = entries.get(index);
        if (entry != null) {
            columnName = entry.getKey();
        }

        return columnName;
    }

    public String getColumnTooltip(int index) {
        String tooltip = null;

        List<Map.Entry<String, String>> entries = new LinkedList<>(columnNames.entrySet());
        Map.Entry<String, String> entry = entries.get(index);
        if (entry != null) {
            tooltip = entry.getValue();
        }

        return tooltip;
    }
//
//
//    @Override
//    public Object getValueAt(int rowIndex, int columnIndex) {
//        Object result = null;
//
//        if (!contents.isEmpty() && rowIndex >= 0 && columnIndex >= 0) {
//            List<Object> colValues = contents.get(rowIndex);
//            if (colValues != null) {
//                result = colValues.get(columnIndex);
//            }
//        }
//
//        return result;
//    }
//
//    @Override
//    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
//        if (!contents.isEmpty() && rowIndex >= 0 && columnIndex >= 0) {
//            List<Object> colValues = contents.get(rowIndex);
//            if (colValues != null) {
//                colValues.set(columnIndex, aValue);
//                fireTableCellUpdated(rowIndex, columnIndex);
//            }
//        }
//    }
}
