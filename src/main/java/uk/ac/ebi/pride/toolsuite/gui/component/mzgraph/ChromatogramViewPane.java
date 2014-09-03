package uk.ac.ebi.pride.toolsuite.gui.component.mzgraph;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.bushe.swing.event.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.PrideDBAccessControllerImpl;
import uk.ac.ebi.pride.utilities.data.core.Chromatogram;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.OpenHelpAction;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.toolsuite.gui.event.container.ChromatogramEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.RetrieveChromatogramTask;
import uk.ac.ebi.pride.toolsuite.mzgraph.ChromatogramBrowser;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Visualize chromatogram
 * <p/>
 * User: rwang
 * Date: 10/06/11
 * Time: 14:42
 */
public class ChromatogramViewPane extends DataAccessControllerPane<Chromatogram, Void> implements EventBusSubscribable {
    private static final Logger logger = LoggerFactory.getLogger(ChromatogramViewPane.class);
    /**
     * In memory spectrum browser
     */
    private ChromatogramBrowser chromaBrowser;
    /**
     * True indicates it is the first spectrum to be visualized
     */
    private boolean isFirstChromatogram;
    /**
     * Subscribe to peptide event
     */
    private SelectChromatogramSubscriber peptideSubscriber;

    public ChromatogramViewPane(DataAccessController controller) {
        super(controller);
    }

    @Override
    protected void setupMainPane() {
        // setup the main pane
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder());
    }

    @Override
    protected void addComponents() {
        isFirstChromatogram = true;
        chromaBrowser = new ChromatogramBrowser();

        // add spectrum help pane
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        String helpTooltip = appContext.getProperty("help.tooltip");
        PrideAction helpAction = new OpenHelpAction(null, helpIcon, "help.mzgraph.chroma");
        helpAction.putValue(Action.SHORT_DESCRIPTION, helpTooltip);
        chromaBrowser.getSidePane().addAction(helpAction, false);

        this.add(chromaBrowser, BorderLayout.CENTER);
    }

    /**
     * Subscribe to local event bus
     */
    public void subscribeToEventBus(EventService eventBus) {
        // get local event bus
        if (eventBus == null) {
            eventBus = ContainerEventServiceFinder.getEventService(this);
        }

        // subscriber
        peptideSubscriber = new SelectChromatogramSubscriber();

        // subscribeToEventBus
        eventBus.subscribe(ChromatogramEvent.class, peptideSubscriber);
    }

    @Override
    public void succeed(TaskEvent<Chromatogram> chromaTaskEvent) {
        Chromatogram chroma = chromaTaskEvent.getValue();
        chromaBrowser.setGraphData(chroma.getTimeArray().getDoubleArray(), chroma.getIntensityArray().getDoubleArray());
        // set source name
        if (controller.getType().equals(DataAccessController.Type.XML_FILE)) {
            chromaBrowser.setSource(((File) controller.getSource()).getName());
        } else if (controller.getType().equals(DataAccessController.Type.DATABASE)) {
            chromaBrowser.setSource("Pride Experiment " + ((PrideDBAccessControllerImpl) controller).getExperimentAcc());
        }
        // set id
        chromaBrowser.setId(chroma.getId());
        isFirstChromatogram = false;
        this.firePropertyChange(DataAccessController.MZGRAPH_TYPE, "", chroma);
    }

    private class SelectChromatogramSubscriber implements EventSubscriber<ChromatogramEvent> {

        @Override
        public void onEvent(ChromatogramEvent event) {
            Comparable chromaId = event.getChromatogramId();

            if (chromaId != null) {
                Task newTask = new RetrieveChromatogramTask(controller, chromaId);
                newTask.addTaskListener(ChromatogramViewPane.this);
                TaskUtil.startBackgroundTask(newTask, controller);
            }
        }
    }
}
