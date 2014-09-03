package uk.ac.ebi.pride.toolsuite.gui.listeners;

import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * MemoryUsageListener monitors the memory usage and show warning message.
 *
 * User: rwang
 * Date: 06-Oct-2010
 * Time: 12:16:03
 */
public class MemoryUsageListener implements ActionListener {
    private static final double MEMORY_WARNING_THRESHOLD = 1048576;

    @Override
    public void actionPerformed(ActionEvent e) {
        // get memory manage bean
        MemoryMXBean mBean = ManagementFactory.getMemoryMXBean();

        // get memory usage
        MemoryUsage mUsage = mBean.getHeapMemoryUsage();

        // calculate memory usage ratio
        double mdiff = mUsage.getMax() - mUsage.getUsed();
        
        if (mdiff < MEMORY_WARNING_THRESHOLD) {
            // show warning message
            GUIUtilities.message(Desktop.getInstance().getMainComponent(), "Memory", "Low memory warning");
        }
    }
}
