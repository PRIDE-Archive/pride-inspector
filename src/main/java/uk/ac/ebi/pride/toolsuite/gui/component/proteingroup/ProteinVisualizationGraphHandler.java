package uk.ac.ebi.pride.toolsuite.gui.component.proteingroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.mpc.pia.core.intermediate.IntermediateGroup;
import de.mpc.pia.core.intermediate.IntermediatePeptide;
import de.mpc.pia.core.intermediate.IntermediatePeptideSpectrumMatch;
import de.mpc.pia.core.intermediate.IntermediateProtein;
import de.mpc.pia.core.intermediate.IntermediateStructure;
import de.mpc.pia.core.intermediate.prideimpl.PrideImportController;
import de.mpc.pia.core.modeller.PIAModeller;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;

/**
 * Handler class for a graph which visualizes a protein ambiguity group
 * @author julian
 *
 */
public class ProteinVisualizationGraphHandler {
    
    /** access controller to access the data (proteins, peptides, PSMs...) */
    private DataAccessController controller;

    /** the accession of the selected protein (created while building the intermediate structure) */
    private String selectedProteinAccession;
    
    /** the PIA intermediate structure for the visualization */
    private IntermediateStructure intermediateStructure;
    
    
    /** the protein-group-peptides-PSMs graph */
    private DirectedGraph<VertexObject, String> graph;
    
    
    /** mapping from the group's label to whether its accessions are shown */
    private Map<String, Boolean> expandedAccessionsMap;

    /** mapping from the group's label to whether its peptides are shown */
    private Map<String, Boolean> expandedPeptidesMap;

    /** mapping from the peptide's label to whether its spectra a shown */
    private Map<String, Boolean> showPSMsMap;


    /** mapping from the group ID to the vertex in the graph */
    private Map<Integer, VertexObject> groupVertices;
    
    
    private static final String PROTEINS_OF_PREFIX = "proteins_of_";
    private static final String PEPTIDES_OF_PREFIX = "peptides_of_";
    
    
    public ProteinVisualizationGraphHandler(DataAccessController controller, Comparable proteinId, Comparable proteinGroupId) {
        this.controller = controller;
        this.selectedProteinAccession = null;
        this.intermediateStructure = null;
        
        this.expandedAccessionsMap = new HashMap<String, Boolean>();
        this.expandedPeptidesMap = new HashMap<String, Boolean>();
        this.showPSMsMap = new HashMap<String, Boolean>();
        
        createGraphFromSelectedProteinGroupId(proteinId, proteinGroupId);
    }
    
    
    /**
     * getter for the graph
     * @return
     */
    public DirectedGraph<VertexObject, String> getGraph() {
        return graph;
    }
    
    
    /**
     * getter for the selected protein accession (around which teh graph is
     * created)
     * @return
     */
    public String getProteinAccession() {
        return selectedProteinAccession;
    }
    
    
    /**
     * returns whether the accessions of the given group vertex are currently
     * shown/expanded
     */
    public boolean isExpandedAccessions(VertexObject vertex) {
        return (expandedAccessionsMap.containsKey(vertex.getLabel())) ?
                expandedAccessionsMap.get(vertex.getLabel()) : false;
    }
    
    
    /**
     * returns whether the peptides of the given group vertex are currently
     * shown/expanded
     */
    public Boolean isExpandedPeptides(VertexObject vertex) {
        return (expandedPeptidesMap.containsKey(vertex.getLabel())) ?
                expandedPeptidesMap.get(vertex.getLabel()) : false;
    }
    
    
    /**
     * returns whether the PSMs of the given peptide vertex are currently
     * shown/expanded
     */
    public Boolean isExpandedPSMs(VertexObject vertex) {
        return (showPSMsMap.containsKey(vertex.getLabel())) ?
                showPSMsMap.get(vertex.getLabel()) : false;
    }
    
    
    /**
     * Creates the graph using the data from the ambiguity group given by
     * selectedProteinGroupId
     */
    private void createGraphFromSelectedProteinGroupId(Comparable proteinId, Comparable proteinGroupId) {
        // create the intermediate structure
        PIAModeller piaModeller = new PIAModeller();
        Integer fileID = piaModeller.addPrideControllerAsInput(controller);
        PrideImportController importController = (PrideImportController)piaModeller.getImportController(fileID);

        for (Comparable protID : controller.getProteinAmbiguityGroupById(proteinGroupId).getProteinIds()) {
            String acc = importController.addProteinsSpectrumIdentificationsToStructCreator(protID, piaModeller.getIntermediateStructureCreator(), null);
            if (proteinId.equals(protID)) {
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
        // initialize the graph to be a directed sparse graph
        graph = new DirectedSparseGraph<VertexObject, String>();
        
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
                    }
                }

                // add the proteins collapsed
                if ((group.getProteins() != null) && (group.getProteins().size() > 0)) {
                    addProteinVertices(groupV, true);
                }

                // add the peptides
                if ((group.getPeptides() != null) && (group.getPeptides().size() > 0)) {
                    addPeptideVertices(groupV, true);
                }
            }
        }
    }


    /**
     * Adds the proteins of the given group to the graph, either collapsed or
     * uncollapsed. If the location is not null, set the proteins' position to
     * the given location.
     */
    private Collection<VertexObject> addProteinVertices(VertexObject groupV, Boolean collapsed) {
        List<VertexObject> proteins = new ArrayList<VertexObject>();
        IntermediateGroup group = (IntermediateGroup)groupV.getObject();
        if (collapsed && (group.getProteins().size() > 1)) {
            // show the proteins collapsed
            String proteinLabel = PROTEINS_OF_PREFIX + groupV.getLabel();
            VertexObject proteinsV =
                    new VertexObject(proteinLabel, group.getProteins());

            graph.addVertex(proteinsV);
            proteins.add(proteinsV);

            String edgeName = "proteinGroup_" + proteinLabel + "_" + groupV.getLabel();
            graph.addEdge(edgeName, proteinsV, groupV);

            expandedAccessionsMap.put(groupV.getLabel(), false);
        } else {
            for (IntermediateProtein protein : group.getProteins()) {
                String proteinLabel = protein.getAccession();
                VertexObject proteinV = new VertexObject(proteinLabel, protein);

                graph.addVertex(proteinV);
                proteins.add(proteinV);

                String edgeName = "proteinGroup_" + proteinLabel + "_" + groupV.getLabel();
                graph.addEdge(edgeName, proteinV, groupV);
            }
            expandedAccessionsMap.put(groupV.getLabel(), true);
        }
        
        return proteins;
    }


    /**
     * Adds the peptides of the given group to the graph, either collapsed or
     * uncollapsed. If the location is not null, set the peptides' position to
     * the given location.
     */
    private Collection<VertexObject> addPeptideVertices(VertexObject groupV, Boolean collapsed) {
        List<VertexObject> peptides = new ArrayList<VertexObject>();
        IntermediateGroup group = (IntermediateGroup)groupV.getObject();
        if (collapsed && (group.getPeptides().size() > 1)) {
            // show the peptides collapsed
            String peptidesLabel = PEPTIDES_OF_PREFIX + groupV.getLabel();
            VertexObject peptidesV = new VertexObject(peptidesLabel, group.getPeptides());

            graph.addVertex(peptidesV);
            peptides.add(peptidesV);

            String edgeName = "groupPeptide_" + groupV.getLabel() + "_" + peptidesLabel;
            graph.addEdge(edgeName, groupV, peptidesV);
            
            expandedPeptidesMap.put(groupV.getLabel(), false);
        } else {
            // uncollapsed peptides
            for (IntermediatePeptide peptide : group.getPeptides()) {
                String peptideLabel = peptide.getID().toString();
                VertexObject peptideV = new VertexObject(peptideLabel, peptide);

                graph.addVertex(peptideV);
                peptides.add(peptideV);

                String edgeName = "groupPeptide_" + groupV.getLabel() + "_" + peptideLabel;
                graph.addEdge(edgeName, groupV, peptideV);

                showPSMsMap.put(peptideLabel, false);
            }
            expandedPeptidesMap.put(groupV.getLabel(), true);
        }
        
        return peptides;
    }


    /**
     * Adds the PSMs of the given peptide to the graph. If the location is not
     * null, set the peptides' position to the given location.
     */
    private Collection<VertexObject> addPSMVertices(VertexObject peptideV) {
        List<VertexObject> psms = new ArrayList<VertexObject>();
        IntermediatePeptide peptide = (IntermediatePeptide)peptideV.getObject();

        // add the PSMs
        for (IntermediatePeptideSpectrumMatch psm : peptide.getPeptideSpectrumMatches()) {
            String psmLabel = psm.getID().toString();
            VertexObject psmV = new VertexObject(psmLabel, psm);

            graph.addVertex(psmV);
            psms.add(psmV);

            String psmEdgeName = "peptidePSM_" + peptideV.getLabel() + "_" + psmLabel;
            graph.addEdge(psmEdgeName, peptideV, psmV);
        }
        
        showPSMsMap.put(peptideV.getLabel(), true);
        return psms;
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

            groupVertices.put(group.getID(), groupV);
        }

        return groupV;
    }
    
    
    /**
     * Uncollapses the proteins of the given {@link VertexObject}, which should
     * be an {@link IntermediateGroup} representative
     * @param groupV
     * @return returns the uncollapsed vertices
     */
    public Collection<VertexObject> uncollapseProteins(VertexObject groupV) {
        if ((groupV == null) ||
                !(groupV.getObject() instanceof IntermediateGroup) ||
                ((expandedAccessionsMap.get(groupV.getLabel()) != null) && expandedAccessionsMap.get(groupV.getLabel()))) {
            return new ArrayList<VertexObject>();
        }

        // remove the collapsed proteins
        Iterator<String> edgeIt = graph.getIncidentEdges(groupV).iterator();
        while (edgeIt.hasNext()) {
            String edge = edgeIt.next();
            VertexObject proteinsV = graph.getOpposite(groupV, edge);
            if (proteinsV.getLabel().equals(PROTEINS_OF_PREFIX + groupV.getLabel())) {
                graph.removeVertex(proteinsV);
                break;
            }
        }

        // add the proteins uncollapsed
        return addProteinVertices(groupV, false);
    }


    /**
     * Collapses the proteins of the given {@link VertexObject}, which should be
     * an {@link IntermediateGroup} representative
     * 
     * @param groupV
     */
    public Collection<VertexObject> collapseProteins(VertexObject groupV) {
        if ((groupV == null) ||
                !(groupV.getObject() instanceof IntermediateGroup) ||
                (expandedAccessionsMap.get(groupV.getLabel()) == null) ||
                !expandedAccessionsMap.get(groupV.getLabel()) ||
                (((IntermediateGroup)groupV.getObject()).getProteins() == null) ||
                (((IntermediateGroup)groupV.getObject()).getProteins().size() < 2)) {
            return new ArrayList<VertexObject>();
        }

        // remove all the protein vertices
        Iterator<String> edgeIt = graph.getIncidentEdges(groupV).iterator();
        while (edgeIt.hasNext()) {
            String edge = edgeIt.next();
            VertexObject proteinV = graph.getOpposite(groupV, edge);
            if (proteinV.getObject() instanceof IntermediateProtein) {
                graph.removeVertex(proteinV);
            }
        }

        // add the proteins collapsed
        return addProteinVertices(groupV, true);
    }


    /**
     * Uncollapses the peptides of the given {@link VertexObject}, which should
     * be an {@link IntermediateGroup} representative
     * @param groupV
     */
    public Collection<VertexObject> uncollapsePeptides(VertexObject groupV) {
        if ((groupV == null) ||
                !(groupV.getObject() instanceof IntermediateGroup) ||
                ((expandedPeptidesMap.get(groupV.getLabel()) != null) && expandedPeptidesMap.get(groupV.getLabel()))) {
            return new ArrayList<VertexObject>();
        }

        // remove the collapsed peptides
        Iterator<String> edgeIt = graph.getIncidentEdges(groupV).iterator();
        while (edgeIt.hasNext()) {
            String edge = edgeIt.next();
            VertexObject peptidesV = graph.getOpposite(groupV, edge);
            if (peptidesV.getLabel().equals(PEPTIDES_OF_PREFIX + groupV.getLabel())) {
                graph.removeVertex(peptidesV);
                break;
            }
        }

        // add the peptides uncollapsed
        return addPeptideVertices(groupV, false);
    }


    /**
     * Collapses the peptides of the given {@link VertexObject}, which should
     * be an {@link IntermediateGroup} representative
     * @param groupV
     */
    public Collection<VertexObject> collapsePeptides(VertexObject groupV) {
        if ((groupV == null) ||
                !(groupV.getObject() instanceof IntermediateGroup) ||
                (expandedPeptidesMap.get(groupV.getLabel()) == null) ||
                !expandedPeptidesMap.get(groupV.getLabel()) ||
                (((IntermediateGroup)groupV.getObject()).getPeptides() == null) ||
                (((IntermediateGroup)groupV.getObject()).getPeptides().size() < 2)) {
            return new ArrayList<VertexObject>();
        }

        // remove all the peptide and PSM vertices
        Iterator<String> edgeIt = graph.getIncidentEdges(groupV).iterator();
        while (edgeIt.hasNext()) {
            String edge = edgeIt.next();
            VertexObject peptideV = graph.getOpposite(groupV, edge);
            if (peptideV.getObject() instanceof IntermediatePeptide) {
                if (isExpandedPSMs(peptideV)) {
                    hidePSMs(peptideV);
                }
                graph.removeVertex(peptideV);
            }
        }

        // add the peptides collapsed
        return addPeptideVertices(groupV, true);
    }


    /**
     * Shows the PSMs of the given {@link VertexObject}, which should
     * be an {@link IntermediatePeptide} representative
     * @param groupV
     */
    public Collection<VertexObject> showPSMs(VertexObject peptideV) {
        if ((peptideV == null) ||
                !(peptideV.getObject() instanceof IntermediatePeptide) ||
                ((showPSMsMap.get(peptideV.getLabel()) != null) && showPSMsMap.get(peptideV.getLabel()))) {
            return new ArrayList<VertexObject>();
        }

        return addPSMVertices(peptideV);
    }


    /**
     * Hides the PSMs of the given {@link VertexObject}, which should
     * be an {@link IntermediatePeptide} representative
     * @param groupV
     */
    public void hidePSMs(VertexObject peptideV) {
        if ((peptideV == null) ||
                !(peptideV.getObject() instanceof IntermediatePeptide) ||
                (showPSMsMap.get(peptideV.getLabel()) == null) ||
                !showPSMsMap.get(peptideV.getLabel())) {
            return;
        }

        // remove the PSMs from the graph
        Iterator<String> edgeIt = graph.getIncidentEdges(peptideV).iterator();
        while (edgeIt.hasNext()) {
            String edge = edgeIt.next();
            VertexObject psmV = graph.getOpposite(peptideV, edge);
            if (psmV.getObject() instanceof IntermediatePeptideSpectrumMatch) {
                graph.removeVertex(psmV);
            }
        }
        
        showPSMsMap.put(peptideV.getLabel(), false);
    }
    
}