package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.util.Tuple;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.utils.CollectionUtils;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.TableContentType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Retrieve details for chromatogram table.
 * <p/>
 * User: rwang
 * Date: 30-Aug-2010
 * Time: 17:48:13
 */
public class RetrieveChromatogramTableTask extends AbstractDataAccessTask<Void, Tuple<TableContentType, List<Object>>> {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveChromatogramTableTask.class);

    /**
     * the size of each read iteration, for example: return every 100 spectra
     */
    private static final int DEFAULT_ITERATION_SIZE = 100;
    /**
     * the default task title
     */
    private static final String DEFAULT_TASK_TITLE = "Loading Chromatogram";
    /**
     * the default task description
     */
    private static final String DEFAULT_TASK_DESCRIPTION = "Loading Chromatogram";
    /**
     * the start index
     */
    private int start;
    /**
     * the number of entries to retrieve
     */
    private int size;

    /**
     * Retrieve all the identifications.
     *
     * @param controller DataAccessController
     * @throws uk.ac.ebi.pride.utilities.data.controller.DataAccessException
     *          thrown when there is error while reading the data.
     */
    public RetrieveChromatogramTableTask(DataAccessController controller) throws DataAccessException {
        this(controller, 0, controller.getNumberOfProteins());
    }

    /**
     * Retrieve a subset of identifications using the default iteration size.
     *
     * @param controller DataAccessController
     * @param start      the start index of the identifications.
     */
    public RetrieveChromatogramTableTask(DataAccessController controller, int start) {
        this(controller, start, DEFAULT_ITERATION_SIZE);
    }

    /**
     * Retrieve a subset of identifications.
     *
     * @param controller DataAccessController
     * @param start      the start index of the identifications.
     * @param size       the total size of the identifications to retrieve.
     */
    public RetrieveChromatogramTableTask(DataAccessController controller,
                                         int start,
                                         int size) {
        super(controller);
        this.start = start;
        this.size = size;
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected Void retrieve() throws Exception {

        try {
            Collection<Comparable> chromatogramIds = controller.getChromatogramIds();

            int chromaSize = chromatogramIds.size();
            if (start >= 0 && start < chromaSize && size > 0) {
                int stop = start + size;
                stop = stop > chromaSize ? chromaSize : stop;

                for (int i = start; i < stop; i++) {

                    List<Object> content = new ArrayList<>();
                    // spectrum id
                    Comparable chromaId = CollectionUtils.getElement(chromatogramIds, i);
                    content.add(chromaId);
                    publish(new Tuple<>(TableContentType.CHROMATOGRAM, content));

                    checkInterruption();
                }
            }
        } catch (DataAccessException dex) {
            String msg = "Failed to retrieve chromatogram related data";
            logger.error(msg, dex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));
        }

        return null;
    }
}

