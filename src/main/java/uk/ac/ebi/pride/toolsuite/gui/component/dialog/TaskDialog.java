package uk.ac.ebi.pride.toolsuite.gui.component.dialog;

import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * TaskDialog monitors
 *
 * User: rwang
 * Date: 23/12/10
 * Time: 10:09
 */
public class TaskDialog<K, V> extends JDialog implements TaskListener<K, V>, ActionListener {
    private static final String CANCEL_ACTION = "Cancel";
    /**
     * Progress bar monitor the task progress
     */
    private JProgressBar progressBar;

    /**
     * Message to be displayed as a label
     */
    private String message;

    /**
     * Cancel button
     */
    private JButton cancelButton;

    /**
     * constructor
     *
     * @param owner
     * @param title
     */
    public TaskDialog(Frame owner, String title, String message) {
        super(owner, title);

        this.message = message;

        this.setLayout(new BorderLayout());
        this.setSize(new Dimension(500, 100));

        // add components
        addComponents();

        // set display location
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((d.width - getWidth())/2, (d.height - getHeight())/2);

    }

    private void addComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // add label
        JLabel label = new JLabel(message);
        mainPanel.add(label, BorderLayout.NORTH);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // add progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(390, 20));
        progressBar.setStringPainted(true);
        progressBar.setBorderPainted(true);
        panel.add(progressBar);

        // add cancel button
        cancelButton = new JButton(CANCEL_ACTION);
        cancelButton.setActionCommand(CANCEL_ACTION);
        cancelButton.addActionListener(this);
        panel.add(cancelButton);

        mainPanel.add(panel, BorderLayout.CENTER);

        this.add(mainPanel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String actCmd = e.getActionCommand();
        if (CANCEL_ACTION.equals(actCmd)) {
            this.setVisible(false);
            PrideInspectorContext context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
            List<Task> tasks = context.getTask((TaskListener)this);
            for (Task task : tasks) {
                context.cancelTask(task, true);
            }
            this.dispose();
        }
    }

    @Override
    public void started(TaskEvent<Void> event) {
    }

    @Override
    public void process(TaskEvent<List<V>> listTaskEvent) {
        List<V> values = listTaskEvent.getValue();
        for (V value : values) {
            if (value instanceof String) {
                progressBar.setString((String)value);
            }
        }
    }

    @Override
    public void progress(TaskEvent<Integer> progress) {
        progressBar.setValue(progress.getValue());
    }

    @Override
    public void finished(TaskEvent<Void> event) {
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
    }

    @Override
    public void succeed(TaskEvent<K> kTaskEvent) {
        this.dispose();
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
    }
}
