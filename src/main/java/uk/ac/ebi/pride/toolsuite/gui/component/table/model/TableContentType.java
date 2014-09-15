package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

/**
 * This enum is used to indicate the type of the table content.
 * <p/>
 * This enum is created for table models to listen to different type incoming data content,
 * and filter out the irrelevant ones.
 * <p/>
 * @author rwang
 * @author ypriverol
 * Date: 14-Sep-2010
 * Time: 11:38:09
 */
public enum TableContentType {
    /**
     * spectrum table
     */
    SPECTRUM,
    /**
     * chromatogram table
     */
    CHROMATOGRAM,
    /**
     * identification table
     */
    PROTEIN,
    /**
     * peptide table
     */
    PEPTIDE,
    /**
     * peptide species
     */
    PEPTIDE_SPECIES,
    /**
     * ptm table
     */
    PTM,
    /**
     * Review table
     */
    REVIEW,
    /**
     * Protein details
     */
    PROTEIN_DETAILS,
    /**
     * Protein sequence coverage
     */
    PROTEIN_SEQUENCE_COVERAGE,

    /**
     * whether peptide fit the protein sequence
     */
    PEPTIDE_FIT,
    /**
     * Quantification for protein
     */
    PROTEIN_QUANTITATION_HEADER,
    /**
     * Quantification for protein
     */
    PROTEIN_QUANTITATION,
    /**
     * Quantification for peptide
     */
    PEPTIDE_QUANTITATION_HEADER,
    /**
     * Quantification for peptide
     */
    PEPTIDE_QUANTITATION,

    PEPTIDE_DELTA,

    PEPTIDE_PRECURSOR_MZ
}
