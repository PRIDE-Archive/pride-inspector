package uk.ac.ebi.pride.toolsuite.gui.component;

import org.bushe.swing.event.ContainerEventServiceSupplier;
import org.bushe.swing.event.EventService;
import org.bushe.swing.event.SwingEventService;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;

import javax.swing.*;

/**
 * This class enables the pane to be a local event bus
 *
 * if you want to create a new tab to view the data for a data access controller, then you should extend this class
 *
 * User: rwang
 * Date: 10/06/11
 * Time: 14:57
 */
public class PrideInspectorTabPane<T, V> extends DataAccessControllerPane<T, V> implements ContainerEventServiceSupplier{
    protected EventService eventService;

    public PrideInspectorTabPane(DataAccessController controller) {
        super(controller);
    }

    public PrideInspectorTabPane(DataAccessController controller, JComponent parentComponent) {
        super(controller, parentComponent);
    }

    public PrideInspectorTabPane(DataAccessController controller, JComponent parentComponent, String title) {
        super(controller, parentComponent, title);
    }

    @Override
    public EventService getContainerEventService() {
        if (eventService == null) {
            eventService = new SwingEventService();
        }
        return eventService;
    }
}
