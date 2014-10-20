package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.Chromatogram;
import uk.ac.ebi.pride.utilities.data.core.Protein;
import uk.ac.ebi.pride.utilities.data.core.Spectrum;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;

/**
 * User: rwang
 * Date: 16-Apr-2010
 * Time: 14:07:16
 */
public class RetrieveEntryTask<T> extends AbstractDataAccessTask<T, String> {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveEntryTask.class);

    private Comparable id = null;
    private Class<T> classType = null;

    public RetrieveEntryTask(DataAccessController controller, Class<T> classType, Comparable id) {
        super(controller);
        this.id = id;
        this.classType = classType;
        this.setName("Loading " + classType.getSimpleName());
        this.setDescription("Loading " + classType.getSimpleName() + "[ID: " + id + "]");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected T retrieve() throws Exception {
        Object result = null;

        try {
            checkInterruption();

            if (Spectrum.class.equals(classType)) {
                result = controller.getSpectrumById(id);
            } else if (Chromatogram.class.equals(classType)) {
                result = controller.getChromatogramById(id);
            } else if (Protein.class.equals(classType)) {
                result = controller.getProteinById(id);
            }
        } catch (DataAccessException dex) {
            String msg = "Failed to retrieve data entry from data source";
            logger.error(msg, dex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));
        }

        return (T) result;
    }
}
