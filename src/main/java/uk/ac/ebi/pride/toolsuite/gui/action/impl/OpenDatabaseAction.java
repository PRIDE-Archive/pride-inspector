package uk.ac.ebi.pride.toolsuite.gui.action.impl;

import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.OpenDatabaseSearchPaneTask;
import uk.ac.ebi.pride.utilities.util.InternetChecker;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Open database action will open a connection PRIDE public instance.
 * <p/>
 * User: rwang
 * Date: 11-Feb-2010
 * Time: 11:49:36
 */
public class OpenDatabaseAction extends PrideAction {

    public OpenDatabaseAction(String desc, Icon icon) {
        super(desc, icon);
        setAccelerator(java.awt.event.KeyEvent.VK_P, ActionEvent.CTRL_MASK);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (InternetChecker.check()) {
            // get desktop context
            PrideInspectorContext context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();

            // create a new connection to pride database
            OpenDatabaseSearchPaneTask newTask = new OpenDatabaseSearchPaneTask();
            TaskUtil.startBackgroundTask(newTask);
        } else {
            String msg = Desktop.getInstance().getDesktopContext().getProperty("internet.connection.warning.message");
            String shortMsg = Desktop.getInstance().getDesktopContext().getProperty("internet.connection.warning.short.message");
            JOptionPane.showMessageDialog(Desktop.getInstance().getMainComponent(), msg, shortMsg, JOptionPane.WARNING_MESSAGE);
        }
    }
}
