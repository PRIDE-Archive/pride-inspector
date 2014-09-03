package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;

import java.util.List;

/**
 * Extend this table model to update data successively.
 *
 * User: rwang
 * Date: 20-Aug-2010
 * Time: 10:34:04
 */
public abstract class SuccessiveListTableModel<T, V> extends ListTableModel<T> implements TaskListener<T, V> {

    @Override
    public void succeed(TaskEvent<T> tTaskEvent) {
        addData(tTaskEvent.getValue());
        fireTableDataChanged();
    }

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
    public void cancelled(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
    }
}
