package uk.ac.ebi.pride.toolsuite.gui.component.table.sorter;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.io.Serializable;
import java.util.Comparator;


/**
 * This code is licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * == General Description ==
 * <p>
 * This class Provides a general information or functionalities for
 * <p>
 * ==Overview==
 * <p>
 * How to used
 * <p>
 * Created by yperez (ypriverol@gmail.com) on 30/11/2016.
 */
public class ProjectTableSorter extends TableRowSorter {

    public ProjectTableSorter(TableModel model) {
        super(model);
        int numOfColumns = model.getColumnCount();
        for (int i = 0; i < numOfColumns; i++) {
            setComparator(i, new ProjectTableSorter.TableComparator());
        }
    }

    private static class TableComparator implements Comparator, Serializable {

        public int compare(Object o1, Object o2) {
            if (o1 instanceof JLabel && o2 instanceof JLabel) {
                String label1 = ((JLabel)o1).getText();
                String label2 = ((JLabel)o2).getText();
               return ((Comparable)label1).compareTo(label2);
            }else{
                return ((Comparable)o1).compareTo(o2);
            }
        }
    }
}
