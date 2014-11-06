package uk.ac.ebi.pride.toolsuite.gui.utils;

import java.util.Comparator;

/**
 * Remote data transfer protocol
 * @author Rui Wang
 * @version $Id$
 */
public enum DataTransferProtocol {
    ASPERA(1),
    FTP(2),
    NONE(Integer.MAX_VALUE);

    private int priority;

    DataTransferProtocol(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }


    public static class PriorityComparator implements Comparator<DataTransferProtocol> {

        @Override
        public int compare(DataTransferProtocol o1, DataTransferProtocol o2) {
            return Integer.compare(o1.getPriority(), o2.getPriority());
        }
    }
}
