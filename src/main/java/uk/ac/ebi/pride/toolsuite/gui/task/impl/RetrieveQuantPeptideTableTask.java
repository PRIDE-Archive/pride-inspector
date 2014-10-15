package uk.ac.ebi.pride.toolsuite.gui.task.impl;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import uk.ac.ebi.pride.toolsuite.gui.component.quant.QuantPeptideSelectionPane;
import uk.ac.ebi.pride.toolsuite.gui.event.QuantSelectionEvent;
import uk.ac.ebi.pride.utilities.util.Tuple;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableDataRetriever;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideTableRow;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskAdapter;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Task to retrieve all the peptide related quantitative information per protein identification
 * @quthor rwang
 * @author ypriverol
 * Date: 18/08/2011
 * Time: 11:09
 */
public class RetrieveQuantPeptideTableTask extends TaskAdapter<Void, Tuple<TableContentType, Object>> {

    private static final String DEFAULT_TASK_NAME = "Updating Peptide Table";

    private static final String DEFAULT_TASK_DESC = "Updating Peptide Table";

    private DataAccessController controller;

    private Comparable identId;

    private int referenceSampleIndex;

    private boolean status;

    private Map<Comparable, List<Comparable>> selectedPeptides;

    public RetrieveQuantPeptideTableTask(DataAccessController controller, Comparable identId, int referenceSampleIndex, Boolean status, Map<Comparable, List<Comparable>> selectedPeptides) {
        this.setName(DEFAULT_TASK_NAME);
        this.setDescription(DEFAULT_TASK_DESC);
        this.controller = controller;
        this.identId = identId;
        this.referenceSampleIndex = referenceSampleIndex;
        this.status = status;
        this.selectedPeptides = selectedPeptides;
    }

    @Override
    protected Void doInBackground() throws Exception {

        // get new headers
        // protein quantitative table header
        Collection<Comparable> peptideIds;
        if(!controller.getType().equals(DataAccessController.Type.MZTAB)){
            List<Object> peptideQuantHeaders = TableDataRetriever.getPeptideQuantTableHeaders(controller, referenceSampleIndex);
            publish(new Tuple<TableContentType, Object>(TableContentType.PEPTIDE_QUANTITATION_HEADER, peptideQuantHeaders));
            peptideIds = controller.getPeptideIds(identId);
        }else{
            peptideIds = controller.getQuantPeptideIds(identId);
        }

        if(status){
            for (Comparable peptideId : peptideIds) {
                // get and publish protein related details
                List<Object> peptideQuantContent;
                PeptideTableRow peptideContent;
                if(!controller.getType().equals(DataAccessController.Type.MZTAB)){
                     peptideContent = TableDataRetriever.getPeptideTableRow(controller, identId, peptideId);
                     peptideQuantContent = TableDataRetriever.getPeptideQuantTableRow(controller, identId, peptideId, referenceSampleIndex);
                }else{
                    peptideContent = TableDataRetriever.getPeptideQuantDataTableRow(controller, identId, peptideId);
                    peptideQuantContent = TableDataRetriever.getPeptideQuantTableRow(controller, identId, peptideId);
                }
                peptideContent.addQuantifications(peptideQuantContent);
                publish(new Tuple<TableContentType, Object>(TableContentType.PEPTIDE_QUANTITATION, peptideContent));
            }
        }else{
            for(Comparable peptideId : peptideIds){
                // get and publish protein related details

                List<Object> peptideQuantContent;
                PeptideTableRow peptideContent;
                if(!controller.getType().equals(DataAccessController.Type.MZTAB)){
                    peptideContent = TableDataRetriever.getPeptideTableRow(controller, identId, peptideId);
                    peptideQuantContent = TableDataRetriever.getPeptideQuantTableRow(controller, identId, peptideId, referenceSampleIndex);
                }else{
                    peptideContent = TableDataRetriever.getPeptideQuantDataTableRow(controller, identId, peptideId);
                    peptideQuantContent = TableDataRetriever.getPeptideQuantTableRow(controller, identId, peptideId);
                }
                peptideContent.addQuantifications(peptideQuantContent);

                if(selectedPeptides.get(identId) != null && selectedPeptides.get(identId).contains(peptideId)){
                    peptideContent.setComparisonState(true);
                }

                publish(new Tuple<TableContentType, Object>(TableContentType.PEPTIDE_QUANTITATION_REMOVE, peptideContent));

            }
        }


        // check for interruption
        checkInterruption();

        return null;
    }

    private void checkInterruption() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }
}