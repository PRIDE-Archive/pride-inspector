package uk.ac.ebi.pride.toolsuite.gui.component.proteingroup;

import java.awt.Color;
import java.awt.Paint;
import java.util.Collection;

import org.apache.commons.collections15.Transformer;

import uk.ac.ebi.pride.toolsuite.gui.component.proteingroup.ProteinVisualizationGraphHandler.VertexRelation;
import edu.uci.ics.jung.visualization.picking.PickedState;
import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediateGroup;
import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediatePeptide;
import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediatePeptideSpectrumMatch;
import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediateProtein;


/**
 * Transformer for the boder colors of the vertexObjects
 * 
 * @author julianu
 *
 */
public class ProteinVertexBorderColorTransformer
        implements Transformer<VertexObject, Paint> {
    
    /** the graph handler which holds also information about the inferred proteins */
    private ProteinVisualizationGraphHandler graphHandler;
    
    /** the currently selected vertex */
    private PickedState<VertexObject> pickedState;
    
    /** the currently picked protein */
    private PickedState<VertexObject> pickedProtein;
    
    
    // some border colors
    private static final Color DEFAULT_BORDER_COLOR = Color.BLACK;
    
    private static final Color SELECTED_BORDER_COLOR = new Color(0x00FFFF);
    
    private static final Color SELECTED_PROTEIN_BORDER_COLOR = Color.RED;
    
    
    /**
     * Constructor
     */
    public ProteinVertexBorderColorTransformer(ProteinVisualizationGraphHandler graphHandler, PickedState<VertexObject> pickedState, PickedState<VertexObject> pickedProtein) {
        this.graphHandler = graphHandler;
        this.pickedState = pickedState;
        this.pickedProtein = pickedProtein;
    }
    
    
    @Override
    public Paint transform(VertexObject vertex) {
        Object vObject = vertex.getObject();
        VertexObject proteinVertex = pickedProtein.getPicked().iterator().next();
        
        if (pickedProtein.isPicked(vertex)) {
            return SELECTED_PROTEIN_BORDER_COLOR;
        } else if (pickedState.isPicked(vertex)) {
            return SELECTED_BORDER_COLOR;
        }
        
        if (vObject instanceof IntermediateGroup) {
            return DEFAULT_BORDER_COLOR;
        }
        
        if (vObject instanceof Collection<?>) {
            vObject = ((Collection<?>)vertex.getObject()).iterator().next();
        }
        
        VertexRelation relation = graphHandler.getProteinsRelation(proteinVertex, vertex);
        
        switch (relation) {
        case IN_SAME_PAG:
        case IN_PARALLEL_PAG:
            return DEFAULT_BORDER_COLOR;
            
        case IN_SUB_PAG:
            if (vObject instanceof IntermediatePeptide)  {
                return ProteinVertexFillColorTransformer.PEPTIDE_COLOR_FAINT;
            } else if (vObject instanceof IntermediateProtein) {
                return ProteinVertexFillColorTransformer.PROTEIN_COLOR_FAINT;
            } else if (vObject instanceof IntermediatePeptideSpectrumMatch) {
                return ProteinVertexFillColorTransformer.PSM_COLOR_FAINT;
            }
            
        case IN_SUPER_PAG:
        case IN_UNRELATED_PAG:
            if (vObject instanceof IntermediatePeptideSpectrumMatch)  {
                return ProteinVertexFillColorTransformer.PEPTIDE_COLOR;
            } else if (vObject instanceof IntermediateProtein) {
                return ProteinVertexFillColorTransformer.PROTEIN_COLOR;
            } else if (vObject instanceof IntermediatePeptideSpectrumMatch) {
                return ProteinVertexFillColorTransformer.PSM_COLOR;
            }
            
        case IN_NO_PAG:
        default:
            return DEFAULT_BORDER_COLOR;
        }
    }
}