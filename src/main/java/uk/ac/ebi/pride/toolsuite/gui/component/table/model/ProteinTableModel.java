package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import uk.ac.ebi.pride.utilities.term.SearchEngineScoreCvTermReference;
import uk.ac.ebi.pride.utilities.util.Tuple;


import java.util.Collection;

/**
 * IdentificationTableModel stores all information to be displayed in the identification table.
 * <p/>
 * User: rwang
 * Date: 14-Apr-2010
 * Time: 15:58:04
 */
public class ProteinTableModel extends AbstractProteinTableModel {

    public ProteinTableModel(Collection<SearchEngineScoreCvTermReference> listProteinScores) {
        super(listProteinScores);
    }

    @Override
    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PROTEIN.equals(type)) {
            addProteinTableRow((ProteinTableRow)newData.getValue());
        } else {
            super.addData(newData);
        }
    }

    /**
     * Add identification detail for each row
     *
     * @param proteinTableRow protein data
     */
    private void addProteinTableRow(ProteinTableRow proteinTableRow) {
        int rowCnt = this.getRowCount();
        this.addRow(proteinTableRow);
        fireTableRowsInserted(rowCnt, rowCnt);
    }
}
