package uk.ac.ebi.pride.toolsuite.gui.component.sequence;

import uk.ac.ebi.pride.toolsuite.gui.utils.PropertyChangeHelper;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;

import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * Protein sequence with its optional annotations
 * <p/>
 * User: rwang
 * Date: 08/06/11
 * Time: 11:37
 */
public class AnnotatedProtein extends Protein {

    public static final String PEPTIDE_SELECTION_PROP = "selectedAnnotation";

    private List<PeptideAnnotation> annotations;
    private PeptideAnnotation selectedAnnotation;
    private PropertyChangeHelper propertyChangeHelper;

    /**
     * these peptides can be duplicated
     */
    private int numOfValidPeptides = -1;
    /**
     * these peptides must be valid as well
     */
    private int numOfUniquePeptides = -1;
    /**
     * the number of amino acids covered
     */
    private int numOfAminoAcidCovered = -1;

    /**
     * Create an annotable protein from an existing protein
     *
     * @param protein given protein
     */
    public AnnotatedProtein(Protein protein) {
        this(protein.getAccession());
        String name = protein.getName();
        STATUS status = protein.getStatus();
        List<Protein> replacements = protein.getReplacingProteins();
        switch (status) {
            case ACTIVE:
                setName(name);
                setSequenceString(protein.getSequenceString());
                break;
            case CHANGED:
                if (replacements != null && replacements.size() > 0) {
                    Protein replacement = replacements.get(0);
                    setName(replacement.getName());
                    setSequenceString(replacement.getSequenceString());
                }
                break;
            case MERGED:
                if (replacements != null && replacements.size() > 0) {
                    Protein replacement = replacements.get(0);
                    setName(replacement.getName());
                    setSequenceString(replacement.getSequenceString());
                }
                break;
            case DEMERGED:
                setName(name);
                break;
            case UNKNOWN:
                setName(name);
                setSequenceString(protein.getSequenceString());
                break;
            case DELETED:
                setName(name);
                break;
            case ERROR:
                break;

        }

        setStatus(status);

        for (PROPERTY property : PROPERTY.values()) {
            setProperty(property, protein.getProperty(property));
        }
    }

    /**
     * Create an annotable active protein from an existing protein
     *
     * @param accession
     * @param name
     * @param status
     * @param sequence
     */
    public AnnotatedProtein(String accession, String name, STATUS status, String sequence) {
        this(accession);
        switch (status) {
            case ACTIVE:
                setName(name);
                setSequenceString(sequence);
                break;
            case DEMERGED:
                setName(name);
                break;
            case UNKNOWN:
                setName(name);
                setSequenceString(sequence);
                break;
            case DELETED:
                setName(name);
                break;
            case ERROR:
                break;
        }
        setStatus(status);
    }

    public AnnotatedProtein(String accession) {
        super(accession);
        this.annotations = new ArrayList<>();
        this.propertyChangeHelper = new PropertyChangeHelper();
    }

    /**
     * Check whether a peptide is valid considering the protein sequence
     *
     * @param annotation peptide annotation
     * @return boolean true means valid
     */
    public boolean isValidPeptideAnnotation(PeptideAnnotation annotation) {
        return hasSubSequenceString(annotation.getSequence());
    }

    /**
     * Check whether a peptide is strictly valid based both the sequence and the location
     *
     * @param annotation peptide annotation
     * @return boolean true means valid
     */
    public boolean isStrictValidPeptideAnnotation(PeptideAnnotation annotation) {
        return hasSubSequenceString(annotation.getSequence(), annotation.getStart(), annotation.getEnd());
    }

    public void addAnnotation(PeptideAnnotation annotation) {
        this.annotations.add(annotation);
    }

    public void removeAnnotation(PeptideAnnotation annotation) {
        this.annotations.remove(annotation);
    }

    public List<PeptideAnnotation> getAnnotations() {
        return new ArrayList<>(annotations);
    }

    public PeptideAnnotation getSelectedAnnotation() {
        return selectedAnnotation;
    }

    /**
     * Set a new selected peptide annotation, and notify all the property change listeners
     *
     * @param selectedAnnotation selected peptide annotation
     */
    public void setSelectedAnnotation(PeptideAnnotation selectedAnnotation) {
        PeptideAnnotation oldPeptide, newPeptide;
        synchronized (this) {
            oldPeptide = this.selectedAnnotation;
            this.selectedAnnotation = selectedAnnotation;
            newPeptide = this.selectedAnnotation;
        }
        propertyChangeHelper.firePropertyChange(PEPTIDE_SELECTION_PROP, oldPeptide, newPeptide);
    }

    /**
     * get the total number of peptides
     *
     * @return int number of peptides
     */
    public int getNumOfPeptides() {
        return annotations.size();
    }

    public int getNumOfValidPeptides() {
        if (numOfValidPeptides == -1) {
            populateCoverage();
        }
        return numOfValidPeptides;
    }

    public void setNumOfValidPeptides(int numOfValidPeptides) {
        this.numOfValidPeptides = numOfValidPeptides;
    }

    public int getNumOfUniquePeptides() {
        if (numOfUniquePeptides == -1) {
            populateCoverage();
        }
        return numOfUniquePeptides;
    }

    public void setNumOfUniquePeptides(int numOfUniquePeptides) {
        this.numOfUniquePeptides = numOfUniquePeptides;
    }

    public Set<Integer> searchStartingPosition(PeptideAnnotation annotation) {
        return searchStartingPosition(annotation.getSequence());
    }

    public double getSequenceCoverage() {

        String sequence = getSequenceString();
        if (sequence == null || sequence.length() == 0) {
            return -1;
        }

        if (numOfAminoAcidCovered == -1) {
            populateCoverage();
        }

        return Double.parseDouble(numOfAminoAcidCovered + "") / sequence.length();
    }

    public int getNumOfAminoAcidCovered() {
        String sequence = getSequenceString();
        if (sequence == null || sequence.length() == 0) {
            return -1;
        }

        if (numOfAminoAcidCovered == -1) {
            populateCoverage();
        }
        return numOfAminoAcidCovered;
    }

    private void populateCoverage() {
        java.util.List<PeptideAnnotation> peptides = this.getAnnotations();
        if (peptides.size() > 0) {
            int numOfValidPeptides = 0;

            // remove invalid peptide
            String sequence = this.getSequenceString();
            if (sequence != null && !"".equals(sequence)) {
                Iterator<PeptideAnnotation> peptideIter = peptides.iterator();
                while (peptideIter.hasNext()) {
                    PeptideAnnotation peptideAnnotation = peptideIter.next();
                    if (!this.isValidPeptideAnnotation(peptideAnnotation)) {
                        peptideIter.remove();
                    } else {
                        numOfValidPeptides++;
                    }
                }
            }

            // set number of Valid peptides
            setNumOfValidPeptides(numOfValidPeptides);

            // keep only unique peptides
            Set<PeptideAnnotation> uniquePeptides = new LinkedHashSet<>();
            uniquePeptides.addAll(peptides);
            setNumOfUniquePeptides(uniquePeptides.size());

            // peptide coverage array
            // it is the length of the protein sequence, and contains the count of sequence coverage for each position
            int length = sequence == null ? 0 : sequence.length();
            int[] coverageArr = new int[length];
            for (PeptideAnnotation uniquePeptide : uniquePeptides) {
                Set<Integer> startingPos = new HashSet<>();
                boolean strictValidPeptideAnnotation = isStrictValidPeptideAnnotation(uniquePeptide);
                if (strictValidPeptideAnnotation) {
                    startingPos.add(uniquePeptide.getStart() - 1);
                } else {
                    startingPos.addAll(searchStartingPosition(uniquePeptide));
                }

                for (Integer start : startingPos) {
                    // if the position does match
                    int peptideLen = uniquePeptide.getSequence().length();
                    int end = start + peptideLen - 1;

                    // iterate peptide
                    for (int i = start; i <= end; i++) {
                        coverageArr[i] += 1;
                    }
                }
            }

            // colour code the peptide positions
            int numOfAminoAcidCovered = 0;
            for (int count : coverageArr) {
                if (count != 0) {
                    numOfAminoAcidCovered++;
                }
            }

            // set number of amino acid being covered
            setNumOfAminoAcidCovered(numOfAminoAcidCovered);
        }
    }

    public void setNumOfAminoAcidCovered(int numOfAminoAcidCovered) {
        this.numOfAminoAcidCovered = numOfAminoAcidCovered;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeHelper.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeHelper.removePropertyChangeListener(listener);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnnotatedProtein)) return false;
        if (!super.equals(o)) return false;

        AnnotatedProtein that = (AnnotatedProtein) o;

        return annotations.equals(that.annotations) && !(selectedAnnotation != null ? !selectedAnnotation.equals(that.selectedAnnotation) : that.selectedAnnotation != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + annotations.hashCode();
        result = 31 * result + (selectedAnnotation != null ? selectedAnnotation.hashCode() : 0);
        return result;
    }
}
