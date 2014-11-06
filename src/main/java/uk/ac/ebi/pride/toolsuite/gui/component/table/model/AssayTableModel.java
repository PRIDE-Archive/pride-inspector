package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.pride.archive.web.service.model.assay.AssayDetail;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class AssayTableModel extends ProgressiveListTableModel<Void, AssayDetail>{

    public enum TableHeader {
        ACCESSION ("Accession", "Project accession"),
        TITLE ("Title", "Project title"),
        SPECIES ("Species", "Species"),
        MODIFICATION ("Modification", "Modification"),
        INSTRUMENT ("Instrument", "Instrument"),
        SOFTWARE ("Software", "Software"),
        NUM_OF_PROTEIN ("#Proteins", "Number of proteins"),
        NUM_OF_PEPTIDE ("#Peptides", "Number of peptides"),
        NUM_OF_UNIQUE_PEPTIDE ("#Unique peptides", "Number of unique peptides"),
        NUM_OF_IDENTIFIED_SPECTRA ("#Identified spectra", "Number of identified spectra"),
        NUM_OF_SPECTRA ("#Spectra", "Number of spectra"),
        DOWNLOAD("Download", "Download and open assay files");

        private final String header;
        private final String toolTip;

        TableHeader(String header, String toolTip) {
            this.header = header;
            this.toolTip = toolTip;
        }

        public String getHeader() {
            return header;
        }

        public String getToolTip() {
            return toolTip;
        }
    }

    @Override
    public void initializeTableModel() {
        columnNames.clear();

        for (TableHeader header : TableHeader.values()) {
            columnNames.put(header.getHeader(), header.getToolTip());
        }
    }

    @Override
    public void addData(AssayDetail newData) {
        int rowCnt = this.getRowCount();
        addRow(newData);
        fireTableRowsInserted(rowCnt, rowCnt);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || columnIndex <0 || rowIndex >= getRowCount() || columnIndex >= TableHeader.values().length) {
            return null;
        }

        AssayDetail assayDetail = (AssayDetail)getRow(rowIndex);
        String columnName = getColumnName(columnIndex);

        if (TableHeader.ACCESSION.getHeader().equals(columnName))  {
            return assayDetail.getAssayAccession();
        } else if (TableHeader.TITLE.getHeader().equals(columnName)) {
            return assayDetail.getTitle();
        } else if (TableHeader.SPECIES.getHeader().equals(columnName)) {
            return StringUtils.join(assayDetail.getSpecies(), ",");
        } else if (TableHeader.MODIFICATION.getHeader().equals(columnName)) {
            return StringUtils.join(assayDetail.getPtmNames(), ",");
        } else if (TableHeader.INSTRUMENT.getHeader().equals(columnName)) {
            return StringUtils.join(assayDetail.getInstrumentNames(), ",");
        } else if (TableHeader.SOFTWARE.getHeader().equals(columnName)) {
            return StringUtils.join(assayDetail.getSoftwares(), ",");
        } else if (TableHeader.NUM_OF_PROTEIN.getHeader().equals(columnName)) {
            return assayDetail.getProteinCount();
        } else if (TableHeader.NUM_OF_PEPTIDE.getHeader().equals(columnName)) {
            return assayDetail.getPeptideCount();
        } else if (TableHeader.NUM_OF_UNIQUE_PEPTIDE.getHeader().equals(columnName)) {
            return assayDetail.getUniquePeptideCount();
        } else if (TableHeader.NUM_OF_IDENTIFIED_SPECTRA.getHeader().equals(columnName)) {
            return assayDetail.getIdentifiedSpectrumCount();
        } else if (TableHeader.NUM_OF_SPECTRA.getHeader().equals(columnName)) {
            return assayDetail.getTotalSpectrumCount();
        } else if (TableHeader.DOWNLOAD.getHeader().equals(columnName)) {
            return assayDetail.getAssayAccession();
        }

        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return TableHeader.DOWNLOAD.getHeader().equals(getColumnName(columnIndex)) || super.isCellEditable(rowIndex, columnIndex);
    }

}
