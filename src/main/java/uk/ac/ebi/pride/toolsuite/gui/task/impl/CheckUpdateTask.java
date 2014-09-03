package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;
import uk.ac.ebi.pride.toolsuite.gui.utils.UpdateChecker;

/**
 * Check for available update
 * <p/>
 * User: rwang
 * Date: 11-Nov-2010
 * Time: 18:06:51
 */
public class CheckUpdateTask extends TaskAdapter<Boolean, Void> {

    public CheckUpdateTask() {
        String msg = "Checking for update";
        this.setName(msg);
        this.setDescription(msg);
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        DesktopContext context = PrideInspector.getInstance().getDesktopContext();
        String updateUrl = context.getProperty("pride.inspector.update.website");
        String currentVersion = context.getProperty("pride.inspector.version");

        UpdateChecker updateChecker = new UpdateChecker(updateUrl);

        return updateChecker.hasUpdate(currentVersion);
    }
}
