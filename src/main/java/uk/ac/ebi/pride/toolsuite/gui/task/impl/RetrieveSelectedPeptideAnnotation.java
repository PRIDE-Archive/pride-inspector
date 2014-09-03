package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.Modification;
import uk.ac.ebi.pride.toolsuite.gui.component.sequence.AnnotatedProtein;
import uk.ac.ebi.pride.toolsuite.gui.component.sequence.PTMAnnotation;
import uk.ac.ebi.pride.toolsuite.gui.component.sequence.PeptideAnnotation;

import java.util.Collection;

/**
 * Retrieve peptide annotation, and set it as selected annotation to a given protein
 *
 * User: rwang
 * Date: 15/06/11
 * Time: 09:25
 */
public class RetrieveSelectedPeptideAnnotation extends AbstractDataAccessTask<Void, Void> {
    /**
     * Protein object, where the selected annotation is going to be set
     */
    private AnnotatedProtein protein;
    /**
     * Identification id and peptide id will be used to get peptide annotation
     */
    private Comparable identId, peptideId;

    /**
     * Constructor
     * @param controller    data access controller
     * @param protein   protein detail object
     * @param identId   identification id
     * @param peptideId peptide id
     */
    public RetrieveSelectedPeptideAnnotation(DataAccessController controller, AnnotatedProtein protein,
                                             Comparable identId, Comparable peptideId) {
        super(controller);
        this.protein = protein;
        this.identId = identId;
        this.peptideId = peptideId;
    }

    @Override
    protected Void retrieve() throws Exception {
        // new selected peptide annotation
        PeptideAnnotation peptideAnnotation = new PeptideAnnotation();

        // set values of peptide annotation
        peptideAnnotation.setSequence(controller.getPeptideSequence(identId, peptideId));
        peptideAnnotation.setStart(controller.getPeptideSequenceStart(identId, peptideId));
        peptideAnnotation.setEnd(controller.getPeptideSequenceEnd(identId, peptideId));

        // add ptm annotations
        addPTMAnnotations(peptideId, peptideAnnotation);

        // set selected peptide annotation
        PeptideAnnotation selectedPeptide = protein.getSelectedAnnotation();

        if (!selectedPeptide.equals(peptideAnnotation)) {
            protein.setSelectedAnnotation(peptideAnnotation);
        }

        return null;
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
