package uk.ac.ebi.pride.toolsuite.gui.component.status;

import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.List;

/**
 * User: rwang
 * Date: 16-Feb-2010
 * Time: 11:18:41
 * <p/>
 * todo: change mouse over effect
 */
public class TaskMonitorPanel extends StatusBarPanel {
    /**
     * Task progress bar indicates there is a background running task when appears
     */
    private JProgressBar taskProgressBar;

    /**
     * dialog for displaying all the ongoing tasks
     */
    private JDialog taskProgressDialog;

    public TaskMonitorPanel() {
        super(0, true);
        this.setLayout(new BorderLayout());
        this.setOpaque(false);
        this.setPreferredSize(new Dimension(150, 18));

        // create a dialog to display all ongoing tasks
        taskProgressDialog = new TaskDialog(uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getMainComponent());
        taskProgressDialog.setVisible(false);

        // create the task progress bar
        taskProgressBar = new JProgressBar();
        // set to fixed size
        taskProgressBar.setIndeterminate(true);
        taskProgressBar.setStringPainted(true);
        taskProgressBar.setToolTipText("Click to show/hide background tasks window");
        taskProgressBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                taskProgressDialog.setVisible(true);
            }
        });
        // the the progress bar
        taskProgressBar.setVisible(false);
        this.add(taskProgressBar, BorderLayout.CENTER);

        // add itself as a listener to task manager
        PrideInspectorContext context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
        TaskManager taskMgr = context.getTaskManager();
        taskMgr.addPropertyChangeListener(this);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void propertyChange(PropertyChangeEvent evt) {
        String eventName = evt.getPropertyName();
        if (TaskManager.ADD_TASK_PROP.equals(eventName)) {
            List<Task> newTasks = (List<Task>) evt.getNewValue();
            if (!newTasks.isEmpty()) {
                // add the new task
                Task newTask = newTasks.get(newTasks.size() - 1);
                taskProgressBar.setString(newTask.getName());

                // display the newest task
                taskProgressBar.setVisible(true);
            }
        } else if (TaskManager.REMOVE_TASK_PROP.equals(eventName)) {
            java.util.List<Task> newTasks = (java.util.List<Task>) evt.getNewValue();

            // when there is no ongoing tasks
            if (newTasks.isEmpty()) {
                taskProgressBar.setVisible(false);
            } else {
                Task newTask = newTasks.get(newTasks.size() - 1);
                taskProgressBar.setString(newTask.getName());
            }
        }
    }
}
