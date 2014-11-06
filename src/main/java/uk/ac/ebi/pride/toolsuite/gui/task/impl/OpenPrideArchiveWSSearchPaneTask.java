package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.bushe.swing.event.EventBus;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.ws.PrideArchiveWSSearchPane;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.event.DatabaseSearchEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;

/**
 * Task to open database search pane
 * <p/>
 * User: rwang
 * Date: 27/05/11
 * Time: 14:44
 */
public class OpenPrideArchiveWSSearchPaneTask extends TaskAdapter<Void, Void> {
    private static final String DEFAULT_TASK_TITLE = "Open database search panel";
    private static final String DEFAULT_TASK_DESCRIPTION = "Open database search panel";

    public OpenPrideArchiveWSSearchPaneTask() {
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void doInBackground() throws Exception {
        final PrideInspectorContext context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();

        PrideArchiveWSSearchPane searchPane = context.getPrideArchiveWSSearchPane();
        if (searchPane == null) {
            context.setPrideArchiveWSSearchPane(new PrideArchiveWSSearchPane());
        }

        EventBus.publish(new DatabaseSearchEvent(DatabaseSearchEvent.Status.SHOW));
        return null;
    }
}
