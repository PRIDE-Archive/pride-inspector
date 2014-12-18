package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.bushe.swing.event.EventBus;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.toolsuite.gui.event.ProcessingDataSourceEvent;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.core.Modification;
import uk.ac.ebi.pride.utilities.mol.MoleculeUtilities;
import uk.ac.ebi.pride.utilities.util.NumberUtilities;
import uk.ac.ebi.pride.utilities.util.Tuple;

import java.util.*;

/**
 * This class Retrieve the Details of Delta Mass for Peptide Column.
 * User: ypriverol
 * Date: 5/23/13
 * Time: 3:10 PM
 */

public class RetrievePeptideSpectrumDetailTask extends AbstractDataAccessTask<Void, Tuple<TableContentType, Object>> {

    private static final String DEFAULT_TASK_NAME = "Retrieve Peptide-Spectrum Details";

    private static final String DEFAULT_TASK_DESC = "Retrieve Peptide-Spectrum Details";

    /**
     * Constructor
     *
     * @param controller data access controller
     */
    public RetrievePeptideSpectrumDetailTask(DataAccessController controller) {
        super(controller);

        // set name and description
        this.setName(DEFAULT_TASK_NAME);
        this.setDescription(DEFAULT_TASK_DESC);
    }

    @Override
    protected Void retrieve() throws Exception {
        // protein identification id
        Collection<Comparable> protIdentIds = controller.getProteinIds();

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

            checkInterruption();
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
