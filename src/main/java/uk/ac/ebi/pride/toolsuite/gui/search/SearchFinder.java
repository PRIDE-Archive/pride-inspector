package uk.ac.ebi.pride.toolsuite.gui.search;

import uk.ac.ebi.pride.utilities.util.NumberUtilities;

import java.util.List;

/**
 * SearchFinder will a collection of string for matches
 * <p/>
 * User: rwang
 * Date: 02/06/11
 * Time: 12:03
 */
public class SearchFinder {
    private SearchEntry searchEntry;
    private List<String> headers;

    public SearchFinder(SearchEntry entry, List<String> headers) {
        this.searchEntry = entry;
        this.headers = headers;
    }

    /**
     * search a collection of values
     *
     * @param values a list of values to search
     * @return boolean true means match
     */
    public boolean search(List<String> values) {

        boolean found = false;
        // get the search field
        String field = searchEntry.getField();
        // get the search criteria
        Criteria criteria = searchEntry.getCriteria();
        // get the search term
        String term = searchEntry.getTerm();

        // find whether the search field exists in the header list
        int index = headers.indexOf(field);
        if (index >= 0) {
            // search a field
            if (index < values.size()) {
                String value = values.get(index);
                found = searchField(value.toLowerCase(), term.toLowerCase(), criteria);
            }
        } else {
            // search the whole fields
            for (String value : values) {
                if (found = searchField(value.toLowerCase(), term.toLowerCase(), criteria)) {
                    break;
                }
            }
        }

        return found;
    }

    /**
     * Search the value of one field
     *
     * @param fieldValue value of the field
     * @param term       search term
     * @param criteria   criteria to be used
     * @return boolean true means match
     */
    public boolean searchField(String fieldValue, String term, Criteria criteria) {
        boolean found = false;
        switch (criteria) {
            case CONTAIN:
                found = fieldValue.contains(term);
                break;
            case START_WITH:
                found = fieldValue.startsWith(term);
                break;
            case EMPTY:
                found = fieldValue.isEmpty();
                break;
            case END_WITH:
                found = fieldValue.endsWith(term);
                break;
            case EQUAL:
                found = fieldValue.equals(term);
                break;
            case EQUAL_OR_LESS_THAN:
                if (NumberUtilities.isNumber(fieldValue) && NumberUtilities.isNumber(term)) {
                    found = Double.parseDouble(fieldValue) <= Double.parseDouble(term);
                }
                break;
            case EQUAL_OR_MORE_THAN:
                if (NumberUtilities.isNumber(fieldValue) && NumberUtilities.isNumber(term)) {
                    found = Double.parseDouble(fieldValue) >= Double.parseDouble(term);
                }
                break;
            case LESS_THAN:
                if (NumberUtilities.isNumber(fieldValue) && NumberUtilities.isNumber(term)) {
                    found = Double.parseDouble(fieldValue) < Double.parseDouble(term);
                }
                break;
            case MORE_THAN:
                if (NumberUtilities.isNumber(fieldValue) && NumberUtilities.isNumber(term)) {
                    found = Double.parseDouble(fieldValue) > Double.parseDouble(term);
                }
                break;
        }

        return found;
    }
}
