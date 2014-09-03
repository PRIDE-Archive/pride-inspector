package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import uk.ac.ebi.pride.utilities.data.core.CvParam;
import uk.ac.ebi.pride.utilities.data.core.Reference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Table model to display reference metadata
 * <p/>
 * User: rwang
 * Date: 24/07/2011
 * Time: 08:15
 */
public class ReferenceTableModel extends ListBasedListTableModel<Reference> {
    /**
     * table column title
     */
    public enum TableHeader {
        REFERENCE_DESCRIPTION("Reference", "Full Details of the reference"),
        PUBMED("PubMed", "Reference ID From PubMed"),
        DOI("DOI", "Reference ID From DOI");

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

    public ReferenceTableModel(Collection<Reference> references) {
        // add the references to table model
        for (Reference reference : references) {
            addData(reference);
        }
    }

    @Override
    public void initializeTableModel() {
        // initialize table headers
        TableHeader[] headers = TableHeader.values();
        for (TableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
        }
    }

    @Override
    public void addData(Reference newData) {
        List<Object> data = new ArrayList<Object>();
        // row count
        int rowCnt = this.getRowCount();
        // add reference
        data.add(newData.getFullReference());

        String pubmed = null;
        String doi = null;

        List<CvParam> cvs = newData.getCvParams();
        if (!cvs.isEmpty()) {
            // pubmed
            for (CvParam cv : cvs) {
                String name = cv.getName();
                String value = cv.getValue();
                if (cv.getCvLookupID().toLowerCase().equals("pubmed") || cv.getName().toLowerCase().equals("pubmed")) {
                    pubmed = cv.getAccession();
                } else if (cv.getCvLookupID().toLowerCase().equals("doi") || cv.getName().toLowerCase().equals("doi")) {
                    doi = (name != null && !"".equals(name)) ? name : value;
                }
            }
        }

        data.add(pubmed);
        data.add(doi);

        this.addRow(data);
        fireTableRowsInserted(rowCnt, rowCnt);
    }
}
