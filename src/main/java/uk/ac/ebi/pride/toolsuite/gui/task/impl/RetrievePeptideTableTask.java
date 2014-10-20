package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableDataRetriever;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideTableRow;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.utils.CollectionUtils;
import uk.ac.ebi.pride.utilities.util.Tuple;

import java.util.Collection;

/**
 * User: rwang, ypriverol
 * Date: 08-Sep-2010
 * Time: 10:25:11
 */
public class RetrievePeptideTableTask extends AbstractDataAccessTask<Void, Tuple<TableContentType, Object>> {
    private static final Logger logger = LoggerFactory.getLogger(RetrievePeptideTableTask.class);

    /**
     * the size of each read iteration, for example: return every 100 spectra
     */
    private static final int DEFAULT_ITERATION_SIZE = 100;
    /**
     * the default task title
     */
    private static final String DEFAULT_TASK_TITLE = "Loading Peptides";
    /**
     * the default task description
     */
    private static final String DEFAULT_TASK_DESCRIPTION = "Loading Peptides";
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
     * @throws uk.ac.ebi.pride.utilities.data.controller.DataAccessException thrown when there is error while reading the data.
     */
    public RetrievePeptideTableTask(DataAccessController controller) throws DataAccessException {
        this(controller, 0, controller.getNumberOfProteins());
    }

    public RetrievePeptideTableTask(DataAccessController controller, Comparable identId) throws DataAccessException {
        this(controller, controller.indexOfProtein(identId), 1);
    }

    /**
     * Retrieve a subset of identifications using the default iteration size.
     *
     * @param controller DataAccessController
     * @param start      the start index of the identifications.
     */
    public RetrievePeptideTableTask(DataAccessController controller, int start) {
        this(controller, start, DEFAULT_ITERATION_SIZE);
    }

    /**
     * Retrieve a subset of identifications.
     *
     * @param controller DataAccessController
     * @param start      the start index of the identifications.
     * @param size       the total size of the identifications to retrieve.
     */
    public RetrievePeptideTableTask(DataAccessController controller,
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
            Collection<Comparable> identIds = controller.getProteinIds();

            int identSize = identIds.size();
            if (start >= 0 && start < identSize && size > 0) {
                int stop = start + size;
                stop = stop > identSize ? identSize : stop;

                for (int i = start; i < stop; i++) {
                    Comparable identId = CollectionUtils.getElement(identIds, i);
                    Collection<Comparable> ids = controller.getPeptideIds(identId);
                    if (ids != null) {
                        for (Comparable peptideId : ids) {
                            PeptideTableRow content = TableDataRetriever.getPeptideTableRow(controller, identId, peptideId);
                            publish(new Tuple<TableContentType, Object>(TableContentType.PEPTIDE, content));
                        }
                    }

                    checkInterruption();
                }

            }

        } catch (DataAccessException dex) {
            String msg = "Failed to retrieve peptide related data";
            logger.error(msg, dex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));
        }

        return null;
    }
}
