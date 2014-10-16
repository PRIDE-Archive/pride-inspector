package uk.ac.ebi.pride.toolsuite.gui.task.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.core.Protein;
import uk.ac.ebi.pride.utilities.data.core.ProteinGroup;



/**
 * Retrieve protein groups from DataAccessController
 * @author ypriverol
 */
public class LoadProteinGroupNodeTreeTask extends TaskAdapter<Void, Protein> {

    DataAccessController controller;
    Comparable proteinId;
    Comparable proteinGroupId;

    private static final Logger logger = LoggerFactory.getLogger(LoadProteinGroupNodeTreeTask.class);

    public LoadProteinGroupNodeTreeTask(DataAccessController controller, Comparable proteinId, Comparable proteinGroupId) {
        this.controller = controller;
        this.proteinId  = proteinId;
        this.proteinGroupId = proteinGroupId;
    }

    @Override
    protected Void doInBackground() throws Exception {
        if(proteinGroupId != null){
            ProteinGroup proteinGroup = controller.getProteinAmbiguityGroupById(proteinGroupId);
            for(Protein protein: proteinGroup.getProteinDetectionHypothesis()){
                publish(protein);
            }
        }
        return null;
    }


}
