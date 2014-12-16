package uk.ac.ebi.pride.toolsuite.gui.component.proteingroup;

import java.awt.BasicStroke;
import java.awt.Stroke;
import java.util.Collection;

import org.apache.commons.collections15.Transformer;

import uk.ac.ebi.pride.toolsuite.gui.component.proteingroup.ProteinVisualizationGraphHandler.VertexRelation;
import edu.uci.ics.jung.visualization.picking.PickedState;
import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediateGroup;


/**
 * Transformer for the colors of the vertexObjects
 * 
 * @author julianu
 *
 */
public class ProteinVertexStrokeTransformer
        implements Transformer<VertexObject, Stroke> {
    
    /** the graph handler which holds also information about the inferred proteins */
    private ProteinVisualizationGraphHandler graphHandler;
    
    /** the picking state of the graph */
    private PickedState<VertexObject> pickedState;
    
    /** the picked protein of the graph */
    private PickedState<VertexObject> pickedProtein;
    
    
    private final BasicStroke thinStroke = new BasicStroke(1);
    private final BasicStroke thickStroke = new BasicStroke(2);
    
    
    /**
     * Constructor
     */
    public ProteinVertexStrokeTransformer(ProteinVisualizationGraphHandler graphHandler, PickedState<VertexObject> pickedState, PickedState<VertexObject> pickedProtein) {
        this.graphHandler = graphHandler;
        this.pickedState = pickedState;
        this.pickedProtein = pickedProtein;
    }
    
    
    
    @Override
    public Stroke transform(VertexObject vertex) {
        // the picked object has always a thick border
        if (pickedState.isPicked(vertex) || pickedProtein.isPicked(vertex)) {
            return thickStroke;
        }
        
        Object vObject = vertex.getObject();
        VertexObject proteinVertex = pickedProtein.getPicked().iterator().next();
        
        if (vObject instanceof IntermediateGroup) {
            return thinStroke;
        }
        
        if (vObject instanceof Collection<?>) {
            vObject = ((Collection<?>)vObject).iterator().next();
        }
        
        VertexRelation relation = graphHandler.getProteinsRelation(proteinVertex, vertex);
        
        switch (relation) {
        case IN_PARALLEL_PAG:
        case IN_SAME_PAG:
        case IN_SUB_PAG:
            return thinStroke;
            
        case IN_SUPER_PAG:
        case IN_UNRELATED_PAG:
            return thickStroke;
            
        case IN_NO_PAG:
        default:
            return thinStroke;
        }
    }
}