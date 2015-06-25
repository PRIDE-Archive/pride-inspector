package uk.ac.ebi.pride.toolsuite.gui.component.quant;

import org.bushe.swing.event.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.EventBusSubscribable;

import javax.swing.*;
import java.awt.*;

/**
 * @author rwang
 * @author ypriverol
 * Date: 15/08/2011
 * Time: 11:36
 */
public class QuantVizPane extends DataAccessControllerPane implements EventBusSubscribable {

    private static Logger logger = LoggerFactory.getLogger(QuantVizPane.class);

    private static final int DIVIDER_SIZE = 5;
    private static final double SPLIT_PANE_RESIZE_WEIGHT = 0.5;

    private QuantProteinComparisonChart comparisonChart;

    private QuantPeptideComparisonChart comparisonPeptideChart;

    public QuantVizPane(DataAccessController controller, JComponent parentComponent) {
        super(controller, parentComponent);
    }

    @Override
    protected void setupMainPane() {
        // set layout
        this.setLayout(new BorderLayout());
    }

    @Override
    protected void addComponents() {

        // protein quantitative data comparison histogram
        comparisonChart = new QuantProteinComparisonChart(controller);
        comparisonChart.setBorder(BorderFactory.createLineBorder(Color.gray));

        // Spectrum view pane
        comparisonPeptideChart = new QuantPeptideComparisonChart(controller);
        comparisonPeptideChart.setBorder(BorderFactory.createLineBorder(Color.gray));


        // put into a split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerSize(DIVIDER_SIZE);
        splitPane.setResizeWeight(SPLIT_PANE_RESIZE_WEIGHT);

        splitPane.setTopComponent(comparisonChart);
        splitPane.setBottomComponent(comparisonPeptideChart);

        this.add(splitPane, BorderLayout.CENTER);
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    }

    @Override
    public void subscribeToEventBus(EventService eventBus) {
        comparisonPeptideChart.subscribeToEventBus(null);
        comparisonChart.subscribeToEventBus(null);
    }
}
