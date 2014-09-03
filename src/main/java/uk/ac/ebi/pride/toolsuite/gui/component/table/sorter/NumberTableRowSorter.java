package uk.ac.ebi.pride.toolsuite.gui.component.table.sorter;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.io.Serializable;
import java.util.Comparator;

/**
 * NumberTableRowSorter sort the column as numbers is the column data type is an instance of Number,
 * otherwise, sort it as string.
 *
 * User: rwang
 * Date: 28-Jul-2010
 * Time: 15:15:18
 */
public class NumberTableRowSorter extends TableRowSorter {

    public NumberTableRowSorter(TableModel model) {
        super(model);
        int numOfColumns = model.getColumnCount();
        for (int i = 0; i < numOfColumns; i++) {
            setComparator(i, new NumberComparator());
        }
    }

    private static class NumberComparator implements Comparator, Serializable {

        public int compare(Object o1, Object o2) {
            if (o1 instanceof Number && o2 instanceof Number) {
                double n1 = ((Number) o1).doubleValue();
                double n2 = ((Number) o2).doubleValue();
                if (n1 > n2) {
                    return 1;
                } else if (n1 == n2){
                    return 0;
                } else {
                    return -1;
                }
            } else {
                return ((Comparable)o1).compareTo(o2);
            }
        }
    }
}
