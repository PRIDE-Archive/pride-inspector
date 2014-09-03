package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;

/**
 * Tasks use any data access controller need to extends this class.
 * This class will guarantee that when data access controller is closed,
 * any task which is using the controller will be cancelled as well.
 *
 * User: rwang
 * Date: 17-Sep-2010
 * Time: 12:08:52
 */
public abstract class AbstractDataAccessTask<K, V> extends TaskAdapter<K, V> {

    /** data access controller used in this task */
    DataAccessController controller;
    /**
     * application context
      */
    PrideInspectorContext appContext;

    AbstractDataAccessTask(DataAccessController controller) {
        this.controller = controller;
        this.appContext = (PrideInspectorContext)uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
        // add controller as property change listener to the task
        this.addOwner(controller);
    }

    /**
     * doInBackground method catches out of memory exception.
     *
     * @return K    task result
     * @throws Exception    exceptions.
     */
    @Override
    protected K doInBackground() throws Exception {
        try {
            return retrieve();
        } catch (OutOfMemoryError ex) {
            GUIUtilities.error(Desktop.getInstance().getMainComponent(),
                                appContext.getProperty("out.of.memory.message"),
                                appContext.getProperty("out.of.memory.title"));
        }

        return null;
    }

    /**
     * This method need to be overwritten to do the actual work.
     *
     * @return K    task result
     * @throws Exception    task exception
     */
    protected abstract K retrieve() throws Exception;


    /**
     * Get the data access controller
     * @return DataAccessController data access controller
     */
    public DataAccessController getController() {
        return controller;
    }
}
