package uk.ac.ebi.pride.toolsuite.gui.task.impl;


import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.core.Protein;
import uk.ac.ebi.pride.utilities.data.core.ProteinGroup;



/**
 * Retrieve protein groups from DataAccessController
 * @author ypriverol
 */
public class LoadProteinGroupNodeTreeTask extends AbstractDataAccessTask<Void, Protein> {

    private static final String DEFAULT_TASK_TITLE = "Loading protein group";
    private static final String DEFAULT_TASK_DESCRIPTION = "Loading protein group";

    Comparable proteinId;
    Comparable proteinGroupId;

    public LoadProteinGroupNodeTreeTask(DataAccessController controller,
                                        Comparable proteinId,
                                        Comparable proteinGroupId) {
        super(controller);
        this.proteinId  = proteinId;
        this.proteinGroupId = proteinGroupId;

        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void retrieve() throws Exception {
        if(proteinGroupId != null){
            ProteinGroup proteinGroup = controller.getProteinAmbiguityGroupById(proteinGroupId);
            for(Protein protein: proteinGroup.getProteinDetectionHypothesis()){
                publish(protein);

                checkInterruption();
            }
        }
        return null;
    }


}
