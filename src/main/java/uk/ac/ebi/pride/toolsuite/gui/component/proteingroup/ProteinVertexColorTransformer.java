package uk.ac.ebi.pride.toolsuite.gui.component.proteingroup;

import java.awt.Color;
import java.awt.Paint;
import java.util.Collection;

import org.apache.commons.collections15.Transformer;

import de.mpc.pia.core.intermediate.IntermediateGroup;
import de.mpc.pia.core.intermediate.IntermediatePeptide;
import de.mpc.pia.core.intermediate.IntermediatePeptideSpectrumMatch;
import de.mpc.pia.core.intermediate.IntermediateProtein;


/**
 * Transformer for the colors of the vertexObjects
 * 
 * @author julianu
 *
 */
public class ProteinVertexColorTransformer
        implements Transformer<VertexObject, Paint> {
    
    private static final Color FADED_COLOR = Color.LIGHT_GRAY;
    
    private static final Color GROUP_COLOR = new Color(0x000080);
    
    private static final Color PROTEIN_COLOR = new Color(0x00c000);
    private static final Color PROTEIN_GROUP_COLOR = new Color(0x00c080);
    
    private static final Color PEPTIDE_COLOR = new Color(0xffa500);
    private static final Color PEPTIDE_GROUP_COLOR = new Color(0xbf7c00);
    
    private static final Color PSM_COLOR = new Color(0x87ceeb);
    
    
    /**
     * Constructor
     */
    public ProteinVertexColorTransformer() {
        
    }
    
    
    @Override
    public Paint transform(VertexObject vertex) {
        // TODO Auto-generated method stub
        Object vObject = vertex.getObject();
        
        if (vObject instanceof Collection<?>) {
            vObject = ((Collection<?>)vertex.getObject()).iterator().next();
            
            if (vObject instanceof IntermediatePeptide)  {
                return PEPTIDE_GROUP_COLOR;
            } else if (vObject instanceof IntermediateProtein) {
                return PROTEIN_GROUP_COLOR;
            }
        } else {
            if (vObject instanceof IntermediateGroup) {
                return GROUP_COLOR;
            } else if (vObject instanceof IntermediatePeptide)  {
                return PEPTIDE_COLOR;
            } else if (vObject instanceof IntermediateProtein) {
                return PROTEIN_COLOR;
            } else if (vObject instanceof IntermediatePeptideSpectrumMatch) {
                return PSM_COLOR;
            }
        }
        
        return FADED_COLOR;
    }
}