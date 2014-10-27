package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.util.Tuple;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorCacheManager;
import uk.ac.ebi.pride.toolsuite.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.toolsuite.gui.component.sequence.PeptideAnnotation;
import uk.ac.ebi.pride.toolsuite.gui.component.sequence.PeptideFitState;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.toolsuite.gui.event.container.SortProteinTableEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;
import uk.ac.ebi.pride.tools.protein_details_fetcher.ProteinDetailFetcher;
import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;
import uk.ac.ebi.pride.tools.utils.AccessionResolver;

import java.util.*;

/**
 * Retrieve protein name for a given set of proteins
 * <p/>
 * @author rwang
 * @author ypriverol
 * Date: 16-Sep-2010
 * Time: 15:53:16
 */
public class RetrieveProteinDetailTask extends TaskAdapter<Void, Tuple<TableContentType, Object>> {

    private static final Logger logger = LoggerFactory.getLogger(RetrieveProteinDetailTask.class);

    private static final String DEFAULT_TASK_NAME = "Downloading protein details";

    private static final String DEFAULT_TASK_DESC = "Downloading protein details using web services";

    /**
     * The number of proteins for each batch download
     */
    private static final int MAX_BATCH_DOWNLOAD_SIZE = 10;

    /**
     * data access controller
     */
    private DataAccessController controller;

    /**
     * Fetcher to download protein details
     */
    private ProteinDetailFetcher fetcher;


    /**
     * Constructor
     *
     * @param controller data access controller
     */
    public RetrieveProteinDetailTask(DataAccessController controller) {

        // set name and description
        this.setName(DEFAULT_TASK_NAME);
        this.setDescription(DEFAULT_TASK_DESC);

        this.controller = controller;

        this.fetcher = new ProteinDetailFetcher();
    }

    @Override
    protected Void doInBackground() throws Exception {
        // protein identification id
        Collection<Comparable> protIdentIds = controller.getProteinIds();


        //protein identification id and accession buffer
        Map<Comparable, String> accBuffer = new LinkedHashMap<Comparable, String>();

        // protein map
        Map<String, Protein> proteins = new HashMap<String, Protein>();

        if(controller.hasProteinAmbiguityGroup())
            EventBus.publish(new SortProteinTableEvent(controller, SortProteinTableEvent.Type.DISABLE_SORT));

        try {

            // iterate over each protein
            for (Comparable protIdentId : protIdentIds) {
                // get mapped protein accession
                String protAcc = controller.getProteinAccession(protIdentId);
                String protAccVersion = controller.getProteinAccessionVersion(protIdentId);
                String database = controller.getSearchDatabase(protIdentId).getName();

                try {
                    AccessionResolver resolver = new AccessionResolver(protAcc, protAccVersion, database, true);
                    String mappedProtAcc = resolver.isValidAccession() ? resolver.getAccession() : null;


                    if (mappedProtAcc != null) {
                        // get existing protein details
                        Protein protDetails = PrideInspectorCacheManager.getInstance().getProteinDetails(mappedProtAcc);
                        if (protDetails != null) {
                            proteins.put(mappedProtAcc, protDetails);
                        }

                        accBuffer.put(protIdentId, mappedProtAcc);
                        if (accBuffer.size() == MAX_BATCH_DOWNLOAD_SIZE) {
                            // fetch and publish protein details
                            fetchAndPublish(accBuffer, proteins);

                            // clear accession buffer
                            accBuffer.clear();

                            // clear protein map
                            proteins = new HashMap<String, Protein>();
                        }
                    }
                    // clear protein map
                    proteins = new HashMap<String, Protein>();
                } catch (IllegalArgumentException ex) {
                    Protein protein = new Protein(protAcc);
                    protein.setStatus(Protein.STATUS.UNKNOWN);
                    proteins.put(protAcc, protein);
                }
            }

            // this is important for cancelling
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            if (!accBuffer.isEmpty() || !proteins.isEmpty()) {
                fetchAndPublish(accBuffer, proteins);
            }
        } catch (InterruptedException e) {
            logger.warn("Protein name download has been cancelled");
        }
        if(controller.hasProteinAmbiguityGroup())
            EventBus.publish(new SortProteinTableEvent(controller, SortProteinTableEvent.Type.ENABLE_SORT));
        return null;
    }


    /**
     * Fetch then publish
     *
     * @param accs     protein accessions
     * @param proteins protein map
     * @throws Exception exception while fetching the protein
     */
    private void fetchAndPublish(Map<Comparable, String> accs, Map<String, Protein> proteins) throws Exception {

        Collection<String> accsToFetch = new HashSet<String>(accs.values());

        accsToFetch.removeAll(proteins.keySet());
        // fetch protein details
        Map<String, Protein> results = fetcher.getProteinDetails(accsToFetch);
        // add results to cache
        PrideInspectorCacheManager.getInstance().addProteinDetails(results.values());
        // add results to protein map
        proteins.putAll(results);
        // public results
        publish(new Tuple<TableContentType, Object>(TableContentType.PROTEIN_DETAILS, proteins));

        // protein sequence coverage
        Map<Comparable, Double> coverageMap = new HashMap<Comparable, Double>();
        // peptide fits
        Map<Tuple<Comparable, Comparable>, Integer> peptideFits = new HashMap<Tuple<Comparable, Comparable>, Integer>();

        for (Comparable protIdentId : accs.keySet()) {
            // protein sequence coverage
            Double coverage = PrideInspectorCacheManager.getInstance().getSequenceCoverage(controller.getUid(), protIdentId);
            if (coverage == null) {
                String mappedProtAcc = accs.get(protIdentId);

                // get protein details
                Protein protein = proteins.get(mappedProtAcc);
                if (protein != null) {
                    AnnotatedProtein annotatedProtein = new AnnotatedProtein(protein);
                    Collection<Comparable> peptideIds = controller.getPeptideIds(protIdentId);
                    for (Comparable peptideId : peptideIds) {
                        PeptideAnnotation peptide = new PeptideAnnotation();
                        peptide.setSequence(controller.getPeptideSequence(protIdentId, peptideId));
                        peptide.setStart(controller.getPeptideSequenceStart(protIdentId, peptideId));
                        peptide.setEnd(controller.getPeptideSequenceEnd(protIdentId, peptideId));
                        annotatedProtein.addAnnotation(peptide);
                    }
                    coverage = annotatedProtein.getSequenceCoverage();
                    coverageMap.put(protIdentId, coverage);
                    // cache the sequence coverage
                    PrideInspectorCacheManager.getInstance().addSequenceCoverage(controller.getUid(), protIdentId, coverage);
                }
            } else {
                coverageMap.put(protIdentId, coverage);
            }

            // peptide fits
            Collection<Comparable> peptideIdentIds = controller.getPeptideIds(protIdentId);
            for (Comparable peptideIdentId : peptideIdentIds) {
                Integer state = PrideInspectorCacheManager.getInstance().getPeptideFitState(controller.getUid(), protIdentId, peptideIdentId);
                if (state == null) {
                    String mappedProtAcc = accs.get(protIdentId);

                    // get protein details
                    Protein protein = proteins.get(mappedProtAcc);
                    if (protein != null) {
                        protein = new AnnotatedProtein(protein);
                    }

                    // get peptide sequence
                    String sequence = controller.getPeptideSequence(protIdentId, peptideIdentId);

                    // start and end position
                    int start = controller.getPeptideSequenceStart(protIdentId, peptideIdentId);
                    int end = controller.getPeptideSequenceEnd(protIdentId, peptideIdentId);

                    // peptide present
                    if (protein == null || sequence == null || protein.getSequenceString() == null) {
                        state = PeptideFitState.UNKNOWN;
                    } else {
                        if (protein.hasSubSequenceString(sequence, start, end)) {
                            state = PeptideFitState.STRICT_FIT;
                        } else if (protein.hasSubSequenceString(sequence)) {
                            state = PeptideFitState.FIT;
                        } else {
                            state = PeptideFitState.NOT_FIT;
                        }
                    }
                    PrideInspectorCacheManager.getInstance().addPeptideFitState(controller.getUid(), protIdentId, peptideIdentId, state);
                }
                peptideFits.put(new Tuple<Comparable, Comparable>(protIdentId, peptideIdentId), state);
            }

        }

        // publish sequence coverage results
        publish(new Tuple<TableContentType, Object>(TableContentType.PROTEIN_SEQUENCE_COVERAGE, coverageMap));

        // publish peptide fit result
        publish(new Tuple<TableContentType, Object>(TableContentType.PEPTIDE_FIT, peptideFits));
    }
}
