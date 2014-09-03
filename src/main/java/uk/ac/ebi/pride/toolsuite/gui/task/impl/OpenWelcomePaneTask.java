package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.bushe.swing.event.EventBus;
import uk.ac.ebi.pride.toolsuite.gui.EDTUtils;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.startup.WelcomePane;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.event.ShowWelcomePaneEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;

/**
 * User: rwang
 * Date: 01/06/11
 * Time: 11:38
 */
public class OpenWelcomePaneTask extends TaskAdapter<Void, Void> {
    private static final String DEFAULT_TASK_TITLE = "Open welcome panel";
    private static final String DEFAULT_TASK_DESCRIPTION = "Open welcome panel";

    public OpenWelcomePaneTask() {
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void doInBackground() throws Exception {
        final PrideInspectorContext context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();

        WelcomePane welcomePane = context.getWelcomePane();
        if (welcomePane == null) {
            // create a database search pane
            Runnable code = new Runnable() {
                @Override
                public void run() {
                    context.setWelcomePane(new WelcomePane());
                }
            };

            EDTUtils.invokeAndWait(code);
        }

        EventBus.publish(new ShowWelcomePaneEvent(null));
        return null;
    }
}
