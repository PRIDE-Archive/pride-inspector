package uk.ac.ebi.pride.toolsuite.gui.event.subscriber;

import org.bushe.swing.event.EventSubscriber;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.event.container.SpectrumEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.RetrieveSpectrumTask;

/**
 * Subscribe to spectrum selection event
 *
 * User: rwang
 * Date: 13/06/11
 * Time: 11:44
 */
public class SpectrumEventSubscriber implements EventSubscriber<SpectrumEvent> {
    private DataAccessController controller;
    private TaskListener taskListener;
    private PrideInspectorContext appContext;

    public SpectrumEventSubscriber(DataAccessController controller, TaskListener taskListener) {
        this.controller = controller;
        this.taskListener = taskListener;
        this.appContext = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
    }

    @Override
    public void onEvent(SpectrumEvent event) {

        Comparable specturmId = event.getSpectrumId();

        Task newTask = new RetrieveSpectrumTask(controller, specturmId);
        newTask.addTaskListener(taskListener);
        TaskUtil.startBackgroundTask(newTask, controller);
    }
}