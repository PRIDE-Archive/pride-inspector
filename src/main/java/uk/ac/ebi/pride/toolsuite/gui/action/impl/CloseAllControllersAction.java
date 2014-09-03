package uk.ac.ebi.pride.toolsuite.gui.action.impl;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.event.AddDataSourceEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.ForegroundDataSourceEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.RemoveDataSourceEvent;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Collection;

/**
 * Close all existing data access controller
 * <p/>
 * User: rwang
 * Date: 11-Oct-2010
 * Time: 13:55:43
 */
public class CloseAllControllersAction extends PrideAction {

    public CloseAllControllersAction(String name, Icon icon) {
        super(name, icon);
        // enable annotation
        AnnotationProcessor.process(this);

        // register this action as property listener to database access monitor

        this.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PrideInspectorContext context = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        for (DataAccessController controller : context.getControllers()) {
            context.removeDataAccessController(controller, true);
        }
    }

    @EventSubscriber(eventClass = ForegroundDataSourceEvent.class)
    public void onForegroundDataSourceEvent(ForegroundDataSourceEvent evt) {
        DataAccessController foregroundController = (DataAccessController) evt.getNewForegroundDataSource();
        this.setEnabled(foregroundController != null);
    }

    @EventSubscriber(eventClass = AddDataSourceEvent.class)
    public void onAddDataSourceEvent(AddDataSourceEvent evt) {
        Collection controllers = evt.getNewDataSources();
        this.setEnabled(controllers.size() > 0);
    }

    @EventSubscriber(eventClass = RemoveDataSourceEvent.class)
    public void onRemoveDataSourceEvent(RemoveDataSourceEvent evt){
        Collection controllers = evt.getNewDataSources();
        this.setEnabled(controllers.size() > 0);
    }
}