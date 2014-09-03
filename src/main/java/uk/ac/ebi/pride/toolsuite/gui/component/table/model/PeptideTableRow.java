package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import uk.ac.ebi.pride.utilities.data.core.Modification;
import uk.ac.ebi.pride.utilities.data.core.PeptideSequence;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;
import uk.ac.ebi.pride.toolsuite.gui.utils.ProteinAccession;

import java.util.*;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class PeptideTableRow {
    private PeptideSequence sequence;
    private ProteinAccession proteinAccession;
    private String proteinName;
    private String proteinAccessionStatus;
    private Double sequenceCoverage;
    private int peptideFitState;
    private Integer ranking;
    private Double deltaMz;
    private Integer precursorCharge;
    private Double precursorMz;
    private Integer numberOfFragmentIons;
    private final List<Double> scores;
    private Integer sequenceStartPosition;
    private Integer sequenceEndPosition;
    private Comparable spectrumId;
    private Comparable proteinId;
    private Comparable peptideId;
    private final List<Object> quantifications;

    public PeptideTableRow() {
        this.scores = new ArrayList<Double>();
        this.quantifications = new ArrayList<Object>();
    }

    public PeptideSequence getSequence() {
        return sequence;
    }

    public void setSequence(PeptideSequence sequence) {
        this.sequence = sequence;
    }

    public ProteinAccession getProteinAccession() {
        return proteinAccession;
    }

    public void setProteinAccession(ProteinAccession proteinAccession) {
        this.proteinAccession = proteinAccession;
    }

    public String getModificationNames() {
        Set<String> modificationNames = new HashSet<String>();
        String concatenatedModificationNames = "";

        for (Modification mod : sequence.getModifications()) {
            String modName = mod.getName();
            if (modName == null) {
                // use mod accession instead
                modName = mod.getId().toString().trim();
            }

            if (modName != null && !modificationNames.contains(modName)) {
                concatenatedModificationNames += modName + "; ";
                modificationNames.add(modName);
            }
        }

        if (concatenatedModificationNames.length() > 1) {
            concatenatedModificationNames = concatenatedModificationNames.substring(0, concatenatedModificationNames.length() - 2);
        }

        return concatenatedModificationNames;
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

    public int getPeptideFitState() {
        return peptideFitState;
    }

    public void setPeptideFitState(int peptideFitState) {
        this.peptideFitState = peptideFitState;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public Double getDeltaMz() {
        return deltaMz;
    }

    public void setDeltaMz(Double deltaMz) {
        this.deltaMz = deltaMz;
    }

    public Integer getPrecursorCharge() {
        return precursorCharge;
    }

    public void setPrecursorCharge(Integer precursorCharge) {
        this.precursorCharge = precursorCharge;
    }

    public Double getPrecursorMz() {
        return precursorMz;
    }

    public void setPrecursorMz(Double precursorMz) {
        this.precursorMz = precursorMz;
    }

    public Integer getNumberOfFragmentIons() {
        return numberOfFragmentIons;
    }

    public void setNumberOfFragmentIons(Integer numberOfFragmentIons) {
        this.numberOfFragmentIons = numberOfFragmentIons;
    }

    public List<Double> getScores() {
        return scores;
    }

    public void addScore(Double score) {
        scores.add(score);
    }

    public Integer getSequenceStartPosition() {
        return sequenceStartPosition;
    }

    public void setSequenceStartPosition(Integer sequenceStartPosition) {
        this.sequenceStartPosition = sequenceStartPosition;
    }

    public Integer getSequenceEndPosition() {
        return sequenceEndPosition;
    }

    public void setSequenceEndPosition(Integer sequenceEndPosition) {
        this.sequenceEndPosition = sequenceEndPosition;
    }

    public Comparable getSpectrumId() {
        return spectrumId;
    }

    public void setSpectrumId(Comparable spectrumId) {
        this.spectrumId = spectrumId;
    }

    public Comparable getProteinId() {
        return proteinId;
    }

    public void setProteinId(Comparable proteinId) {
        this.proteinId = proteinId;
    }

    public Comparable getPeptideId() {
        return peptideId;
    }

    public void setPeptideId(Comparable peptideId) {
        this.peptideId = peptideId;
    }

    public void setPeptideIndex(String peptideIndex) {
        this.peptideId = peptideIndex;
    }

    public int getSequenceLength() {
        if (sequence != null) {
            return sequence.getSequence().length();
        } else {
            return 0;
        }
    }

    public List<Object> getQuantifications() {
        return quantifications;
    }

    public void addQuantifications(List<Object> quantifications) {
        this.quantifications.addAll(quantifications);
    }

    public List<Object> flatten() {
        List<Object> flattenedPeptideTableRow = new ArrayList<Object>();

        flattenedPeptideTableRow.add(getSequence().getSequence());
        flattenedPeptideTableRow.add(getProteinAccession().getAccession());
        flattenedPeptideTableRow.add(getProteinName());
        flattenedPeptideTableRow.add(getProteinAccessionStatus());
        flattenedPeptideTableRow.add(getSequenceCoverage());
        flattenedPeptideTableRow.add(getPeptideFitState());
        flattenedPeptideTableRow.add(getRanking());
        flattenedPeptideTableRow.add(getDeltaMz());
        flattenedPeptideTableRow.add(getPrecursorCharge());
        flattenedPeptideTableRow.add(getPrecursorMz());
        flattenedPeptideTableRow.add(getModificationNames());
        flattenedPeptideTableRow.add(getNumberOfFragmentIons());
        flattenedPeptideTableRow.addAll(getScores());
        flattenedPeptideTableRow.add(getSequenceLength());
        flattenedPeptideTableRow.add(getSequenceStartPosition());
        flattenedPeptideTableRow.add(getSequenceEndPosition());
        flattenedPeptideTableRow.add(getSpectrumId());
        flattenedPeptideTableRow.add(getProteinId());
        flattenedPeptideTableRow.add(getPeptideId());
        flattenedPeptideTableRow.addAll(getQuantifications());

        return flattenedPeptideTableRow;
    }

    private String getProteinAccessionText(Collection<ProteinAccession> accessions) {
        String accessionText = "";

        for (ProteinAccession accession : accessions) {
            accessionText += accession + Constants.COMMA;
        }

        return accessionText.substring(0, accessionText.length() - 1);
    }
}
