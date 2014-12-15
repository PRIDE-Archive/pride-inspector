package uk.ac.ebi.pride.toolsuite.gui.component.proteingroup;

import java.awt.Color;
import java.awt.Paint;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.collections15.Transformer;

import uk.ac.ebi.pride.toolsuite.gui.component.proteingroup.ProteinVisualizationGraphHandler.VertexRelation;
import edu.uci.ics.jung.visualization.picking.PickedState;
import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediateGroup;
import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediatePeptide;
import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediatePeptideSpectrumMatch;
import uk.ac.ebi.pride.utilities.pia.intermediate.IntermediateProtein;


/**
 * Transformer for the colors of the vertexObjects
 * 
 * @author julianu
 *
 */
public class ProteinVertexFillColorTransformer
        implements Transformer<VertexObject, Paint> {
    
    /** the graph handler which holds also information about the inferred proteins */
    private ProteinVisualizationGraphHandler graphHandler;
    
    /** the currently picked protein */
    private PickedState<VertexObject> pickedProtein;
    
    // some color values
    protected static final Color FADED_COLOR = Color.WHITE;
    
    protected static final Color GROUP_COLOR = new Color(0x000080);
    
    protected static final Color PROTEIN_COLOR = new Color(0x00c000);
    protected static final Color PROTEIN_COLOR_FAINT = new Color(0xacffac);
    
    protected static final Color PEPTIDE_COLOR = new Color(0xffa500);
    protected static final Color PEPTIDE_COLOR_FAINT = new Color(0xffd280);
    
    protected static final Color PSM_COLOR = new Color(0x87ceeb);
    protected static final Color PSM_COLOR_FAINT = new Color(0xc1edff);
    
    
    /**
     * Constructor
     */
    public ProteinVertexFillColorTransformer(ProteinVisualizationGraphHandler graphHandler, PickedState<VertexObject> pickedProtein) {
        this.graphHandler = graphHandler;
        this.pickedProtein = pickedProtein;
    }
    
    
    @Override
    public Paint transform(VertexObject vertex) {
        Object vObject = vertex.getObject();
        VertexObject proteinVertex = pickedProtein.getPicked().iterator().next();
        
        if (vObject instanceof IntermediateGroup) {
            return GROUP_COLOR;
        }
        
        if (vObject instanceof Collection<?>) {
            vObject = ((Collection<?>)vObject).iterator().next();
        }
        
        VertexRelation relation = graphHandler.getProteinsRelation(proteinVertex, vertex);
        
        switch (relation) {
        case IN_SAME_PAG:
            if (vObject instanceof IntermediatePeptide)  {
                return PEPTIDE_COLOR;
            } else if (vObject instanceof IntermediateProtein) {
                return PROTEIN_COLOR;
            } else if (vObject instanceof IntermediatePeptideSpectrumMatch) {
                return PSM_COLOR;
            }
            
        case IN_SUB_PAG:
        case IN_SUPER_PAG:
        case IN_PARALLEL_PAG:
            if (vObject instanceof IntermediatePeptide)  {
                return PEPTIDE_COLOR_FAINT;
            } else if (vObject instanceof IntermediateProtein) {
                return PROTEIN_COLOR_FAINT;
            } else if (vObject instanceof IntermediatePeptideSpectrumMatch) {
                return PSM_COLOR_FAINT;
            }
            
        case IN_NO_PAG:
        case IN_UNRELATED_PAG:
        default:
            return FADED_COLOR;
        }
    }
}