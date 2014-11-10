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
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
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

    /** access controller to access the data (proteins, peptides, PSMs...) */
    private DataAccessController controller;

    /** id of the selected protein */
    private Comparable selectedProteinId;

    /** the accession of the selected protein (created while building the intermediate structure) */
    private String selectedProteinAccession;

    /** id of the selected protein group */
    private Comparable selectedProteinGroupId;

    /** the PIA intermediate structure for the visualization */
    private IntermediateStructure intermediateStructure;


    /** the viewer for the graphical visualization */
    private VisualizationViewer<VertexObject, String> visualizationViewer;

    /** the shown graph */
    private DirectedSparseGraph<VertexObject, String> graph;

    /** the picked status of the graph, i.e. which vertex is selected */
    private PickedState<VertexObject> pickedState;

    /** the picked status of the graph, i.e. which vertex is selected */
    private Layout<VertexObject, String> layout;

    /** the mouse handler for the graph */
    private DefaultModalGraphMouse<VertexObject, String> graphMouse;


    /** the currently selected vertex */
    private VertexObject selectedVertex;

    /** mapping from the group's label to whether its accessions are shown */
    private Map<String, Boolean> expandedAccessions;

    /** mapping from the group's label to whether its peptides are shown */
    private Map<String, Boolean> expandedPeptides;

    /** mapping from the peptide's label to whether its spectra a shown */
    private Map<String, Boolean> showPSMs;


    /** mapping from the group ID to the vertex in the graph */
    private Map<Integer, VertexObject> groupVertices;


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

    /** the default used layout */
    private static final Class<? extends Layout> defaultLayout = FRLayout2.class;


    /** the painting information for the vertices (PSMs, peptides, proteins, "groups") */
    private Map<VertexObject, Paint> vertexPaints;

    /** the painting information for the edges */
    private Map<String, Paint> edgePaints;



    private static final Color FADED_COLOR = Color.LIGHT_GRAY;
    private static final Color SELECTED_COLOR = Color.RED;
    private static final Color GROUP_COLOR = new Color(0x000080);
    private static final Color PROTEIN_COLOR = new Color(0x008000);
    private static final Color PEPTIDE_COLOR = new Color(0xffa500);
    private static final Color PSM_COLOR = new Color(0x87ceeb);
    private static final Color EDGE_COLOR = Color.BLACK;

    private static final String PROTEINS_OF_PREFIX = "proteins_of_";
    private static final String PEPTIDES_OF_PREFIX = "peptides_of_";




    public ProteinGroupPane(DataAccessController controller, Comparable proteinId, Comparable proteinGroupId) {
        super(controller);

        this.intermediateStructure = null;
        this.selectedProteinAccession = null;

        this.controller = controller;
        this.selectedProteinId = proteinId;
        this.selectedProteinGroupId = proteinGroupId;

        this.expandedAccessions = new HashMap<String, Boolean>();
        this.expandedPeptides = new HashMap<String, Boolean>();
        this.showPSMs = new HashMap<String, Boolean>();

        createGraphFromSelectedProteinGroupId();
        setUpPaneComponents();
    }


    @Override
    protected void setupMainPane() {
        // setup the main pane
        this.setLayout(new GridLayout(1, 1, 0, 0));
    }


    @Override
    protected void addComponents() {
        // initiate vertex painting and set all not specially set vertices to
        // white
        vertexPaints = LazyMap.<VertexObject, Paint> decorate(
                new HashMap<VertexObject, Paint>(), new ConstantTransformer(FADED_COLOR));

        // initiate edge painting and set all not specially set edges to blue
        edgePaints = LazyMap.<String, Paint> decorate(
                new HashMap<String, Paint>(), new ConstantTransformer(FADED_COLOR));

        // initialize the graph to be a forest
        graph = new DirectedSparseGraph<VertexObject, String>();
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
        layout = new FRLayout2<VertexObject, String>(graph);
        layout.setSize(new Dimension(600, 600));
        Relaxer relaxer = new VisRunner((IterativeContext)layout);
        relaxer.stop();
        relaxer.prerelax();
        Layout<VertexObject, String> staticLayout = new StaticLayout<VertexObject, String>(graph, layout);

        visualizationViewer = new VisualizationViewer<VertexObject,String>(staticLayout, new Dimension(600, 600));
        visualizationViewer.setBackground(Color.white);

        // listen to viewer resizing
        visualizationViewer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                layout.setSize(e.getComponent().getSize());
            }
        });

        // let the pane listen to the vertex-picking
        pickedState = visualizationViewer.getPickedVertexState();
        pickedState.addItemListener(this);

        // tell the renderer to use our own customized colors for the vertices
        visualizationViewer.getRenderContext().setVertexFillPaintTransformer(MapTransformer.<VertexObject, Paint>getInstance(vertexPaints));

        // give a selected vertex red edges, others the same color as the shape edge
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

        // set the special vertex and labeller for the nodes
        ProteinVertexLabeller labeller = new ProteinVertexLabeller(visualizationViewer.getRenderContext(), 5);
        visualizationViewer.getRenderContext().setVertexShapeTransformer(labeller);
        visualizationViewer.getRenderer().setVertexLabelRenderer(labeller);


        // customize the edges
        visualizationViewer.getRenderContext().setEdgeDrawPaintTransformer(MapTransformer.<String, Paint>getInstance(edgePaints));
        visualizationViewer.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<VertexObject, String>());

        // colors for arrowheads are same as the edge
        visualizationViewer.getRenderContext().setArrowDrawPaintTransformer(MapTransformer.<String, Paint>getInstance(edgePaints));
        visualizationViewer.getRenderContext().setArrowFillPaintTransformer(MapTransformer.<String, Paint>getInstance(edgePaints));


        // always show the vertex name as tooltip
        visualizationViewer.setVertexToolTipTransformer(new ToStringLabeller<VertexObject>());

        // show all vertex labels for now
        // TODO: cluster labels...
        visualizationViewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<VertexObject>());

        // gray (= faded out) edges are thin, all others thick
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

        // define a manipulation mouse
        graphMouse = new DefaultModalGraphMouse<VertexObject, String>();
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
                scaler.scale(visualizationViewer, 1.1f,
                        visualizationViewer.getCenter());
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
    private static Class<Layout>[] getAvailableLayoutClasses() {
        List<Class<? extends Layout>> layouts = new ArrayList<Class<? extends Layout>>();
        layouts.add(CircleLayout.class);
        layouts.add(FRLayout2.class);

        return layouts.toArray(new Class[0]);
    }


    /**
     * Creates the graph using the data from the ambiguity group given by
     * selectedProteinGroupId
     * 
     * @param proteins
     */
    private void createGraphFromSelectedProteinGroupId() {
        // create the intermediate structure
        PIAModeller piaModeller = new PIAModeller();
        Integer fileID = piaModeller.addPrideControllerAsInput(controller);
        PrideImportController importController = (PrideImportController) piaModeller.getImportController(fileID);

        for (Comparable protID : controller.getProteinAmbiguityGroupById(selectedProteinGroupId).getProteinIds()) {
            String acc = importController.addProteinsSpectrumIdentificationsToStructCreator(protID, piaModeller.getIntermediateStructureCreator(), null);
            if (selectedProteinId.equals(protID)) {
                selectedProteinAccession = acc;
            }
        }

        intermediateStructure = piaModeller.buildIntermediateStructure();
        createGraphFromIntermediateStructure();
    }


    /**
     * creates the graph from the intermediate structure, also clusters using
     * the given settings
     */
    private void createGraphFromIntermediateStructure() {
        groupVertices = new HashMap<Integer, VertexObject>();

        // go through the clusters and create the graph
        for (Set<IntermediateGroup> cluster : intermediateStructure.getClusters().values()) {
            for (IntermediateGroup group : cluster) {
                VertexObject groupV = addGroupVertex(group);
                String groupLabel = groupV.getLabel();

                // connect to the child-groups
                if (group.getChildren() != null) {
                    for (IntermediateGroup child : group.getChildren()) {
                        VertexObject childV = addGroupVertex(child);
                        String childLabel = childV.getLabel();

                        String edgeName = "groupGroup_" + groupV.getLabel() + "_" + childV.getLabel();

                        graph.addEdge(edgeName, groupV, childV);
                        edgePaints.put(edgeName, EDGE_COLOR);
                    }
                }

                // add the proteins collapsed
                if ((group.getProteins() != null) && (group.getProteins().size() > 0)) {
                    addProteinVertices(groupV, true, null);
                }

                // add the peptides
                if ((group.getPeptides() != null) && (group.getPeptides().size() > 0)) {
                    addPeptideVertices(groupV, true, null);
                }
            }
        }
    }


    /**
     * adds a group vertex to the graph, if not a vertex for this group is
     * already added
     *
     * @param group
     * @return group's VertexObject (either newly created or already from the graph)
     */
    private VertexObject addGroupVertex(IntermediateGroup group) {
        String groupLabel = group.getID().toString();
        VertexObject groupV = groupVertices.get(group.getID());
        if (groupV == null) {
            groupV = new VertexObject(groupLabel, group);
            graph.addVertex(groupV);

            vertexPaints.put(groupV, GROUP_COLOR);
            groupVertices.put(group.getID(), groupV);
        }

        return groupV;
    }


    /**
     * updates information shown on the information panel
     */
    private void updateInformationPanel() {
        selectedVertex = null;

        if ((pickedState.getSelectedObjects().length > 0) &&
                (pickedState.getSelectedObjects()[0] instanceof VertexObject)) {
            selectedVertex = (VertexObject)pickedState.getSelectedObjects()[0];
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
                    if ((showPSMs.get(selectedVertex.getLabel()) != null ) &&
                            showPSMs.get(selectedVertex.getLabel())) {
                        btnShowHidePSMs.setText("Hide PSMs");
                    } else {
                        btnShowHidePSMs.setText("Show PSMs");
                    }
                } else if (selectedObject instanceof IntermediateGroup) {

                    if ((((IntermediateGroup) selectedObject).getProteins() != null) &&
                            (((IntermediateGroup) selectedObject).getProteins().size() > 1)) {
                        btnCollapseUncollapseProteins.setEnabled(true);
                        if ((expandedAccessions.get(selectedVertex.getLabel()) != null ) &&
                                expandedAccessions.get(selectedVertex.getLabel())) {
                            btnCollapseUncollapseProteins.setText("Collapse Proteins");
                        } else {
                            btnCollapseUncollapseProteins.setText("Uncollapse Proteins");
                        }
                    }

                    if ((((IntermediateGroup) selectedObject).getPeptides() != null) &&
                            (((IntermediateGroup) selectedObject).getPeptides().size() > 1)) {
                        btnCollapseUncollapsePeptides.setEnabled(true);
                        if ((expandedPeptides.get(selectedVertex.getLabel()) != null ) &&
                                expandedPeptides.get(selectedVertex.getLabel())) {
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
                VertexObject groupV = null;
                for (String edge : graph.getOutEdges(selectedVertex)) {
                    VertexObject v = graph.getOpposite(selectedVertex, edge);
                    if (v.getObject() instanceof IntermediateGroup) {
                        groupV = v;
                    }
                    if (groupV != null) {
                        selectedVertex = groupV;
                        break;
                    }
                }
            }

            if ((expandedAccessions.get(selectedVertex.getLabel()) == null) ||
                    !expandedAccessions.get(selectedVertex.getLabel())) {
                uncollapseProteins(selectedVertex);
            } else {
                collapseProteins(selectedVertex);
            }

            updateInformationPanel();
        } else if (e.getSource().equals(btnCollapseUncollapsePeptides)) {
            // the peptides (un-)collapse button was pressed
            if (selectedVertex == null) {
                return;
            }

            if (selectedVertex.getObject() instanceof Collection<?>) {
                // this is a collection of peptides, select the group
                VertexObject groupV = null;
                for (String edge : graph.getInEdges(selectedVertex)) {
                    VertexObject v = graph.getOpposite(selectedVertex, edge);
                    if (v.getObject() instanceof IntermediateGroup) {
                        groupV = v;
                    }
                    if (groupV != null) {
                        selectedVertex = groupV;
                        break;
                    }
                }
            }

            if ((expandedPeptides.get(selectedVertex.getLabel()) == null) ||
                    !expandedPeptides.get(selectedVertex.getLabel())) {
                uncollapsePeptides(selectedVertex);
            } else {
                collapsePeptides(selectedVertex);
            }

            updateInformationPanel();
        } else if (e.getSource().equals(btnShowHidePSMs)) {
            // the show/hide PSMs button was pressed
            if (selectedVertex == null) {
                return;
            }

            if ((showPSMs.get(selectedVertex.getLabel()) == null) ||
                    !showPSMs.get(selectedVertex.getLabel())) {
                showPSMs(selectedVertex);
            } else {
                hidePSMs(selectedVertex);
            }

            updateInformationPanel();
        } else if (e.getSource().equals(layoutComboBox)) {
            Dimension dim = layout.getSize();

            if (layoutComboBox.getSelectedItem() == FRLayout2.class) {
                layout = new FRLayout2<VertexObject, String>(graph);
            } else if (layoutComboBox.getSelectedItem() == CircleLayout.class) {
                layout = new CircleLayout<VertexObject, String>(graph);
            } else {
                layout = new FRLayout2<VertexObject, String>(graph);
            }

            layout.setSize(dim);
            recalculateAndAnimateGraphChanges();
        }
    }


    /**
     * Uncollapses the proteins of the given {@link VertexObject}, which should
     * be an {@link IntermediateGroup} representative
     * @param groupV
     */
    private void uncollapseProteins(VertexObject groupV) {
        if ((groupV == null) ||
                !(groupV.getObject() instanceof IntermediateGroup) ||
                ((expandedAccessions.get(groupV.getLabel()) != null) && expandedAccessions.get(groupV.getLabel()))) {
            return;
        }

        // remove the collapsed proteins
        Point2D proteinLocation = null;
        Iterator<String> edgeIt = graph.getIncidentEdges(groupV).iterator();
        while (edgeIt.hasNext()) {
            String edge = edgeIt.next();
            VertexObject proteinsV = graph.getOpposite(groupV, edge);
            if (proteinsV.getLabel().equals(PROTEINS_OF_PREFIX + groupV.getLabel())) {
                proteinLocation = visualizationViewer.getGraphLayout().transform(proteinsV);
                graph.removeVertex(proteinsV);
                vertexPaints.remove(proteinsV);
                break;
            }
        }

        // add the proteins uncollapsed
        addProteinVertices(groupV, false, proteinLocation);

        recalculateAndAnimateGraphChanges();
    }


    /**
     * Collapses the proteins of the given {@link VertexObject}, which should be
     * an {@link IntermediateGroup} representative
     * 
     * @param groupV
     */
    private void collapseProteins(VertexObject groupV) {
        if ((groupV == null) ||
                !(groupV.getObject() instanceof IntermediateGroup) ||
                (expandedAccessions.get(groupV.getLabel()) == null) ||
                !expandedAccessions.get(groupV.getLabel()) ||
                (((IntermediateGroup)groupV.getObject()).getProteins() == null) ||
                (((IntermediateGroup)groupV.getObject()).getProteins().size() < 2)) {
            return;
        }

        // remove all the protein vertices
        Iterator<String> edgeIt = graph.getIncidentEdges(groupV).iterator();
        while (edgeIt.hasNext()) {
            String edge = edgeIt.next();
            VertexObject proteinV = graph.getOpposite(groupV, edge);
            if (proteinV.getObject() instanceof IntermediateProtein) {
                graph.removeVertex(proteinV);
                vertexPaints.remove(proteinV);
            }
        }

        // add the proteins collapsed
        addProteinVertices(groupV, true, null);

        recalculateAndAnimateGraphChanges();
    }


    /**
     * Uncollapses the peptides of the given {@link VertexObject}, which should
     * be an {@link IntermediateGroup} representative
     * @param groupV
     */
    private void uncollapsePeptides(VertexObject groupV) {
        if ((groupV == null) ||
                !(groupV.getObject() instanceof IntermediateGroup) ||
                ((expandedPeptides.get(groupV.getLabel()) != null) && expandedPeptides.get(groupV.getLabel()))) {
            return;
        }

        // remove the collapsed peptides
        Point2D peptideLocation = null;
        Iterator<String> edgeIt = graph.getIncidentEdges(groupV).iterator();
        while (edgeIt.hasNext()) {
            String edge = edgeIt.next();
            VertexObject peptidesV = graph.getOpposite(groupV, edge);
            if (peptidesV.getLabel().equals(PEPTIDES_OF_PREFIX + groupV.getLabel())) {
                peptideLocation = visualizationViewer.getGraphLayout().transform(peptidesV);
                graph.removeVertex(peptidesV);
                vertexPaints.remove(peptidesV);
                break;
            }
        }

        // add the peptides uncollapsed
        addPeptideVertices(groupV, false, peptideLocation);

        recalculateAndAnimateGraphChanges();
    }


    /**
     * Collapses the peptides of the given {@link VertexObject}, which should
     * be an {@link IntermediateGroup} representative
     * @param groupV
     */
    private void collapsePeptides(VertexObject groupV) {
        if ((groupV == null) ||
                !(groupV.getObject() instanceof IntermediateGroup) ||
                (expandedPeptides.get(groupV.getLabel()) == null) ||
                !expandedPeptides.get(groupV.getLabel()) ||
                (((IntermediateGroup)groupV.getObject()).getPeptides() == null) ||
                (((IntermediateGroup)groupV.getObject()).getPeptides().size() < 2)) {
            return;
        }

        // remove all the peptide vertices
        Iterator<String> edgeIt = graph.getIncidentEdges(groupV).iterator();
        while (edgeIt.hasNext()) {
            String edge = edgeIt.next();
            VertexObject peptideV = graph.getOpposite(groupV, edge);
            if (peptideV.getObject() instanceof IntermediatePeptide) {
                graph.removeVertex(peptideV);
                vertexPaints.remove(peptideV);
            }
        }

        // add the peptides collapsed
        addPeptideVertices(groupV, true, null);

        recalculateAndAnimateGraphChanges();
    }


    /**
     * Shows the PSMs of the given {@link VertexObject}, which should
     * be an {@link IntermediatePeptide} representative
     * @param groupV
     */
    private void showPSMs(VertexObject peptideV) {
        if ((peptideV == null) ||
                !(peptideV.getObject() instanceof IntermediatePeptide) ||
                ((showPSMs.get(peptideV.getLabel()) != null) && showPSMs.get(peptideV.getLabel()))) {
            return;
        }

        Point2D peptideLocation = visualizationViewer.getGraphLayout().transform(peptideV);
        addPSMVertices(peptideV, peptideLocation);

        recalculateAndAnimateGraphChanges();
    }


    /**
     * Hides the PSMs of the given {@link VertexObject}, which should
     * be an {@link IntermediatePeptide} representative
     * @param groupV
     */
    private void hidePSMs(VertexObject peptideV) {
        if ((peptideV == null) ||
                !(peptideV.getObject() instanceof IntermediatePeptide) ||
                (showPSMs.get(peptideV.getLabel()) == null) ||
                !showPSMs.get(peptideV.getLabel())) {
            return;
        }

        // remove the PSMs from the graph
        Iterator<String> edgeIt = graph.getIncidentEdges(peptideV).iterator();
        while (edgeIt.hasNext()) {
            String edge = edgeIt.next();
            VertexObject psmV = graph.getOpposite(peptideV, edge);
            if (psmV.getObject() instanceof IntermediatePeptideSpectrumMatch) {
                graph.removeVertex(psmV);
                vertexPaints.remove(psmV);
            }
        }

        recalculateAndAnimateGraphChanges();
    }


    /**
     * Adds the proteins of the given group to the graph, either collapsed or
     * uncollapsed. If the location is not null, set the proteins' position to
     * the given location.
     */
    private void addProteinVertices(VertexObject groupV, Boolean collapsed, Point2D location) {
        IntermediateGroup group = (IntermediateGroup)groupV.getObject();
        if (collapsed && (group.getProteins().size() > 1)) {
            // show the proteins collapsed
            String proteinLabel = PROTEINS_OF_PREFIX + groupV.getLabel();
            VertexObject proteinsV =
                    new VertexObject(proteinLabel, group.getProteins());

            graph.addVertex(proteinsV);
            vertexPaints.put(proteinsV, PROTEIN_COLOR);

            String edgeName = "proteinGroup_" + proteinLabel + "_" + groupV.getLabel();
            graph.addEdge(edgeName, proteinsV, groupV);
            edgePaints.put(edgeName, EDGE_COLOR);

            if (location != null) {
                visualizationViewer.getGraphLayout().setLocation(proteinsV, location);
            }
            expandedAccessions.put(groupV.getLabel(), false);
        } else {
            for (IntermediateProtein protein : group.getProteins()) {
                String proteinLabel = protein.getAccession();
                VertexObject proteinV = new VertexObject(proteinLabel, protein);

                graph.addVertex(proteinV);
                vertexPaints.put(proteinV, PROTEIN_COLOR);

                String edgeName = "proteinGroup_" + proteinLabel + "_" + groupV.getLabel();
                graph.addEdge(edgeName, proteinV, groupV);
                edgePaints.put(edgeName, EDGE_COLOR);

                if (location != null) {
                    visualizationViewer.getGraphLayout().setLocation(proteinV, location);
                }
            }
            expandedAccessions.put(groupV.getLabel(), true);
        }
    }


    /**
     * Adds the peptides of the given group to the graph, either collapsed or
     * uncollapsed. If the location is not null, set the peptides' position to
     * the given location.
     */
    private void addPeptideVertices(VertexObject groupV, Boolean collapsed, Point2D location) {
        IntermediateGroup group = (IntermediateGroup)groupV.getObject();
        if (collapsed && (group.getPeptides().size() > 1)) {
            // show the peptides collapsed
            String peptidesLabel = PEPTIDES_OF_PREFIX + groupV.getLabel();
            VertexObject peptidesV = new VertexObject(peptidesLabel, group.getPeptides());

            graph.addVertex(peptidesV);
            vertexPaints.put(peptidesV, PEPTIDE_COLOR);

            String edgeName = "groupPeptide_" + groupV.getLabel() + "_" + peptidesLabel;
            graph.addEdge(edgeName, groupV, peptidesV);
            edgePaints.put(edgeName, EDGE_COLOR);

            if (location != null) {
                visualizationViewer.getGraphLayout().setLocation(peptidesV, location);
            }
            expandedPeptides.put(groupV.getLabel(), false);
        } else {
            // uncollapsed peptides
            for (IntermediatePeptide peptide : group.getPeptides()) {
                String peptideLabel = peptide.getID().toString();
                VertexObject peptideV = new VertexObject(peptideLabel, peptide);

                graph.addVertex(peptideV);
                vertexPaints.put(peptideV, PEPTIDE_COLOR);

                String edgeName = "groupPeptide_" + groupV.getLabel() + "_" + peptideLabel;
                graph.addEdge(edgeName, groupV, peptideV);
                edgePaints.put(edgeName, EDGE_COLOR);

                if (location != null) {
                    visualizationViewer.getGraphLayout().setLocation(peptideV, location);
                }

                showPSMs.put(peptideLabel, false);
            }
            expandedPeptides.put(groupV.getLabel(), true);
        }
    }


    /**
     * Adds the PSMs of the given peptide to the graph. If the location is not
     * null, set the peptides' position to the given location.
     */
    private void addPSMVertices(VertexObject peptideV, Point2D location) {
        IntermediatePeptide peptide = (IntermediatePeptide)peptideV.getObject();

        // add the PSMs
        for (IntermediatePeptideSpectrumMatch psm : peptide.getPeptideSpectrumMatches()) {
            String psmLabel = psm.getID().toString();
            VertexObject psmV = new VertexObject(psmLabel, psm);

            graph.addVertex(psmV);
            vertexPaints.put(psmV, PSM_COLOR);

            String psmEdgeName = "peptidePSM_" + peptideV.getLabel() + "_" + psmLabel;
            graph.addEdge(psmEdgeName, peptideV, psmV);
            edgePaints.put(psmEdgeName, EDGE_COLOR);
        }

        showPSMs.put(peptideV.getLabel(), true);
    }


    /**
     * Recalculates the layout and visualization of the graph for the changed
     * graph topology
     */
    private void recalculateAndAnimateGraphChanges() {
        layout.initialize();

        if (layout instanceof IterativeContext) {
            Relaxer relaxer = new VisRunner((IterativeContext)layout);
            relaxer.stop();
            relaxer.prerelax();
        }

        StaticLayout<VertexObject, String> staticLayout =
                new StaticLayout<VertexObject, String>(graph, layout);

        LayoutTransition<VertexObject, String> lt =
                new LayoutTransition<VertexObject, String>(visualizationViewer,
                        visualizationViewer.getGraphLayout(),
                        staticLayout);

        Animator animator = new Animator(lt);
        animator.start();
        visualizationViewer.repaint();
    }
}