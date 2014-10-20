package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import uk.ac.ebi.pride.toolsuite.gui.component.table.TableDataRetriever;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProteinTableRow;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.util.Tuple;

import java.util.Collection;
import java.util.List;

/**
 * User: rwang
 * Date: 16/08/2011
 * Time: 16:24
 */
public class RetrieveQuantProteinTableTask extends AbstractDataAccessTask<Void, Tuple<TableContentType, Object>> {

    private static final String DEFAULT_TASK_NAME = "Updating Protein Table";

    private static final String DEFAULT_TASK_DESC = "Updating Protein Table";

    private int referenceSampleIndex;

    public RetrieveQuantProteinTableTask(DataAccessController controller, int referenceSampleIndex) {
        super(controller);
        this.setName(DEFAULT_TASK_NAME);
        this.setDescription(DEFAULT_TASK_DESC);
        this.controller = controller;
        this.referenceSampleIndex = referenceSampleIndex;
    }

    @Override
    protected Void retrieve() throws Exception {
        // get list of protein ids
        Collection<Comparable> identIds = controller.getProteinIds();

        // get new headers
        // protein quantitative table header
        if(!controller.getType().equals(DataAccessController.Type.MZTAB)){
            List<Object> proteinQuantHeaders = TableDataRetriever.getProteinQuantTableHeaders(controller, referenceSampleIndex);
            publish(new Tuple<TableContentType, Object>(TableContentType.PROTEIN_QUANTITATION_HEADER, proteinQuantHeaders));
        }


        // get each row
        for (Comparable identId : identIds) {
            // get and publish protein related details
            ProteinTableRow proteinTableRow = TableDataRetriever.getProteinTableRow(controller, identId, null);
            // get and publish quantitative data
            if(!controller.getType().equals(DataAccessController.Type.MZTAB)){
                List<Object> identQuantContent = TableDataRetriever.getProteinQuantTableRow(controller, identId, referenceSampleIndex);
                proteinTableRow.addQuantifications(identQuantContent);
            }else{
                List<Object> identQuantContent = TableDataRetriever.getProteinQuantTableRow(controller, identId);
                proteinTableRow.addQuantifications(identQuantContent);
            }

            // check for interruption
            checkInterruption();

            publish(new Tuple<TableContentType, Object>(TableContentType.PROTEIN_QUANTITATION, proteinTableRow));
        }

        return null;
    }
}
