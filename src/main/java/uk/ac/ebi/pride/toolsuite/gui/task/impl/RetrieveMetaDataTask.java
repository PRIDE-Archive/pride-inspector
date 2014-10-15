package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.access.GeneralMetaDataGroup;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.ExperimentMetaData;
import uk.ac.ebi.pride.utilities.data.core.IdentificationMetaData;
import uk.ac.ebi.pride.utilities.data.core.MzGraphMetaData;

/**
 * Retrieving MetaData information from the given data access controller.
 * <p/>
 * User: rwang
 * Date: 22-Oct-2010
 * Time: 12:18:34
 */
public class RetrieveMetaDataTask extends AbstractDataAccessTask<GeneralMetaDataGroup, Void> {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveEntryTask.class);
    /**
     * the default task title
     */
    private static final String DEFAULT_TASK_TITLE = "Loading Metadata";
    /**
     * the default task description
     */
    private static final String DEFAULT_TASK_DESCRIPTION = "Loading Experiment Metadata";

    public RetrieveMetaDataTask(DataAccessController controller) {
        super(controller);
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected GeneralMetaDataGroup retrieve() throws Exception {
        GeneralMetaDataGroup groupMetaData = null;

        try {
            if (controller.hasMetaDataInformation()) {
                ExperimentMetaData metaData = controller.getExperimentMetaData();
                MzGraphMetaData mzGraphMetaData = null;
                IdentificationMetaData identificationMetaData = null;
                if (controller.hasSpectrum()) {
                    mzGraphMetaData = controller.getMzGraphMetaData();
                }

                if (controller.hasProtein()) {
                    identificationMetaData = controller.getIdentificationMetaData();
                }

                groupMetaData = new GeneralMetaDataGroup(identificationMetaData, metaData, mzGraphMetaData);
            }
        } catch (DataAccessException dex) {
            String msg = "Failed to retrieve meta data from data source";
            logger.error(msg, dex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));
        }
        return groupMetaData;
    }
}
