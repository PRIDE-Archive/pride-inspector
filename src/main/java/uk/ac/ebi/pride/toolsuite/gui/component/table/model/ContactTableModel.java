package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import uk.ac.ebi.pride.utilities.data.core.CvParam;
import uk.ac.ebi.pride.utilities.data.core.ParamGroup;
import uk.ac.ebi.pride.utilities.data.core.Person;
import uk.ac.ebi.pride.utilities.data.core.UserParam;
import uk.ac.ebi.pride.utilities.term.CvTermReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Table model for contact details
 * <p/>
 * User: rwang
 * Date: 24/07/2011
 * Time: 11:50
 */
public class ContactTableModel extends ListBasedListTableModel<ParamGroup> {

    /**
     * table column title
     */
    public enum TableHeader {
        NAME("Name", "Name Of The Contact"),
        INSTITUTE("Institute", "Institute Or Organization Of The Contact"),
        INFORMATION("Information", "Contact Information");

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

    private static final String CONTACT_INFORMATION = "contact information";

    private List<Person> contacts;

    public ContactTableModel(Collection<Person> contacts) {
        this.contacts = new ArrayList<Person>();

        // add contacts
        for (Person contact : contacts) {
            addData(new ParamGroup(contact.getCvParams(), contact.getUserParams()));
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
    public void addData(ParamGroup newData) {
        List<Object> data = new ArrayList<Object>();
        String name = null;
        String institute = null;
        String info = null;

        // row count
        int rowCnt = this.getRowCount();

        // contact name
        // contact institute
        List<CvParam> cvs = newData.getCvParams();
        if (!cvs.isEmpty()) {
            for (CvParam cv : cvs) {
                String acc = cv.getAccession();
                if (CvTermReference.CONTACT_NAME.getAccession().equals(acc)) {
                    name = cv.getValue();
                } else if (CvTermReference.CONTACT_ORG.getAccession().equals(acc)) {
                    institute = cv.getValue();
                } else if (CvTermReference.CONTACT_EMAIL.getAccession().equals(acc)) {
                    info = cv.getValue();
                }
            }
        }

        // contact information
        List<UserParam> ups = newData.getUserParams();
        if (!ups.isEmpty()) {
            for (UserParam up : ups) {
                if (CONTACT_INFORMATION.equals(up.getName())) {
                    info = up.getValue();
                }
            }
        }

        data.add(name);
        data.add(institute);
        data.add(info);

        this.addRow(data);
        fireTableRowsInserted(rowCnt, rowCnt);
    }
}
