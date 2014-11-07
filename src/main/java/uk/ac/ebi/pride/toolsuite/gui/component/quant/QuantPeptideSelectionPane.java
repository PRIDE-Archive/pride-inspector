package uk.ac.ebi.pride.toolsuite.gui.component.quant;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.bushe.swing.event.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.listener.TableCellMouseMotionListener;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideTableHeader;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProgressiveListTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.QuantPeptideTableModel;
import uk.ac.ebi.pride.toolsuite.gui.event.QuantSelectionEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.ReferenceSampleChangeEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.PSMEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.RetrieveQuantPeptideTableTask;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Quantitative Selection Pane.
 * @author rwang
 * @author ypriverol
 * Date: 15/08/2011
 * Time: 11:33
 */
public class QuantPeptideSelectionPane extends DataAccessControllerPane implements EventBusSubscribable {

    private static final Logger logger = LoggerFactory.getLogger(QuantPeptideSelectionPane.class);

    private static final int MAX_NUMBER_OF_PROTEINS = 10;

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
     * The number of selected proteins
     */
    private int numOfSelectedPeptides = 0;

    private Map<Comparable, java.util.List<Comparable>> selectedPeptides;

        /**
     * Constructor
     *
     * @param controller data access controller
     */
    public QuantPeptideSelectionPane(DataAccessController controller) {
        super(controller);
        this.currentIdentId = -1;
        this.selectedPeptides = new HashMap<Comparable, List<Comparable>>();
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
//        JPanel headerPanel = buildMetaDataPane();
//        this.add(headerPanel, BorderLayout.NORTH);

        // create identification table
        try {
            if(!controller.getType().equals(DataAccessController.Type.MZTAB))
                pepTable = TableFactory.createQuantPeptideTable(controller, controller.getAvailablePeptideLevelScores());
            else
                pepTable = TableFactory.createQuantPeptideTable(controller, controller.getAvailablePeptideLevelScores(), controller.getStudyVariables());

            // createAttributedSequence header panel
            JPanel headerPanel = buildHeaderPane();
            this.add(headerPanel, BorderLayout.NORTH);

            // add row selection listener
            pepTable.getSelectionModel().addListSelectionListener(new PeptideSelectionListener(pepTable));

            // add mouse listener
            String protAccColumnHeader = PeptideTableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
            pepTable.addMouseMotionListener(new TableCellMouseMotionListener(pepTable, protAccColumnHeader));

            // add table model listener to listen to checkbox selection event
            TableModel tableModel = pepTable.getModel();
            tableModel.addTableModelListener(new CheckBoxSelectionListener());

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
     * This builds the top panel to display, it includes
     *
     * @return JPanel  header panel
     */
    private JPanel buildHeaderPane() {
        // add meta data panel
        JPanel metaDataPanel = buildMetaDataPane();
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(metaDataPanel, BorderLayout.WEST);

        return titlePanel;
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
        eventBus.subscribe(QuantSelectionEvent.class, proteinIdentSubscriber);
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
            RetrieveQuantPeptideTableTask retrieveTask = new RetrieveQuantPeptideTableTask(QuantPeptideSelectionPane.this.getController(), identId, referenceSampleIndex, true, selectedPeptides);
            retrieveTask.addTaskListener(tableModel);
            TaskUtil.startBackgroundTask(retrieveTask, QuantPeptideSelectionPane.this.getController());
        }
    }

    /**
     * Listen to the event when a new protein identification has been selected
     */
    private class SelectProteinIdentSubscriber implements EventSubscriber<QuantSelectionEvent> {

        @Override
        public void onEvent(QuantSelectionEvent event) {
            if (QuantSelectionEvent.Type.PROTEIN.equals(event.getType())) {
                Comparable id = event.getId();
                QuantPeptideTableModel tableModel = (QuantPeptideTableModel) pepTable.getModel();
                DataAccessController controller = event.getController();
                if (event.isSelected()) {
                    updateTable(tableModel, id, true);
                } else {
                    updateTable(tableModel, id, false);
                }
            }

//            Comparable identId = event.getId();
//
//            logger.debug("Identification has been selected: {}", identId);
//
//            updateProteinLabel(identId);
//
//            // clear peptide table,
//            QuantPeptideTableModel tableModel = (QuantPeptideTableModel) pepTable.getModel();
//
//            // reset sorting behavior
//            pepTable.setRowSorter(new NumberTableRowSorter(tableModel));
//            tableModel.removeAllRows();
//
//            // cancel ongoing table update tasks
//            cancelOngoingTableUpdates(tableModel);
//
//            // update peptide table
//            updateTable(tableModel, identId);
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
        private void updateTable(ProgressiveListTableModel tableModel, Comparable identId, boolean status) {
            RetrieveQuantPeptideTableTask retrieveTask = new RetrieveQuantPeptideTableTask(QuantPeptideSelectionPane.this.getController(), identId, QuantPeptideSelectionPane.this.referenceSampleIndex, status, selectedPeptides);
            retrieveTask.addTaskListener(tableModel);
            TaskUtil.startBackgroundTask(retrieveTask, QuantPeptideSelectionPane.this.getController());
            if(!status){
                /*
                 * Update the chart panel when whe remove the Peptides from Table if click the Protein.
                 */
                Collection<Comparable> peptideIds = controller.getPeptideIds(identId);
                for(Comparable peptideId: peptideIds){
                    EventService eventBus = ContainerEventServiceFinder.getEventService(QuantPeptideSelectionPane.this);
                    eventBus.publish(new QuantSelectionEvent(pepTable, identId, referenceSampleIndex, QuantSelectionEvent.Type.PEPTIDE, false, controller, peptideId));
                }

            }
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

    /**
     *
     */
    private class CheckBoxSelectionListener implements TableModelListener {
        /**
         * whether to ignore the next event
         */
        private boolean ignore = false;

        @Override
        public void tableChanged(TableModelEvent e) {

            int firstRowIndex = e.getFirstRow();

            int lastRowIndex = e.getLastRow();

            int columnIndex = e.getColumn();

            int type = e.getType();

            // get table model
            QuantPeptideTableModel tableModel = (QuantPeptideTableModel) pepTable.getModel();

            int checkBoxColumnIndex = tableModel.getColumnIndex(PeptideTableHeader.COMPARE.getHeader());

            if (!ignore && firstRowIndex == lastRowIndex && columnIndex == checkBoxColumnIndex && TableModelEvent.UPDATE == type) {
                // get protein identification id
                int identColNum = tableModel.getColumnIndex(PeptideTableHeader.IDENTIFICATION_ID.getHeader());

                Comparable identId = (Comparable) tableModel.getValueAt(firstRowIndex, identColNum);

                int peptideColNum = tableModel.getColumnIndex(PeptideTableHeader.PEPTIDE_ID.getHeader());

                Comparable identPeptide = (Comparable) tableModel.getValueAt(firstRowIndex, peptideColNum);

                // notify protein selection
                notifyPeptideSelection(tableModel, identId, identPeptide, firstRowIndex, columnIndex);
            }

            ignore = false;
        }

        private void notifyPeptideSelection(TableModel tableModel, Comparable identId, Comparable idPeptide, int rowModelIndex, int colModelIndex) {

            Boolean selected = (Boolean) tableModel.getValueAt(rowModelIndex, colModelIndex);

            // check whether reached the maximum number
            if (selected && numOfSelectedPeptides >= MAX_NUMBER_OF_PROTEINS) {
                // show warnings
                GUIUtilities.warn(uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getMainComponent(),
                        "Protein Selection limited is reached: " + MAX_NUMBER_OF_PROTEINS + " maximum",
                        "Selection Limit Reached");
                ignore = true;
                tableModel.setValueAt(false, rowModelIndex, colModelIndex);
            } else {
                EventService eventBus = ContainerEventServiceFinder.getEventService(QuantPeptideSelectionPane.this);
                eventBus.publish(new QuantSelectionEvent(pepTable, identId, referenceSampleIndex, QuantSelectionEvent.Type.PEPTIDE, selected, controller, idPeptide));
                if (selected) {
                    numOfSelectedPeptides++;
                    List<Comparable> peptides = new ArrayList<Comparable>();
                    if(selectedPeptides.containsKey(identId)){
                        peptides = selectedPeptides.get(identId);
                    }
                    peptides.add(idPeptide);
                    selectedPeptides.put(identId,peptides);
                } else {
                    numOfSelectedPeptides--;
                    if(selectedPeptides.containsKey(identId)){
                        List<Comparable> peptides = selectedPeptides.get(identId);
                        peptides.remove(idPeptide);
                        if(peptides.isEmpty())
                            selectedPeptides.remove(identId);
                        else
                            selectedPeptides.put(identId,peptides);
                    }
                }
            }
        }
    }
}
