package uk.ac.ebi.pride.toolsuite.gui.component.sequence;

import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class converts a AnnotatedProtein into a AttributedSequence for drawing
 * <p/>
 * It also takes into account the peptide annotations and ptm annotations
 * <p/>
 * User: rwang
 * Date: 17/06/11
 * Time: 15:10
 */
public class AttributedSequenceBuilder {
    public final static int PROTEIN_SEGMENT_LENGTH = 10;
    public final static String PROTEIN_SEGMENT_GAP = "  ";
    public final static Font DEFAULT_FONT = new Font(Font.MONOSPACED, Font.BOLD, 16);
    public final static Color DEFAULT_FOREGROUND = Color.GRAY;
    public final static Color PEPTIDE_FOREGROUND_COLOUR = Color.BLACK;

    /**
     * This method is responsible for create a formatted and styled protein sequence
     * <p/>
     * 1. insert white space between a segment of the sequence (PROTEIN_SEGMENT_LENGTH)
     * 2. insert tabs between segments of the sequence (PROTEIN_SEGMENT_GAP)
     * 3. highlight the peptide annotations
     * 4. highlight the ptm annotations
     * 5. highlight the selected peptide
     *
     * @param protein protein to be converted to formatted sequence string
     * @return AttributedString    formatted sequence string
     */
    public static AttributedString build(AnnotatedProtein protein) {
        AttributedString formattedSequence = null;
        if (protein != null) {
            String sequence = protein.getSequenceString();
            if (sequence != null && !"".equals(sequence.trim())) {
                // add sequence segment gap
                String gappedSequence = insertSegmentGapToSequence(sequence);

                // create attributed string
                formattedSequence = new AttributedString(gappedSequence);

                // set overall font
                formattedSequence.addAttribute(TextAttribute.FONT, DEFAULT_FONT);
                formattedSequence.addAttribute(TextAttribute.FOREGROUND, DEFAULT_FOREGROUND);

                // color code peptides
                addPeptideAnnotations(protein, formattedSequence);
            }
        }
        return formattedSequence;
    }

    /**
     * Add peptide annotation to the attributed protein sequence
     *
     * @param protein           annotated protein
     * @param formattedSequence formmated protein sequence
     */
    private static void addPeptideAnnotations(AnnotatedProtein protein, AttributedString formattedSequence) {
        java.util.List<PeptideAnnotation> peptides = protein.getAnnotations();
        if (peptides.size() > 0) {
            int numOfValidPeptides = 0;

            // remove invalid peptide
            String sequence = protein.getSequenceString();
            if (sequence != null && !"".equals(sequence)) {
                Iterator<PeptideAnnotation> peptideIter = peptides.iterator();
                while (peptideIter.hasNext()) {
                    PeptideAnnotation peptideAnnotation = peptideIter.next();
                    if (!protein.isValidPeptideAnnotation(peptideAnnotation)) {
                        peptideIter.remove();
                    } else {
                        numOfValidPeptides++;
                    }
                }
            }

            // set number of Valid peptides
            protein.setNumOfValidPeptides(numOfValidPeptides);

            // keep only unique peptides
            Set<PeptideAnnotation> uniquePeptides = new LinkedHashSet<PeptideAnnotation>();
            uniquePeptides.addAll(peptides);
            protein.setNumOfUniquePeptides(uniquePeptides.size());

            // peptide coverage array
            // it is the length of the protein sequence, and contains the count of sequence coverage for each position
            int length = protein.getSequenceString().trim().length();
            int[] coverageArr = new int[length];
            int[] ptmArr = new int[length];
            for (PeptideAnnotation uniquePeptide : uniquePeptides) {
                Set<Integer> startingPos = new HashSet<Integer>();
                boolean strictValidPeptideAnnotation = protein.isStrictValidPeptideAnnotation(uniquePeptide);
                if (strictValidPeptideAnnotation) {
                    startingPos.add(uniquePeptide.getStart() - 1);
                } else {
                    startingPos.addAll(protein.searchStartingPosition(uniquePeptide));
                }

                for (Integer start : startingPos) {
                    // if the position does match
                    int peptideLen = uniquePeptide.getSequence().length();
                    int end = start + peptideLen - 1;
                    boolean selected = uniquePeptide.equals(protein.getSelectedAnnotation());

                    // iterate peptide
                    for (int i = start; i <= end; i++) {
                        if (selected) {
                            // selected
                            coverageArr[i] = PeptideFitState.SELECTED;
                        } else if (coverageArr[i] != PeptideFitState.SELECTED) {
                            // not selected
                            switch (coverageArr[i]) {
                                case PeptideFitState.STRICT_FIT:
                                    coverageArr[i] = PeptideFitState.OVERLAP;
                                    break;
                                case PeptideFitState.FIT:
                                    coverageArr[i] = PeptideFitState.OVERLAP;
                                    break;
                                case PeptideFitState.NOT_FIT:
                                    coverageArr[i] = strictValidPeptideAnnotation ? PeptideFitState.STRICT_FIT : PeptideFitState.FIT;
                                    break;
                            }
                        }
                    }

                    // ptms
                    java.util.List<PTMAnnotation> ptms = uniquePeptide.getPtmAnnotations();
                    for (PTMAnnotation ptm : ptms) {
                        int location = ptm.getLocation();
                        if (location >= 0) {
                            if (location > 0 && location <= peptideLen) {
                                location -= 1;
                            } else if (location == peptideLen + 1) {
                                location -= 2;
                            }

                            ptmArr[start + location] = selected ? PTMFitState.SELECTED : PTMFitState.FIT;
                        }
                    }
                }
            }

            // colour code the peptide positions
            int numOfAminoAcidCovered = 0;
            for (int i = 0; i < coverageArr.length; i++) {
                int count = coverageArr[i];
                int index = mapIndex(i);
                if (count != PeptideFitState.NOT_FIT) {
                    numOfAminoAcidCovered++;
                }
                addAminoAcidAttributes(formattedSequence, index, count);
            }
            // set number of amino acid being covered
            protein.setNumOfAminoAcidCovered(numOfAminoAcidCovered);


            // colour code the ptm positions
            addPTMAttributes(formattedSequence, ptmArr);
        }
    }

    /**
     * Add text attributes for one amino acid
     *
     * @param formattedSequence attributed string
     * @param index             index of the amino acid
     * @param type              type of the amino acid
     */
    private static void addAminoAcidAttributes(AttributedString formattedSequence, int index, int type) {
        switch (type) {
            case PeptideFitState.SELECTED:
                formattedSequence.addAttribute(TextAttribute.BACKGROUND, Constants.PEPTIDE_HIGHLIGHT_COLOUR, index, index + 1);
                formattedSequence.addAttribute(TextAttribute.FOREGROUND, PEPTIDE_FOREGROUND_COLOUR, index, index + 1);
                break;
            case PeptideFitState.STRICT_FIT:
                formattedSequence.addAttribute(TextAttribute.BACKGROUND, Constants.STRICT_FIT_PEPTIDE_BACKGROUND_COLOUR, index, index + 1);
                formattedSequence.addAttribute(TextAttribute.FOREGROUND, PEPTIDE_FOREGROUND_COLOUR, index, index + 1);
                break;
            case PeptideFitState.FIT:
                formattedSequence.addAttribute(TextAttribute.BACKGROUND, Constants.FIT_PEPTIDE_BACKGROUND_COLOUR, index, index + 1);
                formattedSequence.addAttribute(TextAttribute.FOREGROUND, PEPTIDE_FOREGROUND_COLOUR, index, index + 1);
                break;
            case PeptideFitState.OVERLAP:
                formattedSequence.addAttribute(TextAttribute.BACKGROUND, Constants.PEPTIDE_OVERLAP_COLOUR, index, index + 1);
                formattedSequence.addAttribute(TextAttribute.FOREGROUND, PEPTIDE_FOREGROUND_COLOUR, index, index + 1);
                break;
        }

    }


    /**
     * Add text attributes for ptms
     *
     * @param formattedSequence attributed string
     * @param ptmArr            ptm array contains ptm information
     */
    private static void addPTMAttributes(AttributedString formattedSequence, int[] ptmArr) {
        for (int i = 0; i < ptmArr.length; i++) {
            int count = ptmArr[i];
            int index = mapIndex(i);

            switch (count) {
                case PTMFitState.FIT:
                    formattedSequence.addAttribute(TextAttribute.BACKGROUND, Constants.PTM_BACKGROUND_COLOUR, index, index + 1);
                    break;
                case PTMFitState.SELECTED:
                    formattedSequence.addAttribute(TextAttribute.BACKGROUND, Constants.PTM_HIGHLIGHT_COLOUR, index, index + 1);
                    break;
            }
        }
    }

    /**
     * Format protein sequence by inserting segment gaps
     * This is displaying purpose
     *
     * @param sequence protein sequence
     * @return String  formatted protein sequence
     */
    private static String insertSegmentGapToSequence(String sequence) {
        StringBuilder stringBuilder = new StringBuilder();
        char[] cArr = sequence.trim().toCharArray();
        for (int i = 0; i < cArr.length; i++) {
            if (i != 0 && i % PROTEIN_SEGMENT_LENGTH == 0) {
                stringBuilder.append(PROTEIN_SEGMENT_GAP);
            }
            stringBuilder.append(cArr[i]);
        }
        return stringBuilder.toString();
    }

    /**
     * map a index of original protein sequence to a newly formatted protein sequence
     *
     * @param index original index
     * @return int mapped index
     */
    private static int mapIndex(int index) {
        return ((index - index % PROTEIN_SEGMENT_LENGTH) / PROTEIN_SEGMENT_LENGTH) * PROTEIN_SEGMENT_GAP.length() + index;
    }
}
