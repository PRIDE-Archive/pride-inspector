package uk.ac.ebi.pride.toolsuite.gui.component.status;

import uk.ac.ebi.pride.toolsuite.gui.EDTUtils;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;

import javax.swing.*;
import java.util.List;

/**
 * User: rwang
 * Date: 12-Feb-2010
 * Time: 16:06:30
 */
public class TaskProgressBar extends JProgressBar implements TaskListener<Object, Object> {

    public TaskProgressBar(Task task) {
        super();
        task.addTaskListener(this);

        this.setString(task.getName());
        this.setToolTipText(task.getDescription());
        this.setIndeterminate(true);
        this.setStringPainted(true);
    }

    @Override
    public void started(TaskEvent<Void> event) {
    }

    @Override
    public void process(TaskEvent<List<Object>> taskEvent) {
        List<Object> values = taskEvent.getValue();
        for (Object value : values) {
            if (value instanceof String)
                updateMessage((String) value);
        }
    }

    @Override
    public void finished(TaskEvent<Void> event) {
        this.setIndeterminate(false);
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
        updateMessage("Failed!");
    }

    @Override
    public void succeed(TaskEvent<Object> taskEvent) {
        updateMessage("Succeed!");
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
        updateMessage("Cancelled!");
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
        updateMessage("Interrupted!");
    }

    @Override
    public void progress(TaskEvent<Integer> progress) {
    }

    /**
     * Thread safe message update.
     *
     * @param msg message.
     */
    private void updateMessage(final String msg) {
        Runnable eventDispatcher = new Runnable() {
            public void run() {
                TaskProgressBar.this.setString(msg);
            }
        };
        EDTUtils.invokeLater(eventDispatcher);
    }

}
