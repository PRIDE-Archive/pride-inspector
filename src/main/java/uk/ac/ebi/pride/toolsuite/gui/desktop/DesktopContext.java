package uk.ac.ebi.pride.toolsuite.gui.desktop;

import org.apache.log4j.Logger;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableHandler;
import uk.ac.ebi.pride.toolsuite.gui.prop.PropertyManager;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskManager;
import uk.ac.ebi.pride.toolsuite.gui.utils.ClusterAssayProperties;
import uk.ac.ebi.pride.toolsuite.gui.utils.ClusterProjectProperties;
import uk.ac.ebi.pride.toolsuite.gui.utils.PropertyChangeHelper;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * DesktopContext stores the information shared by all application
 * <p/>
 * User: rwang
 * Date: 21-Jan-2010
 * Time: 11:25:45
 */
public class DesktopContext extends PropertyChangeHelper {
    private static final Logger logger = Logger.getLogger(DesktopContext.class.getName());

    /**
     * reference to desktop object
     */
    private Desktop desktop = null;

    /**
     * task manager manages all the ongoing tasks
     */
    private final TaskManager taskManager;

    /**
     * property manager stores all the property assigned to this application
     */
    private final PropertyManager propertyManager;

    /**
     * ThrowableHandler stores all the throwables for this application
     */
    private final ThrowableHandler throwableHandler;

    /**
     * Constructor
     */
    protected DesktopContext() {
        // task manager
        taskManager = new TaskManager();

        // property manager
        propertyManager = new PropertyManager();

        // throwable handler
        throwableHandler = new ThrowableHandler();
    }

    /**
     * Return an instance of the desktop
     *
     * @return Desktop  desktop object
     */
    public final synchronized Desktop getDesktop() {
        return desktop;
    }

    /**
     * Set a desktop object
     *
     * @param desktop desktop object
     */
    public final synchronized void setDesktop(Desktop desktop) {
        if (this.desktop != null) {
            throw new IllegalStateException("Desktop has already been launched");
        }
        this.desktop = desktop;
    }

    /**
     * Return the task manager
     *
     * @return TaskManager task manager
     */
    public final synchronized TaskManager getTaskManager() {
        return taskManager;
    }

    /**
     * Return the property manager
     *
     * @return PropertyManager  property manager
     */
    public final synchronized PropertyManager getPropertyManager() {
        return propertyManager;
    }

    /**
     * Add a task to task manager
     *
     * @param task a new task
     */
    public final synchronized void addTask(Task task) {
        taskManager.addTask(task);
    }

    /**
     * Add a task to task manager, and also specify whether to notify other components
     * Such as: status bar
     *
     * @param task   new task
     * @param notify true means notify
     */
    public final synchronized void addTask(Task task, boolean notify) {
        taskManager.addTask(task, notify);
    }

    /**
     * Get the tasks which is listened by the input task listener
     *
     * @param listener task listener
     * @return List<Task>   a collection of task listeners
     */
    public final synchronized List<Task> getTask(TaskListener listener) {
        return taskManager.getTasks(listener);
    }

    /**
     * Get the tasks which is listened by the input property change listener
     *
     * @param listener property change listener
     * @return List<Task>   a list of tasks
     */
    public final synchronized List<Task> getTask(PropertyChangeListener listener) {
        return taskManager.getTasks(listener);
    }

    /**
     * Get the tasks which is instance of the input task class
     *
     * @param taskClass task class
     * @return List<Task>   a list of tasks
     */
    public final synchronized List<Task> getTask(Class<? extends Task> taskClass) {
        return taskManager.getTasks(taskClass);
    }

    /**
     * Check whether the task exists
     *
     * @param task task
     * @return boolean true if exists
     */
    public final synchronized boolean hasTask(Task task) {
        return taskManager.hasTask(task);
    }

    /**
     * Cancel a existing task
     *
     * @param task      task
     * @param interrupt whether to interrupt the task directly
     * @return boolean  true if cancel task has been successful
     */
    public final synchronized boolean cancelTask(Task task, boolean interrupt) {
        return taskManager.cancelTask(task, interrupt);
    }

    /**
     * Remove task listener
     *
     * @param listener task listener
     */
    public final synchronized void removeTaskListener(TaskListener listener) {
        taskManager.removeTaskListener(listener);
    }

    /**
     * Get all property associcated with this application
     *
     * @return Properties   propeties object
     */
    public final synchronized Properties getProperties() {
        return getPropertyManager().getProperties();
    }

    /**
     * Get property based a property name
     *
     * @param name property name
     * @return String   property value
     */
    public final synchronized String getProperty(String name) {
        return getPropertyManager().getProperty(name);
    }

    /**
     * Get property with a specified property name.
     *
     * @param name  property name
     * @param args  a list of arguments.
     * @return String property value
     */
    public final synchronized String getProperty(String name, Object[] args) {
        return propertyManager.getProperty(name, args);
    }

    /**
     * Set property using a property name and property value
     *
     * @param name  property name
     * @param value property value
     */
    public final synchronized void setProperty(String name, String value) {
        getPropertyManager().setProperty(name, value);
    }

    /**
     * Load system properties from a input stream
     *
     * @param in input stream
     * @throws IOException error while reading property input stream
     */
    public final synchronized void loadSystemProps(InputStream in) throws IOException {
        getPropertyManager().loadSystemProps(in);
    }

    /**
     * Load system properties from a input stream
     *
     * @param in input stream
     * @throws IOException error while reading property input stream
     */
    public final synchronized void loadClusterProjectsProperties(InputStream in) throws IOException {
        getPropertyManager().loadClusterProjectsProperties(in);
    }

    /**
     * Load system properties from a input stream
     *
     * @param in input stream
     * @throws IOException error while reading property input stream
     */
    public final synchronized void loadClusterAssayProperties(InputStream in) throws IOException {
        getPropertyManager().loadClusterAssayProperties(in);
    }


    /**
     * Get ThrowableHandler
     *
     * @return  ThrowableHandler    handler
     */
    public final synchronized ThrowableHandler getThrowableHandler() {
        return throwableHandler;
    }

    public final synchronized List<ThrowableEntry> getAllThrowables() {
        return getThrowableHandler().getAllThrowables();
    }

    public final synchronized void addThrowableEntry(ThrowableEntry entry) {
        getThrowableHandler().addThrowableEntry(entry);
    }

    public final synchronized void removeThrowableEntry(ThrowableEntry entry) {
        getThrowableHandler().removeThrowableEntry(entry);
    }

    public void removeAllThrowableEntries() {
        getThrowableHandler().removeAllThrowableEntries();
    }

    public ClusterProjectProperties getProjectClusterProperties() {
        return getPropertyManager().getProjectClusters();
    }

    public ClusterAssayProperties getProjectAssaysClusterProperties() {
        return getPropertyManager().getAssayClusters();

    }
}
