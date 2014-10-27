package uk.ac.ebi.pride.toolsuite.gui.utils;

import uk.ac.ebi.pride.utilities.util.NumberUtilities;

import java.util.*;

/**
 * AccessionUtils is used for:
 * <p/>
 * 1. expanding a string of accessions into a list of accessions (e.g. 123-345)
 * 2. compacting a list of accessions into a string of accessions
 * <p/>
 * User: rwang
 * Date: 02/03/11
 * Time: 16:27
 */
public class AccessionUtils {
    private static final String RANGE_SEPARATOR = "-";

    /**
     * Expand a string into a list of accessions
     *
     * @param accStr accessions in string
     * @return Set<Comparable>    list of accessions
     */
    public static Set<Comparable> expand(String accStr) {
        if (accStr == null || "".equals(accStr)) {
            throw new IllegalArgumentException("Input string of accessions can not be NULL!");
        }

        // create a empty list
        Set<Comparable> accs = new LinkedHashSet<Comparable>();

        // split the input string
        String[] parts = accStr.split(Constants.COMMA);
        for (String part : parts) {

            String errMsg = "Input string contains illegal accession range: " + part;

            if (part.contains(RANGE_SEPARATOR)) {
                // it is an accession range
                String[] ranges = part.split(RANGE_SEPARATOR);
                if (ranges.length != 2 || !NumberUtilities.isInteger(ranges[0]) || !NumberUtilities.isInteger(ranges[1])) {
                    throw new IllegalArgumentException(errMsg);
                }

                // convert to integer
                int start = Integer.parseInt(ranges[0]);
                int end = Integer.parseInt(ranges[1]);
                if (start > end) {
                    throw new IllegalArgumentException(errMsg);
                }

                for (int i = start; i <= end; i++) {
                    accs.add(i+"");
                }

            } else {
                // it is an accession
                if (!NumberUtilities.isInteger(part)) {
                    throw new IllegalArgumentException(errMsg);
                }
                accs.add(part+"");
            }
        }

        return accs;
    }

    /**
     * Compact a list of accessions of a string
     *
     * @param accs a list of accessions
     * @return String  string represents a list of accessions
     */
    public static String compact(Collection<Comparable> accs) {
        if (accs == null || accs.isEmpty()) {
            throw new IllegalArgumentException("Input list of accessions cannot be NULL or empty");
        }

        // create an integer list based the input accession list
        List<Integer> accsInt = new ArrayList<Integer>();
        for (Comparable acc : accs) {
            if (!NumberUtilities.isInteger(acc.toString())) {
                throw new NumberFormatException("String cannot be converted to integer: " + acc);
            } else {
                int a = Integer.parseInt(acc.toString());
                if (!accsInt.contains(a)) {
                    accsInt.add(a);
                }
            }
        }

        // sort the list to ascending order
        Collections.sort(accsInt);
        String accStr = "";
        for (int i = 0; i < accsInt.size(); i++) {
            if ("".equals(accStr)) {
                accStr += accsInt.get(i);
            } else if (accsInt.get(i) != (accsInt.get(i - 1) + 1)) {
                if (!accStr.endsWith(accsInt.get(i - 1).toString())) {
                    accStr += RANGE_SEPARATOR + accsInt.get(i - 1);
                }
                accStr += Constants.COMMA + accsInt.get(i);
            } else if (i == accsInt.size() -1 ) {
                accStr += RANGE_SEPARATOR + accsInt.get(i);
            }
        }

        return accStr;
    }
}
