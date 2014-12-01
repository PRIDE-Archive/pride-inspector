package uk.ac.ebi.pride.toolsuite.gui.component.table.sorter;

import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

/**
 * NumberTableRowSorter sort the column as numbers is the column data type is an instance of Number,
 * otherwise, sort it as string.
 *
 * @author rwang
 * @author ypriverol
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
            } if(o1 instanceof HashMap && o2 instanceof HashMap){
                return 0;
            } else if(o1 instanceof LinkedHashSet && o2 instanceof LinkedHashSet){
                Object value1 = ((LinkedHashSet) o1).iterator().next();
                Object value2 = ((LinkedHashSet) o2).iterator().next();
                return ((Comparable)value1).compareTo(value2);
            }else{
                return ((Comparable)o1).compareTo(o2);
            }
        }
    }
}
