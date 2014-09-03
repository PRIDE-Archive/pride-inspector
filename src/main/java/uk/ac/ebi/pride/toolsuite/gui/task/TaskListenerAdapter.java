package uk.ac.ebi.pride.toolsuite.gui.task;

import java.util.List;

/**
 * This is convenient class for implementing TaskListener
 * User: rwang
 * Date: 21-May-2010
 * Time: 08:48:01
 */
public class TaskListenerAdapter<T, V> implements TaskListener<T, V>{

    @Override
    public void started(TaskEvent<Void> event) {
    }

    @Override
    public void process(TaskEvent<List<V>> listTaskEvent) {
    }

    @Override
    public void finished(TaskEvent<Void> event) {
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
    }

    @Override
    public void succeed(TaskEvent<T> tTaskEvent) {
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
    }

    @Override
    public void progress(TaskEvent<Integer> progress) {
    }
}
