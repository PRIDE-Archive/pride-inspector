package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import uk.ac.ebi.pride.utilities.term.SearchEngineScoreCvTermReference;
import uk.ac.ebi.pride.utilities.util.Tuple;
import uk.ac.ebi.pride.toolsuite.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;
import uk.ac.ebi.pride.toolsuite.gui.utils.ProteinAccession;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for peptide table model
 * <p/>
 * @author rwang
 * @author ypriverol
 * Date: 24/08/2011
 * Time: 16:36
 */
public abstract class AbstractPeptideTableModel extends ProgressiveListTableModel<Void, Tuple<TableContentType, Object>> {

    protected Collection<SearchEngineScoreCvTermReference> listScores;

    AbstractPeptideTableModel(Collection<SearchEngineScoreCvTermReference> listPeptideScores) {
        this.listScores = listPeptideScores;
        initializeTableModel();
    }

    @Override
    public void initializeTableModel() {
        setColumnHeaders();
    }

    void setColumnHeaders() {
        columnNames.clear();

        // add columns for search engine scores
        PeptideTableHeader[] headers = PeptideTableHeader.values();
        for (PeptideTableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
            if (listScores != null && PeptideTableHeader.NUMBER_OF_FRAGMENT_IONS_COLUMN.getHeader().equals(header.getHeader())) {
                for (SearchEngineScoreCvTermReference scoreCvTerm : listScores) {
                    String name = scoreCvTerm.getName();
                    columnNames.put(name, name);
                }
            }
        }
    }

    @Override
    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PROTEIN_DETAILS.equals(type)) {
            addProteinDetails(newData.getValue());
        } else if (TableContentType.PROTEIN_SEQUENCE_COVERAGE.equals(type)) {
            addSequenceCoverageData(newData.getValue());
        } else if (TableContentType.PEPTIDE_FIT.equals(type)) {
            addPeptideFitData(newData.getValue());
        } else if (TableContentType.PEPTIDE_DELTA.equals(type)) {
            addPeptideDeltaData(newData.getValue());
        } else if (TableContentType.PEPTIDE_PRECURSOR_MZ.equals(type)) {
            addPeptideMzData(newData.getValue());
        }
    }

    /**
     * Add protein related details
     *
     * @param newData protein detail map
     */
    protected void addProteinDetails(Object newData) {
        // get a map of protein accession to protein details
        Map<String, Protein> proteins = (Map<String, Protein>) newData;

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            PeptideTableRow peptideTableRow = (PeptideTableRow) contents.get(row);
            ProteinAccession proteinAccession = peptideTableRow.getProteinAccession();
            if (proteinAccession != null) {
                String mappedAccession = proteinAccession.getMappedAccession();
                if (mappedAccession != null) {
                    Protein protein = proteins.get(mappedAccession);
                    if (protein != null) {
                        AnnotatedProtein annotatedProtein = new AnnotatedProtein(protein);
                        // set protein name
                        peptideTableRow.setProteinName(annotatedProtein.getName());
                        // set protein status
                        peptideTableRow.setProteinAccessionStatus(annotatedProtein.getStatus().name());
                        // notify a row change
                        fireTableRowsUpdated(row, row);
                    }
                }
            }
        }
    }

    /**
     * Add protein sequence coverages
     *
     * @param newData sequence coverage map
     */
    protected void addSequenceCoverageData(Object newData) {
        // column index for protein sequence coverage
        int coverageIndex = getColumnIndex(PeptideTableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());

        // map contains sequence coverage
        Map<Comparable, Double> coverageMap = (Map<Comparable, Double>) newData;

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            PeptideTableRow peptideTableRow = (PeptideTableRow) contents.get(row);
            Comparable identId = peptideTableRow.getProteinId();
            Double coverage = coverageMap.get(identId);
            if (coverage != null) {
                // set protein name
                peptideTableRow.setSequenceCoverage(coverage);
                // notify a row change
                fireTableCellUpdated(row, coverageIndex);
            }
        }
    }

    /**
     * Whether peptide sequence fit the protein sequence
     *
     * @param newDataValue
     */
    protected void addPeptideFitData(Object newDataValue) {
        // map contains peptide fit
        Map<Tuple<Comparable, Comparable>, Integer> peptideFits = (Map<Tuple<Comparable, Comparable>, Integer>) newDataValue;

        // column index for peptide fit
        int peptideFitIndex = getColumnIndex(PeptideTableHeader.PEPTIDE_FIT.getHeader());

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            PeptideTableRow peptideTableRow = (PeptideTableRow) contents.get(row);
            Comparable identId = peptideTableRow.getProteinId();
            Comparable peptideId = peptideTableRow.getPeptideId();
            Integer peptideFit = peptideFits.get(new Tuple<>(identId, peptideId));
            if (peptideFit != null) {
                // set protein name
                peptideTableRow.setPeptideFitState(peptideFit);
                // notify a row change
                fireTableCellUpdated(row, peptideFitIndex);
            }
        }
    }

    /**
     * The Delta mass between the Peptide MZ and the Spectrum MZ
     *
     * @param newDataValue
     */
    protected void addPeptideDeltaData(Object newDataValue) {

        Map<Tuple<Comparable, Comparable>, Double> deltaMzs = (Map<Tuple<Comparable, Comparable>, Double>) newDataValue;

        // column index for peptide fit
        int deltaMzIndex = getColumnIndex(PeptideTableHeader.DELTA_MZ_COLUMN.getHeader());

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            PeptideTableRow content = (PeptideTableRow) contents.get(row);
            Comparable identId = content.getProteinId();
            Comparable peptideId = content.getPeptideId();
            Double deltaMz = deltaMzs.get(new Tuple<>(identId, peptideId));
            if (deltaMz != null) {
                // set delta mz
                content.setDeltaMz(deltaMz);
                // notify a row change
                fireTableCellUpdated(row, deltaMzIndex);
            }
        }
    }

    /**
     * The Precursor Mz
     *
     * @param newDataValue
     */
    protected void addPeptideMzData(Object newDataValue) {
        // map contains peptide fit
        Map<Tuple<Comparable, Comparable>, Double> precursorMzs = (Map<Tuple<Comparable, Comparable>, Double>) newDataValue;

        // column index for peptide fit
        int peptideFitIndex = getColumnIndex(PeptideTableHeader.PRECURSOR_MZ_COLUMN.getHeader());

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            PeptideTableRow peptideTableRow = (PeptideTableRow) contents.get(row);
            Comparable identId = peptideTableRow.getProteinId();
            Comparable peptideId = peptideTableRow.getPeptideId();
            Double precursorMz = precursorMzs.get(new Tuple<>(identId, peptideId));
            if (precursorMz != null) {
                // set protein name
                peptideTableRow.setPrecursorMz(precursorMz);
                // notify a row change
                fireTableCellUpdated(row, peptideFitIndex);
            }
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PeptideTableRow peptideTableRow = (PeptideTableRow) contents.get(rowIndex);

        String columnName = getColumnName(columnIndex);

        if (PeptideTableHeader.PEPTIDE_COLUMN.getHeader().equals(columnName)) {
            return peptideTableRow.getSequence();
        } else if (PeptideTableHeader.PROTEIN_ACCESSION_COLUMN.getHeader().equals(columnName)) {
            return peptideTableRow.getProteinAccession();
        } else if (PeptideTableHeader.PROTEIN_NAME.getHeader().equals(columnName)) {
            return peptideTableRow.getProteinName();
        } else if (PeptideTableHeader.PROTEIN_STATUS.getHeader().equals(columnName)) {
            return peptideTableRow.getProteinAccessionStatus();
        } else if (PeptideTableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader().equals(columnName)) {
            return peptideTableRow.getSequenceCoverage();
        } else if (PeptideTableHeader.PEPTIDE_FIT.getHeader().equals(columnName)) {
            return peptideTableRow.getPeptideFitState();
        } else if (PeptideTableHeader.RANKING.getHeader().equals(columnName)) {
            return peptideTableRow.getRanking();
        } else if (PeptideTableHeader.DELTA_MZ_COLUMN.getHeader().equals(columnName)) {
            return peptideTableRow.getDeltaMz();
        } else if (PeptideTableHeader.PRECURSOR_CHARGE_COLUMN.getHeader().equals(columnName)) {
            return peptideTableRow.getPrecursorCharge();
        } else if (PeptideTableHeader.PRECURSOR_MZ_COLUMN.getHeader().equals(columnName)) {
            return peptideTableRow.getPrecursorMz();
        } else if (PeptideTableHeader.PEPTIDE_MODIFICATION_COLUMN.getHeader().equals(columnName)) {
            return peptideTableRow.getModificationNames();
        } else if (PeptideTableHeader.NUMBER_OF_FRAGMENT_IONS_COLUMN.getHeader().equals(columnName)) {
            return peptideTableRow.getNumberOfFragmentIons();
        } else if (PeptideTableHeader.PEPTIDE_SEQUENCE_LENGTH_COLUMN.getHeader().equals(columnName)) {
            return peptideTableRow.getSequenceLength();
        } else if (PeptideTableHeader.SEQUENCE_START_COLUMN.getHeader().equals(columnName)) {
            return peptideTableRow.getSequenceStartPosition();
        } else if (PeptideTableHeader.SEQUENCE_END_COLUMN.getHeader().equals(columnName)) {
            return peptideTableRow.getSequenceEndPosition();
        } else if (PeptideTableHeader.SPECTRUM_ID.getHeader().equals(columnName)) {
            return peptideTableRow.getSpectrumId();
        } else if (PeptideTableHeader.IDENTIFICATION_ID.getHeader().equals(columnName)) {
            return peptideTableRow.getProteinId();
        } else if (PeptideTableHeader.PEPTIDE_ID.getHeader().equals(columnName)) {
            return peptideTableRow.getPeptideId();
        } else if( PeptideTableHeader.COMPARE.getHeader().equals(columnName)){
            return peptideTableRow.getComparisonState();
        } else if (PeptideTableHeader.ADDITIONAL.getHeader().equals(columnName)) {
            Comparable proteinId = peptideTableRow.getProteinId();
            Comparable peptideId = peptideTableRow.getPeptideId();
            return proteinId + Constants.COMMA + peptideId;
        } else if( PeptideTableHeader.CLUSTER_DETAILS.getHeader().equals(columnName)){
            return peptideTableRow.getSequence().getSequence();
        } else {
            return getPeptideScore(peptideTableRow, columnName);
        }
    }


    private Double getPeptideScore(PeptideTableRow peptideTableRow, String columnName) {
        List<Double> scores = peptideTableRow.getScores();

        int scoreIndex = 0;

        for (SearchEngineScoreCvTermReference scoreTermReference : listScores) {
            if (scoreTermReference.getName().equals(columnName)) {
                return scores.get(scoreIndex);
            }
            scoreIndex++;
        }

        return null;
    }
}
