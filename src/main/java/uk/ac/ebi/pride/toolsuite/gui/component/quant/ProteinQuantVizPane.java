package uk.ac.ebi.pride.toolsuite.gui.component.quant;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bushe.swing.event.EventService;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;

import javax.swing.*;
import java.awt.*;

/**
 * @author ypriverol
 */
public class ProteinQuantVizPane extends DataAccessControllerPane implements EventBusSubscribable {

    private static Logger logger = LoggerFactory.getLogger(ProteinQuantVizPane.class);
    /**
     * the default background color
     */
    private static final Color BACKGROUND_COLOUR = Color.white;

    private QuantProteinComparisonChart comparisonChart;

    private QuantPeptideComparisonChart comparisonPeptideChart;

    JTabbedPane tabbedPane;


    public ProteinQuantVizPane(DataAccessController controller, JComponent parentComponent) {
        super(controller, parentComponent);
    }

    @Override
    protected void setupMainPane() {
        // set layout
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    }

    @Override
    protected void addComponents() {
        // tabbed pane
        tabbedPane = new JTabbedPane();

        tabbedPane.setBackground(BACKGROUND_COLOUR);

        // tab index
        int tabIndex = 0;
        //Protein Quantitation Chart Panel
        comparisonChart = new QuantProteinComparisonChart(controller);
        comparisonChart.setBorder(BorderFactory.createLineBorder(Color.gray));
        tabbedPane.insertTab(appContext.getProperty("quant.protein.histogram.title"), null,
                        comparisonChart, appContext.getProperty("quant.protein.histogram.title"), tabIndex);
        tabIndex++;

        // Peptide Pane
        comparisonPeptideChart = new QuantPeptideComparisonChart(controller);
        comparisonPeptideChart.setBorder(BorderFactory.createLineBorder(Color.gray));

        tabbedPane.insertTab(appContext.getProperty("quant.peptide.histogram.title"), null,
                comparisonPeptideChart, appContext.getProperty("quant.peptide.histogram.title"), tabIndex);

        this.add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    public void subscribeToEventBus(EventService eventBus) {
        comparisonPeptideChart.subscribeToEventBus(null);
        comparisonChart.subscribeToEventBus(null);
    }
}

