package uk.ac.ebi.pride.toolsuite.gui.component.sequence;

import java.util.ArrayList;
import java.util.List;

/**
 * Annotation to describe a peptide
 *
 * @author: rwang
 * Date: 08/06/11
 * Time: 13:52
 */
public class PeptideAnnotation {

    private int start = -1, end = -1;
    /**
     * peptide sequence
     */
    private String sequence;
    /**
     * optional, ptm annotations
     */
    private final List<PTMAnnotation> ptmAnnotations;

    public PeptideAnnotation() {
        this(null, -1 , -1);
    }

    public PeptideAnnotation(String sequence, int start, int end) {
        this.start = start;
        this.end = end;
        this.sequence = sequence == null ? null : sequence.toUpperCase();
        this.ptmAnnotations = new ArrayList<PTMAnnotation>();
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = (sequence == null ? sequence : sequence.toUpperCase());
    }

    public List<PTMAnnotation> getPtmAnnotations() {
        return new ArrayList<PTMAnnotation>(ptmAnnotations);
    }

    public void addPtmAnnotation(PTMAnnotation ptm) {
        ptmAnnotations.add(ptm);
    }

    public void removePtmAnnotation(PTMAnnotation ptm) {
        ptmAnnotations.remove(ptm);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PeptideAnnotation)) return false;

        PeptideAnnotation that = (PeptideAnnotation) o;

        if (end != that.end) return false;
        if (start != that.start) return false;
        return !(ptmAnnotations != null ? !ptmAnnotations.equals(that.ptmAnnotations) : that.ptmAnnotations != null) && !(sequence != null ? !sequence.equals(that.sequence) : that.sequence != null);

    }

    @Override
    public int hashCode() {
        int result = start;
        result = 31 * result + end;
        result = 31 * result + (sequence != null ? sequence.hashCode() : 0);
        result = 31 * result + (ptmAnnotations != null ? ptmAnnotations.hashCode() : 0);
        return result;
    }
}
