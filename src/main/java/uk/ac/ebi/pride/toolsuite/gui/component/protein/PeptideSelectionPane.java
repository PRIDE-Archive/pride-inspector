package uk.ac.ebi.pride.toolsuite.gui.component.protein;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.bushe.swing.event.EventSubscriber;
import org.jdesktop.swingx.table.TableColumnExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.component.proteingroup.ProteinGroupPane;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.event.ProteinGroupChartEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.LoadProteinGroupNodeTreeTask;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.Modification;
import uk.ac.ebi.pride.utilities.data.core.Peptide;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.listener.TableCellMouseMotionListener;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideTableHeader;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProgressiveListTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.sorter.NumberTableRowSorter;
import uk.ac.ebi.pride.toolsuite.gui.event.container.PSMEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.ProteinIdentificationEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.RetrievePeptideTableTask;
import uk.ac.ebi.pride.utilities.data.core.Protein;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * PeptideSelectionPane displays a list of peptide for a selected identification.
 * <p/>
 * @author rwang
 * Date: 16-Apr-2010
 * Time: 11:26:45
 */
public class PeptideSelectionPane extends DataAccessControllerPane<Peptide, Void> implements EventBusSubscribable {

    private static final Logger logger = LoggerFactory.getLogger(PeptideSelectionPane.class);

    /**
     * the title for ptm label
     */
    private final static String PTM_LABEL = "<html><b>Modified residues</b>: ";
    private final static String PEPTIDE_TABLE_DESC = "PSM List";

    /**
     * peptide table for peptide related details
     */
    private JTable pepTable;
    /**
     * Peptide table label also displays the selected protein accession
     */
    private JLabel tableLabel;

    /**
     * PTM label display an overview of the PTMs
     */
    private JLabel ptmLabel;

    /**
     * Subscriber for event bus on protein selection event
     */
    private SelectProteinIdentSubscriber proteinIdentSubscriber;

    /**
     * Current protein to be passed to the Protein Group;
     */
    private Comparable currentProtein;

    private Comparable currentProteinGroupId;

    private JButton proteinGroupButton;

    EventService eventService;

    private ProteinGroupPane proteinGroupPane;

    private AddNodeSuscriber addNodeSuscriber;

    /**
     * Constructor
     *
     * @param controller data access controller
     */
    public PeptideSelectionPane(DataAccessController controller) {
        super(controller);
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
        JPanel metaDataPanel = new JPanel();
        metaDataPanel.setOpaque(false);
        metaDataPanel.setLayout(new BoxLayout(metaDataPanel, BoxLayout.LINE_AXIS));

        JPanel messagePanel = new JPanel();
        messagePanel.setOpaque(false);
        messagePanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        // table label
        tableLabel = new JLabel("<html><b>" + PEPTIDE_TABLE_DESC + "</b></html>");
        messagePanel.add(tableLabel);

        // rigid area
        messagePanel.add(Box.createRigidArea(new Dimension(5, 0)));

        // PTM
        ptmLabel = new JLabel(PTM_LABEL + "NONE</html>");
        messagePanel.add(ptmLabel);
        messagePanel.add(Box.createHorizontalGlue());

        String name = uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext().getProperty("load.protein.groups.detail.title");
        Icon icon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("load.protein.groups.detail.small.icon"));
        proteinGroupButton = new JButton(name,icon);
        proteinGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               if(currentProtein != null){
                   JDialog proteinGroup = new JDialog();
                   proteinGroup.setSize(new Dimension(700,600));
                   proteinGroup.setPreferredSize(new Dimension(700,600));
                   proteinGroup.setTitle("Protein Groups");
                   proteinGroupPane = new ProteinGroupPane(controller,currentProtein, currentProteinGroupId);
                   proteinGroup.add(proteinGroupPane);
                   proteinGroup.setLocationRelativeTo(null);
                   proteinGroup.pack();
                   //noinspection deprecation
                   proteinGroup.setModal(true);
                   proteinGroup.setVisible(true);
                   LoadProteinGroupNodeTreeTask taskNode = new LoadProteinGroupNodeTreeTask(controller, currentProtein, currentProteinGroupId);
                   taskNode.addTaskListener(proteinGroupPane);
                   TaskUtil.startBackgroundTask(taskNode);
               }

            }
        });

        if(controller.hasProteinAmbiguityGroup() && currentProtein != null){
            // add a glue to fill the empty space
            proteinGroupButton.setEnabled(true);

        }else{
            proteinGroupButton.setEnabled(false);
        }
        metaDataPanel.add(messagePanel);
        // add a glue to fill the empty space
        metaDataPanel.add(Box.createHorizontalGlue());

        metaDataPanel.add(proteinGroupButton);

        this.add(metaDataPanel, BorderLayout.NORTH);

        // create identification table
        try {
            pepTable = TableFactory.createPeptideTable(controller.getAvailablePeptideLevelScores(), controller);
            // hide protein accession column
            TableColumnExt proteinAccCol = (TableColumnExt) pepTable.getColumn(PeptideTableHeader.PROTEIN_ACCESSION_COLUMN.getHeader());
            proteinAccCol.setVisible(false);
        } catch (DataAccessException e) {
            String msg = "Failed to retrieve search engine details";
            logger.error(msg, e);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
        }

        // add row selection listener
        pepTable.getSelectionModel().addListSelectionListener(new PeptideSelectionListener(pepTable));

        pepTable.getModel().addTableModelListener(new PeptideInsertListener(pepTable));

        // add mouse listener
        String protAccColumnHeader = PeptideTableHeader.PROTEIN_ACCESSION_COLUMN.getHeader();
        pepTable.addMouseMotionListener(new TableCellMouseMotionListener(pepTable, protAccColumnHeader));

        JScrollPane scrollPane = new JScrollPane(pepTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // add the component
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Get peptide table
     *
     * @return peptide table
     */
    public JTable getPeptideTable() {
        return pepTable;
    }

    @Override
    public void subscribeToEventBus(EventService eventBus) {
        // get local event bus
        if (eventBus == null) {
            eventBus = ContainerEventServiceFinder.getEventService(this);
        }

        // subscriber
        addNodeSuscriber = new AddNodeSuscriber();

        // subscribeToEventBus
        eventBus.subscribe(ProteinGroupChartEvent.class, addNodeSuscriber);

        proteinIdentSubscriber = new SelectProteinIdentSubscriber();

        eventBus.subscribe(ProteinIdentificationEvent.class, proteinIdentSubscriber);

    }

    /**
     * Listen to the event when a new protein identification has been selected
     */
    private class SelectProteinIdentSubscriber implements EventSubscriber<ProteinIdentificationEvent> {

        @Override
        public void onEvent(ProteinIdentificationEvent event) {

            Comparable identId = event.getIdentificationId();

            currentProtein = identId;

            currentProteinGroupId = event.getProteinGroupId();

            logger.debug("Identification has been selected: {}", identId);

            updateProteinLabel(identId);

            // update ptm label
            updatePTMLabel(identId);

            // clear peptide table,
            PeptideTableModel tableModel = (PeptideTableModel) pepTable.getModel();

            // reset sorting behavior
            pepTable.setRowSorter(new NumberTableRowSorter(tableModel));
            tableModel.removeAllRows();

            // cancel ongoing table update tasks
            cancelOngoingTableUpdates(tableModel);

            // update peptide table
            updateTable(tableModel, identId);

            if(controller.hasProteinAmbiguityGroup() && currentProtein != null){
                // add a glue to fill the empty space
                proteinGroupButton.setEnabled(true);
            }else{
                proteinGroupButton.setEnabled(false);
            }
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
         * Update PTM summary label
         *
         * @param identId identification id
         */
        private void updatePTMLabel(Comparable identId) {
            try {
                // generate the ptm label string
                String ptmValues = generateModString(identId);

                // set the string to label
                if ("".equals(ptmValues)) {
                    ptmLabel.setText(PTM_LABEL + "NONE</html>");
                } else {
                    ptmLabel.setText(PTM_LABEL + ptmValues + "</html>");
                }
            } catch (DataAccessException e) {
                String msg = "Failed to generated PTM summary label";
                logger.error(msg, e);
                appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
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
            try {
                RetrievePeptideTableTask retrieveTask = new RetrievePeptideTableTask(PeptideSelectionPane.this.getController(), identId);
                retrieveTask.addTaskListener(tableModel);
                TaskUtil.startBackgroundTask(retrieveTask);
            } catch (DataAccessException e) {
                String msg = "Failed to retrieve information for peptide table";
                logger.error(msg, e);
                appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
            }
        }

        /**
         * Generate PTM summary string, it gathers all the unique amino acids with the same modification.
         *
         * @param identId identificaiton id
         * @return String   ptm summary string in the format of [amin acids - monoisotopic weight]
         * @throws DataAccessException data access exception
         */
        private String generateModString(Comparable identId) throws DataAccessException {
            String modStr = "";
            Map<String, Map<String, Double>> modMap = new HashMap<String, Map<String, Double>>();
            Collection<Comparable> peptideIds = controller.getPeptideIds(identId);
            if (peptideIds != null) {
                for (Comparable peptideId : peptideIds) {
                    String seq = controller.getPeptideSequence(identId, peptideId);
                    Collection<Modification> mods = controller.getPTMs(identId, peptideId);
                    for (Modification mod : mods) {
                        // get accession
                        String accession = (mod.getId() != null) ? mod.getId().toString() : null;
                        Map<String, Double> aminoAcidMap = modMap.get(accession);
                        if (aminoAcidMap == null) {
                            aminoAcidMap = new HashMap<String, Double>();
                            modMap.put(accession, aminoAcidMap);
                        }
                        // get the amino acid according to the location
                        int location = mod.getLocation();
                        location = location == 0 ? 1 : (location == accession.length() + 1 ? location - 1 : location);
                        if (location > 0 && location <= seq.length()) {
                            String aminoAcid = String.valueOf(seq.charAt(location - 1));
                            // get delta mass (monoisotopic)
                            double massDelta = -1;
                            java.util.List<Double> massDeltas = mod.getMonoisotopicMassDelta();
                            if (massDeltas != null && !massDeltas.isEmpty()) {
                                massDelta = mod.getMonoisotopicMassDelta().get(0);
                            }
                            aminoAcidMap.put(aminoAcid, massDelta);
                        }
                    }
                }
            }

            DecimalFormat formatter = new DecimalFormat("#.####");
            for (Map.Entry aminoAcidMapEntry : modMap.entrySet()) {
                Map<String, Double> aminoAcidMap = (Map<String, Double>) aminoAcidMapEntry.getValue();
                StringBuilder aminoAcids = new StringBuilder();
                double massDelta = -1;
                if(aminoAcidMap != null && !aminoAcidMap.isEmpty()){
                    for (Map.Entry<String, Double> aminoAcidEntry : aminoAcidMap.entrySet()) {
                        aminoAcids.append(aminoAcidEntry.getKey());
                        massDelta = aminoAcidEntry.getValue();
                    }
                    String aaStringAA = aminoAcids.toString();
                    String aaString = "";
                    if (!aaStringAA.isEmpty()) {
                        for (Character aaChar : aaStringAA.toCharArray()) {
                            aaString += aaChar.toString() + ",";
                        }
                    }
                    aaString = aaString.substring(0, aaString.length() - 1);
                    modStr += "[" + aaString + " - " + (massDelta == -1 ? " Unknown" : formatter.format(massDelta)) + "] ";
                }

            }
            return modStr;
        }
    }

    /**
     * Subscribe to reference sample change event
     */
    private class AddNodeSuscriber implements EventSubscriber<ProteinGroupChartEvent> {

        @Override
        public void onEvent(ProteinGroupChartEvent proteinGroupChartEvent) {
            // get new index
            ProteinGroupChartEvent.Type type = proteinGroupChartEvent.getType();
            if(type == ProteinGroupChartEvent.Type.PROTEIN){
                Protein protein = (Protein) proteinGroupChartEvent.getSource();
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
                    PeptideTableModel tableModel = (PeptideTableModel) table.getModel();
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
                            EventService eventBus = ContainerEventServiceFinder.getEventService(PeptideSelectionPane.this);
                            eventBus.publish(new PSMEvent(PeptideSelectionPane.this, controller, identId, peptideId));
                        }
                    }
                }
            }
        }
    }

    /**
     * Trigger when a peptide is inserted on the table,
     * a new background task will be started to retrieve the peptide.
     */
    @SuppressWarnings("unchecked")
    private class PeptideInsertListener implements TableModelListener {

        private final JTable table;

        private PeptideInsertListener(JTable table) {
            this.table = table;
        }

        @Override
        public void tableChanged(TableModelEvent e) {
            if (e.getType() == TableModelEvent.UPDATE || e.getType() == TableModelEvent.INSERT) {
                if (table.getRowCount() > 0 && table.getSelectedRow() < 0) {
                    PeptideTableModel peptideModel = (PeptideTableModel) table.getModel();
                    table.changeSelection(0, peptideModel.getColumnIndex(PeptideTableHeader.PEPTIDE_COLUMN.getHeader()), false, false);
                }
            }
        }


    }


}
