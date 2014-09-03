package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import uk.ac.ebi.pride.utilities.data.Tuple;
import uk.ac.ebi.pride.utilities.data.core.Modification;
import uk.ac.ebi.pride.utilities.data.core.PeptideSequence;
import uk.ac.ebi.pride.toolsuite.gui.utils.ProteinAccession;
import uk.ac.ebi.pride.util.NumberUtilities;

import java.util.*;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class PeptideSpeciesTableModel extends ProgressiveListTableModel<Void, Tuple<TableContentType, Object>> {

    public enum TableHeader {
        PEPTIDE_COLUMN("Peptide", "Peptide Sequence"),
        PROTEIN_COLUMN("Protein", "Protein accessions"),
        MODIFICATION_COLUMN("Modification", "Modifications"),
        NUMBER_OF_PSM_COLUMN("#PSM", "Number of PSMs"),
        NUMBER_OF_DELTA_MZ_ERROR_COLUMN("#Delta m/z error", "Number of delta m/z errors"),
        PEPTIDE_LENGTH_COLUMN("Length", "Peptide length"),
        THEORETICAL_ISOELECTRIC_POINT_COLUMN("pI", "Theoretical isoelectric point"),
        PEPTIDE_SPECIES_COLUMN("Peptide species", "Peptide species");

        private final String header;
        private final String toolTip;

        private TableHeader(String header, String tooltip) {
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

    private int rankingThreshold;
    private final double minDeltaMz;
    private final double maxDeltaMz;
    private final List<PeptideSpecies> showingPeptideSpecies;
    private final Map<String, PeptideSpecies> peptideSequenceToPeptideSpeciesMappings;

    public PeptideSpeciesTableModel(int rankingThreshold, double minDeltaMz, double maxDeltaMz) {
        this.showingPeptideSpecies = new ArrayList<PeptideSpecies>();
        this.peptideSequenceToPeptideSpeciesMappings = new LinkedHashMap<String, PeptideSpecies>();
        this.rankingThreshold = rankingThreshold;
        this.minDeltaMz = minDeltaMz;
        this.maxDeltaMz = maxDeltaMz;

        initializeTableModel();
    }

    @Override
    public void initializeTableModel() {
        TableHeader[] headers = TableHeader.values();

        for (TableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
        }
    }

    @Override
    public int getRowCount() {
        return showingPeptideSpecies.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        PeptideSpecies peptideSpecies = showingPeptideSpecies.get(rowIndex);

        if (getColumnName(columnIndex).equals(TableHeader.PEPTIDE_COLUMN.getHeader())) {
            return peptideSpecies.getSequence();
        } else if (getColumnName(columnIndex).equals(TableHeader.NUMBER_OF_PSM_COLUMN.getHeader())) {
            return peptideSpecies.getNumberOfPSMs();
        } else if (getColumnName(columnIndex).equals(TableHeader.PROTEIN_COLUMN.getHeader())) {
            return peptideSpecies.getProteinAccessions();
        } else if (getColumnName(columnIndex).equals(TableHeader.MODIFICATION_COLUMN.getHeader())) {
            return peptideSpecies.getModifications();
        } else if (getColumnName(columnIndex).equals(TableHeader.NUMBER_OF_DELTA_MZ_ERROR_COLUMN.getHeader())) {
            return peptideSpecies.getNumberOfDeltaMzErrors();
        } else if (getColumnName(columnIndex).equals(TableHeader.PEPTIDE_LENGTH_COLUMN.getHeader())) {
            return peptideSpecies.getLength();
        } else if (getColumnName(columnIndex).equals(TableHeader.THEORETICAL_ISOELECTRIC_POINT_COLUMN.getHeader())) {
            return peptideSpecies.getTheoreticalIsoelectricPoint();
        } else if (getColumnName(columnIndex).equals(TableHeader.PEPTIDE_SPECIES_COLUMN.getHeader())) {
            return peptideSpecies;
        } else {
            return null;
        }
    }

    public void setRankingThreshold(int rankingThreshold) {
        if (this.rankingThreshold != rankingThreshold) {
            this.rankingThreshold = rankingThreshold;

            showingPeptideSpecies.clear();

            for (PeptideSpecies peptideSpecies : peptideSequenceToPeptideSpeciesMappings.values()) {
                peptideSpecies.clearStats();

                for (PeptideTableRow peptideTableRow : peptideSpecies.getPeptideTableRowData()) {
                    updatePeptideSpeciesStats(peptideSpecies, peptideTableRow);
                }

                if (peptideSpecies.getNumberOfPSMs() > 0) {
                    showingPeptideSpecies.add(peptideSpecies);
                }

            }

            fireTableDataChanged();
        }
    }

    @Override
    public void addData(Tuple<TableContentType, Object> newData) {
        TableContentType type = newData.getKey();

        if (TableContentType.PEPTIDE.equals(type)) {

            PeptideTableRow peptideTableRowData = (PeptideTableRow) newData.getValue();

            PeptideSpecies peptideSpecies = getPeptideSpecies(peptideTableRowData);

            if (!hasPSM(peptideSpecies, peptideTableRowData)) {
                peptideSpecies.addPeptideTableRowData(peptideTableRowData);

                updatePeptideSpeciesStats(peptideSpecies, peptideTableRowData);
            }

            notifyTableModelUpdate(peptideSpecies);

        }
    }

    private void notifyTableModelUpdate(PeptideSpecies peptideSpecies) {
        boolean peptideSpeciesAlreadyExist = showingPeptideSpecies.contains(peptideSpecies);

        if (isShowablePeptideSpecies(peptideSpecies)) {
            if (peptideSpeciesAlreadyExist) {
                int rowIndex = showingPeptideSpecies.indexOf(peptideSpecies);
                fireTableRowsUpdated(rowIndex, rowIndex);
            } else {
                int maxRow = showingPeptideSpecies.size();
                showingPeptideSpecies.add(peptideSpecies);
                fireTableRowsInserted(maxRow, maxRow);
            }
        } else if (showingPeptideSpecies.contains(peptideSpecies)) {
            int rowIndex = showingPeptideSpecies.indexOf(peptideSpecies);
            showingPeptideSpecies.remove(peptideSpecies);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }

    }

    private boolean isShowablePeptideSpecies(PeptideSpecies peptideSpecies) {
        return peptideSpecies.getNumberOfPSMs() > 0;
    }

    private PeptideSpecies getPeptideSpecies(PeptideTableRow peptideTableRowData) {
        // sequence
        PeptideSequence peptide = peptideTableRowData.getSequence();
        String peptideSequence = getModifiedPeptideString(peptide.getModifications(), peptide.getSequence());

        PeptideSpecies peptideSpecies = peptideSequenceToPeptideSpeciesMappings.get(peptideSequence);
        if (peptideSpecies == null) {
            peptideSpecies = new PeptideSpecies(peptide);
            peptideSequenceToPeptideSpeciesMappings.put(peptideSequence, peptideSpecies);
        }

        return peptideSpecies;
    }

    private static String getModifiedPeptideString(List<Modification> mods, String sequence) {
        // Map for grouping PTMs based on location
        Map<Integer, List<Double>> locationMap = new LinkedHashMap<Integer, List<Double>>();

        // Iterate over each modification
        for (Modification mod : mods) {
            // store the location
            int location = mod.getLocation();
            if (location == 0) {
                location = 1;
            } else if (location == (sequence.length() + 1)) {
                location--;
            }

            List<Double> massDiffs = locationMap.get(location);
            if (massDiffs == null) {
                massDiffs = new ArrayList<Double>();
                locationMap.put(location, massDiffs);
                List<Double> md = mod.getMonoisotopicMassDelta();
                if (md != null && !md.isEmpty()) {
                    massDiffs.add(md.get(0));
                }
            }
        }

        // Modified Peptide Sequence
        //todo: can be simplified when there is no modifications
        StringBuilder modPeptide = new StringBuilder();
        for (int i = 0; i < sequence.length(); i++) {
            // append the amino acid
            modPeptide.append(sequence.charAt(i));
            // append mass differences if there is any
            List<Double> massDiffs = locationMap.get(i + 1);
            if (massDiffs != null) {
                modPeptide.append("[");
                if (massDiffs.isEmpty()) {
                    modPeptide.append("*");
                } else {
                    for (int j = 0; j < massDiffs.size(); j++) {
                        if (j != 0) {
                            modPeptide.append(",");
                        }
                        modPeptide.append(NumberUtilities.scaleDouble(massDiffs.get(j), 1));
                    }
                }
                modPeptide.append("]");
            }
        }

        return modPeptide.toString();
    }

    private void updatePeptideSpeciesStats(PeptideSpecies peptideSpecies, PeptideTableRow peptideTableRowData) {
        // modification
        Object modificationText = peptideTableRowData.getModificationNames();
        peptideSpecies.setModifications((String) modificationText);

        // ranking
        Integer ranking = peptideTableRowData.getRanking();

        if (ranking == null || ranking <= rankingThreshold) {
            // psm
            peptideSpecies.setNumberOfPSMs(peptideSpecies.getNumberOfPSMs() + 1);

            // protein accession
            ProteinAccession proteinAccession = peptideTableRowData.getProteinAccession();
            peptideSpecies.addProteinAccession(proteinAccession);

            // delta m/z error
            Object deltaMz = peptideTableRowData.getDeltaMz();
            if (deltaMz == null) {
                peptideSpecies.setNumberOfDeltaMzErrors(peptideSpecies.getNumberOfDeltaMzErrors() + 1);
            } else {
                double delta = (Double) deltaMz;
                if (delta < minDeltaMz || delta > maxDeltaMz) {
                    peptideSpecies.setNumberOfDeltaMzErrors(peptideSpecies.getNumberOfDeltaMzErrors() + 1);
                }
            }
        }
    }


    private boolean hasPSM(PeptideSpecies peptideSpecies, PeptideTableRow peptideTableRowData) {
        List<PeptideTableRow> peptideTableRows = peptideSpecies.getPeptideTableRowData();

        if (peptideTableRows.size() != 0) {
            Object spectrumId = peptideTableRowData.getSpectrumId();

            for (PeptideTableRow peptideTableRow : peptideTableRows) {
                PeptideTableRow peptideDetails = peptideTableRow;
                Object existingSpectrumId = peptideDetails.getSpectrumId();

                if (existingSpectrumId != null && existingSpectrumId.equals(spectrumId)) {
                    return true;
                }
            }
        }

        return false;
    }
}
