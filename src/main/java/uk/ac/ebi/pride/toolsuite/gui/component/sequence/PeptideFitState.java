package uk.ac.ebi.pride.toolsuite.gui.component.sequence;

/**
 * This class defines various of states for peptide sequence fitting protein sequence
 *
 * User: rwang
 * Date: 24/06/11
 * Time: 11:24
 */
public class PeptideFitState {
    public final static int UNKNOWN = -2;
    public final static int SELECTED = -1;
    public final static int NOT_FIT = 0;
    public final static int STRICT_FIT = 1;
    public final static int FIT = 2;
    public final static int OVERLAP = 3;
}
