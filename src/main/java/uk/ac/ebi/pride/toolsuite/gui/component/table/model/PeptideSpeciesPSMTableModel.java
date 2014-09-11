package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import uk.ac.ebi.pride.utilities.util.Tuple;
import uk.ac.ebi.pride.utilities.term.CvTermReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author rwang, ypriverol
 * @version $Id$
 */
public class PeptideSpeciesPSMTableModel extends AbstractPeptideTableModel {

    private final List<PeptideTableRow> allPeptideTableRows;
    private int rankingThreshold;

    public PeptideSpeciesPSMTableModel(Collection<CvTermReference> listPeptideScores, int rankingThreshold) {
        super(listPeptideScores);
        this.rankingThreshold = rankingThreshold;
        this.allPeptideTableRows = new ArrayList<PeptideTableRow>();
    }

    @Override
    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PEPTIDE_SPECIES.equals(type)) {
            addPeptideSpecies((PeptideSpecies) newData.getValue());
        } else {
            super.addData(newData);
        }
    }

    public void setRankingThreshold(int rankingThreshold) {
        if (this.rankingThreshold != rankingThreshold) {
            this.rankingThreshold = rankingThreshold;

            removeAllRows();

            showSuitablePeptideTableRows();
        }
    }

    @Override
    public void removeAllRows() {
        allPeptideTableRows.clear();
        super.removeAllRows();
    }

    /**
     * Add peptide row data
     *
     * @param peptideSpecies peptide species
     */
    private void addPeptideSpecies(PeptideSpecies peptideSpecies) {
        List<PeptideTableRow> peptideTableRowData = peptideSpecies.getPeptideTableRowData();
        allPeptideTableRows.addAll(peptideTableRowData);
        showSuitablePeptideTableRows();

    }

    private void showSuitablePeptideTableRows() {
        for (PeptideTableRow peptide : allPeptideTableRows) {
            Integer ranking = peptide.getRanking();
            if (ranking == null || ranking <= rankingThreshold) {
                addRow(peptide);
            }
        }
        fireTableDataChanged();
    }
}