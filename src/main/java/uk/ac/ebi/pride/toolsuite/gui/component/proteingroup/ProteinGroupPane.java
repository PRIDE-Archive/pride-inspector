package uk.ac.ebi.pride.toolsuite.gui.component.proteingroup;

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.Graphs;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.util.Animator;
import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.commons.collections15.functors.MapTransformer;
import org.apache.commons.collections15.map.LazyMap;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.utilities.data.core.PeptideSequence;
import uk.ac.ebi.pride.utilities.data.core.Protein;
import uk.ac.ebi.pride.utilities.mol.PTModification;
import uk.ac.ebi.pride.utilities.util.Tuple;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * This panel will show the information of the Protein Groups and the peptides and proteins that are connected
 * @author ypriverol
 */
public class ProteinGroupPane extends DataAccessControllerPane<Void, Protein>{

    DataAccessController controller;

    private VisualizationViewer<String,String> visualizationViewer;

    //To be used with two main layout Tree Layout and FreeLayout
    ConcurrentMap<uk.ac.ebi.pride.utilities.mol.Peptide, String>   peptideVertices = null;

    ConcurrentMap<Protein, String>           proteinVertices = null;

    ConcurrentMap<Peptide, String>           psmVertices = null;

    List<Tuple<Protein, uk.ac.ebi.pride.utilities.mol.Peptide>> edgestProteinPeptides;

    Forest<String, String> graph;

    Map<String,Paint> vertexPaints;

    Map<String,Paint> edgePaints;

    Comparable idProtein = null;

    ScalingControl scaler = new CrossoverScalingControl();

    final String COMMAND_STRING = "Score Threshold: ";

    Factory<DirectedGraph<String,String>> graphFactory;

    Factory<Tree<String,String>> treeFactory;
    Factory<String> edgeFactory;

    Factory<String> vertexFactory;

    private Comparable proteinGroupId;



    public ProteinGroupPane(DataAccessController controller, Comparable proteinId, Comparable proteinGroupId) {
        super(controller);
        this.controller = controller;
        this.idProtein = proteinId;
        this.proteinGroupId = proteinGroupId;
        peptideVertices = new ConcurrentHashMap<uk.ac.ebi.pride.utilities.mol.Peptide, String>();
        proteinVertices = new ConcurrentHashMap<Protein, String>();
        psmVertices     = new ConcurrentHashMap<Peptide, String>();
        edgestProteinPeptides = new ArrayList<Tuple<Protein, uk.ac.ebi.pride.utilities.mol.Peptide>>();
        try {
            setUpView();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void setupMainPane() {
        // setup the main pane
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder());

    }

    @Override
    public void process(TaskEvent<List<Protein>> listTaskEvent) {
        for(Protein protein: listTaskEvent.getValue()){
            if(!proteinVertices.containsKey(protein)) {
                proteinVertices.put(protein, protein.getId().toString());
            }
            for(Peptide peptide: protein.getPeptides()){
                PeptideSequence peptideSequence = peptide.getPeptideSequence();
                uk.ac.ebi.pride.utilities.mol.Peptide peptideSequenceNew = translate(peptideSequence);
                if(!peptideVertices.containsKey(peptideSequenceNew)){
                  peptideVertices.put(peptideSequenceNew, peptideSequenceNew.toString());
                }
                if(!psmVertices.containsKey(peptide)){
                    String id = String.valueOf(psmVertices.size() + 1);
                    psmVertices.put(peptide, id);
                }
            }
        }
    }
    public uk.ac.ebi.pride.utilities.mol.Peptide translate(PeptideSequence peptideSequence) {
        uk.ac.ebi.pride.utilities.mol.Peptide newPeptide = new uk.ac.ebi.pride.utilities.mol.Peptide(peptideSequence.getSequence());
        PTModification newModification;

        String name;
        String type = null;
        String label;
        List<Double> monoMassDeltas;
        List<Double> avgMassDeltas;
        int position;
        for (uk.ac.ebi.pride.utilities.data.core.Modification oldModification : peptideSequence.getModifications()) {
            name = oldModification.getName();
            label = null;
            monoMassDeltas = oldModification.getMonoisotopicMassDelta();
            avgMassDeltas = oldModification.getAvgMassDelta();
            newModification = new PTModification(name, type, label, monoMassDeltas, avgMassDeltas);

            /**
             * old modification position from [0..length], 0 means the position locate in c-terminal.
             * the new modification from [0..length-1], 0 means the first amino acid of peptide.
             * The modification worked in c-terminal or first amino acid, the theoretical mass are same.
             */
            position = oldModification.getLocation() - 1;
            if (position == -1) {
                position = 0;
            }

            newPeptide.addModification(position, newModification);
        }

        return newPeptide;
    }

    @Override
    public void succeed(TaskEvent<Void> voidTaskEvent) {

    }

    @Override
    public void finished(TaskEvent<Void> event) {
        for(Protein protein: proteinVertices.keySet()){
            graph.addVertex(protein.getId().toString());
            if(protein.getId() == idProtein)
                vertexPaints.put(protein.getId().toString(), Color.RED);
            else
                vertexPaints.put(protein.getId().toString(), Color.LIGHT_GRAY);
        }
        for(uk.ac.ebi.pride.utilities.mol.Peptide peptideSequence: peptideVertices.keySet()){
            graph.addVertex(peptideVertices.get(peptideSequence));
            vertexPaints.put(peptideVertices.get(peptideSequence), Color.green);
            for(Protein protein: proteinVertices.keySet()){
                for(Peptide peptide: protein.getPeptides()){
                    Tuple<Protein, uk.ac.ebi.pride.utilities.mol.Peptide> edge = new Tuple<Protein, uk.ac.ebi.pride.utilities.mol.Peptide>(protein, peptideSequence);
                    if(peptideSequence.equals(translate(peptide.getPeptideSequence())) && !edgestProteinPeptides.contains(edge)){
                        graph.addEdge(String.valueOf(graph.getEdgeCount()), proteinVertices.get(protein), peptideVertices.get(peptideSequence));
                        edgestProteinPeptides.add(edge);
                    }
                }
            }
        }

        Layout<String,String> l = visualizationViewer.getGraphLayout();

        LayoutTransition<String,String> lt =
                new LayoutTransition<String,String>(visualizationViewer, visualizationViewer.getGraphLayout(), l);
        Animator animator = new Animator(lt);
        animator.start();
        visualizationViewer.getRenderContext().getMultiLayerTransformer().setToIdentity();

        Forest<String, String> graphLayout = (Forest<String, String>) graph;
        visualizationViewer.validate();
        visualizationViewer.repaint();
    }

    @Override
    protected void addComponents() {
        vertexPaints = LazyMap.<String,Paint>decorate(new HashMap<String, Paint>(), new ConstantTransformer(Color.white));
        edgePaints   = LazyMap.<String,Paint>decorate(new HashMap<String,Paint>(), new ConstantTransformer(Color.BLUE));
        graph        = Graphs.<String,String>synchronizedForest(new DelegateForest<String, String>());
    }

    /**
     * Add the rest of components
     */
    @Override
    public void populate() {
        //removeAll();
        //createTree();
    }

    public ProteinGroupPane(DataAccessController controller, JComponent parentComponent) {
        super(controller, parentComponent);
    }

    public ProteinGroupPane(DataAccessController controller, JComponent parentComponent, String title) {
        super(controller, parentComponent, title);
    }
    private void setUpView() throws IOException {

        final AggregateLayout<String,String> layout = new AggregateLayout<String,String>(new FRLayout2<String, String>(graph));

        visualizationViewer = new VisualizationViewer<String,String>(layout);
        visualizationViewer.setBackground( Color.white );
        //Tell the renderer to use our own customized color rendering
        visualizationViewer.getRenderContext().setVertexFillPaintTransformer(MapTransformer.<String,Paint>getInstance(vertexPaints));

        visualizationViewer.getRenderContext().setVertexDrawPaintTransformer(new Transformer<String, Paint>() {
            public Paint transform(String v) {
                if (visualizationViewer.getPickedVertexState().isPicked(v)) {
                    return Color.cyan;
                } else {
                    return Color.BLACK;
                }
            }
        });

        visualizationViewer.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));
        visualizationViewer.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line());

        visualizationViewer.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
        // add a listener for ToolTips
        visualizationViewer.setVertexToolTipTransformer(new ToStringLabeller());

        visualizationViewer.getRenderContext().setEdgeDrawPaintTransformer(MapTransformer.<String,Paint>getInstance(edgePaints));

        visualizationViewer.getRenderContext().setEdgeStrokeTransformer(new Transformer<String,Stroke>() {
            protected final Stroke THIN = new BasicStroke(1);
            protected final Stroke THICK= new BasicStroke(2);
            public Stroke transform(String e)
            {
                Paint c = edgePaints.get(e);
                if (c == Color.LIGHT_GRAY)
                    return THIN;
                else
                    return THICK;
            }
        });

        DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
        visualizationViewer.setGraphMouse(gm);

        // Visual Controls
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

        Class[] combos = getCombos();
        final JComboBox comboBoxLayout = new JComboBox(combos);
        // use a renderer to shorten the layout name presentation
        comboBoxLayout.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                String valueString = value.toString();
                valueString = valueString.substring(valueString.lastIndexOf('.')+1);
                return super.getListCellRendererComponent(list, valueString, index, isSelected,
                        cellHasFocus);
            }
        });
        comboBoxLayout.addActionListener(new LayoutChooser(comboBoxLayout, visualizationViewer,graph));
        comboBoxLayout.setSelectedItem(FRLayout2.class);

        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBorder(BorderFactory.createTitledBorder("Visual Options"));
        JPanel sliderPanel = new JPanel(new GridLayout(3,1));
        JPanel sliderLabelPanel = new JPanel(new GridLayout(3,1));
        JPanel zoomPanel = new JPanel();
        zoomPanel.setLayout(new FlowLayout());
        zoomPanel.add(minus);
        zoomPanel.add(plus);
        sliderPanel.add(comboBoxLayout);
        sliderPanel.add(gm.getModeComboBox());
        sliderPanel.add(zoomPanel);
        sliderLabelPanel.add(new JLabel("Layout:", JLabel.RIGHT));
        sliderLabelPanel.add(new JLabel("Mouse Mode:", JLabel.RIGHT));
        sliderLabelPanel.add(new JLabel("Zoom:", JLabel.RIGHT));
        controlsPanel.add(sliderLabelPanel, BorderLayout.WEST);
        controlsPanel.add(sliderPanel);

        //Create slider to adjust the number of edges to remove when clustering
        final JSlider edgeBetweennessSlider = new JSlider(JSlider.HORIZONTAL);
        edgeBetweennessSlider.setBackground(Color.WHITE);
        //edgeBetweennessSlider.setPreferredSize(new Dimension(210, 50));
        edgeBetweennessSlider.setPaintTicks(true);
        edgeBetweennessSlider.setMaximum(graph.getEdgeCount());
        edgeBetweennessSlider.setMinimum(0);
        edgeBetweennessSlider.setValue(0);
        edgeBetweennessSlider.setMajorTickSpacing(10);
        edgeBetweennessSlider.setPaintLabels(true);
        edgeBetweennessSlider.setPaintTicks(true);

//		edgeBetweennessSlider.setBorder(BorderFactory.createLineBorder(Color.black));
        //TO DO: edgeBetweennessSlider.add(new JLabel("Node Size (PageRank With Priors):"));
        //I also want the slider value to appear
        final JPanel eastControls = new JPanel();
        eastControls.setOpaque(true);
        eastControls.setLayout(new BoxLayout(eastControls, BoxLayout.Y_AXIS));
        eastControls.add(Box.createVerticalGlue());
        eastControls.add(edgeBetweennessSlider);


        final String eastSize = COMMAND_STRING + edgeBetweennessSlider.getValue();

        final TitledBorder sliderBorder = BorderFactory.createTitledBorder(eastSize);
        eastControls.setBorder(sliderBorder);
        //eastControls.add(eastSize);
        eastControls.add(Box.createVerticalGlue());

        edgeBetweennessSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (!source.getValueIsAdjusting()) {
                    int numEdgesToRemove = source.getValue();
                    clusterAndRecolor(layout, numEdgesToRemove, similarColors,
                            true);
                    sliderBorder.setTitle(
                            COMMAND_STRING + edgeBetweennessSlider.getValue());
                    eastControls.repaint();
                    visualizationViewer.validate();
                    visualizationViewer.repaint();
                }
            }
        });

//        Container content = this.getContentPane();
        add(new GraphZoomScrollPane(visualizationViewer));
        JPanel south = new JPanel();
        JPanel grid = new JPanel(new GridLayout(2,1));
        south.add(grid);
        south.add(eastControls);
        south.add(controlsPanel);
        add(south, BorderLayout.SOUTH);
    }

    public void clusterAndRecolor(AggregateLayout<String,String> layout,
                                  int numEdgesToRemove,
                                  Color[] colors, boolean groupClusters) {

        Graph<String,String> g = layout.getGraph();
        layout.removeAll();

        EdgeBetweennessClusterer<String,String> clusterer =
                new EdgeBetweennessClusterer<String,String>(numEdgesToRemove);
        Set<Set<String>> clusterSet = clusterer.transform(g);
        java.util.List<String> edges = clusterer.getEdgesRemoved();

        int i = 0;
        //Set the colors of each node so that each cluster's vertices have the same color
        for (Iterator<Set<String>> cIt = clusterSet.iterator(); cIt.hasNext();) {

            Set<String> vertices = cIt.next();
            Color c = colors[i % colors.length];

            colorCluster(vertices, c);
            if(groupClusters == true) {
                groupCluster(layout, vertices);
            }
            i++;
        }
        for (String e : g.getEdges()) {

            if (edges.contains(e)) {
                edgePaints.put(e, Color.lightGray);
            } else {
                edgePaints.put(e, Color.black);
            }
        }

    }

    private void colorCluster(Set<String> vertices, Color c) {
        for (String v : vertices) {
            vertexPaints.put(v, c);
        }
    }

    private void groupCluster(AggregateLayout<String,String> layout, Set<String> vertices) {
        if(vertices.size() < layout.getGraph().getVertexCount()) {
            Point2D center = layout.transform(vertices.iterator().next());
            Graph<String,String> subGraph = SparseMultigraph.<String,String>getFactory().create();
            for(String v : vertices) {
                subGraph.addVertex(v);
            }
            Layout<String,String> subLayout =
                    new CircleLayout<String,String>(subGraph);
            subLayout.setInitializer(visualizationViewer.getGraphLayout());
            subLayout.setSize(new Dimension(40,40));

            layout.put(subLayout,center);
            visualizationViewer.repaint();
        }
    }

    public static final Color[] similarColors =
            {
                    new Color(216, 134, 134),
                    new Color(135, 137, 211),
                    new Color(134, 206, 189),
                    new Color(206, 176, 134),
                    new Color(194, 204, 134),
                    new Color(145, 214, 134),
                    new Color(133, 178, 209),
                    new Color(103, 148, 255),
                    new Color(60, 220, 220),
                    new Color(30, 250, 100)
            };

    public static final Color currentProtein = Color.RED;


    private static final class LayoutChooser implements ActionListener {

        private JComboBox layoutCombo;

        private VisualizationViewer<String,String> visualizationViewer;

        final Graph<String, String> graph;

        private LayoutChooser(JComboBox layoutCombo, VisualizationViewer<String,String> visualizationViewer, Graph<String, String> graph){
            super();
            this.layoutCombo = layoutCombo;
            this.visualizationViewer = visualizationViewer;
            this.graph = graph;
        }

        public void actionPerformed(ActionEvent arg0){

            Class layoutC = (Class) layoutCombo.getSelectedItem();

            try {
                Layout<String, String> previousLayout = visualizationViewer.getGraphLayout();
                if (previousLayout.getClass() == RadialTreeLayout.class) {
                    Rings rings = new Rings((RadialTreeLayout<String, String>) previousLayout);
                    visualizationViewer.removePreRenderPaintable(rings);
                }

                Object o;
                Constructor<? extends Layout<String, String>> constructor;
                if (layoutC != TreeLayout.class && layoutC != RadialTreeLayout.class){
                    constructor = layoutC.getConstructor(new Class[]{Graph.class});
                    o = constructor.newInstance(graph);
                }else if (layoutC != TreeLayout.class){
                    constructor = layoutC.getConstructor(new Class[]{Forest.class});
                    o = constructor.newInstance(graph);
                }else
                    o = new TreeLayout<String, String>((Forest<String, String>)graph, 60, 100);

                Layout<String,String> l = (Layout<String,String>) o;

                l.setInitializer(visualizationViewer.getGraphLayout());

                if(layoutC != TreeLayout.class)
                     l.setSize(visualizationViewer.getSize());

                LayoutTransition<String,String> lt =
                        new LayoutTransition<String,String>(visualizationViewer, visualizationViewer.getGraphLayout(), l);
                Animator animator = new Animator(lt);
                animator.start();
                visualizationViewer.getRenderContext().getMultiLayerTransformer().setToIdentity();

                Forest<String, String> graphLayout = (Forest<String, String>) graph;
                RadialTreeLayout<String, String> radialTreeLayout = new RadialTreeLayout<String, String>(graphLayout);
                Rings rings = new Rings(radialTreeLayout);
                if(layoutC == RadialTreeLayout.class){
                //    visualizationViewer.addPreRenderPaintable(rings);
                }else{
                    visualizationViewer.removePreRenderPaintable(rings);
                }

                visualizationViewer.repaint();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        class Rings implements VisualizationServer.Paintable {

            Collection<Double> depths;
            RadialTreeLayout radialLayout;

            public Rings(RadialTreeLayout radialLayout) {
                this.radialLayout = radialLayout;
                depths = getDepths();
            }

            private Collection<Double> getDepths() {
                Set<Double> depths = new HashSet<Double>();
                Map<String,PolarPoint> polarLocations = radialLayout.getPolarLocations();
                for(String v : graph.getVertices()) {
                    PolarPoint pp = polarLocations.get(v);
                    depths.add(pp.getRadius());
                }
                return depths;
            }

            public void paint(Graphics g) {
                g.setColor(Color.lightGray);

                Graphics2D g2d = (Graphics2D)g;
                Point2D center = radialLayout.getCenter();

                Ellipse2D ellipse = new Ellipse2D.Double();
                for(double d : depths) {
                    ellipse.setFrameFromDiagonal(center.getX()-d, center.getY()-d,
                            center.getX()+d, center.getY()+d);
                    Shape shape = visualizationViewer.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).transform(ellipse);
                    g2d.draw(shape);
                }
            }

            public boolean useTransform() {
                return true;
            }
        }
    }

    private static Class<? extends Layout>[] getCombos()
    {
        List<Class<? extends Layout>> layouts = new ArrayList<Class<? extends Layout>>();
        layouts.add(TreeLayout.class);
        layouts.add(CircleLayout.class);
        layouts.add(RadialTreeLayout.class);
        layouts.add(FRLayout2.class);
        return layouts.toArray(new Class[0]);
    }

    private void createTreeOld() {


        graphFactory = new Factory<DirectedGraph<String,String>>() {

            public DirectedGraph<String, String> create() {
                return new DirectedSparseMultigraph<String,String>();
            }
        };

        treeFactory = new Factory<Tree<String,String>> () {

            public Tree<String, String> create() {
                return new DelegateTree<String,String>(graphFactory);
            }
        };

        edgeFactory = new Factory<String>() {
            int i=0;
            public String create() {
                return String.valueOf(i++);
            }};
        vertexFactory = new Factory<String>() {
            int i=0;
            public String create() {
                return "V"+i++;
            }};

        graph.addVertex("V0");
        graph.addEdge(edgeFactory.create(), "V0", "V1");
        graph.addEdge(edgeFactory.create(), "V0", "V2");
        graph.addEdge(edgeFactory.create(), "V1", "V4");
        graph.addEdge(edgeFactory.create(), "V2", "V3");
        graph.addEdge(edgeFactory.create(), "V2", "V5");
        graph.addEdge(edgeFactory.create(), "V4", "V6");
        graph.addEdge(edgeFactory.create(), "V4", "V7");
        graph.addEdge(edgeFactory.create(), "V3", "V8");
        graph.addEdge(edgeFactory.create(), "V6", "V9");
        graph.addEdge(edgeFactory.create(), "V4", "V10");

        graph.addVertex("A0");
        graph.addEdge(edgeFactory.create(), "A0", "A1");
        graph.addEdge(edgeFactory.create(), "A0", "A2");
        graph.addEdge(edgeFactory.create(), "A0", "A3");

        graph.addVertex("B0");
        graph.addEdge(edgeFactory.create(), "B0", "B1");
        graph.addEdge(edgeFactory.create(), "B0", "B2");
        graph.addEdge(edgeFactory.create(), "B1", "B4");
        graph.addEdge(edgeFactory.create(), "B2", "B3");
        graph.addEdge(edgeFactory.create(), "B2", "B5");
        graph.addEdge(edgeFactory.create(), "B4", "B6");
        graph.addEdge(edgeFactory.create(), "B4", "B7");
        graph.addEdge(edgeFactory.create(), "B3", "B8");
        graph.addEdge(edgeFactory.create(), "B6", "B9");

    }
}
