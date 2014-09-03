package uk.ac.ebi.pride.toolsuite.gui.action.impl;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.event.ForegroundDataSourceEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Close controller action close the assigned controller, if there is not assigned controller,
 * then the foreground data access controller is closed.
 * <p/>
 * <p/>
 * User: rwang
 * Date: 17-Aug-2010
 * Time: 09:22:58
 */
public class CloseControllerAction extends PrideAction {
    private final DataAccessController controller;
    private PrideInspectorContext context;

    public CloseControllerAction(String name, Icon icon) {
        this(name, icon, null);
    }

    public CloseControllerAction(String name, Icon icon, DataAccessController controller) {
        super(name, icon);
        this.controller = controller;
        this.setEnabled(controller != null);

        // enable annotation
        AnnotationProcessor.process(this);

        context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DataAccessController controllerToClose = controller == null ? context.getForegroundDataAccessController() : controller;
        context.removeDataAccessController(controllerToClose, true);
    }

    @EventSubscriber(eventClass = ForegroundDataSourceEvent.class)
    public void onForegroundDataSourceEvent(ForegroundDataSourceEvent evt) {
        if (controller == null) {
            DataAccessController foregroundController = (DataAccessController) evt.getNewForegroundDataSource();
            this.setEnabled(foregroundController != null);
        }
    }
}
