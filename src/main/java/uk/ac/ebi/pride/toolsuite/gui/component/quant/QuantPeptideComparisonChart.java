package uk.ac.ebi.pride.toolsuite.gui.component.quant;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.bushe.swing.event.EventSubscriber;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.jmztab.model.StudyVariable;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.toolsuite.gui.event.QuantSelectionEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.ReferenceSampleChangeEvent;
import uk.ac.ebi.pride.toolsuite.gui.io.FileExtension;
import uk.ac.ebi.pride.toolsuite.gui.io.SaveComponentUtils;
import uk.ac.ebi.pride.toolsuite.gui.io.SaveImageDialog;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.CvParam;
import uk.ac.ebi.pride.utilities.data.core.QuantScore;
import uk.ac.ebi.pride.utilities.data.core.Quantification;
import uk.ac.ebi.pride.utilities.data.core.QuantitativeSample;
import uk.ac.ebi.pride.utilities.term.QuantCvTermReference;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JFreeChar Panel for Peptide Quantitation data.
 * @author ypriverol
 * @author rwang
 *
 */
public class QuantPeptideComparisonChart extends DataAccessControllerPane implements EventBusSubscribable {

    private static final Logger logger = LoggerFactory.getLogger(QuantPeptideComparisonChart.class);

    /**
     * Data set used to render the bar chart
     */
    private QuantCategoryDataset dataset;
    /**
     * Renderer used to draw the bars
     */
    private BarRenderer renderer;
    /**
     * whether to set the default colour series for bars
     */
    private boolean colorSet;
    /**
     * Index of the control sample
     */
    private int referenceSampleIndex = -1;
    /**
     * mapping between peptide identification id and the label of each category
     */
    private Map<Comparable, Map<Comparable, List<Comparable>>> idMapping;
    /**
     * Event subscriber listens to Peptide selection event
     */
    private QuantSelectionSubscriber peptideSelectionSubscriber;
    /**
     * Event subscriber listens to reference sample change event
     */
    private ReferenceSampleSubscriber referenceSampleSubscriber;
    /**
     * Warning message to show when no protein is selected
     */
    private String noPeptideSelectionMessage;
    /**
     * Whether there are proteins selected
     */
    private boolean noProteinSelected;


    public QuantPeptideComparisonChart(DataAccessController controller) {
        super(controller);
        this.idMapping = new HashMap<Comparable, Map<Comparable, List<Comparable>>>();
        this.noProteinSelected = true;
        this.noPeptideSelectionMessage = appContext.getProperty("no.peptide.selection.warning.message");
        if(controller.getType().equals(DataAccessController.Type.MZTAB))
            referenceSampleIndex = 0;
    }

    @Override
    protected void setupMainPane() {
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createLineBorder(Color.gray));
    }

    @Override
    protected void addComponents() {
        dataset = new QuantCategoryDataset();
        JFreeChart chart = ChartFactory.createBarChart(appContext.getProperty("quant.peptide.histogram.title"),
                appContext.getProperty("quant.histogram.x.axis"),
                appContext.getProperty("quant.histogram.y.axis"),
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);
        // set chart title size
        TextTitle title = chart.getTitle();
        title.setFont(title.getFont().deriveFont(15f));
        // plot
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setRangePannable(true);
        plot.setRangeZeroBaselineVisible(true);
        plot.setRangeGridlinePaint(Color.lightGray);
        plot.setRangeCrosshairVisible(true);
        plot.setRangeCrosshairValue(1);
        // renderer
        renderer = new BarRenderer();
        plot.setRenderer(renderer);
        renderer.setItemMargin(0);
        renderer.setMaximumBarWidth(20);
        renderer.setShadowVisible(false);

        //Legend
        QuantCategoryItemLaegendLabelGenerator legendlabelGenerator = new QuantCategoryItemLaegendLabelGenerator();
        renderer.setLegendItemLabelGenerator(legendlabelGenerator);

        // label
        renderer.setBaseItemLabelPaint(Color.black);
        QuantCategoryItemLabelGenerator labelGenerator = new QuantCategoryItemLabelGenerator();
        renderer.setBaseItemLabelGenerator(labelGenerator);
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelFont(new Font("SansSerif", Font.PLAIN, 10));
        ItemLabelPosition p = new ItemLabelPosition(
                ItemLabelAnchor.CENTER, TextAnchor.CENTER,
                TextAnchor.CENTER, -Math.PI / 2.0);
        renderer.setBasePositiveItemLabelPosition(p);

        ItemLabelPosition p2 = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.CENTER_LEFT,
                TextAnchor.CENTER_LEFT, -Math.PI / 2.0);
        renderer.setPositiveItemLabelPositionFallback(p2);
        // tooltips
        renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator(
                "{2}", NumberFormat.getInstance()));

        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setBorder(null);
        chartPanel.setMouseZoomable(true, true);
        chartPanel.setZoomAroundAnchor(true);
        chartPanel.setDisplayToolTips(true);
        // change the scaling of the chart
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        // get screen size
        Dimension screenDim = toolkit.getScreenSize();
        chartPanel.setMaximumDrawWidth(screenDim.width);
        chartPanel.setMaximumDrawHeight(screenDim.height);
        // not popup menu
        chartPanel.setPopupMenu(createPopupMenu(chartPanel));
        this.add(chartPanel, BorderLayout.CENTER);
    }

    /**
     * Create a popup menu for saving and zoom out the jfreechart
     *
     * @param chartPanel
     * @return  JPopupMenu
     */
    private JPopupMenu createPopupMenu(final ChartPanel chartPanel) {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem saveMenuItem = new JMenuItem("Save...");
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SaveImageDialog saveImageDialog = new SaveImageDialog(new File(appContext.getOpenFilePath()), "");
                int result = saveImageDialog.showSaveDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    String outputFile = saveImageDialog.getSelectedFile().getAbsolutePath();
                    String extensionDesc = saveImageDialog.getFileFilter().getDescription();
                    try {
                        if (FileExtension.PDF.getExtensionDescription().equals(extensionDesc)) {
                            if (!outputFile.endsWith(FileExtension.PDF.getExtension())) {
                                outputFile += FileExtension.PDF.getExtension();
                            }
                            SaveComponentUtils.writeAsPDF(new File(outputFile), chartPanel);
                        } else if (FileExtension.SVG.getExtensionDescription().equals(extensionDesc)) {
                            if (!outputFile.endsWith(FileExtension.SVG.getExtension())) {
                                outputFile += FileExtension.SVG.getExtension();
                            }
                            SaveComponentUtils.writeAsSVG(new File(outputFile), chartPanel);
                        } else if (FileExtension.PNG.getExtensionDescription().equals(extensionDesc)) {
                            if (!outputFile.endsWith(FileExtension.PNG.getExtension())) {
                                outputFile += FileExtension.PNG.getExtension();
                            }
                            SaveComponentUtils.writeAsPNG(new File(outputFile), chartPanel);
                        } else if (FileExtension.JPEG.getExtensionDescription().equals(extensionDesc)) {
                            if (!outputFile.endsWith(FileExtension.JPEG.getExtension())) {
                                outputFile += FileExtension.JPEG.getExtension();
                            }
                            SaveComponentUtils.writeAsJPEG(new File(outputFile), chartPanel);
                        } else if (FileExtension.GIF.getExtensionDescription().equals(extensionDesc)) {
                            if (!outputFile.endsWith(FileExtension.GIF.getExtension())) {
                                outputFile += FileExtension.GIF.getExtension();
                            }
                            SaveComponentUtils.writeAsGIF(new File(outputFile), chartPanel);
                        }
                    } catch (IOException ioe) {
                        logger.error("Failed to save the protein quantitative comparison chart");
                    }
                }
            }
        });
        popupMenu.add(saveMenuItem);

        JMenuItem printMenuItem = new JMenuItem("Print");
        printMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartPanel.createChartPrintJob();
            }
        });
        popupMenu.add(printMenuItem);

        popupMenu.add(new JSeparator());

        JMenuItem zoomOutMenuItem = new JMenuItem("Zoom out");
        zoomOutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartPanel.restoreAutoBounds();
            }
        });
        popupMenu.add(zoomOutMenuItem);

        return popupMenu;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (noProteinSelected) {
            // paint a semi transparent glass pane
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // get bound
            Rectangle clip = g.getClipBounds();

            // set composite
            Composite oldComposite = g2.getComposite();
            g2.setComposite(AlphaComposite.SrcOver.derive(0.30f));
            // set colour
            g2.setPaint(new Color(185, 218, 201));
            // paint panel
            g2.fillRect(clip.x, clip.y, clip.width, clip.height);

            // reset composite
            g2.setComposite(oldComposite);

            // paint message
            g2.setPaint(Color.gray);
            g2.setFont(g2.getFont().deriveFont(20f).deriveFont(Font.BOLD));
            FontMetrics fontMetrics = g2.getFontMetrics();
            int msgWidth = fontMetrics.stringWidth(noPeptideSelectionMessage);
            int xPos = clip.x + clip.width / 2 - msgWidth / 2;
            g2.drawString(noPeptideSelectionMessage, xPos, clip.height / 2);
            g2.dispose();
        }
    }

    @Override
    public void subscribeToEventBus(EventService eventBus) {
        // get local event bus
        if (eventBus == null) {
            eventBus = ContainerEventServiceFinder.getEventService(this);
        }

        // subscriber
        peptideSelectionSubscriber = new QuantSelectionSubscriber();
        referenceSampleSubscriber = new ReferenceSampleSubscriber();

        // subscribeToEventBus
        eventBus.subscribe(QuantSelectionEvent.class, peptideSelectionSubscriber);
        eventBus.subscribe(ReferenceSampleChangeEvent.class, referenceSampleSubscriber);
    }

    /**
     * Set the colour for different render series, this is specific to jfreechart
     */
    private void setChartBarColour() {
        renderer.setSeriesPaint(0, new Color(166, 206, 227));
        renderer.setSeriesPaint(1, new Color(31, 120, 180));
        renderer.setSeriesPaint(2, new Color(51, 160, 44));
        renderer.setSeriesPaint(3, new Color(255, 127, 0));
        renderer.setSeriesPaint(4, new Color(127, 201, 127));
        renderer.setSeriesPaint(5, new Color(190, 174, 212));
        renderer.setSeriesPaint(6, new Color(253, 192, 134));
        renderer.setSeriesPaint(7, new Color(56, 108, 176));
        renderer.setSeriesPaint(8, new Color(240, 2, 127));
        renderer.setSeriesPaint(9, new Color(191, 91, 23));
    }

    /**
     * Listen to QuantSelectionEvent
     */
    private class QuantSelectionSubscriber implements EventSubscriber<QuantSelectionEvent> {

        @Override
        public void onEvent(QuantSelectionEvent selectionEvent) {
            if (QuantSelectionEvent.Type.PEPTIDE.equals(selectionEvent.getType())) {
                Comparable id = selectionEvent.getId();
                Comparable idPeptide = selectionEvent.getPeptideId();
                if (selectionEvent.isSelected()) {
                    noProteinSelected = false;
                    addData(id, idPeptide);
                    if (!colorSet) {
                        setChartBarColour();
                        colorSet = true;
                    }
                } else {
                    removeData(id, idPeptide);
                }
            }
        }
    }

    /**
     * Listen to ReferenceSampleChangeEvent
     */
    private class ReferenceSampleSubscriber implements EventSubscriber<ReferenceSampleChangeEvent> {
        @Override
        public void onEvent(ReferenceSampleChangeEvent referenceSampleChangeEvent) {
            int newReferenceSampleIndex = referenceSampleChangeEvent.getReferenceSampleIndex();
            if (newReferenceSampleIndex != referenceSampleIndex) {
                // clear dataset
                dataset.clear();
                // set new reference sample index
                referenceSampleIndex = newReferenceSampleIndex;
                // regenerate new dataset
                for (Map.Entry proteinEntry : idMapping.entrySet()) {
                    Comparable proteinId = (Comparable) proteinEntry.getKey();
                    Map<Comparable, List<Comparable>> peptideIds = (Map<Comparable, List<Comparable>>) proteinEntry.getValue();
                    for(Comparable peptideId: peptideIds.keySet())
                        addData(proteinId, peptideId);
                }
            }
        }
    }

    /**
     * Add a new data row into the bar chart
     *
     * @param id protein id
     */
    private void addData(Comparable id, Comparable idPeptide) {
        try {
            if(!controller.getType().equals(DataAccessController.Type.MZTAB)){
                // get protein accession
                String proteinAcc = controller.getProteinAccession(id);

                String sequence   = controller.getPeptideSequence(id,idPeptide);
                // get quantitation data
                Quantification quantitation = controller.getPeptideQuantData(id,idPeptide);

                QuantitativeSample sample = controller.getQuantSample();
                if (referenceSampleIndex < 1) {
                    referenceSampleIndex = controller.getReferenceSubSampleIndex();
                }
                // get reference reagent
                Double referenceReagentResult = quantitation.getIsotopeLabellingResult(referenceSampleIndex);
                CvParam referenceReagent = sample.getReagent(referenceSampleIndex);
                // get short label for the reagent
                for (int i = 1; i < QuantitativeSample.MAX_SUB_SAMPLE_SIZE; i++) {
                    if (referenceSampleIndex != i) {
                        CvParam reagent = sample.getReagent(i);
                        if (reagent != null) {
                            Double reagentResult = quantitation.getIsotopeLabellingResult(i);
                            double value = (referenceReagentResult == null || reagentResult == null) ? 0 : (reagentResult / referenceReagentResult);
                            Comparable column = QuantCvTermReference.getReagentShortLabel(reagent.getAccession())
                                    + "/" + QuantCvTermReference.getReagentShortLabel(referenceReagent.getAccession());
                            dataset.addValue(value, sequence, proteinAcc, id.toString() + idPeptide.toString(), column);
                            Map<Comparable, List<Comparable>> peptides = new HashMap<Comparable, List<Comparable>>();
                            List<Comparable> columns = new ArrayList<Comparable>();

                            if( idMapping.get(id) != null ){
                                peptides = idMapping.get(id);
                                if(peptides.get(idPeptide) != null)
                                    columns = peptides.get(idPeptide);
                            }
                            columns.add(column);
                            peptides.put(idPeptide, columns);
                            idMapping.put(id, peptides);
                        }
                    }
                }
            }else{
                // get protein accession
                String proteinAcc = controller.getProteinAccession(id);

                String sequence   = controller.getQuantPeptideSequence(id, idPeptide);
                // get quantitation data
                QuantScore quantitation = controller.getQuantPeptideByIndex(id, idPeptide).getQuantScore();


                if(referenceSampleIndex == 0){
                   for(Comparable column: quantitation.getStudyVariableScores().keySet()){
                       dataset.addValue(quantitation.getStudyVariableScores().get(column), sequence, proteinAcc, id.toString() + idPeptide.toString(), column.toString());

                       Map<Comparable, List<Comparable>> peptides = new HashMap<Comparable, List<Comparable>>();
                       List<Comparable> columns = new ArrayList<Comparable>();

                       if( idMapping.get(id) != null ){
                           peptides = idMapping.get(id);
                           if(peptides.get(idPeptide) != null)
                               columns = peptides.get(idPeptide);
                       }
                       columns.add(column);
                       peptides.put(idPeptide, columns);
                       idMapping.put(id, peptides);
                   }
                }else{
                    for(Comparable column: quantitation.getAssayAbundance().keySet()){
                        dataset.addValue(quantitation.getAssayAbundance().get(column), sequence, proteinAcc, id.toString() + idPeptide.toString(), column.toString());

                        Map<Comparable, List<Comparable>> peptides = new HashMap<Comparable, List<Comparable>>();
                        List<Comparable> columns = new ArrayList<Comparable>();

                        if( idMapping.get(id) != null ){
                            peptides = idMapping.get(id);
                            if(peptides.get(idPeptide) != null)
                                columns = peptides.get(idPeptide);
                        }
                        columns.add(column);
                        peptides.put(idPeptide, columns);
                        idMapping.put(id, peptides);
                    }

                }
            }

        } catch (DataAccessException e) {
            logger.error("Failed to retrieve quantitative data", e);
        }
    }

    /**
     * Remove a data row from the bar chart
     *
     * @param id protein id
     */
    private void removeData(Comparable id, Comparable idPeptide) {
        // remove from bar chart
        Map<Comparable, List<Comparable>> peptides = idMapping.get(id);
        if (peptides != null && peptides.get(idPeptide) != null) {
            List<Comparable> columns = peptides.get(idPeptide);
            for (Comparable column : columns) {
                dataset.removeValue(id.toString() + idPeptide.toString(), column);
            }
            // remove mapping
            peptides.remove(idPeptide);
            if(idMapping.get(id).size() == 0)
                idMapping.remove(id);
        }
    }

    private class QuantCategoryDataset extends DefaultCategoryDataset {

        private Map<Comparable, Comparable> labelMap;
        private Map<Comparable, Comparable> legendMap;

        private QuantCategoryDataset() {
            labelMap = new HashMap<Comparable, Comparable>();
            legendMap = new HashMap<Comparable, Comparable>();
        }

        public void addValue(Number value, Comparable label, Comparable legendItem,Comparable rowKey, Comparable columnKey) {
            labelMap.put(rowKey, label);
            legendMap.put(rowKey, legendItem);
            super.addValue(value, rowKey, columnKey);
        }

        @Override
        public void removeValue(Comparable rowKey, Comparable columnKey) {
            super.removeValue(rowKey, columnKey);
            labelMap.remove(rowKey);
            legendMap.remove(rowKey);
        }

        public Comparable getLabel(Comparable rowKey) {
            return labelMap.get(rowKey);
        }

        public Comparable getLegend(Comparable rowKey) {
            return legendMap.get(rowKey);
        }



    }

    private class QuantCategoryItemLabelGenerator extends StandardCategoryItemLabelGenerator {

        protected QuantCategoryItemLabelGenerator() {
            super("{1}", NumberFormat.getInstance());
        }

        @Override
        protected String generateLabelString(CategoryDataset dataset, int row, int column) {
            if (dataset instanceof QuantCategoryDataset) {
                Comparable rowKey = dataset.getRowKey(row);
                Comparable label = ((QuantCategoryDataset) dataset).getLabel(rowKey);
                return label == null ? null : label.toString();
            } else {
                return super.generateLabelString(dataset, row, column);
            }
        }
    }

    private class QuantCategoryItemLaegendLabelGenerator extends StandardCategorySeriesLabelGenerator {

        protected QuantCategoryItemLaegendLabelGenerator() {
            super();
        }

        @Override
        public String generateLabel(CategoryDataset dataset, int series) {
            if (dataset instanceof QuantCategoryDataset) {
                Comparable rowKey = dataset.getRowKey(series);
                Comparable label = ((QuantCategoryDataset) dataset).getLegend(rowKey);
                return label == null ? null : label.toString();
            } else {
                return super.generateLabel(dataset, series);
            }
        }
    }
}
