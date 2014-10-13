package uk.ac.ebi.pride.toolsuite.gui.component;

import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

/**
 * DataAccessControllerPane responsible for three things:
 * <p/>
 * 1. It maintains a reference to DataAccessController and also add itself as
 * a PropertyChangeListener to the DataAccessController.
 * <p/>
 * 2. It defines several methods for building the Panel.
 * <p/>
 * 3. It implements TaskListener interface for to listen to Task events, no concrete implementations
 * are given.
 * <p/>
 * @author rwang
 * @author ypriverol
 *
 * Date: 03-May-2010
 * Time: 18:36:45
 */
public abstract class DataAccessControllerPane<T, V> extends JPanel
        implements PropertyChangeListener,
        TaskListener<T, V> {

    /** DataAccessController behind this panel */
    protected DataAccessController controller = null;

    /** A reference to parent component */
    protected JComponent parentComponent;

    /**
     * reference to application context
     */
    protected PrideInspectorContext appContext;

    /** Title for the panel*/
    private String title = null;

    /** Icon to use when there is no ongoing background tasks associated with this panel */
    private Icon finalIcon =  null;

    /** Icon to use when there is ongoing background tasks associated with this panel */
    private Icon loadingIcon = null;

    public DataAccessControllerPane(DataAccessController controller) {
        this(controller, null, null);
    }

    public DataAccessControllerPane(DataAccessController controller, JComponent parentComponent) {
        this(controller, parentComponent, null);
    }

    public DataAccessControllerPane(DataAccessController controller, JComponent parentComponent, String title) {
        this.controller = controller;
        this.parentComponent = parentComponent;
        this.title = title;
        this.appContext = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        setupMainPane();
        addComponents();
    }

    /**
     * Return the parent component
     *
     * @return JComponnet   parent component
     */
    public JComponent getParentComponent() {
        return parentComponent;
    }

    /**
     * Set the parent component
     * @param parentComponent   parent component
     */
    public void setParentComponent(JComponent parentComponent) {
        this.parentComponent = parentComponent;
    }

    /**
     * Get application context
     * @return  PrideInspectorContext   application context
     */
    public PrideInspectorContext getAppContext() {
        return appContext;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the final icon for the panel
     * @return Icon icon object
     */
    public Icon getIcon() {
        return finalIcon;
    }

    /**
     * Set the final icon for the panel
     * @param icon  icon object
     */
    public void setIcon(Icon icon) {
        finalIcon = icon;
    }

    /**
     * Get the loading icon for the panel
     *
     * @return Icon loading icon object
     */
    public Icon getLoadingIcon() {
        return loadingIcon;
    }

    /**
     * Set the loading icon for the panel
     * @param icon  loading icon object
     */
    public void setLoadingIcon(Icon icon) {
        loadingIcon = icon;
    }

    /**
     * Setup the property this DataAccessControllerPane, such as: layout manager,
     * visibility and et.al.
     */
    protected void setupMainPane() {
    }

    /**
     * Add extra components into this DataAccessControllerPane
     */
    protected void addComponents() {
    }

    /**
     * This method is called manually to populate the pane with content.
     */
    public void populate() {
    }

    public DataAccessController getController() {
        return controller;
    }
    /*
    private void setController(DataAccessController controller) {
        // remove existing controller
        if (this.controller != null) {
            this.controller.removePropertyChangeListener(this);
        }
        this.controller = controller;
        // add new controller
        if (this.controller != null) {
            this.controller.addPropertyChangeListener(this);
        }
    } */

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    @Override
    public void started(TaskEvent<Void> event) {
    }

    @Override
    public void process(TaskEvent<List<V>> listTaskEvent) {
    }

    @Override
    public void finished(TaskEvent<Void> event) {
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
    }

    @Override
    public void succeed(TaskEvent<T> tTaskEvent) {
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
    }

    @Override
    public void progress(TaskEvent<Integer> progress) {
    }
}
