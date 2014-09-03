package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.Protein;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;

/**
 * Get protein identification by id
 *
 * User: rwang
 * Date: 10/06/11
 * Time: 16:08
 */
public class RetrieveIdentificationTask extends AbstractDataAccessTask<Protein, Void> {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveIdentificationTask.class);

    private Comparable identId;

    public RetrieveIdentificationTask(DataAccessController controller, Comparable identId) {
        super(controller);
        this.identId = identId;
    }

    @Override
    protected Protein retrieve() throws Exception {
        Protein result = null;

        try {
                result = controller.getProteinById(identId);
        } catch (DataAccessException dex) {
            String msg = "Failed to retrieve data entry from data source";
            logger.error(msg, dex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));
        }

        return result;
    }
}
