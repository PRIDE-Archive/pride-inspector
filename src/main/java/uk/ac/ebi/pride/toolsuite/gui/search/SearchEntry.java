package uk.ac.ebi.pride.toolsuite.gui.search;

/**
 * An search entry should contain one search field, criteria and term.
 *
 * User: rwang
 * Date: 31/05/11
 * Time: 16:06
 */
public class SearchEntry {

    private String field;
    private Criteria criteria;
    private String term;

    public SearchEntry(String field, Criteria criteria, String term) {
        this.field = field;
        this.criteria = criteria;
        this.term = term;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
