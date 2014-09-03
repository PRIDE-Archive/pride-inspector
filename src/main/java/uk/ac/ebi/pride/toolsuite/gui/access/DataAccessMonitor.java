package uk.ac.ebi.pride.toolsuite.gui.access;

import org.bushe.swing.event.EventBus;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.event.AddDataSourceEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.ForegroundDataSourceEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.ProcessingDataSourceEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.RemoveDataSourceEvent;
import uk.ac.ebi.pride.toolsuite.gui.utils.PropertyChangeHelper;

import java.util.*;

/**
 * DataAccessMonitor acts as a data model for DataSourceViewer.
 * It maintains a list of DataAccessControllers currently in use in PRIDE GUI.
 * DataAccessControllers represent data sources, which could be either file or database.
 * <p/>
 * User: ypriverol, rwang
 * Date: 26-Feb-2010
 * Time: 11:10:57
 */
public class DataAccessMonitor extends PropertyChangeHelper {
    /**
     * A list of existing DataAccessControllers
     */
    private final List<DataAccessController> controllers;
    /**
     * Foreground DataAccessController
     */
    private DataAccessController foregroundController = null;

    /**
     * This map maintains a reference between DataAccessController and Status for dataSource
     */

    private final Map<DataAccessController, List<ProcessingDataSourceEvent.Status>> dataAccessControllerStatus;


    public DataAccessMonitor() {
        this.controllers = Collections.synchronizedList(new ArrayList<DataAccessController>());
        this.dataAccessControllerStatus = Collections.synchronizedMap(new HashMap<DataAccessController, List<ProcessingDataSourceEvent.Status>>());
    }


    /**
     * Add a new data access controller and set it as the foreground controller
     * @param controller    new data access controller
     */
    public synchronized void addDataAccessController(DataAccessController controller) {
        addDataAccessController(controller, true);
    }

    /**
     * Add a new data access controller
     * @param controller    new data access controller
     * @param foreground    true will set this data access controller to foreground
     */
    public synchronized void addDataAccessController(DataAccessController controller, boolean foreground) {
        // new controller should always be added to the end of the list
        List<DataAccessController> oldControllers, newControllers;

        if (!controllers.contains(controller)) {
            oldControllers = new ArrayList<DataAccessController>(controllers);
            controllers.add(controller);
            newControllers = new ArrayList<DataAccessController>(controllers);
            EventBus.publish(new AddDataSourceEvent<DataAccessController>(this, oldControllers, newControllers));
            if (foreground) {
                setForegroundDataAccessController(controller);
            }
        }
    }

    public synchronized void setAllInitialDataAccessControllerStatuses(DataAccessController controller){
        List<ProcessingDataSourceEvent.Status> statuses = new ArrayList<ProcessingDataSourceEvent.Status>();
        statuses.add(ProcessingDataSourceEvent.Status.INIT_LOADING);
        dataAccessControllerStatus.put(controller, statuses);
    }

    public synchronized void removeDataAccessController(DataAccessController controller) {
        List<DataAccessController> oldControllers, newControllers;

        int index = controllers.indexOf(controller);
        if (index >= 0) {
            oldControllers = new ArrayList<DataAccessController>(controllers);
            // get the next available controller's index
            int nextIndex = controllers.size() - 1 > index ? index : index - 1;
            controllers.remove(controller);
            if (foregroundController != null && foregroundController.equals(controller)) {
                setForegroundDataAccessController(nextIndex >= 0 ? controllers.get(nextIndex) : null);
            }
            controller.close();
            newControllers = new ArrayList<DataAccessController>(controllers);

            EventBus.publish(new RemoveDataSourceEvent<DataAccessController>(this, oldControllers, newControllers));
        }
    }

    /**
     * Replace one data access controller with another, and retain the position in the DataAccessMonitor.
     *
     * @param original    original data access controller
     * @param replacement replacement data access controller
     */
    public synchronized void replaceDataAccessController(DataAccessController original, DataAccessController replacement) {
        List<DataAccessController> oldControllers, newControllers;

        int index = controllers.indexOf(original);
        if (index >= 0) {
            oldControllers = new ArrayList<DataAccessController>(controllers);
            controllers.add(index, replacement);
            controllers.remove(original);
            if (foregroundController != null && foregroundController.equals(original)) {
                setForegroundDataAccessController(replacement);
            }
            original.close();
            newControllers = new ArrayList<DataAccessController>(controllers);
            // notify others
            EventBus.publish(new AddDataSourceEvent<DataAccessController>(this, oldControllers, newControllers));
        } else {
            // add as a new data access controller
            addDataAccessController(replacement);
        }
    }

    public synchronized void setForegroundDataAccessController(DataAccessController controller) {
        DataAccessController oldController, newController;

        oldController = this.foregroundController;
        foregroundController = controller;
        newController = controller;

        ForegroundDataSourceEvent.Status status = ForegroundDataSourceEvent.Status.DATA;
        if (newController instanceof EmptyDataAccessController) {
            status = ForegroundDataSourceEvent.Status.DUMMY;
        } else if (oldController instanceof EmptyDataAccessController) {
            status = ForegroundDataSourceEvent.Status.DUMMY_TO_DATA;
            setAllInitialDataAccessControllerStatuses(controller);
        } else if (newController == null) {
            status = ForegroundDataSourceEvent.Status.EMPTY;
        }

        EventBus.publish(new ForegroundDataSourceEvent<DataAccessController>(this, status, oldController, newController));
    }

    public synchronized DataAccessController getForegroundDataAccessController() {
        return this.foregroundController;
    }

    /**
     * Check whether the given data access controller is foreground data access controller
     *
     * @param controller data access controller
     * @return boolean  true if it is foreground data access controller
     */
    public synchronized boolean isForegroundDataAccessController(DataAccessController controller) {
        return foregroundController != null && foregroundController.equals(controller);
    }

    public List<DataAccessController> getControllers() {
        return copyControllerList();
    }

    public int getNumberOfControllers() {
        return controllers.size();
    }

    public synchronized void close() {
        // ToDo: Exception handling
        for (DataAccessController controller : controllers) {
            controller.close();
        }
    }

    private List<DataAccessController> copyControllerList() {
        synchronized (this) {
            if (controllers.isEmpty()) {
                return Collections.emptyList();
            } else {
                return new ArrayList<DataAccessController>(controllers);
            }
        }
    }

    public synchronized void addStatusController(DataAccessController controller, ProcessingDataSourceEvent.Status status){
        List<ProcessingDataSourceEvent.Status> statuses = null;
        if(dataAccessControllerStatus.containsKey(controller)){
            statuses = dataAccessControllerStatus.get(controller);
        }else{
            statuses = new ArrayList<ProcessingDataSourceEvent.Status>();
        }
        statuses.add(status);
        dataAccessControllerStatus.put(controller,statuses);
        //if(!dataAccessControllerStatus.get(controller).equals(status)) dataAccessControllerStatus.put(controller,status);
    }

    public synchronized void removeStatusController(DataAccessController controller, ProcessingDataSourceEvent.Status status){
        List<ProcessingDataSourceEvent.Status> statuses = dataAccessControllerStatus.get(controller);
        statuses.remove(status);
        if(statuses.contains(ProcessingDataSourceEvent.Status.INIT_LOADING)) statuses.remove(ProcessingDataSourceEvent.Status.INIT_LOADING);
        dataAccessControllerStatus.put(controller,statuses);
    }

    public synchronized List<ProcessingDataSourceEvent.Status> getStatusController(DataAccessController controller){
        if(dataAccessControllerStatus.containsKey(controller)){
            return dataAccessControllerStatus.get(controller);
        }
        return Collections.emptyList();
    }

    public boolean containStatusController(DataAccessController controller, ProcessingDataSourceEvent.Status status) {
        if(dataAccessControllerStatus.containsKey(controller)){
            return dataAccessControllerStatus.get(controller).contains(status);
        }
        return false;
    }
}