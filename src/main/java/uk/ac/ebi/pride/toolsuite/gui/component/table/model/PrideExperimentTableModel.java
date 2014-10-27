package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import uk.ac.ebi.pride.utilities.util.NumberUtilities;

import java.io.Serializable;
import java.util.*;

/**
 * PrideExperimentTableModel contains the details that displayed in review donwload table
 * User: rwang
 * Date: 11-Sep-2010
 * Time: 14:02:07
 */
public class PrideExperimentTableModel extends ListBasedListTableModel<List<Map<String, String>>> {

    public enum TableHeader {
        EXP_ACC_COLUMN("Accession", "Pride Experiment Accession"),
        SIZE_COLUMN("Size (M)", "Download File Size (M)"),
        EXP_TITLE_COLUMN("Title", "Experiment Title"),
        SPECIES_COLUMN("Species", "Species"),
        SPECTRA_CNT_COLUMN("Spectra", "Number of Spectra"),
        PROTEIN_CNT_COLUMN("Protein", "Number of Proteins"),
        PEPTIDE_CNT_COLUMN("Peptide", "Number of Peptides"),
        DOWNLOAD_COLUMN("Download", "Download Option");

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

    @Override
    public void initializeTableModel() {
        TableHeader[] headers = TableHeader.values();
        for (TableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
        }
    }

    @Override
    public void addData(List<Map<String, String>> data) {
        contents.clear();
        if (data != null && !data.isEmpty()) {
            Collections.sort(data, new MetaDataComparator());
            for (Map<String, String> exp : data) {
                List<Object> content = new ArrayList<Object>();
                String accession = exp.get("Accession");
                if (!isAlreadyExist(accession)) {
                    content.add(Integer.parseInt(accession));
                    double size = Double.valueOf(exp.get("Size"));
                    size = NumberUtilities.scaleDouble(size/(1024*1024), 2);
                    content.add(size);
                    content.add(exp.get("Title"));
                    content.add(exp.get("Species"));
                    content.add(Integer.valueOf(exp.get("Spectrum Count")));
                    content.add(Integer.valueOf(exp.get("Identification Count")));
                    content.add(Integer.valueOf(exp.get("Peptide Count")));
                    content.add(true);
                    contents.add(content);
                }
            }
        }
    }

    private boolean isAlreadyExist(String accession) {
        boolean exist = false;
        for (Object content : contents) {
            List<Object> contentList = (List<Object>)content;
            if (contentList.get(0).equals(accession)) {
                exist = true;
            }
        }
        return exist;
    }

    /**
     * Enable cell editor
     *
     * @param columnIndex column number
     * @return Class<?> column class type
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Object val = getValueAt(0, columnIndex);
        return val == null ? Object.class : val.getClass();
    }

    /**
     * Is Cell content is Boolean then it is editable
     *
     * @param rowIndex    row number
     * @param columnIndex column number
     * @return boolean  true if cell is editable
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        boolean editable = false;
        Object val = getValueAt(rowIndex, columnIndex);
        if (val instanceof Boolean) {
            editable = true;
        }
        return editable;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        List<Object> content = (List<Object>)contents.get(rowIndex);
        content.set(columnIndex, aValue);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    public static class MetaDataComparator implements Comparator<Map<String, String>>, Serializable {

        @Override
        public int compare(Map<String, String> o1, Map<String, String> o2) {
            int acc1 = Integer.parseInt(o1.get("Accession"));
            int acc2 = Integer.parseInt(o2.get("Accession"));
            
            if (acc1 > acc2) {
                return 1;
            } else if (acc1 < acc2) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
