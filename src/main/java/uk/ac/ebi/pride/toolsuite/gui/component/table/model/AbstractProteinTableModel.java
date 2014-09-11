package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import uk.ac.ebi.pride.utilities.util.Tuple;
import uk.ac.ebi.pride.toolsuite.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.toolsuite.gui.utils.ProteinAccession;
import uk.ac.ebi.pride.utilities.term.CvTermReference;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Abstract class of protein table model When the file do not contains protein inference
 * <p/>
 * User: ypriverol, rwang
 * Date: 24/08/2011
 * Time: 16:21
 */
public class AbstractProteinTableModel extends ProgressiveListTableModel<Void, Tuple<TableContentType, Object>> {

    protected Collection<CvTermReference> listScores;

    public AbstractProteinTableModel(Collection<CvTermReference> listScores) {
        this.listScores = listScores;
        initializeTableModel();
    }

    @Override
    public void initializeTableModel() {
        setColumnHeaders();
    }

    void setColumnHeaders() {
        columnNames.clear();

        // add columns for search engine scores
        ProteinTableHeader[] headers = ProteinTableHeader.values();
        for (ProteinTableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
            if (listScores != null && ProteinTableHeader.PROTEIN_ID.getHeader().equals(header.getHeader())) {
                for (CvTermReference scoreCvTerm : listScores) {
                    String name = scoreCvTerm.getName();
                    columnNames.put(name, name);
                }
            }
        }
        //Remove protein group from header because is not supported for files without protein Inference
        columnNames.remove(ProteinTableHeader.PROTEIN_GROUP_ID.getHeader());

    }

    @Override
    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PROTEIN_DETAILS.equals(type)) {
            addProteinDetailData(newData.getValue());
        } else if (TableContentType.PROTEIN_SEQUENCE_COVERAGE.equals(type)) {
            addSequenceCoverageData(newData.getValue());
        }
    }

    /**
     * Add protein detail data
     *
     * @param newData protein detail map
     */
    protected void addProteinDetailData(Object newData) {
        // get a map of protein accession to protein details
        Map<String, Protein> proteins = (Map<String, Protein>) newData;

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            ProteinTableRow proteinTableRow = (ProteinTableRow) contents.get(row);
            ProteinAccession proteinAccession = proteinTableRow.getProteinAccession();
            if (proteinAccession != null) {
                String mappedAccession = proteinAccession.getMappedAccession();
                if (mappedAccession != null) {
                    Protein protein = proteins.get(mappedAccession);
                    if (protein != null) {
                        AnnotatedProtein annotatedProtein = new AnnotatedProtein(protein);
                        // set protein name
                        proteinTableRow.setProteinName(annotatedProtein.getName());
                        // set protein status
                        proteinTableRow.setProteinAccessionStatus(annotatedProtein.getStatus().name());
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
    void addSequenceCoverageData(Object newData) {
        // column index for protein sequence coverage
        int coverageIndex = getColumnIndex(ProteinTableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader());

        // map contains sequence coverage
        Map<Comparable, Double> coverageMap = (Map<Comparable, Double>) newData;

        // iterate over each row, set the protein name
        for (int row = 0; row < contents.size(); row++) {
            ProteinTableRow proteinTableRow = (ProteinTableRow) contents.get(row);
            
            Comparable proteinId = proteinTableRow.getProteinId();
            Double coverage = coverageMap.get(proteinId);
            if (coverage != null) {
                // set protein name
                proteinTableRow.setSequenceCoverage(coverage);
                // notify a row change
                fireTableCellUpdated(row, coverageIndex);
            }
        }
    }



    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ProteinTableRow proteinTableRow = (ProteinTableRow) contents.get(rowIndex);

        String columnName = getColumnName(columnIndex);

        if (ProteinTableHeader.COMPARE.getHeader().equals(columnName)) {
            return proteinTableRow.getComparisonState();
        } else if (ProteinTableHeader.PROTEIN_ACCESSION.getHeader().equals(columnName)) {
            return proteinTableRow.getProteinAccession();
        } else if (ProteinTableHeader.PROTEIN_NAME.getHeader().equals(columnName)) {
            return proteinTableRow.getProteinName();
        } else if (ProteinTableHeader.PROTEIN_STATUS.getHeader().equals(columnName)) {
            return proteinTableRow.getProteinAccessionStatus();
        } else if (ProteinTableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader().equals(columnName)) {
            return proteinTableRow.getSequenceCoverage();
        } else if (ProteinTableHeader.THEORETICAL_ISOELECTRIC_POINT.getHeader().equals(columnName)) {
            return proteinTableRow.getIsoelectricPoint();
        } else if (ProteinTableHeader.IDENTIFICATION_THRESHOLD.getHeader().equals(columnName)) {
            return proteinTableRow.getThreshold();
        } else if (ProteinTableHeader.NUMBER_OF_PEPTIDES.getHeader().equals(columnName)) {
            return proteinTableRow.getNumberOfPeptides();
        } else if (ProteinTableHeader.NUMBER_OF_UNIQUE_PEPTIDES.getHeader().equals(columnName)) {
            return proteinTableRow.getNumberOfUniquePeptides();
        } else if (ProteinTableHeader.NUMBER_OF_PTMS.getHeader().equals(columnName)) {
            return proteinTableRow.getNumberOfPTMs();
        } else if (ProteinTableHeader.PROTEIN_ID.getHeader().equals(columnName)) {
            return proteinTableRow.getProteinId();
        } else if (ProteinTableHeader.PROTEIN_GROUP_ID.getHeader().equals(columnName)) {
            return proteinTableRow.getProteinGroupId();
        } else if (ProteinTableHeader.ADDITIONAL.getHeader().equals(columnName)) {
            return proteinTableRow.getProteinId();
        } else {
            return getProteinScore(proteinTableRow, columnName);
        }
    }

    private Double getProteinScore(ProteinTableRow proteinTableRow, String columnName) {
        List<Double> scores = proteinTableRow.getScores();

        int scoreIndex = 0;

        for (CvTermReference scoreTermReference : listScores) {
            if (scoreTermReference.getName().equals(columnName)) {
                return scores.get(scoreIndex);
            }
            scoreIndex++;
        }

        return null;
    }
}