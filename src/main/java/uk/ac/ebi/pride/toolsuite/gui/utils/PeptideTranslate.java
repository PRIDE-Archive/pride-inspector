package uk.ac.ebi.pride.toolsuite.gui.utils;

import uk.ac.ebi.pride.utilities.mol.PTModification;
import uk.ac.ebi.pride.utilities.mol.Peptide;

import java.util.List;

/**
 * Translate uk.ac.ebi.pride.utilities.data.core.Peptide into uk.ac.ebi.pride.iongen.model.Peptide
 *
 * Creator: Qingwei-XU
 * Date: 19/10/12
 */

public class PeptideTranslate {
    private String sequence;
    private List<uk.ac.ebi.pride.utilities.data.core.Modification> oldModifications;

    public PeptideTranslate(uk.ac.ebi.pride.utilities.data.core.Peptide oldPeptide) {
        this.sequence = oldPeptide.getSequence();
        this.oldModifications = oldPeptide.getModifications();
    }

    public Peptide translate() {
        Peptide newPeptide = new Peptide(sequence);
        PTModification newModification;

        String name;
        String type = null;
        String label;
        List<Double> monoMassDeltas;
        List<Double> avgMassDeltas;
        int position;
        for (uk.ac.ebi.pride.utilities.data.core.Modification oldModification : oldModifications) {
            name = oldModification.getName();
            label = null;
            monoMassDeltas = oldModification.getMonoisotopicMassDelta();
            avgMassDeltas = oldModification.getAvgMassDelta();
            newModification = new PTModification(name, type, label, monoMassDeltas, avgMassDeltas);

            /**
             * old modification position from [0..length], 0 means the position locate in c-terminal.
             * the new modification from [0..length-1], 0 means the first amino acid of peptide.
             * The modification worked in c-terminal or first amino acid, the theoretical mass are same.
             */
            position = oldModification.getLocation() - 1;
            if (position == -1) {
                position = 0;
            }

            newPeptide.addModification(position, newModification);
        }

        return newPeptide;
    }
}
