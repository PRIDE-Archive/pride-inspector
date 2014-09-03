package uk.ac.ebi.pride.toolsuite.gui.component.table.model;

import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;

import java.util.List;

/**
 * Extend this table model to update data progressively.
 * 
 * User: rwang
 * Date: 14-Apr-2010
 * Time: 16:46:32
 */
public abstract class ProgressiveListTableModel<T, V> extends ListTableModel<V> implements TaskListener<T, V> {

    @Override
    public void process(TaskEvent<List<V>> listTaskEvent) {
        List<V> newDataList = listTaskEvent.getValue();
        for (V newData : newDataList) {
            addData(newData);
        }
    }

    @Override
    public void started(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
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
    public void progress(TaskEvent<Integer> progress) {
    }
}
