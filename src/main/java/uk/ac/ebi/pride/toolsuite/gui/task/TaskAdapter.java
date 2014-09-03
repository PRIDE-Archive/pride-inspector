package uk.ac.ebi.pride.toolsuite.gui.task;

/**
 * User: rwang
 * Date: 01-Aug-2010
 * Time: 10:49:47
 */
public abstract class TaskAdapter<T, V> extends Task<T, V> {

    @Override
    protected void finished() {

    }

    @Override
    protected void succeed(T results) {
    }

    @Override
    protected void cancelled() {

    }

    @Override
    protected void interrupted(InterruptedException iex) {

    }


}
