package uk.ac.ebi.pride.toolsuite.gui.component.proteingroup;

import de.mpc.pia.core.intermediate.IntermediateGroup;
import de.mpc.pia.core.intermediate.IntermediatePeptide;
import de.mpc.pia.core.intermediate.IntermediatePeptideSpectrumMatch;
import de.mpc.pia.core.intermediate.IntermediateProtein;
import de.mpc.pia.core.intermediate.IntermediateStructure;
import de.mpc.pia.core.intermediate.prideimpl.PrideImportController;
import de.mpc.pia.core.modeller.PIAModeller;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.algorithms.layout.util.VisRunner;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.picking.PickedState;
import edu.uci.ics.jung.visualization.util.Animator;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.commons.collections15.functors.MapTransformer;
import org.apache.commons.collections15.map.LazyMap;

import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.core.Protein;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This panel will show the information of the Protein Groups and the peptides
 * and proteins that are connected
 *
 * @author ypriverol, julianu
 */
public class ProteinGroupPane
        extends DataAccessControllerPane<Void, Protein>
        implements ItemListener, ActionListener {
    /** handler for the shown graph */
    private ProteinVisualizationGraphHandler visGraph;

    /** the currently selected vertex */
    private VertexObject selectedVertex;

    /** the default used layout */
    private static final Class<? extends Layout> defaultLayout = ProteinLayout.class;
    
    
    /** the viewer for the graphical visualization */
    private VisualizationViewer<VertexObject, String> visualizationViewer;

    /** the picked status of the graph, i.e. which vertex is selected */
    private PickedState<VertexObject> pickedState;

    /** the picked status of the graph, i.e. which vertex is selected */
    private Layout<VertexObject, String> layout;

    /** the mouse handler for the graph */
    private DefaultModalGraphMouse<VertexObject, String> graphMouse;


    /** button to collapse/uncollapse proteins */
    private JButton btnCollapseUncollapseProteins;

    /** button to collapse/uncollapse peptides */
    private JButton btnCollapseUncollapsePeptides;

    /** button to show/hide PSMs */
    private JButton btnShowHidePSMs;

    /** table showing information of selected vertex */
    private JTable informationTable;

    /** comboBox for layout changing */
    private JComboBox layoutComboBox;


    /** title for the score threshold slider */
    private static final String TITLE_SCORE_THRESHOLD = "Score Threshold: ";





    private static final Color FADED_COLOR = Color.LIGHT_GRAY;
    private static final Color SELECTED_COLOR = Color.RED;
    private static final Color GROUP_COLOR = new Color(0x000080);
    private static final Color PROTEIN_COLOR = new Color(0x008000);
    private static final Color PEPTIDE_COLOR = new Color(0xffa500);
    private static final Color PSM_COLOR = new Color(0x87ceeb);
    private static final Color EDGE_COLOR = Color.BLACK;





    public ProteinGroupPane(DataAccessController controller, Comparable proteinId, Comparable proteinGroupId) {
        super(controller);
        
        this.visGraph = new ProteinVisualizationGraphHandler(controller, proteinId, proteinGroupId);
        this.selectedVertex = null;
        setUpPaneComponents();
    }


    @Override
    protected void setupMainPane() {
        // setup the main pane
        this.setLayout(new GridLayout(1, 1, 0, 0));
    }


    @Override
    protected void addComponents() {
    }


    @Override
    public void process(TaskEvent<List<Protein>> listTaskEvent) {
    }


    @Override
    public void finished(TaskEvent<Void> event) {
        // would be called after process
    }


    /**
     * Create and set up everything for the viewer
     *
     * @throws IOException
     */
    private void setUpPaneComponents() {
        // use a splitPane as basic layout
        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(1.0);
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        this.add(splitPane);

        // add the graph visualization to the top
        setUpVisualizationViewer();
        splitPane.setTopComponent(new GraphZoomScrollPane(visualizationViewer));
        
        // the panel for the bottom
        JPanel bottomPanel = new JPanel();
        GridBagLayout gbl_bottomPanel = new GridBagLayout();
        gbl_bottomPanel.columnWidths = new int[]{0, 0, 0, 0};
        gbl_bottomPanel.rowHeights = new int[]{0, 0};
        gbl_bottomPanel.columnWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
        gbl_bottomPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
        bottomPanel.setLayout(gbl_bottomPanel);
        splitPane.setBottomComponent(bottomPanel);

        GridBagConstraints gbc = new GridBagConstraints();


        // information panel
        JPanel informationPanel = new JPanel();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        bottomPanel.add(informationPanel, gbc);
        GridBagLayout gbl_informationPanel = new GridBagLayout();
        gbl_informationPanel.columnWidths = new int[]{0, 0};
        gbl_informationPanel.rowHeights = new int[]{0, 0, 0};
        gbl_informationPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_informationPanel.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
        informationPanel.setLayout(gbl_informationPanel);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(0, 3, 0, 0));
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        informationPanel.add(buttonPanel, gbc);

        btnCollapseUncollapseProteins = new JButton("Proteins");
        btnCollapseUncollapseProteins.setEnabled(false);
        btnCollapseUncollapseProteins.addActionListener(this);
        buttonPanel.add(btnCollapseUncollapseProteins);

        btnCollapseUncollapsePeptides = new JButton("Proteins");
        btnCollapseUncollapsePeptides.setEnabled(false);
        btnCollapseUncollapsePeptides.addActionListener(this);
        buttonPanel.add(btnCollapseUncollapsePeptides);

        btnShowHidePSMs = new JButton("PSMs");
        btnShowHidePSMs.setEnabled(false);
        btnShowHidePSMs.addActionListener(this);
        buttonPanel.add(btnShowHidePSMs);

        JScrollPane scrollPane = new JScrollPane();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        informationPanel.add(scrollPane, gbc);

        informationTable = new JTable();
        informationTable.setFillsViewportHeight(true);
        scrollPane.setViewportView(informationTable);

        updateInformationPanel();

        // the score threshold panel
        JPanel scoreThresholdPanel = setUpScoreThresholdPanel();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;
        gbc.gridy = 0;
        bottomPanel.add(scoreThresholdPanel, gbc);

        // the visualization controls
        JPanel visualizationControlsPanel = setUpVisualizationControls();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 2;
        gbc.gridy = 0;
        bottomPanel.add(visualizationControlsPanel, gbc);
    }


    /**
     * set up the graph rendering and visualization
     */
    private void setUpVisualizationViewer() {
        // set up the layout
        layout = new ProteinLayout<String>(visGraph.getGraph());
        Layout<VertexObject, String> staticLayout = new StaticLayout<VertexObject, String>(visGraph.getGraph(), layout, layout.getSize());

        visualizationViewer = new VisualizationViewer<VertexObject,String>(staticLayout, layout.getSize());
        
        visualizationViewer.setBackground(Color.white);

        // listen to viewer resizing
        visualizationViewer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                if (!(layout instanceof ProteinLayout)) {
                    layout.setSize(e.getComponent().getSize());
                }
            }
        });

        // let the pane listen to the vertex-picking
        pickedState = visualizationViewer.getPickedVertexState();
        pickedState.addItemListener(this);

        // tell the renderer to use our own customized colors for the vertices
        //visualizationViewer.getRenderContext().setVertexFillPaintTransformer(MapTransformer.<VertexObject, Paint>getInstance(vertexPaints));

        // give a selected vertex red edges, others the same color as the shape edge
        /*
        visualizationViewer.getRenderContext().setVertexDrawPaintTransformer(
                new Transformer<VertexObject, Paint>() {
                    @Override
                    public Paint transform(VertexObject v) {
                        if (pickedState.isPicked(v)) {
                            return SELECTED_COLOR;
                        } else {
                            return vertexPaints.get(v);
                        }
                    }
                });
        */

        // set the special vertex and labeller for the nodes
        ProteinVertexLabeller labeller = new ProteinVertexLabeller(visualizationViewer.getRenderContext(), 5);
        visualizationViewer.getRenderContext().setVertexShapeTransformer(labeller);
        visualizationViewer.getRenderer().setVertexLabelRenderer(labeller);


        // customize the edges
        //visualizationViewer.getRenderContext().setEdgeDrawPaintTransformer(MapTransformer.<String, Paint>getInstance(edgePaints));
        visualizationViewer.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<VertexObject, String>());

        // colors for arrowheads are same as the edge
        //visualizationViewer.getRenderContext().setArrowDrawPaintTransformer(MapTransformer.<String, Paint>getInstance(edgePaints));
        //visualizationViewer.getRenderContext().setArrowFillPaintTransformer(MapTransformer.<String, Paint>getInstance(edgePaints));


        // always show the vertex name as tooltip
        visualizationViewer.setVertexToolTipTransformer(new ToStringLabeller<VertexObject>());

        // show all vertex labels for now
        visualizationViewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<VertexObject>());

        // gray (= faded out) edges are thin, all others thick
        /*
        visualizationViewer.getRenderContext().setEdgeStrokeTransformer(
                new Transformer<String, Stroke>() {
                    protected final Stroke THIN = new BasicStroke(1);
                    protected final Stroke THICK = new BasicStroke(2);

                    @Override
                    public Stroke transform(String e) {
                        Paint c = edgePaints.get(e);
                        if (c == FADED_COLOR)
                            return THIN;
                        else
                            return THICK;
                    }
                });
        */

        // define a manipulation mouse
        graphMouse = new DefaultModalGraphMouse<VertexObject, String>();
        // set PICKING as default mouse behaviour
        graphMouse.setMode(Mode.PICKING);
        
        visualizationViewer.setGraphMouse(graphMouse);
    }


    /**
     * set up the score threshold panel
     */
    private JPanel setUpScoreThresholdPanel() {
        //---------------------------------------------------------------------
        // create the slider for the score threshold

        final JSlider scoreThresholdSlider = new JSlider(JSlider.HORIZONTAL);
        scoreThresholdSlider.setBackground(Color.WHITE);
        scoreThresholdSlider.setPaintTicks(true);
        scoreThresholdSlider.setMaximum(10); // this must be set, when the graph is completely built
        scoreThresholdSlider.setMinimum(0);
        scoreThresholdSlider.setValue(0);
        scoreThresholdSlider.setMajorTickSpacing(10);
        scoreThresholdSlider.setPaintLabels(true);
        scoreThresholdSlider.setPaintTicks(true);

        JPanel scoreThresholdControls = new JPanel();
        scoreThresholdControls.setOpaque(true);
        scoreThresholdControls.setLayout(new BoxLayout(scoreThresholdControls, BoxLayout.Y_AXIS));
        scoreThresholdControls.add(Box.createVerticalGlue());
        scoreThresholdControls.add(scoreThresholdSlider);

        TitledBorder sliderBorder = BorderFactory.createTitledBorder(
                TITLE_SCORE_THRESHOLD + scoreThresholdSlider.getValue());
        scoreThresholdControls.setBorder(sliderBorder);
        scoreThresholdControls.add(Box.createVerticalGlue());

        scoreThresholdSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    // the slider was moved
		        	/*
		            int numEdgesToRemove = source.getValue();
		            clusterAndRecolor(layout, numEdgesToRemove, similarColors,
		                    true);
		            sliderBorder.setTitle(
		            		TITLE_SCORE_THRESHOLD + scoreThresholdSlider.getValue());
		            scoreThresholdControls.repaint();
		            visualizationViewer.validate();
		            visualizationViewer.repaint();
		            */
                }
            }
        });

        return scoreThresholdControls;
    }


    /**
     * set up the controls panel
     */
    private JPanel setUpVisualizationControls() {
        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(visualizationViewer, 1.1f, visualizationViewer.getCenter());
            }
        });

        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(visualizationViewer, 1/1.1f, visualizationViewer.getCenter());
            }
        });


        Class<? extends Layout>[] combos = getAvailableLayoutClasses();

        layoutComboBox = new JComboBox(combos);
        // use a renderer to shorten the layout name presentation
        layoutComboBox.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                String valueString = value.toString();
                valueString = valueString.substring(valueString.lastIndexOf('.')+1);

                return super.getListCellRendererComponent(list, valueString, index, isSelected, cellHasFocus);
            }
        });
        layoutComboBox.addActionListener(this);
        layoutComboBox.setSelectedItem(defaultLayout);

        JPanel visualizationControlsPanel = new JPanel(new BorderLayout());
        visualizationControlsPanel.setBorder(BorderFactory.createTitledBorder("Visualization Options"));
        JPanel sliderPanel = new JPanel(new GridLayout(3,1));
        JPanel sliderLabelPanel = new JPanel(new GridLayout(3,1));
        JPanel zoomPanel = new JPanel();
        zoomPanel.setLayout(new FlowLayout());
        zoomPanel.add(minus);
        zoomPanel.add(plus);
        sliderPanel.add(layoutComboBox);
        sliderPanel.add(graphMouse.getModeComboBox());
        sliderPanel.add(zoomPanel);
        sliderLabelPanel.add(new JLabel("Layout:", JLabel.RIGHT));
        sliderLabelPanel.add(new JLabel("Mouse Mode:", JLabel.RIGHT));
        sliderLabelPanel.add(new JLabel("Zoom:", JLabel.RIGHT));
        visualizationControlsPanel.add(sliderLabelPanel, BorderLayout.WEST);
        visualizationControlsPanel.add(sliderPanel);

        return visualizationControlsPanel;
    }


    @Override
    public void itemStateChanged(ItemEvent e) {
        Object subject = e.getItem();

        if (subject instanceof VertexObject) {
            updateInformationPanel();
        }
    }


    /**
     * Returns an array of available layout classes
     * 
     * @return
     */
    private Class<Layout>[] getAvailableLayoutClasses() {
        List<Class<? extends Layout>> layouts = new ArrayList<Class<? extends Layout>>();
        layouts.add(ProteinLayout.class);
        layouts.add(CircleLayout.class);
        layouts.add(FRLayout2.class);
        
        return layouts.toArray(new Class[0]);
    }


    /**
     * updates information shown on the information panel
     */
    private void updateInformationPanel() {
        if ((pickedState.getSelectedObjects().length > 0) &&
                (pickedState.getSelectedObjects()[0] instanceof VertexObject)) {
            selectedVertex = (VertexObject)pickedState.getSelectedObjects()[0];
        } else {
            return;
        }

        // reset all the information
        btnCollapseUncollapseProteins.setText("Proteins");
        btnCollapseUncollapseProteins.setEnabled(false);
        btnCollapseUncollapsePeptides.setText("Peptides");
        btnCollapseUncollapsePeptides.setEnabled(false);
        btnShowHidePSMs.setText("PSMs");
        btnShowHidePSMs.setEnabled(false);
        informationTable.setModel(new DefaultTableModel());

        if (selectedVertex != null) {
            Object selectedObject = selectedVertex.getObject();

            if (selectedObject instanceof Collection) {
                // the object contains collapsed information
                Iterator<?> objectIterator = ((Collection<?>)selectedObject).iterator();

                if (objectIterator.hasNext()) {
                    Object firstObject = objectIterator.next();

                    if (firstObject instanceof IntermediateProtein) {
                        informationTable.setModel(
                                new ProteinInformationTableModel((Collection<?>)selectedObject));
                        btnCollapseUncollapseProteins.setText("Uncollapse Proteins");
                        btnCollapseUncollapseProteins.setEnabled(true);
                    } else if (firstObject instanceof IntermediatePeptide) {
                        informationTable.setModel(
                                new PeptideInformationTableModel((Collection<?>)selectedObject));
                        btnCollapseUncollapsePeptides.setText("Uncollapse Peptides");
                        btnCollapseUncollapsePeptides.setEnabled(true);
                    }
                }
            } else {
                if (selectedObject instanceof IntermediateProtein) {
                    informationTable.setModel(
                            new ProteinInformationTableModel(Arrays.asList(new Object[]{selectedObject})));
                } else if (selectedObject instanceof IntermediatePeptide) {
                    informationTable.setModel(
                            new PeptideInformationTableModel(Arrays.asList(new Object[]{selectedObject})));

                    btnShowHidePSMs.setEnabled(true);
                    if (visGraph.isExpandedPSMs(selectedVertex)) {
                        btnShowHidePSMs.setText("Hide PSMs");
                    } else {
                        btnShowHidePSMs.setText("Show PSMs");
                    }
                } else if (selectedObject instanceof IntermediateGroup) {

                    if ((((IntermediateGroup) selectedObject).getProteins() != null) &&
                            (((IntermediateGroup) selectedObject).getProteins().size() > 1)) {
                        btnCollapseUncollapseProteins.setEnabled(true);
                        if (visGraph.isExpandedAccessions(selectedVertex)) {
                            btnCollapseUncollapseProteins.setText("Collapse Proteins");
                        } else {
                            btnCollapseUncollapseProteins.setText("Uncollapse Proteins");
                        }
                    }

                    if ((((IntermediateGroup) selectedObject).getPeptides() != null) &&
                            (((IntermediateGroup) selectedObject).getPeptides().size() > 1)) {
                        btnCollapseUncollapsePeptides.setEnabled(true);
                        if (visGraph.isExpandedPeptides(selectedVertex)) {
                            btnCollapseUncollapsePeptides.setText("Collapse Peptides");
                        } else {
                            btnCollapseUncollapsePeptides.setText("Uncollapse Peptides");
                        }
                    }
                }
            }
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnCollapseUncollapseProteins)) {
            // the proteins (un-)collapse button was pressed
            if (selectedVertex == null) {
                return;
            }

            if (selectedVertex.getObject() instanceof Collection<?>) {
                // this is a collection of proteins, select the group
                for (VertexObject vertex : visGraph.getGraph().getSuccessors(selectedVertex)) {
                    if (vertex.getObject() instanceof IntermediateGroup) {
                        selectedVertex = vertex;
                        break;
                    }
                }
            }
            
/*
TODO:
hier auch die position speichern und setzen für den collapsed/uncollapsed vertex (bei allen collapse/uncollapse)
if ((location != null) && (visualizationViewer != null)) {
    visualizationViewer.getGraphLayout().setLocation(proteinsV, location);
}
*/
            
            if (!visGraph.isExpandedAccessions(selectedVertex)) {
                visGraph.uncollapseProteins(selectedVertex);
            } else {
                visGraph.collapseProteins(selectedVertex);
            }
            
            pickedState.clear();
            pickedState.pick(selectedVertex, true);
            recalculateAndAnimateGraphChanges();
            updateInformationPanel();
        } else if (e.getSource().equals(btnCollapseUncollapsePeptides)) {
            // the peptides (un-)collapse button was pressed
            if (selectedVertex == null) {
                return;
            }

            if (selectedVertex.getObject() instanceof Collection<?>) {
                // this is a collection of peptides, select the group
                for (VertexObject vertex : visGraph.getGraph().getPredecessors(selectedVertex)) {
                    if (vertex.getObject() instanceof IntermediateGroup) {
                        selectedVertex = vertex;
                        break;
                    }
                }
            }

            if (!visGraph.isExpandedPeptides(selectedVertex)) {
                visGraph.uncollapsePeptides(selectedVertex);
            } else {
                visGraph.collapsePeptides(selectedVertex);
            }

            pickedState.clear();
            pickedState.pick(selectedVertex, true);
            recalculateAndAnimateGraphChanges();
            updateInformationPanel();
        } else if (e.getSource().equals(btnShowHidePSMs)) {
            // the show/hide PSMs button was pressed
            if (selectedVertex == null) {
                return;
            }

            if (!visGraph.isExpandedPSMs(selectedVertex)) {
                visGraph.showPSMs(selectedVertex);
            } else {
                visGraph.hidePSMs(selectedVertex);
            }

            pickedState.clear();
            pickedState.pick(selectedVertex, true);
            recalculateAndAnimateGraphChanges();
            updateInformationPanel();
        } else if (e.getSource().equals(layoutComboBox)) {
            Dimension dim = layout.getSize();

            if (layoutComboBox.getSelectedItem() == FRLayout2.class) {
                layout = new FRLayout2<VertexObject, String>(visGraph.getGraph());
            } else if (layoutComboBox.getSelectedItem() == CircleLayout.class) {
                layout = new CircleLayout<VertexObject, String>(visGraph.getGraph());
            } else if (layoutComboBox.getSelectedItem() == ProteinLayout.class) {
                layout = new ProteinLayout<String>(visGraph.getGraph());
            } else {
                layout = new ProteinLayout<String>(visGraph.getGraph());
            }
            
            if (!(layout instanceof ProteinLayout)) {
                layout.setSize(dim);
            }
            
            recalculateAndAnimateGraphChanges();
        }
    }



    /**
     * Recalculates the layout and visualization of the graph for the changed
     * graph topology
     */
    private void recalculateAndAnimateGraphChanges() {
        layout.setGraph(visGraph.getGraph());
        layout.initialize();

        if (layout instanceof IterativeContext) {
            Relaxer relaxer = new VisRunner((IterativeContext)layout);
            relaxer.stop();
            relaxer.prerelax();
        }
        
        StaticLayout<VertexObject, String> staticLayout =
                new StaticLayout<VertexObject, String>(visGraph.getGraph(), layout, layout.getSize());
        
        LayoutTransition<VertexObject, String> lt =
                new LayoutTransition<VertexObject, String>(visualizationViewer,
                        visualizationViewer.getGraphLayout(),
                        staticLayout);

        Animator animator = new Animator(lt);
        animator.start();
        
        visualizationViewer.repaint();
    }
}