package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.Chromatogram;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;

/**
 * Retrieve chromatogram
 *
 * User: rwang
 * Date: 10/06/11
 * Time: 15:54
 */
public class RetrieveChromatogramTask extends AbstractDataAccessTask<Chromatogram, Void> {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveEntryTask.class);
    private static final String DEFAULT_TASK_TITLE = "Retrieving chromatogram";
    private static final String DEFAULT_TASK_DESCRIPTION = "Retrieving chromatogram";

    private Comparable chromaId;

    public RetrieveChromatogramTask(DataAccessController controller, Comparable chromaId) {
        super(controller);
        this.chromaId = chromaId;

        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Chromatogram retrieve() throws Exception {
        Chromatogram result = null;

        try {
            checkInterruption();

            result = controller.getChromatogramById(chromaId);
        } catch (DataAccessException dex) {
            String msg = "Failed to retrieve data entry from data source";
            logger.error(msg, dex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));
        }

        return result;
    }
}
