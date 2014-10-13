package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

/**
 * @author rwang
 * @author ypriverol
 * @version $Id$
 */
public enum ProteinTableHeader {
    PROTEIN_GROUP_ID("Protein Group ID", "Protein Group ID"),
    PROTEIN_ACCESSION("Protein", "Protein Accession"),
    COMPARE("Compare", "Click to choose the protein you want to compare"),
    PROTEIN_NAME("Protein Name", "Protein Name Retrieved Using Web"),
    PROTEIN_STATUS("Status", "Status Of The Protein Accession"),
    PROTEIN_SEQUENCE_COVERAGE("Coverage", "Protein Sequence Coverage"),
    THEORETICAL_ISOELECTRIC_POINT("pI", "Theoretical isoelectric point"),
    IDENTIFICATION_THRESHOLD("Threshold", "PRIDE Protein Threshold"),
    NUMBER_OF_PEPTIDES("#PSMs", "Number of peptide spectrum matches"),
    NUMBER_OF_UNIQUE_PEPTIDES("#Distinct Peptides", "Number of Distinct Peptides"),
    NUMBER_OF_PTMS("#PTMs", "Number of PTMs"),
    PROTEIN_ID("Protein ID", "Protein ID"),
    ADDITIONAL("More", "Additional Details");

    private final String header;
    private final String toolTip;

    private ProteinTableHeader(String header, String tooltip) {
        this.header = header;
        this.toolTip = tooltip;
    }

    public String getHeader() {
        return header;
    }

    public String getToolTip() {
        return toolTip;
    }
}
