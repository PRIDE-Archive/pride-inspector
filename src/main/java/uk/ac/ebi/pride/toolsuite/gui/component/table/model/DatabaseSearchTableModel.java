package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import uk.ac.ebi.pride.toolsuite.gui.event.DatabaseSearchEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Table model for database search table
 * <p/>
 * User: rwang
 * Date: 02/06/11
 * Time: 15:23
 */
public class DatabaseSearchTableModel extends ListBasedListTableModel<List<Object>> {
    /**
     * table column title
     */
    public enum TableHeader {
        VIEW("View", "View experiment"),
        EXPERIMENT_ACCESSION("Accession", "PRIDE Experiment Accession"),
        EXPERIMENT_TITLE("Title", "PRIDE Experiment Title"),
        PROJECT("Project", "PRIDE Project Name"),
        SPECIES("Species", "Sample Species"),
        TAXONOMY_ID("Taxonomy ID", "Sample Taxonomy ID"),
        TISSUE("Tissue", "Sample Tissue"),
        BRENDA_ID("BRENDA ID (Tissue)", "Tissue's BRENDA ID"),
        PTM("PTM", "Post Translational Modifications"),
        NUMBER_OF_SPECTRA("#Spectra", "Number of spectra"),
        NUMBER_OF_PROTEIN("#Proteins", "Number of proteins"),
        NUMBER_OF_PEPTIDE("#Peptides", "Number of peptides"),
        REFERENCE("Reference", "Full Reference Line"),
        PUBMED_ID("PubMed ID", "PubMed ID");

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

    public DatabaseSearchTableModel() {
        // enable annotation
        AnnotationProcessor.process(this);
    }

    @Override
    public void initializeTableModel() {
        for (TableHeader tableHeader : TableHeader.values()) {
            addColumn(tableHeader.getHeader(), tableHeader.getToolTip());
        }
    }

    @Override
    public void addData(List<Object> newData) {
        int rowCnt = this.getRowCount();
        contents.add(newData);

        fireTableRowsInserted(rowCnt, rowCnt);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        List<Object> content = (List<Object>) contents.get(rowIndex);
        if (content != null) {
            content.set(columnIndex, aValue);
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    /**
     * Get all the values of search results
     * Note: the result excludes the the row number, selection column and view column
     *
     * @return List<List<Object>>  all the actual values in the table
     */
    public List<List<String>> getAllContent() {
        List<List<String>> results = new ArrayList<List<String>>();
        int startIndexOfContent = getStartIndexOfContent();
        for (Object content : contents) {
            List<Object> contentList = (List<Object>) content;
            List<String> result = new ArrayList<String>();
            for (int i = startIndexOfContent; i < contentList.size(); i++) {
                result.add(contentList.get(i).toString());
            }
            results.add(result);
        }
        return results;
    }

    private int getStartIndexOfContent() {
        int index = -1;

        for (TableHeader tableHeader : TableHeader.values()) {
            index++;
            if (tableHeader.equals(TableHeader.EXPERIMENT_ACCESSION)) {
                break;
            }
        }

        return index;
    }

    /**
     * Get all the headers of the table without the row number, view column
     *
     * @return List<String>    a list of headers
     */
    public List<String> getAllHeaders() {
        List<String> headers = new ArrayList<String>();
        int cnt = this.getColumnCount();
        for (int i = 1; i < cnt; i++) {
            headers.add(this.getColumnName(i));
        }
        return headers;
    }

    @EventSubscriber(eventClass = DatabaseSearchEvent.class)
    public void onDatabaseSearchEvent(DatabaseSearchEvent evt) {
        if (DatabaseSearchEvent.Status.RESULT.equals(evt.getStatus())) {
            List<List<Object>> newData = (List<List<Object>>) evt.getResult();
            for (List<Object> data : newData) {
                addData(data);
            }
        }
    }
}
