package uk.ac.ebi.pride.toolsuite.gui.event.subscriber;

import org.bushe.swing.event.EventSubscriber;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.event.container.PSMEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.RetrievePeptideTask;

/**
 * Event subscriber to peptide event
 *
 * User: rwang
 * Date: 13/06/11
 * Time: 09:01
 */
public class PeptideEventSubscriber implements EventSubscriber<PSMEvent> {
    private DataAccessController controller;
    private TaskListener taskListener;
    private PrideInspectorContext appContext;

    public PeptideEventSubscriber(DataAccessController controller, TaskListener taskListener) {
        this.controller = controller;
        this.taskListener = taskListener;
        this.appContext = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
    }

    @Override
    public void onEvent(PSMEvent event) {
        Comparable peptideId = event.getPeptideId();
        Comparable protId = event.getIdentificationId();

        Task newTask = new RetrievePeptideTask(controller, protId, peptideId);
        newTask.addTaskListener(taskListener);
        TaskUtil.startBackgroundTask(newTask, controller);
    }
}
