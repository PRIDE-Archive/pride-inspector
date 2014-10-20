package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.Modification;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorCacheManager;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;
import uk.ac.ebi.pride.toolsuite.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.toolsuite.gui.component.sequence.PTMAnnotation;
import uk.ac.ebi.pride.toolsuite.gui.component.sequence.PeptideAnnotation;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;
import uk.ac.ebi.pride.tools.utils.AccessionResolver;

import java.util.Collection;

/**
 * Retrieve protein detail object, this is mainly used to visualize protein sequence
 * <p/>
 * User: rwang
 * Date: 14/06/11
 * Time: 15:08
 */
public class RetrieveProteinDetailModelTask extends AbstractDataAccessTask<AnnotatedProtein, Void> {
    private static final Logger logger = LoggerFactory.getLogger(RetrieveProteinDetailModelTask.class);
    private static final String DEFAULT_TASK_TITLE = "Retrieving protein details";
    private static final String DEFAULT_TASK_DESCRIPTION = "Retrieving protein details";
    /**
     * protein identification id
     */
    private Comparable identId;
    /**
     * peptide identification id
     */
    private Comparable peptideId;

    /**
     * Constructor
     *
     * @param controller data source
     * @param identId    protein identification id
     * @param peptideId  peptide identification id
     */
    public RetrieveProteinDetailModelTask(DataAccessController controller,
                                          Comparable identId,
                                          Comparable peptideId) {
        super(controller);
        this.identId = identId;
        this.peptideId = peptideId;

        this.setName(DEFAULT_TASK_TITLE);
        this.setDescription(DEFAULT_TASK_DESCRIPTION);
    }

    @Override
    protected AnnotatedProtein retrieve() throws Exception {
        AnnotatedProtein protein = null;
        try {
            Protein proteinDetails = getExistingProteinDetails();

            checkInterruption();

            if (proteinDetails != null) {
                // create a new annotated protein
                protein = new AnnotatedProtein(proteinDetails);
                // get peptide annotations
                addPeptideAnnotations(protein);
            }
        } catch (DataAccessException ex) {
            String msg = "Failed to get protein or peptide related details";
            logger.error(msg, ex);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, ex));
        }
        return protein;
    }

    /**
     * Get the existing protein details from application context
     *
     * @return  Protein protein details
     * @throws DataAccessException  data access exception
     */
    private Protein getExistingProteinDetails() throws DataAccessException {
        // get formmatted protein accession
        String protAcc = controller.getProteinAccession(identId);
        String protAccVersion = controller.getProteinAccessionVersion(identId);
        String database = (controller.getSearchDatabase(identId).getName() == null)?"":controller.getSearchDatabase(identId).getName();
        AccessionResolver resolver = new AccessionResolver(protAcc, protAccVersion, database, true);
        String mappedProtAcc = resolver.isValidAccession()? resolver.getAccession() : null;

        // get protein details
        return PrideInspectorCacheManager.getInstance().getProteinDetails(mappedProtAcc);
    }

    /**
     * Add peptide annotations
     *
     * @param protein   protein details
     * @throws uk.ac.ebi.pride.utilities.data.controller.DataAccessException  data access exception
     */
    private void addPeptideAnnotations(AnnotatedProtein protein) throws DataAccessException {
        Collection<Comparable> peptideIds = controller.getPeptideIds(identId);
        for (Comparable id : peptideIds) {
            PeptideAnnotation peptideAnnotation = new PeptideAnnotation();
            peptideAnnotation.setSequence(controller.getPeptideSequence(identId, id));
            peptideAnnotation.setStart(controller.getPeptideSequenceStart(identId, id));
            peptideAnnotation.setEnd(controller.getPeptideSequenceEnd(identId, id));

            // add ptm annotations
            addPTMAnnotations(id, peptideAnnotation);

            // add annotation to protein
            protein.addAnnotation(peptideAnnotation);

            // set selected annotation
            if (id.equals(peptideId)) {
                protein.setSelectedAnnotation(peptideAnnotation);
            }
        }
    }

    /**
     * Add PTM annotations
     *
     * @param id    peptide id
     * @param peptide   peptide annotation
     * @throws uk.ac.ebi.pride.utilities.data.controller.DataAccessException  data access exception
     */
    private void addPTMAnnotations(Comparable id, PeptideAnnotation peptide) throws DataAccessException {
        Collection<Modification> ptms = controller.getPTMs(identId, id);
        for (Modification ptm : ptms) {
            PTMAnnotation ptmAnnotation = new PTMAnnotation();

            // copy all the values from ptm to ptm annotation
            ptmAnnotation.setAccession(ptm.getId().toString());
            ptmAnnotation.setLocation(ptm.getLocation());
            ptmAnnotation.setModDatabaseVersion(ptm.getModDatabaseVersion());
            ptmAnnotation.setName(ptm.getName());
            ptmAnnotation.setAvgMassDeltas(ptm.getAvgMassDelta());
            ptmAnnotation.setMonoMassDeltas(ptm.getMonoisotopicMassDelta());

            peptide.addPtmAnnotation(ptmAnnotation);
        }
    }
}
