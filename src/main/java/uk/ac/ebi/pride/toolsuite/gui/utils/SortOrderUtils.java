package uk.ac.ebi.pride.toolsuite.gui.utils;

import javax.swing.*;

/**
 * @author ypriverol
 * @author rwang
 */
public class SortOrderUtils {

    /**
     * Convenience to check if the order is sorted.
     * @return false if unsorted, true for ascending/descending.
     */
    public static boolean isSorted(SortOrder order) {
        return order != SortOrder.UNSORTED;
    }

    public static boolean isSorted(SortOrder order, boolean ascending) {
        return isSorted(order) && (ascending == isAscending(order));
    }

    /**
     * Convenience to check for ascending sort order.
     * PENDING: is this helpful at all?
     *
     * @return true if ascendingly sorted, false for unsorted/descending.
     */
    public static boolean isAscending(SortOrder order) {
        return order == SortOrder.ASCENDING;
    }
}
