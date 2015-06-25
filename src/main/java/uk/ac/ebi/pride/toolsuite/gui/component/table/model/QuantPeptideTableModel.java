package uk.ac.ebi.pride.toolsuite.gui.component.table.model;


import uk.ac.ebi.pride.utilities.data.core.StudyVariable;
import uk.ac.ebi.pride.utilities.term.SearchEngineScoreCvTermReference;
import uk.ac.ebi.pride.utilities.util.Tuple;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Quantitative peptide table model
 *
 * @author: rwang, ypriverol
 * Date: 11/08/2011
 * Time: 13:34
 */
public class QuantPeptideTableModel extends AbstractPeptideTableModel {

    private Map<Comparable, uk.ac.ebi.pride.utilities.data.core.StudyVariable> studyVariables;

    public static String ABUNDANCE_HEADER = "abundance_";

    public QuantPeptideTableModel(Collection<SearchEngineScoreCvTermReference> listScores) {
        super(listScores);
    }

    public QuantPeptideTableModel(Collection<SearchEngineScoreCvTermReference> listScores, Map<Comparable, StudyVariable> studyVariables){
        super(listScores);
        this.studyVariables = studyVariables;
        updateStudyVariablesHeader();
    }


    @Override
    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PEPTIDE_QUANTITATION_HEADER.equals(type)) {
            updateQuantColumnHeaders(newData.getValue());
        } else if (TableContentType.PEPTIDE_QUANTITATION.equals(type)) {
            addPeptideData((PeptideTableRow)newData.getValue());
        } else if (TableContentType.PEPTIDE_QUANTITATION_REMOVE.equals(type)){
            removePeptideData((PeptideTableRow)newData.getValue());
        }else {
            super.addData(newData);
        }
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

    @SuppressWarnings("unchecked")
    private void updateStudyVariablesHeader(){
        setColumnHeaders();

        if(this.studyVariables != null && !studyVariables.isEmpty()){
            for(uk.ac.ebi.pride.utilities.data.core.StudyVariable studyVariable: studyVariables.values()){
                columnNames.put(studyVariable.getDescription(), studyVariable.getDescription());
            }
            for(uk.ac.ebi.pride.utilities.data.core.StudyVariable studyVariable: studyVariables.values()){
                columnNames.put(ABUNDANCE_HEADER + studyVariable.getDescription(), ABUNDANCE_HEADER + studyVariable.getDescription());
            }
            fireTableStructureChanged();
        }
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
        return columnName.equals(PeptideTableHeader.COMPARE.getHeader()) || super.isCellEditable(rowIndex, columnIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        String columnName = getColumnName(columnIndex);
        if (columnName.equals(PeptideTableHeader.COMPARE.getHeader())) {
            PeptideTableRow peptideTableRow = (PeptideTableRow)contents.get(rowIndex);
            peptideTableRow.setComparisonState((Boolean)aValue);
            fireTableCellUpdated(rowIndex, columnIndex);
        } else {
            super.setValueAt(aValue, rowIndex, columnIndex);
        }
    }

    /**
     * Add peptide row data
     *
     * @param peptideTableRow peptide table row
     */
    private void addPeptideData(PeptideTableRow peptideTableRow) {
        int rowCnt = this.getRowCount();
        this.addRow(peptideTableRow);
        fireTableRowsInserted(rowCnt, rowCnt);
    }

    private void removePeptideData(PeptideTableRow peptideTableRow){
        int rowPosition = this.getRowPostion(peptideTableRow);
        this.removeRow(peptideTableRow);
        fireTableRowsDeleted(rowPosition, rowPosition);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PeptideTableRow peptideTableRow = (PeptideTableRow)contents.get(rowIndex);

        int additionalColumnIndex = getColumnIndex(PeptideTableHeader.ADDITIONAL.getHeader());

        if (columnIndex > additionalColumnIndex) {
            // quantification columns will always be at the end of the table
            List<Object> quantifications = peptideTableRow.getQuantifications();
            return quantifications.get(columnIndex - additionalColumnIndex - 1);
        } else {
            return super.getValueAt(rowIndex, columnIndex);
        }
    }
}