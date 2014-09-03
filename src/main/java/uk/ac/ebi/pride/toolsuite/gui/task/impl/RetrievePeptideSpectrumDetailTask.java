package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.Tuple;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.core.Modification;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.toolsuite.gui.event.ProcessingDataSourceEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;
import uk.ac.ebi.pride.utilities.mol.MoleculeUtilities;
import uk.ac.ebi.pride.util.NumberUtilities;

import java.util.*;

/**
 * This class Retrieve the Details of Delta Mass for Peptide Column.
 * User: ypriverol
 * Date: 5/23/13
 * Time: 3:10 PM
 */

public class RetrievePeptideSpectrumDetailTask extends TaskAdapter<Void, Tuple<TableContentType, Object>> {

    private static final Logger logger = LoggerFactory.getLogger(RetrievePeptideSpectrumDetailTask.class);

    private static final String DEFAULT_TASK_NAME = "Retrieve Peptide-Spectrum Details";

    private static final String DEFAULT_TASK_DESC = "Retrieve Peptide-Spectrum Details";

    /**
     * The number of proteins for each batch download
     */
    private static final int MAX_BATCH_DOWNLOAD_SIZE = 10;

    /**
     * data access controller
     */
    private DataAccessController controller;

    /**
     * Constructor
     *
     * @param controller data access controller
     */
    public RetrievePeptideSpectrumDetailTask(DataAccessController controller) {

        // set name and description
        this.setName(DEFAULT_TASK_NAME);
        this.setDescription(DEFAULT_TASK_DESC);

        this.controller = controller;
    }

    @Override
    protected Void doInBackground() throws Exception {
        // protein identification id
        Collection<Comparable> protIdentIds = controller.getProteinIds();


        //protein identification id and accession buffer
        Map<Comparable, String> accBuffer = new LinkedHashMap<Comparable, String>();

        Map<Tuple<Comparable, Comparable>, Double> peptideDeltaMap = new HashMap<Tuple<Comparable, Comparable>, Double>();

        Map<Tuple<Comparable, Comparable>, Double> peptidePrecursorMap = new HashMap<Tuple<Comparable, Comparable>, Double>();

        EventBus.publish(new ProcessingDataSourceEvent<DataAccessController>(controller, ProcessingDataSourceEvent.Status.SPECTRA_READING, controller));

        // iterate over each protein
        for (Comparable protIdentId : protIdentIds) {
            Collection<Comparable> peptideIdentIds = controller.getPeptideIds(protIdentId);
            for (Comparable peptideId : peptideIdentIds) {
                Double delta = computeDeltaMz(peptideId, protIdentId);
                Double precursorMz = computePrecursorMz(peptideId, protIdentId);
                peptideDeltaMap.put(new Tuple<Comparable, Comparable>(protIdentId, peptideId), delta);
                peptidePrecursorMap.put(new Tuple<Comparable, Comparable>(protIdentId, peptideId), precursorMz);
            }
        }
        publish(new Tuple<TableContentType, Object>(TableContentType.PEPTIDE_DELTA, peptideDeltaMap));
        publish(new Tuple<TableContentType, Object>(TableContentType.PEPTIDE_PRECURSOR_MZ, peptidePrecursorMap));
        EventBus.publish(new ProcessingDataSourceEvent<DataAccessController>(controller, ProcessingDataSourceEvent.Status.SPECTRA_READING, controller));
        return null;
    }

    private Double computePrecursorMz(Comparable peptideId, Comparable protIdentId) {
        Double mz = null;
        Comparable specId = controller.getPeptideSpectrumId(protIdentId, peptideId);
        if (specId != null) {
            mz = controller.getSpectrumPrecursorMz(specId);
            mz = (mz == -1) ? null : NumberUtilities.scaleDouble(mz, 2);
        }
        return mz;
    }

    private Double computeDeltaMz(Comparable peptideId, Comparable identId) {
        Double deltaMass = null;

        List<Modification> mods = new ArrayList<Modification>(controller.getPTMs(identId, peptideId));
        String sequence = controller.getPeptideSequence(identId, peptideId);

        Integer charge = controller.getPeptidePrecursorCharge(identId, peptideId);
        Comparable specId = controller.getPeptideSpectrumId(identId, peptideId);

        if (charge == null && specId != null) {
            charge = controller.getSpectrumPrecursorCharge(specId);
            if (charge == null || charge == 0) {
                charge = null;
            }
        }

        if (specId != null) {
            double mz = controller.getSpectrumPrecursorMz(specId);
            List<Double> ptmMasses = new ArrayList<Double>();
            for (Modification mod : mods) {
                List<Double> monoMasses = mod.getMonoisotopicMassDelta();
                if (monoMasses != null && !monoMasses.isEmpty()) {
                    ptmMasses.add(monoMasses.get(0));
                }
            }
            deltaMass = MoleculeUtilities.calculateDeltaMz(sequence, mz, charge, ptmMasses);
        }
        return deltaMass;
    }

}
