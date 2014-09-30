package uk.ac.ebi.pride.toolsuite.gui.component.quant;

import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.component.PrideInspectorTabPane;
import uk.ac.ebi.pride.toolsuite.gui.component.startup.ControllerContentPane;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.ScanExperimentTask;

import javax.swing.*;
import java.awt.*;

/**
 * Quantitative data tab is used to visualize quantitative results
 *
 * User: rwang
 * Date: 11/08/2011
 * Time: 08:42
 */
public class QuantTabPane extends PrideInspectorTabPane {

    /**
     * title
     */
    private static final String QUANTITATION_TITLE = "Quantification";
    /**
     * resize weight for inner split pane
     */
    private static final double INNER_SPLIT_PANE_RESIZE_WEIGHT = 0.5;
    /**
     * resize weight for outer split pane
     */
    private static final double OUTER_SPLIT_PANE_RESIZE_WEIGHT = 0.4;
    /**
     * the size of the divider for split pane
     */
    private static final int DIVIDER_SIZE = 6;
    /**
     * Inner split pane contains protein table
     */
    private JSplitPane proteinInnerPane;

    /**
     * Inner split pane contains sample pane and proteinVizPane
     */
    private JSplitPane sampleInnerPane;
    /**
     * Outer split pane contains inner split pane and protein panel
     */
    private JSplitPane outerPane;
    /**
     * Identification selection pane
     */
    private QuantProteinSelectionPane proteinPane;
    /**
     * Peptide selection pane
     */
    private QuantPeptideSelectionPane peptidePane;
    /**
     * Quantitative sample pane
     */
    //private QuantSamplePane samplePane;
    /**
     * visualize spectrum
     */
    private QuantVizPane quantVizPane;

    /**
     * Constructor
     *
     * @param controller data access controller
     * @param parentComp DataContentDisplayPane
     */
    public QuantTabPane(DataAccessController controller, JComponent parentComp) {
        super(controller, parentComp);
    }

    /**
     * Setup the main display pane and its title
     */
    @Override
    protected void setupMainPane() {
        // layout
        this.setLayout(new BorderLayout());

        // set title
        this.setTitle(QUANTITATION_TITLE);

        // set the final icon
//        this.setIcon(GUIUtilities.loadIcon(appContext.getProperty("quantitation.tab.icon.small")));

        // set the loading icon
        this.setLoadingIcon(GUIUtilities.loadIcon(appContext.getProperty("quantitation.tab.loading.icon.small")));
    }

    /**
     * Add the rest components
     */
    @Override
    protected void addComponents() {
        // create the inner split pane
        proteinInnerPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        proteinInnerPane.setBorder(BorderFactory.createEmptyBorder());
        proteinInnerPane.setOneTouchExpandable(true);
        proteinInnerPane.setDividerSize(DIVIDER_SIZE);
        proteinInnerPane.setResizeWeight(INNER_SPLIT_PANE_RESIZE_WEIGHT);

        //sampleInnerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        //sampleInnerPane.setBorder(BorderFactory.createEmptyBorder());
       // sampleInnerPane.setOneTouchExpandable(true);
       // sampleInnerPane.setDividerSize(DIVIDER_SIZE);
       // sampleInnerPane.setResizeWeight(INNER_SPLIT_PANE_RESIZE_WEIGHT);

        // create the outer split pane
        outerPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        outerPane.setBorder(BorderFactory.createEmptyBorder());
        outerPane.setOneTouchExpandable(false);
        outerPane.setDividerSize(DIVIDER_SIZE);
        outerPane.setResizeWeight(OUTER_SPLIT_PANE_RESIZE_WEIGHT);

        // protein identification selection pane
        proteinPane = new QuantProteinSelectionPane(controller, this);
        proteinInnerPane.setTopComponent(proteinPane);

        // sample pane
       // samplePane = new QuantSamplePane(controller);
       // sampleInnerPane.setTopComponent(samplePane);

        // peptide selection pane
        peptidePane = new QuantPeptideSelectionPane(controller);
        //sampleInnerPane.setTopComponent(peptidePane);
        outerPane.setTopComponent(proteinInnerPane);
        proteinInnerPane.setBottomComponent(peptidePane);

        // visualization tab pane
        quantVizPane = new QuantVizPane(controller, this);
        outerPane.setBottomComponent(quantVizPane);

        this.add(outerPane, BorderLayout.CENTER);

        // subscribe to event bus
        proteinPane.subscribeToEventBus(null);
        peptidePane.subscribeToEventBus(null);
        quantVizPane.subscribeToEventBus(null);
    }

    /**
     * Return a reference to the identification pane
     *
     * @return IdentificationSelectionPane  identification selection pane
     */
    public QuantProteinSelectionPane getQuantProteinSelectionPane() {
        return proteinPane;
    }

    /**
     * Return a reference to the peptide pane
     *
     * @return PeptideSelectionPane peptide pane
     */
    public QuantPeptideSelectionPane getQuantPeptideSelectionPane() {
        return peptidePane;
    }

    /**
     * Return a referenc eto the sample pane
     * @return
     */
   // public QuantSamplePane getQuantSamplePane() {
      //  return samplePane;
   // }

    @Override
    public void started(TaskEvent event) {
      //  showIcon(getLoadingIcon());
    }

    @Override
    public void finished(TaskEvent event) {
        showIcon(getIcon());
        Task task = (Task)event.getSource();
        if (task instanceof ScanExperimentTask) {
            proteinPane.getRefSampleButton().getAction().setEnabled(true);
        }
    }

    /**
     * Show a different icon if the parent component is not null and an instance of DataContentDisplayPane
     *
     * @param icon icon to show
     */
    private void showIcon(Icon icon) {
        if (parentComponent != null && parentComponent instanceof ControllerContentPane) {
            ControllerContentPane contentPane = (ControllerContentPane) parentComponent;
            contentPane.setTabIcon(contentPane.getQuantTabIndex(), icon);
        }
    }
}
