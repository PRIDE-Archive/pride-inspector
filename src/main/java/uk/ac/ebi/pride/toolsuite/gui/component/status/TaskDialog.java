package uk.ac.ebi.pride.toolsuite.gui.component.status;

import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.metadata.CollapsiblePane;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskManager;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.AbstractDataAccessTask;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * TaskDialog is displayed when TaskMonitorPanel's progress bar is clicked
 * It displays a list of all ongoing tasks.
 * <p/>
 * User: rwang
 * Date: 22-Sep-2010
 * Time: 14:27:19
 */
public class TaskDialog extends JDialog implements PropertyChangeListener {
    /**
     * the title of the dialog
     */
    private static final String TASK_DIALOG_TITLE = "Background Tasks";

    /**
     * main pane contains all the ongoing task panels
     */
    private JPanel mainPane;

    /**
     * a map of task panels, each task panel is created using createTaskPanel() method
     */
    private Map<Task, JPanel> taskPanelMap;

    /**
     * PRIDE Inspector context
     */
    private PrideInspectorContext context;

    public TaskDialog(JFrame owner) {
        super(owner, TASK_DIALOG_TITLE);
        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(570, 400));

        // main panel
        mainPane = new JPanel();
        mainPane.setLayout(new BoxLayout(mainPane, BoxLayout.Y_AXIS));
        mainPane.setBackground(Color.white);
        mainPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);

        // todo: add icon
        this.taskPanelMap = new HashMap<Task, JPanel>();

        // set display location
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((d.width - getWidth())/2, (d.height - getHeight())/2);

        // add itself as a listener to task manager
        context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
        TaskManager taskMgr = context.getTaskManager();
        taskMgr.addPropertyChangeListener(this);
    }

    /**
     * Create a JPanel which shows the name of the task
     * and progress.
     *
     * @param task a background running task
     * @return JPanel a panel which cotains a JLabel and a progress bar
     */
    private JPanel createTaskPanel(Task task) {
        JPanel displayPane = new JPanel();
        displayPane.setOpaque(false);
        displayPane.setLayout(new BorderLayout());
        displayPane.setMaximumSize(new Dimension(520, 80));

        // get title
        String title = getTaskPanelTitle(task);
        CollapsiblePane panel = new CollapsiblePane(title);

        // progress bar panel
        JPanel progBarPanel = new JPanel();
        progBarPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        progBarPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // add progress bar
        TaskProgressBar progBar = new TaskProgressBar(task);
        progBar.setPreferredSize(new Dimension(440, 20));
        progBarPanel.add(progBar);

        // add close button
        Icon icon = GUIUtilities.loadIcon(context.getProperty("close.source.enable.icon.small"));
        JButton closeButton = GUIUtilities.createLabelLikeButton(icon, null);
        closeButton.addActionListener(new CloseTaskListener(task));
        progBarPanel.add(closeButton);

        panel.setContentComponent(progBarPanel);

        // add collapsible pane
        displayPane.add(panel, BorderLayout.CENTER);

        return displayPane;
    }

    /**
     * Get the title for the task panel
     * @param task  task
     * @return  String task panel title
     */
    private String getTaskPanelTitle(Task task) {
        String title = task.getName();
        if (task instanceof AbstractDataAccessTask) {
            DataAccessController controller = ((AbstractDataAccessTask)task).getController();
            if (controller != null) {
                title += " (" + controller.getName() + ")";
            }
        }
        return title;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void propertyChange(PropertyChangeEvent evt) {
        String eventName = evt.getPropertyName();
        if (TaskManager.ADD_TASK_PROP.equals(eventName)) {
            java.util.List<Task> newTasks = (java.util.List<Task>) evt.getNewValue();
            // get the newest task
            Task newTask = newTasks.get(newTasks.size() - 1);
            // 1. create a new progress bar
            JPanel taskPanel = createTaskPanel(newTask);
            // display the newest task
            mainPane.add(taskPanel);
            mainPane.revalidate();
            mainPane.repaint();
            this.repaint();
            // register the mapping
            taskPanelMap.put(newTask, taskPanel);
        } else if (TaskManager.REMOVE_TASK_PROP.equals(eventName)) {
            java.util.List<Task> oldTasks = (java.util.List<Task>) evt.getOldValue();
            java.util.List<Task> newTasks = (java.util.List<Task>) evt.getNewValue();
            oldTasks.removeAll(newTasks);
            for (Task task : oldTasks) {
                JPanel taskPanel = taskPanelMap.get(task);
                if (taskPanel != null) {
                    mainPane.remove(taskPanel);
                    mainPane.revalidate();
                    mainPane.repaint();

                    // remove from memory
                    taskPanelMap.remove(task);
                }
            }
            this.repaint();
        }
    }

    private static class CloseTaskListener implements ActionListener {
        private Task task;

        private CloseTaskListener(Task task) {
            this.task = task;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext().cancelTask(task, true);
        }
    }
}
