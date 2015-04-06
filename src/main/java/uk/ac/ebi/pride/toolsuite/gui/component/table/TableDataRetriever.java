package uk.ac.ebi.pride.toolsuite.gui.component.table;

import uk.ac.ebi.pride.tools.protein_details_fetcher.model.Protein;
import uk.ac.ebi.pride.tools.utils.AccessionResolver;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorCacheManager;
import uk.ac.ebi.pride.toolsuite.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.toolsuite.gui.component.sequence.PeptideFitState;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideTableRow;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProteinTableRow;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;
import uk.ac.ebi.pride.toolsuite.gui.utils.ProteinAccession;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.*;
import uk.ac.ebi.pride.utilities.mol.IsoelectricPointUtils;
import uk.ac.ebi.pride.utilities.mol.MoleculeUtilities;
import uk.ac.ebi.pride.utilities.term.CvTermReference;
import uk.ac.ebi.pride.utilities.term.QuantCvTermReference;
import uk.ac.ebi.pride.utilities.term.SearchEngineScoreCvTermReference;
import uk.ac.ebi.pride.utilities.util.NumberUtilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * <code>TableDataRetriever </code> provides methods for retrieving row data for tables.
 * <p/>
 * @author rwang
 * @author ypriverol
 */
public class TableDataRetriever {

    /**
     * Get a row of data for peptide table.
     *
     * @param controller data access controller
     * @param identId    identification id
     * @param peptideId  peptide id
     * @return List<Object> row data
     * @throws uk.ac.ebi.pride.utilities.data.controller.DataAccessException
     *          data access exception
     */
    public static PeptideTableRow getPeptideTableRow(DataAccessController controller,
                                                     Comparable identId,
                                                     Comparable peptideId) throws DataAccessException {
        PeptideTableRow peptideTableRow = new PeptideTableRow();

        // peptide sequence with modifications
        List<Modification> mods = new ArrayList<Modification>(controller.getPTMs(identId, peptideId));
        String sequence = controller.getPeptideSequence(identId, peptideId);
        peptideTableRow.setSequence(new PeptideSequence(null, null, sequence, mods, null));

        // start and end position
        int start = controller.getPeptideSequenceStart(identId, peptideId);
        int end = controller.getPeptideSequenceEnd(identId, peptideId);

        // Protein Accession
        String protAcc = controller.getProteinAccession(identId);
        String protAccVersion = controller.getProteinAccessionVersion(identId);
        String database = (controller.getSearchDatabase(identId).getName() == null) ? "" : controller.getSearchDatabase(identId).getName();
        AccessionResolver resolver = new AccessionResolver(protAcc, protAccVersion, database, true);
        String mappedProtAcc = resolver.isValidAccession() ? resolver.getAccession() : null;
        peptideTableRow.setProteinAccession(new ProteinAccession(protAcc, mappedProtAcc));

        // get protein details
        Protein protein = PrideInspectorCacheManager.getInstance().getProteinDetails(mappedProtAcc);
        if (protein != null) {
            protein = new AnnotatedProtein(protein);
        }

        // Protein name
        peptideTableRow.setProteinName(protein == null ? null : protein.getName());

        // protein status
        peptideTableRow.setProteinAccessionStatus(protein == null ? null : protein.getStatus().name());

        // sequence coverage
        Double coverage = PrideInspectorCacheManager.getInstance().getSequenceCoverage(controller.getUid(), identId);
        peptideTableRow.setSequenceCoverage(coverage);

        // peptide present
        int peptideFitState;
        if (protein == null || protein.getSequenceString() == null || "".equals(protein.getSequenceString())) {
            peptideFitState = PeptideFitState.UNKNOWN;
        } else {
            if (protein.hasSubSequenceString(sequence, start, end)) {
                peptideFitState = PeptideFitState.STRICT_FIT;
            } else if (protein.hasSubSequenceString(sequence)) {
                peptideFitState = PeptideFitState.FIT;
            } else {
                peptideFitState = PeptideFitState.NOT_FIT;
            }
        }
        peptideTableRow.setPeptideFitState(peptideFitState);

        // ranking
        int rank = controller.getPeptideRank(identId, peptideId);
        peptideTableRow.setRanking(rank == -1 ? null : rank);

        // precursor charge
        Integer charge = controller.getPeptidePrecursorCharge(identId, peptideId);
        Comparable specId = controller.getPeptideSpectrumId(identId, peptideId);
        if (charge == null && specId != null) {
            charge = controller.getSpectrumPrecursorCharge(specId);
            if (charge == null || charge == 0) {
                charge = null;
            }
        }
        peptideTableRow.setPrecursorCharge(charge);

        if (specId != null) {
            double mz = controller.getSpectrumPrecursorMz(specId);
            mz = (mz == -1)? controller.getPeptidePrecursorMz(identId,peptideId):mz;
            List<Double> ptmMasses = new ArrayList<Double>();
            for (Modification mod : mods) {
                List<Double> monoMasses = mod.getMonoisotopicMassDelta();
                if (monoMasses != null && !monoMasses.isEmpty()) {
                    ptmMasses.add(monoMasses.get(0));
                }
            }
            double theoreticalMz = controller.getPeptideTheoreticalMz(identId, peptideId);
            Double deltaMass = 0.0;
            if(theoreticalMz == -1)
              deltaMass = MoleculeUtilities.calculateDeltaMz(sequence, mz, charge, ptmMasses);
            else
                deltaMass = MoleculeUtilities.calculateDeltaMz(mz, theoreticalMz);

            peptideTableRow.setDeltaMz(deltaMass == null ? null : NumberUtilities.scaleDouble(deltaMass, 4));

            peptideTableRow.setPrecursorMz(mz == -1 ? null : NumberUtilities.scaleDouble(mz, 4));
        } else {
            peptideTableRow.setDeltaMz(null);
            peptideTableRow.setPrecursorMz(null);
        }

        // Number of fragment ions
        peptideTableRow.setNumberOfFragmentIons(controller.getNumberOfFragmentIons(identId, peptideId));

        // peptide scores
        addPeptideScores(peptideTableRow, controller, identId, peptideId);

        // Start
        peptideTableRow.setSequenceStartPosition(start == -1 ? null : start);

        // End
        peptideTableRow.setSequenceEndPosition(end == -1 ? null : end);

        // Spectrum reference
        peptideTableRow.setSpectrumId(specId);

        // identification id
        peptideTableRow.setProteinId(identId);

        // peptide index
        peptideTableRow.setPeptideId(peptideId);

        return peptideTableRow;
    }

    /**
     * Get a row of data for peptide table.
     *
     * @param controller data access controller
     * @param identId    identification id
     * @param peptideId  peptide id
     * @return List<Object> row data
     * @throws uk.ac.ebi.pride.utilities.data.controller.DataAccessException
     *          data access exception
     */
    public static PeptideTableRow getPeptideQuantDataTableRow(DataAccessController controller,
                                                     Comparable identId,
                                                     Comparable peptideId) throws DataAccessException {
        PeptideTableRow peptideTableRow = new PeptideTableRow();

        // peptide sequence with modifications
        List<Modification> mods = new ArrayList<Modification>(controller.getNumberOfQuantPTMs(identId, peptideId));
        String sequence = controller.getQuantPeptideSequence(identId, peptideId);
        peptideTableRow.setSequence(new PeptideSequence(null, null, sequence, mods, null));

        // start and end position
        int start = -1;
        int end   = -1;

        // Protein Accession
        String protAcc = controller.getProteinAccession(identId);
        String protAccVersion = controller.getProteinAccessionVersion(identId);
        String database = (controller.getSearchDatabase(identId).getName() == null) ? "" : controller.getSearchDatabase(identId).getName();
        AccessionResolver resolver = new AccessionResolver(protAcc, protAccVersion, database, true);
        String mappedProtAcc = resolver.isValidAccession() ? resolver.getAccession() : null;
        peptideTableRow.setProteinAccession(new ProteinAccession(protAcc, mappedProtAcc));

        // get protein details
        Protein protein = PrideInspectorCacheManager.getInstance().getProteinDetails(mappedProtAcc);
        if (protein != null) {
            protein = new AnnotatedProtein(protein);
        }

        // Protein name
        peptideTableRow.setProteinName(protein == null ? null : protein.getName());

        // protein status
        peptideTableRow.setProteinAccessionStatus(protein == null ? null : protein.getStatus().name());

        // sequence coverage
        Double coverage = PrideInspectorCacheManager.getInstance().getSequenceCoverage(controller.getUid(), identId);
        peptideTableRow.setSequenceCoverage(coverage);

        // peptide present
        int peptideFitState;
        if (protein == null || protein.getSequenceString() == null || "".equals(protein.getSequenceString())) {
            peptideFitState = PeptideFitState.UNKNOWN;
        } else {
            if (protein.hasSubSequenceString(sequence, start, end)) {
                peptideFitState = PeptideFitState.STRICT_FIT;
            } else if (protein.hasSubSequenceString(sequence)) {
                peptideFitState = PeptideFitState.FIT;
            } else {
                peptideFitState = PeptideFitState.NOT_FIT;
            }
        }
        peptideTableRow.setPeptideFitState(peptideFitState);

        // ranking
        int rank = -1;
        peptideTableRow.setRanking(rank == -1 ? null : rank);

        // precursor charge
        QuantPeptide quantPeptide = controller.getQuantPeptideByIndex(identId, peptideId);
        Integer charge = quantPeptide.getPrecursorCharge();
        Comparable specId = controller.getQuantPeptideSpectrumId(identId,peptideId);
        if (charge == null && specId != null) {
            charge = controller.getSpectrumPrecursorCharge(specId);
            if (charge == null || charge == 0) {
                charge = null;
            }
        }
        peptideTableRow.setPrecursorCharge(charge);

        if (specId != null) {
            double mz = controller.getSpectrumPrecursorMz(specId);
            mz = (mz == -1)? quantPeptide.getPrecursorMz():mz;
            List<Double> ptmMasses = new ArrayList<Double>();
            for (Modification mod : mods) {
                List<Double> monoMasses = mod.getMonoisotopicMassDelta();
                if (monoMasses != null && !monoMasses.isEmpty()) {
                    ptmMasses.add(monoMasses.get(0));
                }
            }
            Double deltaMass = MoleculeUtilities.calculateDeltaMz(sequence, mz, charge, ptmMasses);
            peptideTableRow.setDeltaMz(deltaMass == null ? null : NumberUtilities.scaleDouble(deltaMass, 4));

            peptideTableRow.setPrecursorMz(mz == -1 ? null : NumberUtilities.scaleDouble(mz, 4));
        } else {
            peptideTableRow.setDeltaMz(null);
            peptideTableRow.setPrecursorMz(null);
        }

        // Number of fragment ions
        peptideTableRow.setNumberOfFragmentIons(0);

        // peptide scores
        addQuantPeptideScores(peptideTableRow, controller, identId, peptideId);

        // Start
        peptideTableRow.setSequenceStartPosition(start == -1 ? null : start);

        // End
        peptideTableRow.setSequenceEndPosition(end == -1 ? null : end);

        // Spectrum reference
        peptideTableRow.setSpectrumId(specId);

        // identification id
        peptideTableRow.setProteinId(identId);

        // peptide index
        peptideTableRow.setPeptideId(peptideId);

        return peptideTableRow;
    }

    private static void addPeptideScores(PeptideTableRow peptideTableRow, DataAccessController controller,
                                         Comparable identId, Comparable peptideId) {
        Score score = controller.getPeptideScore(identId, peptideId);
        Collection<SearchEngineScoreCvTermReference> availablePeptideLevelScores = controller.getAvailablePeptideLevelScores();
        if (score != null) {
            for (SearchEngineScoreCvTermReference availablePeptideLevelScore : availablePeptideLevelScores) {
                List<Number> values = score.getScores(availablePeptideLevelScore);
                if (!values.isEmpty()) {
                    // take the first by default
                    //content.add(values.get(0));
                    Double value =  (values.get(0) != null)?NumberUtilities.scaleDouble(values.get(0).doubleValue(),4):-1.0;
                    peptideTableRow.addScore(value);

                } else {
                    peptideTableRow.addScore(null);
                }
            }
        } else {
            for (SearchEngineScoreCvTermReference availablePeptideLevelScore : availablePeptideLevelScores) {
                peptideTableRow.addScore(null);
            }
        }
    }

    private static void addQuantPeptideScores(PeptideTableRow peptideTableRow, DataAccessController controller,
                                              Comparable identId, Comparable peptideId) {
        Score score = controller.getQuantPeptideScore(identId, peptideId);
        Collection<SearchEngineScoreCvTermReference> availablePeptideLevelScores = controller.getAvailablePeptideLevelScores();
        if (score != null) {
            for (SearchEngineScoreCvTermReference availablePeptideLevelScore : availablePeptideLevelScores) {
                List<Number> values = score.getScores(availablePeptideLevelScore);
                if (!values.isEmpty()) {
                    Double value =  (values.get(0) != null)?NumberUtilities.scaleDouble(values.get(0).doubleValue(),4):-1.0;
                    peptideTableRow.addScore(value);

                } else {
                    peptideTableRow.addScore(null);
                }
            }
        } else {
            for (SearchEngineScoreCvTermReference availablePeptideLevelScore : availablePeptideLevelScores) {
                peptideTableRow.addScore(null);
            }
        }
    }

    /**
     * Retrieve a row of data for identification table.
     *
     * @param controller data access controller
     * @param proteinId  identification id
     * @return List<Object> row data
     * @throws DataAccessException data access exception
     */
    public static ProteinTableRow getProteinTableRow(DataAccessController controller,
                                                     Comparable proteinId,
                                                     Comparable proteinGroupId) throws DataAccessException {

        ProteinTableRow proteinTableRow = new ProteinTableRow();

        // Original Protein Accession
        String protAcc = controller.getProteinAccession(proteinId);
        String protAccVersion = controller.getProteinAccessionVersion(proteinId);
        String database = (controller.getSearchDatabase(proteinId).getName() == null) ? "" : controller.getSearchDatabase(proteinId).getName();
        AccessionResolver resolver = new AccessionResolver(protAcc, protAccVersion, database, true);
        String mappedProtAcc = resolver.isValidAccession() ? resolver.getAccession() : protAcc;
        proteinTableRow.setProteinAccession(new ProteinAccession(protAcc, mappedProtAcc));

        // get protein details
        Protein protein = PrideInspectorCacheManager.getInstance().getProteinDetails(mappedProtAcc);
        if (protein != null) {
            protein = new AnnotatedProtein(protein);
        } else if (mappedProtAcc != null && controller.getProteinById(proteinId).getDbSequence() != null && controller.getProteinById(proteinId).getDbSequence().getSequence() != null) {
            protein = new AnnotatedProtein(mappedProtAcc, controller.getProteinById(proteinId).getDbSequence().getName(), Protein.STATUS.UNKNOWN, controller.getProteinById(proteinId).getDbSequence().getSequence());
            PrideInspectorCacheManager.getInstance().addProteinDetails(protein);
        }

        // Protein name
        proteinTableRow.setProteinName(protein == null ? null : protein.getName());

        // protein status
        proteinTableRow.setProteinAccessionStatus(protein == null ? null : protein.getStatus().name());

        // sequence coverage
        Double coverage = PrideInspectorCacheManager.getInstance().getSequenceCoverage(controller.getUid(), proteinId);
        proteinTableRow.setSequenceCoverage(coverage);

        // isoelectric points
        if (protein != null && protein.getSequenceString() != null && protein.getSequenceString().length()>0) {
            proteinTableRow.setIsoelectricPoint(IsoelectricPointUtils.calculate(protein.getSequenceString()));
        } else {
            proteinTableRow.setIsoelectricPoint(null);
        }

        // Threshold
        double threshold = controller.getProteinThreshold(proteinId);
        proteinTableRow.setThreshold(threshold == -1 ? null : threshold);

        // number of peptides
        proteinTableRow.setNumberOfPeptides(controller.getNumberOfPeptides(proteinId));

        // unique peptides
        proteinTableRow.setNumberOfUniquePeptides(controller.getNumberOfUniquePeptides(proteinId));

        // number of PTMs
        proteinTableRow.setNumberOfPTMs(controller.getNumberOfPTMs(proteinId));

        // unique id for identification
        proteinTableRow.setProteinId(proteinId);

        // protein group id
        proteinTableRow.setProteinGroupId(proteinGroupId);

        // protein scores
        addProteinScores(proteinTableRow, controller, proteinId);

        return proteinTableRow;
    }

    private static void addProteinScores(ProteinTableRow proteinTableRow, DataAccessController controller, Comparable identId) {
        Score score = controller.getProteinScores(identId);
        Collection<SearchEngineScoreCvTermReference> availablePeptideLevelScores = controller.getAvailableProteinLevelScores();
        if (score != null) {
            for (SearchEngineScoreCvTermReference availablePeptideLevelScore : availablePeptideLevelScores) {
                List<Number> values = score.getScores(availablePeptideLevelScore);
                if (!values.isEmpty()) {
                    // take the first by default
                    Number value = values.get(0);
                    proteinTableRow.addScore(value == null ? null : NumberUtilities.scaleDouble(value.doubleValue(), 4));
                } else {
                    proteinTableRow.addScore(null);
                }
            }
        } else {
            for (SearchEngineScoreCvTermReference availablePeptideLevelScore : availablePeptideLevelScores) {
                proteinTableRow.addScore(null);
            }
        }
    }

    /**
     * Get the headers for the identification quantitative table
     *
     * @param controller     data access controller
     * @param refSampleIndex given reference sub sample index
     * @return List<Object>    a list of headers
     * @throws DataAccessException data access exception
     */
    public static List<Object> getProteinQuantTableHeaders(DataAccessController controller, int refSampleIndex) throws DataAccessException {
        return getQuantTableHeaders(controller, refSampleIndex, true);
    }

    /**
     * Get the headers for the peptide quantitative table
     *
     * @param controller     data access controller
     * @param refSampleIndex reference sub sample index
     * @return List<Object> peptide quantitative table headers
     * @throws DataAccessException data access exception
     */
    public static List<Object> getPeptideQuantTableHeaders(DataAccessController controller, int refSampleIndex) throws DataAccessException {
        return getQuantTableHeaders(controller, refSampleIndex, false);

    }

    /**
     * Get table header for quantitative data
     *
     * @param controller     data access controller
     * @param refSampleIndex reference sub sample index
     * @param isProteinIdent whether it is protein identification or peptide identification
     * @return List<Object>    a list of quantitative table headers
     * @throws DataAccessException data access exception
     */
    private static List<Object> getQuantTableHeaders(DataAccessController controller, int refSampleIndex, boolean isProteinIdent) throws DataAccessException {
        List<Object> headers = new ArrayList<Object>();

        // label free methods
        if (controller.hasLabelFreeQuantMethods()) {
            Collection<QuantCvTermReference> methods = isProteinIdent ? controller.getProteinLabelFreeQuantMethods() : controller.getPeptideLabelFreeQuantMethods();
            headers.addAll(getLabelFreeMethodHeaders(methods));
        }

        // isotope labelling methods
        if (controller.hasIsotopeLabellingQuantMethods()) {
            Collection<QuantCvTermReference> methods = isProteinIdent ? controller.getProteinIsotopeLabellingQuantMethods() : controller.getPeptideIsotopeLabellingQuantMethods();
            headers.addAll(getIsotopeLabellingMethodHeaders(methods, controller, refSampleIndex, isProteinIdent));
        }

        return headers;
    }

    /**
     * Get table header for quantitative data
     *
     * @param controller data access controller
     * @return List<Object>    a list of quantitative table headers
     * @throws DataAccessException data access exception
     */
    public static List<Object> getProteinScoreHeaders(DataAccessController controller) throws DataAccessException {
        List<Object> headers = new ArrayList<Object>();

        Collection<SearchEngineScoreCvTermReference> cvHeaders = controller.getAvailableProteinLevelScores();

        if (cvHeaders != null && !cvHeaders.isEmpty()) {
            Iterator<SearchEngineScoreCvTermReference> cvHeader = cvHeaders.iterator();
            while (cvHeader.hasNext()) {
                headers.add((cvHeader.next().getName()));
            }
        }
        return headers;
    }

    /**
     * Create isotope labelling method headers
     *
     * @param methods        isotope labelling methods
     * @param controller     data access controller
     * @param refSampleIndex reference sub sample index
     * @param isProteinIdent whether is protein identification or peptide identification
     * @return List<Object>    a list of headers
     * @throws DataAccessException data access exception
     */
    private static List<Object> getIsotopeLabellingMethodHeaders(Collection<QuantCvTermReference> methods,
                                                                 DataAccessController controller,
                                                                 int refSampleIndex,
                                                                 boolean isProteinIdent) throws DataAccessException {
        List<Object> headers = new ArrayList<Object>();

        if (methods.size() > 0) {
            QuantitativeSample sample = controller.getQuantSample();
            // total intensities
            boolean hasTotalIntensities = isProteinIdent ? controller.hasProteinTotalIntensities() : controller.hasPeptideTotalIntensities();
            if (hasTotalIntensities) {
                headers.addAll(getTotalIntensityHeaders(sample));
            }

            int existingRefSampleIndex = controller.getReferenceSubSampleIndex();

            if (refSampleIndex < 1 || refSampleIndex == existingRefSampleIndex) {
                // show the original ratios
                if (existingRefSampleIndex >= 1) {
                    // the original quant data has a reference sample already
                    headers.addAll(getReagentRatioHeaders(sample, existingRefSampleIndex));
                }
            } else {
                // show the newly calculated ratios
                headers.addAll(getReagentRatioHeaders(sample, refSampleIndex));
            }

        }

        return headers;
    }


    /**
     * Create a list of label free method headers
     *
     * @param methods label free methods
     * @return List<Object>    label free method headers
     */
    private static List<Object> getLabelFreeMethodHeaders(Collection<QuantCvTermReference> methods) {
        List<Object> headers = new ArrayList<Object>();

        for (QuantCvTermReference method : methods) {
            headers.add(method.getName());
        }

        return headers;
    }

    /**
     * Create a list of headers for intensities
     *
     * @param sample qauntitative sample
     * @return List<String>    total intensity headers
     */
    private static List<Object> getTotalIntensityHeaders(QuantitativeSample sample) {
        List<Object> headers = new ArrayList<Object>();

        for (int i = 1; i <= QuantitativeSample.MAX_SUB_SAMPLE_SIZE; i++) {
            CvParam reagent = sample.getReagent(i);
            if (reagent != null) {
                headers.add(QuantCvTermReference.getReagentShortLabel(reagent.getAccession()));
            }
        }

        return headers;
    }

    /**
     * Create a list of headers for reagents accorrding to the given reference sample
     *
     * @param sample         qauntitative sample
     * @param refSampleIndex reference sub sample index
     * @return List<Object>    a list of headers
     */
    private static List<Object> getReagentRatioHeaders(QuantitativeSample sample, int refSampleIndex) {
        List<Object> headers = new ArrayList<Object>();

        // get reference reagent
        CvParam referenceReagent = sample.getReagent(refSampleIndex);
        // get short label for the reagent
        String shortenedReferenceReagent = QuantCvTermReference.getReagentShortLabel(referenceReagent.getAccession());
        for (int i = 1; i < QuantitativeSample.MAX_SUB_SAMPLE_SIZE; i++) {
            if (refSampleIndex != i) {
                CvParam reagent = sample.getReagent(i);
                if (reagent != null) {
                    headers.add(QuantCvTermReference.getReagentShortLabel(reagent.getAccession()) + Constants.QUANTIFICATION_RATIO_CHAR + shortenedReferenceReagent);
                }
            }
        }

        return headers;
    }

    /**
     * Retrieve a row for identification quantitative table
     *
     * @param controller              data access controller
     * @param identId                 identification id
     * @param referenceSubSampleIndex reference sub sample index
     * @return List<Object> a list of results
     * @throws DataAccessException data access exception
     */
    public static List<Object> getProteinQuantTableRow(DataAccessController controller,
                                                       Comparable identId,
                                                       int referenceSubSampleIndex) throws DataAccessException {
        Quantification quant = controller.getProteinQuantData(identId);
        return getQuantTableRow(controller, quant, referenceSubSampleIndex, true);
    }

    /**
     * Retrieve a row for identification quantitative table
     *
     * @param controller              data access controller
     * @param identId                 identification id
     * @return List<Object> a list of results
     * @throws DataAccessException data access exception
     */
    public static List<Object> getProteinQuantTableRow(DataAccessController controller,
                                                       Comparable identId) throws DataAccessException {
        return getQuantTableRow(controller, identId);
    }


    /**
     * Retrieve a row for peptide quantitative table
     *
     * @param controller              data access controller
     * @param identId                 identification id
     * @param peptideId               peptide id
     * @param referenceSubSampleIndex reference sub sample index
     * @return List<Object>    a list of results
     * @throws DataAccessException data access exception
     */
    public static List<Object> getPeptideQuantTableRow(DataAccessController controller,
                                                       Comparable identId,
                                                       Comparable peptideId,
                                                       int referenceSubSampleIndex) throws DataAccessException {
        Quantification quant = controller.getPeptideQuantData(identId, peptideId);
        return getQuantTableRow(controller, quant, referenceSubSampleIndex, false);
    }

    /**
     * Retrieve a row for peptide quantitative table
     *
     * @param controller              data access controller
     * @param identId                 identification id
     * @param peptideId               peptide id
     * @return List<Object>    a list of results
     * @throws DataAccessException data access exception
     */
    public static List<Object> getPeptideQuantTableRow(DataAccessController controller,
                                                       Comparable identId,
                                                       Comparable peptideId) throws DataAccessException {
        return getQuantTableRow(controller, identId, peptideId);
    }

    private static List<Object> getQuantTableRow(DataAccessController controller, Comparable idProtein, Comparable idPeptide) throws DataAccessException {

        List<Object> contents = new ArrayList<Object>();

        uk.ac.ebi.pride.utilities.data.core.QuantPeptide peptide  = controller.getQuantPeptideByIndex(idProtein, idPeptide);


        if(peptide.getQuantScore() != null && !peptide.getQuantScore().getStudyVariableScores().isEmpty()){
            for(Double studyVariableValue: peptide.getQuantScore().getStudyVariableScores().values()){
                contents.add(studyVariableValue);
            }
            for(Double abundance: peptide.getQuantScore().getAssayAbundance().values()){
                contents.add(abundance);
            }
        }
        return contents;
    }

    /**
     * Get table header for quantitative data
     *
     * @param controller     data access controller
     * @param quant          quantitative data
     * @param refSampleIndex reference sub sample index
     * @param isProteinIdent whether it is protein identification or peptide identification
     * @return List<String>    a list of quantitative table headers
     * @throws DataAccessException data access exception
     */
    private static List<Object> getQuantTableRow(DataAccessController controller, Quantification quant, int refSampleIndex, boolean isProteinIdent) throws DataAccessException {
        List<Object> contents = new ArrayList<Object>();

        // label free methods
        if (controller.hasLabelFreeQuantMethods()) {
            Collection<QuantCvTermReference> methods = isProteinIdent ? controller.getProteinLabelFreeQuantMethods() : controller.getPeptideLabelFreeQuantMethods();
            contents.addAll(getLabelFreeQuantData(methods, quant));
        }

        // isotope labelling methods
        if (controller.hasIsotopeLabellingQuantMethods()) {
            Collection<QuantCvTermReference> methods = isProteinIdent ? controller.getProteinIsotopeLabellingQuantMethods() : controller.getPeptideIsotopeLabellingQuantMethods();
            contents.addAll(getIsotopeLabellingQuantData(methods, controller, quant, refSampleIndex, isProteinIdent));
        }

        return contents;
    }


    private static List<Object> getQuantTableRow(DataAccessController controller, Comparable idProtein) throws DataAccessException {
        List<Object> contents = new ArrayList<Object>();

        uk.ac.ebi.pride.utilities.data.core.Protein protein = controller.getProteinById(idProtein);

        if(protein.getQuantScore() != null && !protein.getQuantScore().getStudyVariableScores().isEmpty()){
            for(Double studyVariableValue: protein.getQuantScore().getStudyVariableScores().values()){
                contents.add(studyVariableValue);
            }
            for(Double abundance: protein.getQuantScore().getAssayAbundance().values()){
                contents.add(abundance);
            }
        }
        return contents;
    }

    /**
     * Get label free quantitative data
     *
     * @param methods label free methods
     * @param quant   quantitative object
     * @return List<Double>    a list of label free results
     */
    private static List<Double> getLabelFreeQuantData(Collection<QuantCvTermReference> methods, Quantification quant) {
        return quant.getLabelFreeResults(methods);
    }

    /**
     * Get isotope labeling quantitative data
     *
     * @param methods        isotople labelling methods
     * @param controller     data access controller
     * @param quant          quantitative object
     * @param refSampleIndex reference sub sample index
     * @param isProteinIdent whether is protein identification or peptide identification
     * @return List<Object>    a list of results
     * @throws DataAccessException data access exception
     */
    private static List<Object> getIsotopeLabellingQuantData(Collection<QuantCvTermReference> methods, DataAccessController controller,
                                                             Quantification quant, int refSampleIndex, boolean isProteinIdent) throws DataAccessException {

        List<Object> contents = new ArrayList<Object>();

        if (methods.size() > 0) {
            QuantitativeSample sample = controller.getQuantSample();
            // total intensities
            boolean hasTotalIntensities = isProteinIdent ? controller.hasProteinTotalIntensities() : controller.hasPeptideTotalIntensities();
            if (hasTotalIntensities) {
                contents.addAll(getTotalIntensityQuantData(sample, quant));
            }

            int existingRefSampleIndex = controller.getReferenceSubSampleIndex();

            if (refSampleIndex < 1 || refSampleIndex == existingRefSampleIndex) {
                // show the original ratios
                if (existingRefSampleIndex >= 1) {
                    // the original quant data has a reference sample already
                    contents.addAll(getReagentRatioQuantData(sample, quant, existingRefSampleIndex));
                }
            } else {
                // show the newly calculated ratios
                contents.addAll(getReagentRatioQuantData(sample, quant, refSampleIndex));
            }

        }

        return contents;
    }

    /**
     * Get total intensities
     *
     * @param sample quantitative sample
     * @param quant  quantitative data
     * @return List<Object>    a list of total intensities
     */
    private static List<Object> getTotalIntensityQuantData(QuantitativeSample sample, Quantification quant) {
        List<Object> contents = new ArrayList<Object>();

        for (int i = 1; i <= QuantitativeSample.MAX_SUB_SAMPLE_SIZE; i++) {
            CvParam reagent = sample.getReagent(i);
            if (reagent != null) {
                contents.add(quant.getIsotopeLabellingResult(i));
            }
        }

        return contents;
    }


    /**
     * Get reagent quantitative data
     *
     * @param sample         quantitative sample
     * @param quant          quantitative data
     * @param refSampleIndex reference sub sample index
     * @return List<Object>    a list of reagent ratio data
     */
    private static List<Object> getReagentRatioQuantData(QuantitativeSample sample,
                                                         Quantification quant,
                                                         int refSampleIndex) {
        List<Object> contents = new ArrayList<Object>();

        // get reference reagent
        Double referenceReagentResult = quant.getIsotopeLabellingResult(refSampleIndex);
        // get short label for the reagent
        for (int i = 1; i < QuantitativeSample.MAX_SUB_SAMPLE_SIZE; i++) {
            if (refSampleIndex != i) {
                CvParam reagent = sample.getReagent(i);
                if (reagent != null) {
                    Double reagentResult = quant.getIsotopeLabellingResult(i);
                    if (referenceReagentResult != null && reagentResult != null) {
                        contents.add(reagentResult / referenceReagentResult);
                    } else {
                        contents.add(null);
                    }
                }
            }
        }

        return contents;
    }
}
