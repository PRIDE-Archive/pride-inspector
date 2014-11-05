package uk.ac.ebi.pride.toolsuite.gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.access.DataAccessMonitor;
import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.component.report.ReportListModel;
import uk.ac.ebi.pride.toolsuite.gui.component.reviewer.LoginRecord;
import uk.ac.ebi.pride.toolsuite.gui.component.startup.WelcomePane;
import uk.ac.ebi.pride.toolsuite.gui.component.ws.PrideArchiveWSSearchPane;
import uk.ac.ebi.pride.toolsuite.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskManager;
import uk.ac.ebi.pride.toolsuite.gui.utils.DataTransferProtocol;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.help.SwingHelpUtilities;
import javax.swing.*;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Overall context of the GUI, this object should only have one instance per application.
 * <p/>
 * It contains all the information which is shared by the whole application
 * <p/>
 * This class contains a list of delegate methods, you should use the methods provided here when possible
 * <p/>
 * User: rwang
 * Date: 03-Mar-2010
 * Time: 16:45:38
 */
public class PrideInspectorContext extends DesktopContext {

    private static final Logger logger = LoggerFactory.getLogger(PrideInspector.class);

    /**
     * triggered when the visibility property of data source browser is changed
     */
    public static final String LEFT_CONTROL_PANE_VISIBILITY = "leftControlPaneVisible";
    /**
     * DataAccessMonitor manages a list of
     */
    private final DataAccessMonitor dataAccessMonitor;

    /**
     * This map maintains a reference between DataAccessController and its data content display pane
     */
    private final Map<DataAccessController, JComponent> dataContentPaneCache;

    /**
     * This map maintains a reference between DataAccessController and its Summary pane
     */

    private final Map<DataAccessController, JComponent> dataSummaryPaneCache;

    /**
     * This map maintains a reference between DataAccessController and its action
     */
    private final Map<DataAccessController, Map<Class<? extends PrideAction>, PrideAction>> sharedActionCache;

    /**
     * data source browser visibility
     */
    private boolean leftControlPaneVisible;

    /**
     * Tracking all the summary report in a list model for each database access controller
     */
    private final Map<DataAccessController, ListModel> summaryReportTracker;

    /**
     * The main help set for PRIDE Inspector
     */
    private HelpSet mainHelpSet;

    /**
     * The main help broker
     */
    private HelpBroker mainHelpBroker;

    /**
     * The path to open file
     */
    private String openFilePath;

    /**
     * List of project details after login to PRIDE-R
     */
    private LoginRecord loginRecord;

    /**
     * welcome pane
     */
    private WelcomePane welcomePane = null;

    /**
     * database search pane
     */
    private PrideArchiveWSSearchPane prideArchiveWSSearchPane = null;

    /**
     * selected data transfter protocol
     */
    private DataTransferProtocol dataTransferProtocol;

    /**
     * Constructor
     */
    public PrideInspectorContext() {
        // instantiate data access monitor
        this.dataAccessMonitor = new DataAccessMonitor();

        // data content pane cache
        this.dataContentPaneCache = new ConcurrentHashMap<DataAccessController, JComponent>();

        // data summary pane cache
        this.dataSummaryPaneCache = new ConcurrentHashMap<DataAccessController, JComponent>();

        // action map
        this.sharedActionCache = new ConcurrentHashMap<DataAccessController, Map<Class<? extends PrideAction>, PrideAction>>();

        // summary report tracker
        this.summaryReportTracker = new ConcurrentHashMap<DataAccessController, ListModel>();

        // by default the data source browser is invisible
        this.leftControlPaneVisible = false;

        // set the default path for opening/saving files
        this.setOpenFilePath(System.getProperty("user.dir"));

        // set the default protocol to aspera
        this.setDataTransferProtocol(DataTransferProtocol.NONE);

    }

    public synchronized DataTransferProtocol getDataTransferProtocol() {
        return dataTransferProtocol;
    }

    public synchronized void setDataTransferProtocol(DataTransferProtocol dataTransferProtocol) {
        this.dataTransferProtocol = dataTransferProtocol;
    }

    /**
     * Get data access monitor
     *
     * @return DataAccessMonitor    data access monitor manages all data access controllers
     */
    public final DataAccessMonitor getDataAccessMonitor() {
        return dataAccessMonitor;
    }

    public WelcomePane getWelcomePane() {
        return welcomePane;
    }

    public void setWelcomePane(WelcomePane welcomePane) {
        this.welcomePane = welcomePane;
    }

    public PrideArchiveWSSearchPane getPrideArchiveWSSearchPane() {
        return prideArchiveWSSearchPane;
    }

    public void setPrideArchiveWSSearchPane(PrideArchiveWSSearchPane prideArchiveWSSearchPane) {
        this.prideArchiveWSSearchPane = prideArchiveWSSearchPane;
    }

    /**
     * Get a list of existing data access controllers
     * <p/>
     * Delegate method
     *
     * @return List<DataAccessController>   a list of existing data access controller
     */
    public final List<DataAccessController> getControllers() {
        return dataAccessMonitor.getControllers();
    }

    /**
     * Get the number of existing data access controllers
     * <p/>
     * Delegate method
     *
     * @return int  the number of data access controllers
     */
    public final int getNumberOfControllers() {
        return dataAccessMonitor.getNumberOfControllers();
    }

    /**
     * Check whether the data access controller is a foreground data access controller
     * <p/>
     * Delegate method
     *
     * @param controller data access controller
     * @return boolean  true if it is data access controller
     */
    public final boolean isForegroundDataAccessController(DataAccessController controller) {
        return dataAccessMonitor.isForegroundDataAccessController(controller);
    }

    /**
     * Get foreground data access controller
     * <p/>
     * Delegate method
     *
     * @return DataAccessController data access controller
     */
    public final DataAccessController getForegroundDataAccessController() {
        return dataAccessMonitor.getForegroundDataAccessController();
    }

    /**
     * Set foreground data access controller
     * <p/>
     * Delegate method
     *
     * @param controller data access controller
     */
    public final void setForegroundDataAccessController(DataAccessController controller) {
        dataAccessMonitor.setForegroundDataAccessController(controller);
    }

    /**
     * Remove data access controller
     * <p/>
     * This method will close data access controller, it will also stop all ongoing tasks related to this
     * data access controller.
     *
     * @param controller  data access controller
     * @param cancelTasks whether to cancel the tasks associated with this controller
     */
    public final void removeDataAccessController(DataAccessController controller, boolean cancelTasks) {
        if (cancelTasks) {
            // cancel all the tasks related to this data access controller
            TaskManager taskMgr = this.getTaskManager();
            taskMgr.cancelTasksByOwner(controller);
        }

        // remove gui component associated with this data access controller
        removeDataContentPane(controller);

        // remove pride action
        removePrideAction(controller);

        // remove summary report
        summaryReportTracker.remove(controller);

        // remove data access controller
        dataAccessMonitor.removeDataAccessController(controller);

    }

    /**
     * Remove summary report
     * <p/>
     * This method will close data access controller, it will also stop all ongoing tasks related to this
     * data access controller.
     *
     * @param controller data access controller
     */
    public final void replaceSummaryReport(DataAccessController controller, DataAccessController replacement) {
        // remove summary report
        summaryReportTracker.remove(controller);
        // add new summary report for the new data access controller
        getSummaryReportModel(replacement);
    }


    /**
     * Replace one data access controller with another.
     *
     * @param original    original data access controller
     * @param replacement replacement data access controller
     * @param cancelTasks whether to cancel the tasks associated with this controller
     */
    public final void replaceDataAccessController(DataAccessController original, DataAccessController replacement, boolean cancelTasks) {
        if (cancelTasks) {
            // cancel all the tasks related to this data access controller
            TaskManager taskMgr = this.getTaskManager();
            taskMgr.cancelTasksByOwner(original);
        }

        // remove gui component
        removeDataContentPane(original);

        // remove pride action
        removePrideAction(original);

        // remove summary report for the original data access controller
        summaryReportTracker.remove(original);

        // add new summary report for the new data access controller
        getSummaryReportModel(replacement);

        // replace
        dataAccessMonitor.replaceDataAccessController(original, replacement);
    }

    /**
     * Add a new data access controller, this will register the controller with PRIDE inspector system.
     * <p/>
     * Delegate method
     *
     * @param controller data access controller
     */
    public final void addDataAccessController(DataAccessController controller) {
        addDataAccessController(controller, true);
    }

    /**
     * Add a new data access controller, and set it foreground status
     *
     * @param controller data access controller
     * @param foreground foreground status
     */
    public final void addDataAccessController(DataAccessController controller, boolean foreground) {
        // initialize summary report model
        getSummaryReportModel(controller);

        // set left control panel to visible
        setLeftControlPaneVisible(true);

        dataAccessMonitor.addDataAccessController(controller, foreground);
    }

    /**
     * Get data content pane created using the input data access controller
     * <p/>
     * Delegate method
     *
     * @param controller data access controller
     * @return JComponent   DataContentDisplayPane
     */
    public final JComponent getDataContentPane(DataAccessController controller) {
        return dataContentPaneCache.get(controller);
    }

    /**
     * Get summary pane created using the input data access controller
     * <p/>
     * Delegate method
     *
     * @param controller data access controller
     * @return JComponent   DataContentDisplayPane
     */
    public final JComponent getSummaryPane(DataAccessController controller) {
        return dataSummaryPaneCache.get(controller);
    }

    /**
     * Cache a content display pane for a data access controller
     * <p/>
     * Delegate method
     *
     * @param controller data access controller
     * @param component  data content pane
     */
    public final void addDataContentPane(DataAccessController controller, JComponent component) {
        dataContentPaneCache.put(controller, component);
    }

    /**
     * Cache a content display summary for a data access controller
     * <p/>
     * Delegate method
     *
     * @param controller data access controller
     * @param component  summary pane
     */
    public final void addSummaryContentPane(DataAccessController controller, JComponent component) {
        dataSummaryPaneCache.put(controller, component);
    }

    /**
     * Remove a data content pane from cache
     * <p/>
     * Delegate method
     *
     * @param controller data access controller
     */
    public final void removeDataContentPane(DataAccessController controller) {
        dataContentPaneCache.remove(controller);
    }

    /**
     * Get pride action of a given data access controller and pride action class
     *
     * @param controller  data access controller
     * @param actionClass pride action class
     * @return PrideAction pride action
     */
    public final synchronized PrideAction getPrideAction(DataAccessController controller, Class<? extends PrideAction> actionClass) {
        Map<Class<? extends PrideAction>, PrideAction> actionMap = sharedActionCache.get(controller);
        if (actionMap != null) {
            return actionMap.get(actionClass);
        }
        return null;
    }

    /**
     * Add a pride action of a given data access controller
     *
     * @param controller data access controller
     * @param action     pride action
     */
    public final synchronized void addPrideAction(DataAccessController controller, PrideAction action) {
        Map<Class<? extends PrideAction>, PrideAction> actionMap = sharedActionCache.get(controller);
        if (actionMap == null) {
            actionMap = new HashMap<Class<? extends PrideAction>, PrideAction>();
            sharedActionCache.put(controller, actionMap);
        }
        actionMap.put(action.getClass(), action);
    }

    /**
     * Remove a pride action of a given data access controller
     *
     * @param controller  data access controller
     * @param actionClass pride action class
     */
    public final void removePrideAction(DataAccessController controller, Class<? extends PrideAction> actionClass) {
        Map<Class<? extends PrideAction>, PrideAction> actionMap = sharedActionCache.get(controller);
        if (actionMap != null) {
            actionMap.remove(actionClass);
            if (actionMap.isEmpty()) {
                sharedActionCache.remove(controller);
            }
        }
    }

    /**
     * Remove all the pride actions related to a given data access controller
     *
     * @param controller data access controller
     */
    public final void removePrideAction(DataAccessController controller) {
        sharedActionCache.remove(controller);
    }

    /**
     * Return the visibility of data source browser
     *
     * @return boolean visibility of the data source browser
     */
    public final synchronized boolean isLeftControlPaneVisible() {
        return leftControlPaneVisible;
    }

    /**
     * Set the visibility of the data source browser
     * A LEFT_CONTROL_PANE_VISIBILITY property change event will be triggered.
     *
     * @param leftControlPaneVisible the new visibility of the data source browser
     */
    public void setLeftControlPaneVisible(boolean leftControlPaneVisible) {
        logger.debug("Set data source browser visibility to: {}", leftControlPaneVisible);
        boolean oldVis, newVis;
        synchronized (this) {
            oldVis = this.leftControlPaneVisible;
            this.leftControlPaneVisible = leftControlPaneVisible;
            newVis = leftControlPaneVisible;
        }
        firePropertyChange(LEFT_CONTROL_PANE_VISIBILITY, oldVis, newVis);
    }

    private void createHelp() {
        try {
            SwingHelpUtilities.setContentViewerUI("uk.ac.ebi.pride.toolsuite.gui.help.ExternalLinkContentViewerUI");
            ClassLoader cl = PrideInspectorContext.class.getClassLoader();
            URL url = HelpSet.findHelpSet(cl, this.getProperty("help.main.set"));
            mainHelpSet = new HelpSet(cl, url);
            mainHelpBroker = mainHelpSet.createHelpBroker();
        } catch (HelpSetException e) {
            logger.error("Failed to initialize help documents", e);
        }
    }

    public HelpSet getMainHelpSet() {
        if (mainHelpSet == null) {
            createHelp();
        }
        return mainHelpSet;
    }

    public HelpBroker getMainHelpBroker() {
        if (mainHelpBroker == null) {
            createHelp();
        }
        return mainHelpBroker;
    }

    /**
     * Get the current file open path
     *
     * @return String file path
     */
    public String getOpenFilePath() {
        return openFilePath;
    }

    /**
     * Set the file open path, this will effect the starting directory of
     * the <code>OpenFileDialog</code>
     *
     * @param openFilePath file open path
     */
    public void setOpenFilePath(String openFilePath) {
        this.openFilePath = openFilePath;
    }

    public LoginRecord getLoginRecord() {
        return loginRecord;
    }

    public void setLoginRecord(LoginRecord loginRecord) {
        this.loginRecord = loginRecord;
    }

    /**
     * Get the summary report model for a given data access controller
     *
     * @param controller data access controller
     * @return ListModel   summary report model
     */
    public synchronized ListModel getSummaryReportModel(DataAccessController controller) {
        ListModel model = controller == null ? null : summaryReportTracker.get(controller);

        if (model == null) {
            model = new ReportListModel(controller);
        }

        if (controller != null)
            summaryReportTracker.put(controller, model);

        return model;
    }
}
