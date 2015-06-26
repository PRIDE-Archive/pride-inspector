package uk.ac.ebi.pride.toolsuite.gui.component.proteingroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediateGroup;
import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediatePeptide;
import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediatePeptideSpectrumMatch;
import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediateProtein;
import uk.ac.ebi.pride.utilities.pia.intermediate.prideimpl.PrideImportController;
import uk.ac.ebi.pride.utilities.pia.modeller.PIAModeller;
import uk.ac.ebi.pride.utilities.pia.modeller.filter.AbstractFilter;
import uk.ac.ebi.pride.utilities.pia.modeller.filter.FilterComparator;
import uk.ac.ebi.pride.utilities.pia.modeller.filter.psm.PSMScoreFilter;
import uk.ac.ebi.pride.utilities.pia.modeller.protein.inference.AbstractProteinInference;
import uk.ac.ebi.pride.utilities.pia.modeller.protein.inference.InferenceProteinGroup;
import uk.ac.ebi.pride.utilities.pia.modeller.scores.CvScore;
import uk.ac.ebi.pride.utilities.pia.modeller.scores.peptide.PeptideScoring;
import uk.ac.ebi.pride.utilities.pia.modeller.scores.peptide.PeptideScoringUseBestPSM;
import uk.ac.ebi.pride.utilities.pia.modeller.scores.protein.ProteinScoring;
import uk.ac.ebi.pride.utilities.pia.modeller.scores.protein.ProteinScoringAdditive;
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

    /** the accession of the reference protein (created while building the intermediate structure) */
    private String referenceProteinAccession;
    
    /** the vertex containing the reference protein, this may change during collapsing/uncollapsing */
    private VertexObject referenceVertex;
    
    /** the PIA intermediate structure for the visualization */
    private PIAModeller piaModeller;
    
    
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
    
    
    /** the main score accession */
    private String mainScoreAccession; 
    
    /** the highest main score in the cluster */
    private Double highestMainScore;
    
    /** the lowest main score in the cluster */
    private Double lowestMainScore;
    
    
    /** mapping from the vertices to relations of other vertices  */
    private Map<VertexObject, Map<VertexObject, VertexRelation>> proteinRelationMaps;
    
    
    private static final String PROTEINS_OF_PREFIX = "proteins_of_";
    private static final String PEPTIDES_OF_PREFIX = "peptides_of_";
    
    
    public ProteinVisualizationGraphHandler(DataAccessController controller, Comparable proteinId, Comparable proteinGroupId) {
        this.controller = controller;
        this.referenceProteinAccession = null;
        this.referenceVertex = null;
        
        this.piaModeller = null;
        
        this.expandedAccessionsMap = new HashMap<String, Boolean>();
        this.expandedPeptidesMap = new HashMap<String, Boolean>();
        this.showPSMsMap = new HashMap<String, Boolean>();
        
        this.mainScoreAccession = null;
        this.highestMainScore = null;
        this.lowestMainScore = null;
        
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
     * getter for the reference protein accession (around which the graph is
     * created)
     * @return
     */
    public String getReferenceAccession() {
        return referenceProteinAccession;
    }
    
    
    /**
     * getter for the vertex containing the reference protein, this may change
     * during collapsing/uncollapsing
     * @return
     */
    public VertexObject getReferenceVertex() {
        return referenceVertex;
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
        piaModeller = new PIAModeller();
        Integer fileID = piaModeller.addPrideControllerAsInput(controller);
        PrideImportController importController = (PrideImportController)piaModeller.getImportController(fileID);
        
        for (Comparable protID : controller.getProteinAmbiguityGroupById(proteinGroupId).getProteinIds()) {
            String acc = importController.addProteinsSpectrumIdentificationsToStructCreator(protID, null, null);
            if (proteinId.equals(protID)) {
                referenceProteinAccession = acc;
            }
        }
        
        piaModeller.buildIntermediateStructure();
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
        
        mainScoreAccession = piaModeller.getPSMModeller().getFilesMainScoreAccession(1);
        
        // go through the clusters and create the graph
        for (Set<IntermediateGroup> cluster : piaModeller.getIntermediateStructure().getClusters().values()) {
            for (IntermediateGroup group : cluster) {
                VertexObject groupV = addGroupVertex(group);

                // connect to the child-groups
                if (group.getChildren() != null) {
                    for (IntermediateGroup child : group.getChildren()) {
                        VertexObject childV = addGroupVertex(child);
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
                    
                    for (IntermediatePeptide peptide : group.getPeptides()) {
                        for (IntermediatePeptideSpectrumMatch psm : peptide.getAllPeptideSpectrumMatches()) {
                            Double score = psm.getScore(mainScoreAccession);
                            
                            if ((score != null) && !score.equals(Double.NaN)) {
                                if ((highestMainScore == null) || highestMainScore.equals(Double.NaN)) {
                                    highestMainScore = score;
                                } else if (score > highestMainScore){
                                    highestMainScore = score;
                                }
                                
                                if ((lowestMainScore == null) || lowestMainScore.equals(Double.NaN)) {
                                    lowestMainScore = score;
                                } else if (score < lowestMainScore){
                                    lowestMainScore = score;
                                }
                            }
                        }
                    }
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
            
            // check if it contains the reference protein
            if (referenceProteinAccession != null) {
                for (IntermediateProtein protein : group.getProteins()) {
                    if (referenceProteinAccession.equals(protein.getAccession())) {
                        referenceVertex = proteinsV;
                        break;
                    }
                }
            }
        } else {
            for (IntermediateProtein protein : group.getProteins()) {
                String proteinLabel = protein.getAccession();
                VertexObject proteinV = new VertexObject(proteinLabel, protein);

                graph.addVertex(proteinV);
                proteins.add(proteinV);

                String edgeName = "proteinGroup_" + proteinLabel + "_" + groupV.getLabel();
                graph.addEdge(edgeName, proteinV, groupV);
                
                // check if this is the reference protein
                if (referenceProteinAccession.equals(protein.getAccession())) {
                    referenceVertex = proteinV;
                }
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
        for (IntermediatePeptideSpectrumMatch psm : peptide.getAllPeptideSpectrumMatches()) {
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
        proteinRelationMaps = new HashMap<VertexObject, Map<VertexObject,VertexRelation>>();
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
        proteinRelationMaps = new HashMap<VertexObject, Map<VertexObject,VertexRelation>>();
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
        proteinRelationMaps = new HashMap<VertexObject, Map<VertexObject,VertexRelation>>();
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
        proteinRelationMaps = new HashMap<VertexObject, Map<VertexObject,VertexRelation>>();
        return addPeptideVertices(groupV, true);
    }


    /**
     * Shows the PSMs of the given {@link VertexObject}, which should
     * be an {@link IntermediatePeptide} representative
     * @param peptideV
     */
    public Collection<VertexObject> showPSMs(VertexObject peptideV) {
        if ((peptideV == null) ||
                !(peptideV.getObject() instanceof IntermediatePeptide) ||
                ((showPSMsMap.get(peptideV.getLabel()) != null) && showPSMsMap.get(peptideV.getLabel()))) {
            return new ArrayList<VertexObject>();
        }
        
        proteinRelationMaps = new HashMap<VertexObject, Map<VertexObject,VertexRelation>>();
        return addPSMVertices(peptideV);
    }


    /**
     * Hides the PSMs of the given {@link VertexObject}, which should
     * be an {@link IntermediatePeptide} representative
     * @param peptideV
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
        proteinRelationMaps = new HashMap<VertexObject, Map<VertexObject,VertexRelation>>();
    }
    
    
    /**
     * Returns the accession of teh main score
     * @return
     */
    public String getMainScoreAccession() {
        return mainScoreAccession;
    }
    
    
    /**
     * Returns the lowest value of the main score in the intermediate structure
     * @return
     */
    public Double getLowestMainScore() {
        return lowestMainScore;
    }
    
    
    /**
     * Returns the highest value of the main score in the intermediate structure
     * @return
     */
    public Double getHighestMainScore() {
        return highestMainScore;
    }
    
    
    /**
     * Inferes the proteins in this graph, using the given inference and score
     * threshold.
     * 
     * @param scoreThreshold
     */
    public void infereProteins(Double scoreThreshold, Class<? extends AbstractProteinInference> inferenceClass, 
            Boolean considerModifications) {
        PeptideScoring pepScoring = new PeptideScoringUseBestPSM(mainScoreAccession, false);
        ProteinScoring protScoring = new ProteinScoringAdditive(false, pepScoring);
        
        List<AbstractFilter> filters = new ArrayList<AbstractFilter>();
        
        if (mainScoreAccession != null) {
            CvScore cvScore = CvScore.getCvRefByAccession(mainScoreAccession);
            
            filters.add(
                    new PSMScoreFilter(
                        ((cvScore == null) || cvScore.getHigherScoreBetter()) ? FilterComparator.greater_equal : FilterComparator.less_equal,
                        scoreThreshold,
                        false,
                        mainScoreAccession,
                        false)
                    );
        }
        
        piaModeller.getProteinModeller().infereProteins(
                pepScoring,
                protScoring,
                inferenceClass,
                filters,
                considerModifications);
        
        proteinRelationMaps = new HashMap<VertexObject, Map<VertexObject,VertexRelation>>();
    }
    
    
    /**
     * This enum describes the relativity of a vertex to another considering
     * their PAGs
     * @author julian
     *
     */
    protected enum VertexRelation {
        IN_NO_PAG,
        IN_UNRELATED_PAG,
        IN_SAME_PAG,
        IN_SUPER_PAG,
        IN_SUB_PAG,
        IN_PARALLEL_PAG,
        ;
    }
    
    
    /**
     * creates a mapping from each vertex in the graph to its relation to the
     * given proteinVertex
     *  
     * @param proteinVertex
     * @return
     */
    private Map<VertexObject, VertexRelation> createProteinsRelationsMap(VertexObject proteinVertex) {
        // first get the PAG of the proteinVertex
        IntermediateProtein protein;
        Object vObject = proteinVertex.getObject();
        if (vObject instanceof Collection) {
            vObject = ((Collection<?>)vObject).iterator().next();
        }
        if (vObject instanceof IntermediateProtein) {
            protein = (IntermediateProtein)vObject;
        } else {
            return null;
        }
        
        InferenceProteinGroup pag = getProteinsPAG(protein);
        Map<VertexObject, VertexRelation> relations = new HashMap<VertexObject, ProteinVisualizationGraphHandler.VertexRelation>(); 
        if (pag != null) {
            for (VertexObject relatedVertex : graph.getVertices()) {
                Object objElement = relatedVertex.getObject();
                boolean done = false;
                
                if (objElement instanceof IntermediateGroup) {
                    // groups have no relation
                    continue;
                }
                
                // check for same PAG
                if (isObjectInPAG(objElement, pag)) {
                    relations.put(relatedVertex, VertexRelation.IN_SAME_PAG);
                    continue;
                }
                
                // check for sub-PAG
                for (InferenceProteinGroup subPAG : pag.getSubGroups()) {
                    if (isObjectInPAG(objElement, subPAG)) {
                        relations.put(relatedVertex, VertexRelation.IN_SUB_PAG);
                        done = true;
                    }
                    
                    if (done) {
                        break;
                    }
                }
                if (!done) {
                    VertexRelation highestRelation = VertexRelation.IN_NO_PAG;
                    
                    // check for super- and parallel-PAG
                    for (InferenceProteinGroup mainPAG : piaModeller.getProteinModeller().getInferredProteins()) {
                        if (mainPAG.getSubGroups().contains(pag)) {
                            // check for super-PAG
                            if (isObjectInPAG(objElement, mainPAG)) {
                                relations.put(relatedVertex, VertexRelation.IN_SUPER_PAG);
                                done = true;
                            }
                            
                            // check for parallel-PAG
                            for (InferenceProteinGroup parallelPAG : mainPAG.getSubGroups()) {
                                if (isObjectInPAG(objElement, parallelPAG)) {
                                    highestRelation = VertexRelation.IN_PARALLEL_PAG;
                                }
                            }
                        }
                        
                        if (done) {
                            break;
                        }
                        
                        // record, if it is in any PAG at all
                        if (highestRelation.equals(VertexRelation.IN_NO_PAG)) {
                            if (isObjectInPAG(objElement, mainPAG)) {
                                highestRelation = VertexRelation.IN_UNRELATED_PAG;
                            }
                            for (InferenceProteinGroup subPAG : mainPAG.getSubGroups()) {
                                if (isObjectInPAG(objElement, subPAG)) {
                                    highestRelation = VertexRelation.IN_UNRELATED_PAG;
                                }
                            }
                        }
                    }
                    
                    if (!done) {
                        relations.put(relatedVertex, highestRelation);
                    }
                }
            }
        } else {
            // no PAG for this vertex -> all vertices are either unrelated or in no PAG
            
            for (VertexObject relatedVertex : graph.getVertices()) {
                Object objElement = relatedVertex.getObject();
                VertexRelation highestRelation = VertexRelation.IN_NO_PAG;
                
                for (InferenceProteinGroup mainPAG : piaModeller.getProteinModeller().getInferredProteins()) {
                    // check, if it is in any PAG at all
                    if (highestRelation.equals(VertexRelation.IN_NO_PAG)) {
                        if (isObjectInPAG(objElement, mainPAG)) {
                            highestRelation = VertexRelation.IN_UNRELATED_PAG;
                        }
                        for (InferenceProteinGroup subPAG : mainPAG.getSubGroups()) {
                            if (isObjectInPAG(objElement, subPAG)) {
                                highestRelation = VertexRelation.IN_UNRELATED_PAG;
                            }
                        }
                    } else {
                        break;
                    }
                }
                
                relations.put(relatedVertex, highestRelation);
            }
        }
        
        return relations;
    }
    
    
    /**
     * gets the relation of the given proteinVertex to the other given vertex
     * 
     * @param proteinVertex
     * @param relatedVertex
     * @return
     */
    protected VertexRelation getProteinsRelation(VertexObject proteinVertex, VertexObject relatedVertex) {
        Map<VertexObject, VertexRelation> relationsMap = proteinRelationMaps.get(proteinVertex);
        if (relationsMap == null) {
            relationsMap = createProteinsRelationsMap(proteinVertex);
            proteinRelationMaps.put(proteinVertex, relationsMap);
        }
        
        if (relationsMap != null) {
            return relationsMap.get(relatedVertex);
        } else {
            return null;
        }
    }
    
    
    /**
     * Returns the protein ambiguity group of the given protein. This is only
     * possible, if the inference was run.
     * 
     * @return the PAG or null, if the protein is not in a reported PAG
     */
    private InferenceProteinGroup getProteinsPAG(IntermediateProtein protein) {
        List<InferenceProteinGroup> pags = piaModeller.getProteinModeller().getInferredProteins();
        if (pags == null) {
            return null;
        }
        
        for (InferenceProteinGroup pag : piaModeller.getProteinModeller().getInferredProteins()) {
            if (pag.getProteins().contains(protein))  {
                return pag;
            }
            
            for (InferenceProteinGroup subPAG : pag.getSubGroups()) {
                if (subPAG.getProteins().contains(protein)) {
                    return subPAG;
                }
            }
        }
        
        return null;
    }
    
    
    /**
     * Checks for a given object whether it is active in the given inferred
     * protein group.
     * 
     * @param objElement
     * @param pag
     * @return
     */
    private boolean isObjectInPAG(Object objElement, InferenceProteinGroup pag) {
        if (objElement instanceof IntermediateProtein) {
            // check whether protein is in the PAG
            if (pag.getProteins().contains(objElement)) {
                return true;
            }
        } else if (objElement instanceof IntermediatePeptide) {
            // check whether peptide is in the PAG
            if (pag.getPeptides().contains(objElement)) {
                return true;
            }
        } else if (objElement instanceof IntermediatePeptideSpectrumMatch) {
            // check whether PSM is in the PAG
            for (IntermediatePeptide pep : pag.getPeptides()) {
                if (pep.getPeptideSpectrumMatches().contains(objElement)) {
                    return true;
                }
            }
        } else if (objElement instanceof Collection<?>) {
            // return true, if at least one if the objects in the collection is in the PAG
            Iterator<?> iter = ((Collection<?>)objElement).iterator();
            while (iter.hasNext()) {
                if (isObjectInPAG(iter.next(), pag)) {
                    return true;
                }
            }
        }
        
        return false;
    }
}