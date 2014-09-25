package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import uk.ac.ebi.pride.utilities.util.Tuple;
import uk.ac.ebi.pride.utilities.term.CvTermReference;

import java.util.*;

/**
 * Quantitative protein table model
 * <p/>
 * @author rwang
 * @author ypriverol
 * Date: 11/08/2011
 * Time: 09:17
 */
public class QuantProteinTableModel extends AbstractProteinTableModel {

    public QuantProteinTableModel(Collection<CvTermReference> listProteinScores) {
        super(listProteinScores);
    }

    @SuppressWarnings("unchecked")
    private void updateQuantColumnHeaders(Object value) {
        // add fixed columns
        setColumnHeaders();

        List<String> hs = (List<String>)value;
        for (String h : hs) {
            columnNames.put(h, h);
        }

        fireTableStructureChanged();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        String columnName = getColumnName(columnIndex);
        if (columnName.equals(ProteinTableHeader.COMPARE.getHeader())) {
            return Boolean.class;
        } else {
            return super.getColumnClass(columnIndex);
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        String columnName = getColumnName(columnIndex);
        return columnName.equals(ProteinTableHeader.COMPARE.getHeader()) || super.isCellEditable(rowIndex, columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String columnName = getColumnName(columnIndex);
        if (columnName.equals(ProteinTableHeader.COMPARE.getHeader())) {
            ProteinTableRow proteinTableRow = (ProteinTableRow)contents.get(rowIndex);
            proteinTableRow.setComparisonState((Boolean)aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        } else {
            super.setValueAt(aValue, rowIndex, columnIndex);
        }
    }

    @Override
    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PROTEIN_QUANTITATION_HEADER.equals(type)) {
            updateQuantColumnHeaders(newData.getValue());
        } else if (TableContentType.PROTEIN_QUANTITATION.equals(type)) {
            addProteinQuantData((ProteinTableRow)newData.getValue());
        } else {
            super.addData(newData);
        }
    }

    @SuppressWarnings("unchecked")
    private void addProteinQuantData(ProteinTableRow proteinTableRow) {
        int rowCnt = this.getRowCount();
        this.addRow(proteinTableRow);
        fireTableRowsInserted(rowCnt, rowCnt);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ProteinTableRow proteinTableRow = (ProteinTableRow)contents.get(rowIndex);

        int additionalColumnIndex = getColumnIndex(ProteinTableHeader.ADDITIONAL.getHeader());

        if (columnIndex > additionalColumnIndex) {
            // quantification columns will always be at the end of the table
            List<Object> quantifications = proteinTableRow.getQuantifications();
            return quantifications.get(columnIndex - additionalColumnIndex - 1);
        } else {
            return super.getValueAt(rowIndex, columnIndex);
        }
    }
}
