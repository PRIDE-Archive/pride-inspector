package uk.ac.ebi.pride.toolsuite.gui.task;

import java.util.List;

/**
 * User: rwang
 * Date: 22-Jan-2010
 * Time: 13:38:59
 */
public interface TaskListener<T, V> {
    /**
     * Called before the Task's <code> doInBackground </code>
     * method is called.
     *
     * @param event a TaskEvent whose source is the Task object.
     */
    public void started(TaskEvent<Void> event);
    public void process(TaskEvent<List<V>> event);
    public void finished(TaskEvent<Void> event);
    public void failed(TaskEvent<Throwable> event);
    public void succeed(TaskEvent<T> event);
    public void cancelled(TaskEvent<Void> event);
    public void interrupted(TaskEvent<InterruptedException> iex);
    public void progress(TaskEvent<Integer> progress);
}
