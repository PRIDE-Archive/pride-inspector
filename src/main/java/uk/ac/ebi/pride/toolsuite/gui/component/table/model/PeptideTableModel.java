package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import uk.ac.ebi.pride.utilities.term.SearchEngineScoreCvTermReference;
import uk.ac.ebi.pride.utilities.util.Tuple;

import java.util.Collection;

/**
 * PeptideTableModel contains all the detailed that displayed in peptide table.
 * <p/>
 * @user: rwang, ypriverol
 * Date: 14-Apr-2010
 * Time: 15:58:15
 */
public class PeptideTableModel extends AbstractPeptideTableModel {


    public PeptideTableModel(Collection<SearchEngineScoreCvTermReference> listPeptideScores) {
        super(listPeptideScores);
    }

    @Override
    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PEPTIDE.equals(type)) {
            addPeptideTableRow((PeptideTableRow) newData.getValue());
        } else {
            super.addData(newData);
        }
    }

    /**
     * Add peptide row data
     *
     * @param peptideTableRow peptide data
     */
    protected void addPeptideTableRow(PeptideTableRow peptideTableRow) {
        int rowCnt = this.getRowCount();
        this.addRow(peptideTableRow);
        fireTableRowsInserted(rowCnt, rowCnt);
    }
}
