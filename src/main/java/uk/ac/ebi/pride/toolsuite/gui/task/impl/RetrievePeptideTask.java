package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;

/**
 * Retrieve a peptide object using identification id and peptide id
 *
 * User: rwang
 * Date: 08-Sep-2010
 * Time: 15:13:56
 */
public class RetrievePeptideTask extends AbstractDataAccessTask<Peptide, String> {
    private static final Logger logger = LoggerFactory.getLogger(RetrievePeptideTask.class);

    private final Comparable identId;
    private final Comparable peptideId;

    public RetrievePeptideTask(DataAccessController controller, Comparable identId, Comparable peptideId) {
        super(controller);
        this.identId = identId;
        this.peptideId = peptideId;
        this.setName("Loading Spectrum");
        this.setDescription("Loading Spectrum [ID: " + peptideId + "]");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Peptide retrieve() throws Exception {
        Peptide peptide = null;
        try {
            peptide = controller.getPeptideByIndex(identId, peptideId);

            checkInterruption();
        } catch(DataAccessException dex) {
            String msg = "Failed to retrieve peptide";
            logger.error(msg, dex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));
        }

        return peptide;
    }
}
