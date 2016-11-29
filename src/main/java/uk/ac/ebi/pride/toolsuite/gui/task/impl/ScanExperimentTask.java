package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;
import uk.ac.ebi.pride.toolsuite.gui.component.report.SummaryReportMessage;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableDataRetriever;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideTableRow;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProteinTableRow;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.toolsuite.gui.event.ProcessingDataSourceEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.SummaryReportEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.SortProteinTableEvent;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.Modification;
import uk.ac.ebi.pride.utilities.data.core.Protein;
import uk.ac.ebi.pride.utilities.data.core.ProteinGroup;
import uk.ac.ebi.pride.utilities.util.Tuple;

import java.util.*;

/**
 * Scan experiment for all the data related to identification, peptide and quantitation
 * <p/>
 * @author rwang
 * @author ypriverol
 *
 * Date: 14-Sep-2010
 * Time: 11:34:33
 */
public class ScanExperimentTask extends AbstractDataAccessTask<Void, Tuple<TableContentType, Object>> {
    private final static Logger logger = LoggerFactory.getLogger(ScanExperimentTask.class);

    /**
     * the default task title
     */
    private static final String DEFAULT_TASK_TITLE = "loading experiment content";
    /**
     * the default task description
     */
    private static final String DEFAULT_TASK_DESCRIPTION = "Loading experiment content";

    private int missingSpectrumLinks = 0;

    private final Map<String, String> ptmMap = new HashMap<>();


    /**
     * Retrieve a subset of identifications.
     *
     * @param controller DataAccessController
     */
    public ScanExperimentTask(DataAccessController controller) {
        super(controller);
        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }


    @Override
    protected Void retrieve() throws Exception {

        try {
            EventBus.publish(new ProcessingDataSourceEvent<>(controller, ProcessingDataSourceEvent.Status.IDENTIFICATION_READING, controller));
            // get quant headers
            boolean hasQuantData = controller.hasQuantData();
            if (hasQuantData) {
                getQuantHeaders();
            }
            long date = System.currentTimeMillis();
            // retrieve protein group ids
            Collection<Comparable> proteinGroupIds = getProteinGroupIds();

            // retrieve protein, peptide and PTM details
            for (Comparable proteinGroupId : proteinGroupIds) {
                // retrieve protein id belongs to the protein group
                Collection<Comparable> proteinIds = getProteinIds(proteinGroupId);

                for (Comparable proteinId : proteinIds) {
                    // get and publish protein related details
                    ProteinTableRow proteinData = getProteinData(proteinId, proteinGroupId);


                    if (hasQuantData) {
                        // get and publish quantitative data
                        getQuantData(proteinId, proteinData);
                    }

                    // get and publish peptide related details
                    Collection<Comparable> ids = controller.getPeptideIds(proteinId);
                    if (ids != null) {
                        for (Comparable peptideId : ids) {
                            getPeptideData(proteinId, peptideId);
//                            if (!peptideData.isSpectrumInformation()) {
//                                missingSpectrumLinks++;
//                            }

                            sendPTMNotification(proteinId, peptideId);
                        }
                    }

                    checkInterruption();
                }
            }

            missingSpectrumLinks = controller.getNumberOfMissingSpectra();
            EventBus.publish(new ProcessingDataSourceEvent<>(controller, ProcessingDataSourceEvent.Status.IDENTIFICATION_READING, controller));
            if (controller.hasProteinAmbiguityGroup())
                EventBus.publish(new SortProteinTableEvent(controller, SortProteinTableEvent.Type.ENABLE_SORT));

            if (missingSpectrumLinks > 0) {
                EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(SummaryReportMessage.Type.WARNING, "Missing spectra [" + missingSpectrumLinks + "]", "The number of peptides without spectrum links")));
            }
            logger.debug("LOAD INFORMATION | All the proteins has been shown in: |{}| milliseconds", System.currentTimeMillis() - date);
        } catch (DataAccessException dex) {
            String msg = "Failed to retrieve protein and peptide related data";
            logger.error(msg, dex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, dex));
        }

        return null;
    }

    private Collection<Comparable> getProteinGroupIds() {
        Collection<Comparable> proteinGroupIds;
        if (controller.hasProteinAmbiguityGroup()) {
            proteinGroupIds = controller.getProteinAmbiguityGroupIds();
        } else {
            proteinGroupIds = new HashSet<>();
            proteinGroupIds.add(null);
        }
        return proteinGroupIds;
    }

    private Collection<Comparable> getProteinIds(Comparable proteinGroupId) {
        if (proteinGroupId == null) {
            return controller.getProteinIds();
        } else {
            ProteinGroup proteinGroup = controller.getProteinAmbiguityGroupById(proteinGroupId);
            Collection<Comparable> proteinIds = new LinkedHashSet<>();
            List<Protein> proteins = proteinGroup.getProteinDetectionHypothesis();
            for (Protein protein : proteins) {
                proteinIds.add(protein.getId());
            }

            return proteinIds;
        }
    }

    private void sendPTMNotification(Comparable proteinId, Comparable peptideId) {
        Collection<Modification> mods = controller.getPTMs(proteinId, peptideId);
        if (mods != null) {
            for (Modification mod : mods) {
                String accession = (mod.getId() != null) ? mod.getId().toString() : null;
                String name = mod.getName();
                if (!ptmMap.containsKey(accession)) {
                    accession = (accession == null || accession.length() ==0)?"Unknown":accession;
                    EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(SummaryReportMessage.Type.INFO, "Mod: " + accession,
                            "Modification found: [" + accession + "]\t" + name)));
                    ptmMap.put(mod.getId().toString(), mod.getName());
                }
            }
        }
    }

    private ProteinTableRow getProteinData(Comparable proteinId, Comparable proteinGroupId) {
        logger.debug("Scan protein details: {}", proteinId);
        ProteinTableRow proteinTableRow = TableDataRetriever.getProteinTableRow(controller, proteinId, proteinGroupId);
        publish(new Tuple<TableContentType, Object>(TableContentType.PROTEIN, proteinTableRow));
        return proteinTableRow;
    }

    private PeptideTableRow getPeptideData(Comparable identId, Comparable peptideId) {
        logger.debug("Scan peptide details: {}-{}", identId, peptideId);
        PeptideTableRow peptideTableRow = TableDataRetriever.getPeptideTableRow(controller, identId, peptideId);
        publish(new Tuple<TableContentType, Object>(TableContentType.PEPTIDE, peptideTableRow));
        return peptideTableRow;
    }

    private void getQuantData(Comparable identId, ProteinTableRow proteinTableRow) {
        logger.debug("Scan quantification details: {}", identId);
        if(!controller.getType().equals(DataAccessController.Type.MZTAB)){
            List<Object> identQuantContent = TableDataRetriever.getProteinQuantTableRow(controller, identId, -1);
            proteinTableRow.addQuantifications(identQuantContent);
        }else{
            List<Object> identQuantContent = TableDataRetriever.getProteinQuantTableRow(controller, identId);
            proteinTableRow.addQuantifications(identQuantContent);
        }
        publish(new Tuple<TableContentType, Object>(TableContentType.PROTEIN_QUANTITATION, proteinTableRow));
    }

    private void getQuantHeaders() {
        logger.debug("Scan quantification table header");
        // protein quantitative table header
        if(!controller.getType().equals(DataAccessController.Type.MZTAB)){
            List<Object> proteinQuantHeaders = TableDataRetriever.getProteinQuantTableHeaders(controller, -1);
            publish(new Tuple<TableContentType, Object>(TableContentType.PROTEIN_QUANTITATION_HEADER, proteinQuantHeaders));
        }
    }
}
