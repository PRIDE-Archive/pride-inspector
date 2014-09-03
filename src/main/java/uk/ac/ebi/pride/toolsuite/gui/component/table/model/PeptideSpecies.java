package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import uk.ac.ebi.pride.utilities.data.core.PeptideSequence;
import uk.ac.ebi.pride.toolsuite.gui.utils.ProteinAccession;
import uk.ac.ebi.pride.utilities.mol.IsoelectricPointUtils;

import java.util.*;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class PeptideSpecies {

    private PeptideSequence sequence;
    private int numberOfPSMs;
    private final Set<ProteinAccession> proteinAccessions;
    private String modifications;
    private int numberOfDeltaMzErrors;
    private double theoreticalIsoelectricPoint;
    private int length;
    private final List<PeptideTableRow> peptideTableRowData;

    public PeptideSpecies(PeptideSequence sequence) {
        this.numberOfPSMs = 0;
        this.numberOfDeltaMzErrors = 0;
        this.theoreticalIsoelectricPoint = 0;
        this.length = 0;
        this.proteinAccessions = new LinkedHashSet<ProteinAccession>();
        this.modifications = null;
        this.peptideTableRowData = new ArrayList<PeptideTableRow>();
        setSequence(sequence);
    }

    public PeptideSequence getSequence() {
        return sequence;
    }

    public void setSequence(PeptideSequence sequence) {
        this.sequence = sequence;

        updatePeptideSpecies(sequence);
    }

    private void updatePeptideSpecies(PeptideSequence sequence) {
        String rawSequence = sequence.getSequence();

        this.theoreticalIsoelectricPoint = IsoelectricPointUtils.calculate(rawSequence);
        this.length = rawSequence.length();
        this.numberOfPSMs = 0;
        this.numberOfDeltaMzErrors = 0;
        this.proteinAccessions.clear();
        this.modifications = null;
        this.peptideTableRowData.clear();
    }

    public int getNumberOfPSMs() {
        return numberOfPSMs;
    }

    public void setNumberOfPSMs(int numberOfPSMs) {
        this.numberOfPSMs = numberOfPSMs;
    }

    public Set<ProteinAccession> getProteinAccessions() {
        return proteinAccessions;
    }

    public void addProteinAccessions(Collection<ProteinAccession> proteinAccessions) {
        proteinAccessions.addAll(proteinAccessions);
    }

    public void addProteinAccession(ProteinAccession proteinAccession) {
        proteinAccessions.add(proteinAccession);
    }

    public String getModifications() {
        return modifications;
    }

    public int getNumberOfDeltaMzErrors() {
        return numberOfDeltaMzErrors;
    }

    public void setNumberOfDeltaMzErrors(int numberOfDeltaMzErrors) {
        this.numberOfDeltaMzErrors = numberOfDeltaMzErrors;
    }

    public double getTheoreticalIsoelectricPoint() {
        return theoreticalIsoelectricPoint;
    }

    public int getLength() {
        return length;
    }

    public List<PeptideTableRow> getPeptideTableRowData() {
        return peptideTableRowData;
    }

    public void addPeptideTableRowData(PeptideTableRow peptideData) {
        this.peptideTableRowData.add(peptideData);
    }

    public void setModifications(String modifications) {
        this.modifications = modifications;
    }

    public void clearStats() {
        this.setNumberOfDeltaMzErrors(0);
        this.setNumberOfPSMs(0);
    }
}
