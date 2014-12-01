package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import uk.ac.ebi.pride.toolsuite.gui.component.table.sorttreetable.SortableTreeTableNode;
import uk.ac.ebi.pride.toolsuite.gui.utils.ProteinAccession;
import uk.ac.ebi.pride.utilities.term.CvTermReference;

import java.util.*;

/**
 * @author ypriverol
 * @author rwang
 */
public class SortableProteinNode  extends SortableTreeTableNode{

    private Boolean comparisonState;

    private ProteinAccession proteinAccession;

    private String proteinName;

    private String proteinAccessionStatus;

    private Double sequenceCoverage;

    private Double isoelectricPoint;

    private Double threshold;

    private Integer numberOfPeptides;

    private Integer numberOfUniquePeptides;

    private Integer numberOfPTMs;

    private Comparable proteinId;

    private Comparable proteinGroupId;

    private final List<Double> scores;

    private final List<Object> quantifications;

    private final List<SortableProteinNode> childNodes;

    public SortableProteinNode() {
        this.scores = new ArrayList<Double>();
        this.quantifications = new ArrayList<Object>();
        this.childNodes = new ArrayList<SortableProteinNode>();
    }

    public SortableProteinNode(ProteinTableRow proteinTableRow, Map<String, String> columns, Collection<CvTermReference> scoresNames){
        super();
        childNodes = new ArrayList<SortableProteinNode>();
        scores = proteinTableRow.getScores();
        proteinAccession = proteinTableRow.getProteinAccession();
        proteinName = proteinTableRow.getProteinName();
        proteinAccessionStatus = proteinTableRow.getProteinAccessionStatus();
        sequenceCoverage = proteinTableRow.getSequenceCoverage();
        isoelectricPoint = proteinTableRow.getIsoelectricPoint();
        threshold = proteinTableRow.getThreshold();
        numberOfPeptides = proteinTableRow.getNumberOfPeptides();
        numberOfUniquePeptides = proteinTableRow.getNumberOfUniquePeptides();
        numberOfPTMs = proteinTableRow.getNumberOfPTMs();
        proteinId = proteinTableRow.getProteinId();
        proteinGroupId = proteinTableRow.getProteinGroupId();
        comparisonState = proteinTableRow.getComparisonState();
        quantifications = proteinTableRow.getQuantifications();
        setUserObjects(getObjectValues(columns.keySet(), scoresNames));
        for(ProteinTableRow child: proteinTableRow.getChildProteinTableRows()){
            SortableProteinNode childNode = new SortableProteinNode(child,columns, scoresNames);
            childNodes.add(childNode);
        }

    }

    public Boolean getComparisonState() {
        return comparisonState;
    }

    public void setComparisonState(Boolean comparisonState) {
        this.comparisonState = comparisonState;
    }

    public ProteinAccession getProteinAccession() {
        return proteinAccession;
    }

    public void setProteinAccession(ProteinAccession accession) {
        this.proteinAccession = accession;
    }

    public String getProteinName() {
        return proteinName;
    }

    public void setProteinName(String proteinName) {
        this.proteinName = proteinName;
    }

    public String getProteinAccessionStatus() {
        return proteinAccessionStatus;
    }

    public void setProteinAccessionStatus(String proteinAccessionStatus) {
        this.proteinAccessionStatus = proteinAccessionStatus;
    }

    public Double getSequenceCoverage() {
        return sequenceCoverage;
    }

    public void setSequenceCoverage(Double sequenceCoverage) {
        this.sequenceCoverage = sequenceCoverage;
    }

    public void updatePropertyObject(Map<String, String> columns, Collection<CvTermReference> scoresNames){
        setUserObjects(getObjectValues(columns.keySet(), scoresNames));
    }

    public Double getIsoelectricPoint() {
        return isoelectricPoint;
    }

    public void setIsoelectricPoint(Double isoelectricPoint) {
        this.isoelectricPoint = isoelectricPoint;
    }

    public Double getThreshold() {
        return threshold;
    }

    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    public Integer getNumberOfPeptides() {
        return numberOfPeptides;
    }

    public void setNumberOfPeptides(Integer numberOfPeptides) {
        this.numberOfPeptides = numberOfPeptides;
    }

    public Integer getNumberOfUniquePeptides() {
        return numberOfUniquePeptides;
    }

    public void setNumberOfUniquePeptides(Integer numberOfUniquePeptides) {
        this.numberOfUniquePeptides = numberOfUniquePeptides;
    }

    public Integer getNumberOfPTMs() {
        return numberOfPTMs;
    }

    public void setNumberOfPTMs(Integer numberOfPTMs) {
        this.numberOfPTMs = numberOfPTMs;
    }

    public Comparable getProteinId() {
        return proteinId;
    }

    public void setProteinId(Comparable proteinId) {
        this.proteinId = proteinId;
    }

    public Comparable getProteinGroupId() {
        return proteinGroupId;
    }

    public void setProteinGroupId(Comparable proteinGroupId) {
        this.proteinGroupId = proteinGroupId;
    }

    public List<Double> getScores() {
        return scores;
    }

    public void addScore(Double score) {
        scores.add(score);
    }

    public List<Object> getQuantifications() {
        return quantifications;
    }

    public void addQuantifications(List<Object> quantifications) {
        this.quantifications.addAll(quantifications);
    }

    public List<SortableProteinNode> getChildProteinTableRows() {
        return childNodes;
    }

    public void addChildProteinTableRow(SortableProteinNode proteinTableRow) {
        this.childNodes.add(proteinTableRow);
    }

    @Override
    public int getColumnCount() {
        return ProteinTableHeader.values().length + scores.size();
    }

    private Object[] getObjectValues(Set<String> columns, Collection<CvTermReference> proteinScores){
        List<String> listColumns = new ArrayList<String>(columns);
        Object[] values = new Object[columns.size()];
        for(int i = 0; i < listColumns.size(); i++){
            String columnName = listColumns.get(i);
            if (ProteinTableHeader.COMPARE.getHeader().equals(columnName)) {
                values[i] = getComparisonState();
            } else if (ProteinTableHeader.PROTEIN_ACCESSION.getHeader().equals(columnName)) {
                values[i] = getProteinAccession();
            } else if (ProteinTableHeader.PROTEIN_NAME.getHeader().equals(columnName)) {
                values[i] = getProteinName();
            } else if (ProteinTableHeader.PROTEIN_STATUS.getHeader().equals(columnName)) {
                values[i] = getProteinAccessionStatus();
            } else if (ProteinTableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader().equals(columnName)) {
                values[i] = getSequenceCoverage();
            } else if (ProteinTableHeader.THEORETICAL_ISOELECTRIC_POINT.getHeader().equals(columnName)) {
                values[i] = getIsoelectricPoint();
            } else if (ProteinTableHeader.IDENTIFICATION_THRESHOLD.getHeader().equals(columnName)) {
                values[i] = getThreshold();
            } else if (ProteinTableHeader.NUMBER_OF_PEPTIDES.getHeader().equals(columnName)) {
                values[i] = getNumberOfPeptides();
            } else if (ProteinTableHeader.NUMBER_OF_UNIQUE_PEPTIDES.getHeader().equals(columnName)) {
                values[i] = getNumberOfUniquePeptides();
            } else if (ProteinTableHeader.NUMBER_OF_PTMS.getHeader().equals(columnName)) {
                values[i] = getNumberOfPTMs();
            } else if (ProteinTableHeader.PROTEIN_ID.getHeader().equals(columnName)) {
                values[i] = getProteinId();
            } else if (ProteinTableHeader.PROTEIN_GROUP_ID.getHeader().equals(columnName)) {
                values[i] = getProteinGroupId();
            } else if (ProteinTableHeader.ADDITIONAL.getHeader().equals(columnName)) {
                values[i] = getProteinId();
            } else {
                values[i] = getProteinScore(this, columnName, proteinScores);
            }
        }
        return values;
    }

    private Double getProteinScore(SortableProteinNode proteinTableRow, String columnName, Collection<CvTermReference> proteinScores) {
        List<Double> scores = proteinTableRow.getScores();

        int scoreIndex = 0;

        for (CvTermReference scoreTermReference : proteinScores) {
            if (scoreTermReference.getName().equals(columnName)) {
                return scores.get(scoreIndex);
            }
            scoreIndex++;
        }

        return null;
    }

    @Override
    public String toString() {
        if(proteinGroupId != null)
            return proteinGroupId.toString();
        return null;
    }
}
