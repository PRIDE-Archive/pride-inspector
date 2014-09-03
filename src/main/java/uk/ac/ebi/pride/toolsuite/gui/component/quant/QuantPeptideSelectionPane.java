package uk.ac.ebi.pride.toolsuite.gui.component.quant;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.bushe.swing.event.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.listener.TableCellMouseMotionListener;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideTableHeader;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProgressiveListTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.QuantPeptideTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.sorter.NumberTableRowSorter;
import uk.ac.ebi.pride.toolsuite.gui.event.ReferenceSampleChangeEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.PSMEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.ProteinIdentificationEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.RetrieveQuantPeptideTableTask;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

/**
 * User: rwang
 * Date: 15/08/2011
 * Time: 11:33
 */
public class QuantPeptideSelectionPane extends DataAccessControllerPane implements EventBusSubscribable {
    private static final Logger logger = LoggerFactory.getLogger(QuantPeptideSelectionPane.class);

    /**
     * the title for ptm label
     */
    private final static String PEPTIDE_TABLE_DESC = "Peptide";

    /**
     * peptide table for peptide related details
     */
    private JTable pepTable;
    /**
     * Peptide table label also displays the selected protein accession
     */
    private JLabel tableLabel;

    /**
     * the current protein identification id
     */
    private Comparable currentIdentId;

    /**
     * the index of the reference sample
     */
    private int referenceSampleIndex;

    /**
     * Subscriber for event bus on protein selection event
     */
    private SelectProteinIdentSubscriber proteinIdentSubscriber;

    /**
     * Subscriber for event bus on reference sample index change event
     */
    private ReferenceSampleSubscriber referenceSampleSubscriber;

    /**
     * Constructor
     *
     * @param controller data access controller
     */
    public QuantPeptideSelectionPane(DataAccessController controller) {
        super(controller);
        this.currentIdentId = -1;
    }

    /**
     * Setup main pane
     */
    @Override
    protected void setupMainPane() {
        // set layout
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    }

    /**
     * Add the rest of the components
     */
    @Override
    protected void addComponents() {
        // add descriptive panel
        JPanel headerPanel = buildMetaDataPane();
        this.add(headerPanel, BorderLayout.NORTH);

        // create identification table
        try {
            pepTable = TableFactory.createQuantPeptideTable(controller, controller.getAvailablePeptideLevelScores());


            // add row selection listener
            pepTable.getSelectionModel().addListSelectionListener(new PeptideSelectionListener(pepTable));

            // add mouse listener
            String protAccColumnHeader = PeptideTableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
            pepTable.addMouseMotionListener(new TableCellMouseMotionListener(pepTable, protAccColumnHeader));

            JScrollPane scrollPane = new JScrollPane(pepTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            // add the component
            this.add(scrollPane, BorderLayout.CENTER);
        } catch (DataAccessException e) {
            String msg = "Failed to retrieve search engine details";
            logger.error(msg, e);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
        }
    }

    /**
     * Build meta data pane, this panel displays the identification type, search engine and search database
     *
     * @return JPanel   meta data pane
     */
    private JPanel buildMetaDataPane() {
        // add descriptive panel
        JPanel metaDataPanel = new JPanel();
        metaDataPanel.setOpaque(false);
        metaDataPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        // protein table label
        tableLabel = new JLabel("<html><b>" + PEPTIDE_TABLE_DESC + "</b></html>");
        metaDataPanel.add(tableLabel);

        return metaDataPanel;
    }

    @Override
    public void subscribeToEventBus(EventService eventBus) {
        // get local event bus
        if (eventBus == null) {
            eventBus = ContainerEventServiceFinder.getEventService(this);
        }

        // subscriber
        proteinIdentSubscriber = new SelectProteinIdentSubscriber();

        // subscriber
        referenceSampleSubscriber = new ReferenceSampleSubscriber();

        // subscribeToEventBus
        eventBus.subscribe(ProteinIdentificationEvent.class, proteinIdentSubscriber);
        eventBus.subscribe(ReferenceSampleChangeEvent.class, referenceSampleSubscriber);
    }

    /**
     * Subscribe to reference sample change event
     */
    private class ReferenceSampleSubscriber implements EventSubscriber<ReferenceSampleChangeEvent> {

        @Override
        public void onEvent(ReferenceSampleChangeEvent referenceSampleChangeEvent) {
            // get new index
            int newIndex = referenceSampleChangeEvent.getReferenceSampleIndex();
            if (newIndex != referenceSampleIndex) {
                // set the new index
                referenceSampleIndex = newIndex;

                QuantPeptideTableModel tableModel = (QuantPeptideTableModel) pepTable.getModel();
                // cancel on going tasks
                cancelOngoingTableUpdates(tableModel);
                // start a new task to update the table
                updateTable(tableModel, currentIdentId);
            }
        }

        /**
         * Cancel ongoing table update task
         *
         * @param tableModel peptide table model
         */
        private void cancelOngoingTableUpdates(ProgressiveListTableModel tableModel) {
            // stop any running retrieving task
            java.util.List<Task> existingTask = appContext.getTask(tableModel);
            for (Task task : existingTask) {
                appContext.cancelTask(task, true);
            }
        }

        /**
         * Fire up a peptide table update table in the background.
         *
         * @param tableModel peptide table model
         * @param identId    identification id
         */
        @SuppressWarnings("unchecked")
        private void updateTable(ProgressiveListTableModel tableModel, Comparable identId) {
            RetrieveQuantPeptideTableTask retrieveTask = new RetrieveQuantPeptideTableTask(QuantPeptideSelectionPane.this.getController(), identId, referenceSampleIndex);
            retrieveTask.addTaskListener(tableModel);
            TaskUtil.startBackgroundTask(retrieveTask, QuantPeptideSelectionPane.this.getController());
        }
    }

    /**
     * Listen to the event when a new protein identification has been selected
     */
    private class SelectProteinIdentSubscriber implements EventSubscriber<ProteinIdentificationEvent> {

        @Override
        public void onEvent(ProteinIdentificationEvent event) {
            Comparable identId = event.getIdentificationId();
            logger.debug("Identification has been selected: {}", identId);

            updateProteinLabel(identId);

            // clear peptide table,
            QuantPeptideTableModel tableModel = (QuantPeptideTableModel) pepTable.getModel();

            // reset sorting behavior
            pepTable.setRowSorter(new NumberTableRowSorter(tableModel));
            tableModel.removeAllRows();

            // cancel ongoing table update tasks
            cancelOngoingTableUpdates(tableModel);

            // update peptide table
            updateTable(tableModel, identId);
        }

        private void updateProteinLabel(Comparable identId) {
            if (identId == null) {
                tableLabel.setText("<html><b>" + PEPTIDE_TABLE_DESC + "</b></html>");
            } else {
                String acc = null;
                try {
                    acc = controller.getProteinAccession(identId);
                } catch (DataAccessException e) {
                    String msg = "Failed to get protein accession";
                    logger.error(msg);
                }
                tableLabel.setText("<html><b>" + PEPTIDE_TABLE_DESC + "</b> [" + acc + "]</html>");
            }
        }


        /**
         * Cancel ongoing table update task
         *
         * @param tableModel peptide table model
         */
        private void cancelOngoingTableUpdates(ProgressiveListTableModel tableModel) {
            // stop any running retrieving task
            java.util.List<Task> existingTask = appContext.getTask(tableModel);
            for (Task task : existingTask) {
                appContext.cancelTask(task, true);
            }
        }

        /**
         * Fire up a peptide table update table in the background.
         *
         * @param tableModel peptide table model
         * @param identId    identification id
         */
        @SuppressWarnings("unchecked")
        private void updateTable(ProgressiveListTableModel tableModel, Comparable identId) {
            RetrieveQuantPeptideTableTask retrieveTask = new RetrieveQuantPeptideTableTask(QuantPeptideSelectionPane.this.getController(), identId, QuantPeptideSelectionPane.this.referenceSampleIndex);
            retrieveTask.addTaskListener(tableModel);
            TaskUtil.startBackgroundTask(retrieveTask, QuantPeptideSelectionPane.this.getController());
        }
    }

    /**
     * Trigger when a peptide is selected from the table,
     * a new background task will be started to retrieve the peptide.
     */
    @SuppressWarnings("unchecked")
    private class PeptideSelectionListener implements ListSelectionListener {
        private final JTable table;

        private PeptideSelectionListener(JTable table) {
            this.table = table;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {

            if (!e.getValueIsAdjusting()) {
                int rowNum = table.getSelectedRow();
                if (rowNum >= 0) {
                    // get table model
                    QuantPeptideTableModel tableModel = (QuantPeptideTableModel) table.getModel();
                    // get identification and peptide column
                    int identColNum = tableModel.getColumnIndex(PeptideTableHeader.IDENTIFICATION_ID.getHeader());
                    int peptideColNum = tableModel.getColumnIndex(PeptideTableHeader.PEPTIDE_ID.getHeader());

                    // get identification and peptide id
                    if (table.getRowCount() > 0) {
                        int modelRowIndex = table.convertRowIndexToModel(rowNum);
                        Comparable identId = (Comparable) tableModel.getValueAt(modelRowIndex, identColNum);
                        Comparable peptideId = (Comparable) tableModel.getValueAt(modelRowIndex, peptideColNum);

                        if (peptideId != null && identId != null) {
                            // publish the event to local event bus
                            EventService eventBus = ContainerEventServiceFinder.getEventService(QuantPeptideSelectionPane.this);
                            eventBus.publish(new PSMEvent(QuantPeptideSelectionPane.this, controller, identId, peptideId));
                        }
                    }
                }
            }
        }
    }
}
