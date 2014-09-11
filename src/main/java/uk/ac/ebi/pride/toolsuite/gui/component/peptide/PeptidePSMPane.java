package uk.ac.ebi.pride.toolsuite.gui.component.peptide;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.bushe.swing.event.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.util.Tuple;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideSpecies;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideSpeciesPSMTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideTableHeader;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.TableContentType;
import uk.ac.ebi.pride.toolsuite.gui.event.container.ChangeRankingThresholdEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.PSMEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.PeptideSpeciesEvent;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

/**
 * This pane is to display the PTMs details for a peptide
 * <p/>
 * User: rwang, ypriverol
 * Date: 09-Sep-2010
 * Time: 08:37:03
 */
public class PeptidePSMPane extends DataAccessControllerPane<Peptide, Void> implements EventBusSubscribable {
    private static final Logger logger = LoggerFactory.getLogger(PeptidePSMPane.class);

    private static final String PSM_TABLE_DESC = "PSM";

    private JTable psmTable;

    private PeptideSpeciesEventSubscriber peptideSpeciesEventSubscriber;

    private ChangeRankingThresholdEventSubscriber changeRankingThresholdEventSubscriber;

    public PeptidePSMPane(DataAccessController controller) {
        super(controller);
    }

    @Override
    protected void setupMainPane() {
        // set layout
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    }

    @Override
    protected void addComponents() {
        // add descriptive panel
        JPanel metaDataPanel = new JPanel();
        metaDataPanel.setOpaque(false);
        metaDataPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // table label
        JLabel tableLabel = new JLabel("<html><b>" + PSM_TABLE_DESC + "</b></html>");
        metaDataPanel.add(tableLabel);
        this.add(metaDataPanel, BorderLayout.NORTH);

        psmTable = TableFactory.createPSMTable(controller.getAvailablePeptideLevelScores(), PeptideRankingFilter.LESS_EQUAL_THAN_ONE.getRankingThreshold(), controller);

        // add PSM table selection listener
        psmTable.getSelectionModel().addListSelectionListener(new PSMSelectionListener(psmTable));

        JScrollPane scrollPane = new JScrollPane(psmTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public JTable getPSMTable() {
        return psmTable;
    }


    @Override
    public void subscribeToEventBus(EventService eventBus) {
        if (eventBus == null) {
            eventBus = ContainerEventServiceFinder.getEventService(this);
        }
        peptideSpeciesEventSubscriber = new PeptideSpeciesEventSubscriber(psmTable);
        eventBus.subscribe(PeptideSpeciesEvent.class, peptideSpeciesEventSubscriber);

        changeRankingThresholdEventSubscriber = new ChangeRankingThresholdEventSubscriber(psmTable);
        eventBus.subscribe(ChangeRankingThresholdEvent.class, changeRankingThresholdEventSubscriber);
    }

    private static class PeptideSpeciesEventSubscriber implements EventSubscriber<PeptideSpeciesEvent> {

        private JTable table;

        private PeptideSpeciesEventSubscriber(JTable table) {
            this.table = table;
        }

        @Override
        public void onEvent(PeptideSpeciesEvent event) {
            PeptideSpecies peptideSpecies = event.getPeptideSpecies();

            // table model
            PeptideSpeciesPSMTableModel peptideSpeciesTableModel = (PeptideSpeciesPSMTableModel) table.getModel();
            // delete all rows
            peptideSpeciesTableModel.removeAllRows();
            // get peptide
            if (peptideSpecies != null) {
                peptideSpeciesTableModel.addData(new Tuple<TableContentType, Object>(TableContentType.PEPTIDE_SPECIES, peptideSpecies));
                table.changeSelection(0, peptideSpeciesTableModel.getColumnIndex(PeptideTableHeader.PEPTIDE_COLUMN.getHeader()), false, false);
            }
        }
    }

    private static class ChangeRankingThresholdEventSubscriber implements EventSubscriber<ChangeRankingThresholdEvent> {

        private JTable table;

        private ChangeRankingThresholdEventSubscriber(JTable table) {
            this.table = table;
        }

        @Override
        public void onEvent(ChangeRankingThresholdEvent event) {
            int rankingThreshold = event.getRankingThreshold();

            // table model
            PeptideSpeciesPSMTableModel peptideSpeciesTableModel = (PeptideSpeciesPSMTableModel) table.getModel();
            peptideSpeciesTableModel.setRankingThreshold(rankingThreshold);
            table.changeSelection(0, peptideSpeciesTableModel.getColumnIndex(PeptideTableHeader.PEPTIDE_COLUMN.getHeader()), false, false);
        }
    }

    /**
     * Trigger when a peptide is selected
     */
    @SuppressWarnings("unchecked")
    private class PSMSelectionListener implements ListSelectionListener {
        private final JTable table;

        private PSMSelectionListener(JTable table) {
            this.table = table;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {

            if (!e.getValueIsAdjusting()) {
                int rowNum = table.getSelectedRow();
                if (rowNum >= 0) {
                    // get table model
                    PeptideSpeciesPSMTableModel pepTableModel = (PeptideSpeciesPSMTableModel) table.getModel();

                    // get spectrum reference column
                    int identColNum = pepTableModel.getColumnIndex(PeptideTableHeader.IDENTIFICATION_ID.getHeader());
                    int peptideColNum = pepTableModel.getColumnIndex(PeptideTableHeader.PEPTIDE_ID.getHeader());

                    // get spectrum id
                    int modelRowIndex = table.convertRowIndexToModel(rowNum);
                    Comparable identId = (Comparable) pepTableModel.getValueAt(modelRowIndex, identColNum);
                    Comparable peptideId = (Comparable) pepTableModel.getValueAt(modelRowIndex, peptideColNum);

                    logger.debug("Peptide table selection:  Protein id: " + identId + " Peptide Id: " + peptideId);

                    // fire a background task to retrieve peptide
                    if (peptideId != null && identId != null) {
                        // publish the event to local event bus
                        EventService eventBus = ContainerEventServiceFinder.getEventService(PeptidePSMPane.this);
                        eventBus.publish(new PSMEvent(PeptidePSMPane.this, controller, identId, peptideId));
                    }
                }
            }
        }
    }
}
