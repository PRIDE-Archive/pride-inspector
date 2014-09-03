package uk.ac.ebi.pride.toolsuite.gui.component.table.filter;

import javax.swing.*;

/**
 * User: rwang
 * Date: 01/09/2011
 * Time: 12:19
 */
public class DecoyAccessionFilter extends RowFilter {
    public enum Type {PREFIX, POSTFIX, CONTAIN}

    /**
     * Type of the matching mechanism
     */
    private Type type;

    /**
     * Matching criteria
     */
    private String criteria;
    /**
     * Index of the protein accession column
     */
    private int accessionColumnIndex;

    /**
     * whether to show only decoy accessions
     */
    private boolean decoyOnly;

    /**
     * Constructor
     *
     * @param type                 type of the matching mechanism
     * @param criteria             matching criteria
     * @param accessionColumnIndex index of the protein accession
     * @param decoyOnly            whether to show only decoy accessions
     */
    public DecoyAccessionFilter(Type type, String criteria, int accessionColumnIndex, boolean decoyOnly) {
        this.type = type;
        this.criteria = criteria.toLowerCase();
        this.accessionColumnIndex = accessionColumnIndex;
        this.decoyOnly = decoyOnly;
    }

    @Override
    public boolean include(Entry entry) {
        String accession = entry.getStringValue(accessionColumnIndex);

        if (accession != null) {
            accession = accession.toLowerCase();
            switch (type) {
                case PREFIX:
                    return decoyOnly ? accession.startsWith(criteria) : !accession.startsWith(criteria);
                case POSTFIX:
                    return decoyOnly ? accession.endsWith(criteria) : !accession.endsWith(criteria);
                case CONTAIN:
                    return decoyOnly ? accession.contains(criteria) : !accession.contains(criteria);
            }
        }
        return false;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public int getAccessionColumnIndex() {
        return accessionColumnIndex;
    }

    public void setAccessionColumnIndex(int accessionColumnIndex) {
        this.accessionColumnIndex = accessionColumnIndex;
    }

    public boolean isDecoyOnly() {
        return decoyOnly;
    }

    public void setDecoyOnly(boolean decoyOnly) {
        this.decoyOnly = decoyOnly;
    }
}
