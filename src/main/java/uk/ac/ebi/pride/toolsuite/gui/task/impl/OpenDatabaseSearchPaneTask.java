package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.bushe.swing.event.EventBus;
import uk.ac.ebi.pride.toolsuite.gui.EDTUtils;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.db.DatabaseSearchPane;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.event.DatabaseSearchEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;

/**
 * Task to open database search pane
 * <p/>
 * User: rwang
 * Date: 27/05/11
 * Time: 14:44
 */
public class OpenDatabaseSearchPaneTask extends TaskAdapter<Void, Void> {
    private static final String DEFAULT_TASK_TITLE = "Open database search panel";
    private static final String DEFAULT_TASK_DESCRIPTION = "Open database search panel";

    public OpenDatabaseSearchPaneTask() {
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void doInBackground() throws Exception {
        final PrideInspectorContext context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();

        DatabaseSearchPane searchPane = context.getDatabaseSearchPane();
        if (searchPane == null) {
            // create a database search pane
            Runnable code = new Runnable() {
                @Override
                public void run() {
                    context.setDatabaseSearchPane(new DatabaseSearchPane(null));
                }
            };

            EDTUtils.invokeAndWait(code);

            // start loading
            SearchDatabaseTask searchTask = new SearchDatabaseTask();
            TaskUtil.startBackgroundTask(searchTask);
        }

        EventBus.publish(new DatabaseSearchEvent(DatabaseSearchEvent.Status.SHOW));
        return null;
    }
}
