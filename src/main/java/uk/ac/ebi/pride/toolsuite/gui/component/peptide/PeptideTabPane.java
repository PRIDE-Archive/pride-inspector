package uk.ac.ebi.pride.toolsuite.gui.component.peptide;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.bushe.swing.event.EventSubscriber;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.toolsuite.gui.component.PrideInspectorTabPane;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;
import uk.ac.ebi.pride.toolsuite.gui.component.mzdata.MzDataTabPane;
import uk.ac.ebi.pride.toolsuite.gui.component.startup.ControllerContentPane;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideSpeciesPSMTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideTableHeader;
import uk.ac.ebi.pride.toolsuite.gui.event.container.ExpandPanelEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.PSMEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;


import javax.swing.*;
import java.awt.*;

/**
 * PeptideTabPane provides a peptide centric view to all peptides in one experiment.
 * <p/>
 * User: rwang
 * Date: 03-Sep-2010
 * Time: 10:46:06
 */
public class PeptideTabPane extends PrideInspectorTabPane {

    private static final Logger logger = Logger.getLogger(MzDataTabPane.class.getName());

    /**
     * title
     */
    private static final String PEPTIDE_TITLE = "Peptide";
    /**
     * resize weight for inner split pane
     */
    private static final double INNER_SPLIT_PANE_RESIZE_WEIGHT = 0.6;
    /**
     * resize weight for outer split pane
     */
    private static final double OUTER_SPLIT_PANE_RESIZE_WEIGHT = 0.6;
    /**
     * the size of the divider for split pane
     */
    private static final int DIVIDER_SIZE = 5;
    /**
     * Inner split pane contains peptideDescPane and peptidePTMPane
     */
    private JSplitPane innerSplitPane;
    /**
     * Outer split pane contains inner split pane and spectrumViewPane
     */
    private JSplitPane outterSplitPane;
    /**
     * Display peptide details
     */
    private PeptideDescriptionPane peptideDescPane;
    /**
     * Display PSM details
     */
    private PeptidePSMPane peptidePSMPane;
    /**
     * Visualize spectrum and protein sequence
     */
    private PeptideVizPane vizTabPane;
    /**
     * Subscribe to expand peptide panel
     */
    private ExpandPeptidePanelSubscriber expandPeptidePanelSubscriber;

       /**
     * Constructor
     *
     * @param controller data access controller
     * @param parentComp parent container
     */
    public PeptideTabPane(DataAccessController controller, JComponent parentComp) {
        super(controller, parentComp);
    }

    /**
     * Setup the main display main
     */
    @Override
    protected void setupMainPane() {
        // add event subscriber
        expandPeptidePanelSubscriber = new ExpandPeptidePanelSubscriber();
        getContainerEventService().subscribe(ExpandPanelEvent.class, expandPeptidePanelSubscriber);

        // set properties for IdentTabPane
        this.setLayout(new BorderLayout());

        // title for the tab pane
        try {
            //int numberOfPeptides = controller.getNumberOfPeptides();
            //String title = " (" + numberOfPeptides + ")";
            //this.setTitle(PEPTIDE_TITLE + title);
            this.setTitle(PEPTIDE_TITLE);
        } catch (DataAccessException dex) {
            String msg = String.format("%s failed on : %s", this, dex);
            logger.log(Level.ERROR, msg, dex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));
        }
        // set the final icon
//        this.setIcon(GUIUtilities.loadIcon(appContext.getProperty("peptide.tab.icon.small")));

        // set the loading icon
        //this.setLoadingIcon(GUIUtilities.loadIcon(appContext.getProperty("peptide.tab.loading.icon.small")));
    }

    /**
     * Add the rest of components
     */
    @Override
    protected void addComponents() {
        // inner split pane
        innerSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        innerSplitPane.setBorder(BorderFactory.createEmptyBorder());
        innerSplitPane.setOneTouchExpandable(false);
        innerSplitPane.setDividerSize(DIVIDER_SIZE);
        innerSplitPane.setResizeWeight(INNER_SPLIT_PANE_RESIZE_WEIGHT);

        // outer split pane
        outterSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        outterSplitPane.setBorder(BorderFactory.createEmptyBorder());
        outterSplitPane.setOneTouchExpandable(false);
        outterSplitPane.setDividerSize(DIVIDER_SIZE);
        outterSplitPane.setResizeWeight(OUTER_SPLIT_PANE_RESIZE_WEIGHT);

        // protein identification selection pane
        peptideDescPane = new PeptideDescriptionPane(controller);
        outterSplitPane.setTopComponent(peptideDescPane);

        // peptide selection pane
        peptidePSMPane = new PeptidePSMPane(controller);
        innerSplitPane.setTopComponent(peptidePSMPane);


        // Spectrum view pane
        vizTabPane = new PeptideVizPane(controller, this);
        vizTabPane.setMinimumSize(new Dimension(200, 200));
        innerSplitPane.setBottomComponent(vizTabPane);
        outterSplitPane.setBottomComponent(innerSplitPane);

        this.add(outterSplitPane, BorderLayout.CENTER);

        // subscribe to local event bus
        peptidePSMPane.subscribeToEventBus(null);
        vizTabPane.subscribeToEventBus(null);
    }

    /**
     * Get a reference to peptide pane
     *
     * @return PeptideDescriptionPane   peptide pane
     */
    public PeptideDescriptionPane getPeptidePane() {
        return peptideDescPane;
    }

    public PeptideVizPane getVizTabPane() {
        return vizTabPane;
    }

    @Override
    public void started(TaskEvent event) {
        parentComponent.revalidate();
        parentComponent.repaint();
     //showIcon(getLoadingIcon());
     //loadingIcon.addTaskListener(this);
     //TaskUtil.startBackgroundTask(loadingIcon, controller);
    }

    @Override
    public void finished(TaskEvent event) {
        showIcon(getIcon());
    }

    /**
     * Show a different icon if the parent component is not null and an instance of DataContentDisplayPane
     *
     * @param icon icon to show
     */
    private void showIcon(Icon icon) {
        if (parentComponent != null && parentComponent instanceof ControllerContentPane) {
            ControllerContentPane contentPane = (ControllerContentPane) parentComponent;
            contentPane.setTabIcon(contentPane.getPeptideTabIndex(), icon);
        }
    }

    /**
     * Event handler for expanding protein panel
     */
    private class ExpandPeptidePanelSubscriber implements EventSubscriber<ExpandPanelEvent> {

        @Override
        public void onEvent(ExpandPanelEvent event) {
            boolean visible = vizTabPane.isVisible();
            vizTabPane.setVisible(!visible);
            outterSplitPane.setDividerSize(visible ? 0 : DIVIDER_SIZE);
            outterSplitPane.resetToPreferredSizes();
        }
    }

    public void peptideChange() {

        JTable psmTable = peptidePSMPane.getPSMTable();

        int rowNum = (psmTable.getSelectedRow() >= 0) ? psmTable.getSelectedRow() : 0;
        if (rowNum >= 0) {
            // get table model
            PeptideSpeciesPSMTableModel pepTableModel = (PeptideSpeciesPSMTableModel) psmTable.getModel();

            // get spectrum reference column
            int identColNum = pepTableModel.getColumnIndex(PeptideTableHeader.IDENTIFICATION_ID.getHeader());
            int peptideColNum = pepTableModel.getColumnIndex(PeptideTableHeader.PEPTIDE_ID.getHeader());

            // get spectrum id
            int modelRowIndex = peptideDescPane.getPeptideTable().convertRowIndexToModel(rowNum);
            Comparable identId = (Comparable) pepTableModel.getValueAt(modelRowIndex, identColNum);
            Comparable peptideId = (Comparable) pepTableModel.getValueAt(modelRowIndex, peptideColNum);

            // fire a background task to retrieve peptide
            if (peptideId != null && identId != null) {
                // publish the event to local event bus
                EventService eventBus = ContainerEventServiceFinder.getEventService(peptideDescPane);
                eventBus.publish(new PSMEvent(peptideDescPane, controller, identId, peptideId));
            }
        }
    }


}
