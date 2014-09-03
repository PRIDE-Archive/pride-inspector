package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

/**
 * @author rwang, ypriverol
 * @version $Id$
 */
public enum PeptideTableHeader {
    PEPTIDE_COLUMN("Peptide", "Peptide Sequence"),
    PROTEIN_ACCESSION_COLUMN("Protein", "Protein Accession"),
    PROTEIN_NAME("Protein Name", "Protein Name Retrieved Using Web"),
    PROTEIN_STATUS("Status", "Status Of The Protein Accession"),
    PROTEIN_SEQUENCE_COVERAGE("Coverage", "Protein Sequence Coverage"),
    PEPTIDE_FIT("Fit", "Peptide Sequence Fit In Protein Sequence"),
    RANKING("Ranking", "Ranking"),
    DELTA_MZ_COLUMN("Delta m/z", "Delta m/z [Experimental m/z - Theoretical m/z]"),
    PRECURSOR_CHARGE_COLUMN("Charge", "Precursor Charge"),
    PRECURSOR_MZ_COLUMN("Precursor m/z", "Precursor m/z"),
    PEPTIDE_MODIFICATION_COLUMN("Modifications", "Post translational modifications"),
    NUMBER_OF_FRAGMENT_IONS_COLUMN("# Ions", "Number of Fragment Ions"),
    PEPTIDE_SEQUENCE_LENGTH_COLUMN("Length", "Length"),
    SEQUENCE_START_COLUMN("Start", "Start Position"),
    SEQUENCE_END_COLUMN("Stop", "Stop Position"),
    SPECTRUM_ID("Spectrum Id", "Spectrum Reference"),
    IDENTIFICATION_ID("Identification Id", "Identification Id"),
    PEPTIDE_ID("Peptide Id", "Peptide Id"),
    ADDITIONAL("More", "Additional Details");

    private final String header;
    private final String toolTip;

    private PeptideTableHeader(String header, String tooltip) {
        this.header = header;
        this.toolTip = tooltip;
    }

    public String getHeader() {
        return header;
    }

    public String getToolTip() {
        return toolTip;
    }

    public static int getHeaderIndex(PeptideTableHeader header) {
        PeptideTableHeader[] headers = PeptideTableHeader.values();
        for (int i = 0; i < headers.length; i++) {
            PeptideTableHeader peptideTableHeader = headers[i];
            if (peptideTableHeader.equals(header)) {
                return i;
            }
        }
        return -1;
    }
}
