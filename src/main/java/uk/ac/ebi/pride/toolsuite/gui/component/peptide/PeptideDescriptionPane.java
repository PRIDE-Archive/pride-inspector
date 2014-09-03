package uk.ac.ebi.pride.toolsuite.gui.component.peptide;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.jdesktop.swingx.JXTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ListTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideSpecies;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideSpeciesTableModel;
import uk.ac.ebi.pride.toolsuite.gui.event.container.ChangeRankingThresholdEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.ExpandPanelEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.PeptideSpeciesEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.FilterPeptideRankingTask;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * PeptideDescriptionPane displays all peptides details.
 * <p/>
 * User: rwang
 * Date: 03-Sep-2010
 * Time: 11:53:51
 */
public class PeptideDescriptionPane extends DataAccessControllerPane {
    private static final Logger logger = LoggerFactory.getLogger(PeptideDescriptionPane.class);

    public static final String PEPTIDE_TABLE_DESC = "Peptide";
    public static final String FILTER_BY_RANKING_DESC = "Filter by PSM ranking: ";

    /**
     * peptide details table
     */
    private JXTable pepTable;

    /**
     * Constructor
     *
     * @param controller data access controller
     */
    public PeptideDescriptionPane(DataAccessController controller) {
        super(controller);
    }

    /**
     * Setup the main display pane
     */
    @Override
    protected void setupMainPane() {
        // set layout
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    }

    /**
     * Add the rest of components
     */
    @Override
    protected void addComponents() {
        // create identification table
        try {
            double minLimit = Double.parseDouble(uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext().getProperty("delta.mz.min.limit"));
            double maxLimit = Double.parseDouble(uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext().getProperty("delta.mz.max.limit"));
            pepTable = TableFactory.createPeptideSpeciesTable(PeptideRankingFilter.LESS_EQUAL_THAN_ONE.getRankingThreshold(), minLimit, maxLimit);
        } catch (DataAccessException e) {
            String msg = "Failed to retrieve search engine details";
            logger.error(msg, e);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
        }

        // meta data panel
        JPanel titlePanel = buildHeaderPane();
        this.add(titlePanel, BorderLayout.NORTH);

        // add selection listener
        pepTable.getSelectionModel().addListSelectionListener(new PeptideSelectionListener(pepTable));

        // add modification listener
        pepTable.getModel().addTableModelListener(new PeptideInsertListener(pepTable));

        JScrollPane scrollPane = new JScrollPane(pepTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // add the component
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Build the header panel
     *
     * @return JPanel  header panel
     */
    private JPanel buildHeaderPane() {
        JPanel metaDataPanel = buildMetaPane();

        // create button panel
        JToolBar toolBar = buildButtonPane();

        // add both meta data and button panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(metaDataPanel, BorderLayout.WEST);
        titlePanel.add(toolBar, BorderLayout.EAST);

        return titlePanel;
    }

    private JPanel buildMetaPane() {
        JPanel metaDataPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        metaDataPanel.setOpaque(false);

        // table label
        JLabel label = new JLabel(PEPTIDE_TABLE_DESC);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        metaDataPanel.add(label);

        return metaDataPanel;
    }

    private JToolBar buildButtonPane() {
        JToolBar toolBar = new JToolBar();
        toolBar.setBorder(BorderFactory.createEmptyBorder());
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);

        // filter peptide by ranking
        JLabel rankingFilterLabel = new JLabel(FILTER_BY_RANKING_DESC);
        toolBar.add(rankingFilterLabel);

        JComboBox rankingFilterList = getRankingFilterComboBox();
        toolBar.add(rankingFilterList);

        // add gap
        toolBar.add(Box.createRigidArea(new Dimension(10, 10)));

//        // load protein names
//        JButton loadAllProteinNameButton = GUIUtilities.createLabelLikeButton(null, null);
//        loadAllProteinNameButton.setForeground(Color.blue);
//        loadAllProteinNameButton.setAction(new ExtraProteinDetailAction(controller));
//        toolBar.add(loadAllProteinNameButton);
//
//        // add gap
//        toolBar.add(Box.createRigidArea(new Dimension(10, 10)));

//        // decoy filter
//        JButton decoyFilterButton = GUIUtilities.createLabelLikeButton(null, null);
//        decoyFilterButton.setForeground(Color.blue);
//        PrideAction action = appContext.getPrideAction(controller, DecoyFilterAction.class);
//        if (action == null) {
//            action = new DecoyFilterAction(controller);
//            appContext.addPrideAction(controller, action);
//        }
//        decoyFilterButton.setAction(action);
//        toolBar.add(decoyFilterButton);
//
//        // add gap
//        toolBar.add(Box.createRigidArea(new Dimension(10, 10)));

        // expand button
        Icon expandIcon = GUIUtilities.loadIcon(appContext.getProperty("expand.table.icon.small"));
        JButton expandButton = GUIUtilities.createLabelLikeButton(expandIcon, null);
        expandButton.setToolTipText("Expand");
        expandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventService eventBus = ContainerEventServiceFinder.getEventService(PeptideDescriptionPane.this);
                eventBus.publish(new ExpandPanelEvent(PeptideDescriptionPane.this, PeptideDescriptionPane.this));
            }
        });
        toolBar.add(expandButton);

        // Help button
        // load icon
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, null);
        helpButton.setToolTipText("Help");
        CSH.setHelpIDString(helpButton, "help.browse.peptide");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));
        toolBar.add(helpButton);

        return toolBar;
    }

    private JComboBox getRankingFilterComboBox() {
        JComboBox rankingFilterList = new JComboBox(PeptideRankingFilter.getRankingFilters().toArray());
        rankingFilterList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox filterComboBox = (JComboBox) e.getSource();
                String filter = (String) filterComboBox.getSelectedItem();
                int rankingThreshold = PeptideRankingFilter.getRankingThreshold(filter);
                PeptideSpeciesTableModel peptideSpeciesTableModel = (PeptideSpeciesTableModel) pepTable.getModel();
                FilterPeptideRankingTask filterPeptideRankingTask = new FilterPeptideRankingTask(peptideSpeciesTableModel, rankingThreshold);
                TaskUtil.startBackgroundTask(filterPeptideRankingTask, controller);

                // publish the event to local event bus
                EventService eventBus = ContainerEventServiceFinder.getEventService(PeptideDescriptionPane.this);
                eventBus.publish(new ChangeRankingThresholdEvent(PeptideDescriptionPane.this, rankingThreshold));
            }
        });
        if (!controller.getType().equals(DataAccessController.Type.MZIDENTML)) rankingFilterList.setEnabled(false);
        return rankingFilterList;
    }

    /**
     * Return peptide table
     *
     * @return JTable  peptide table
     */
    public JXTable getPeptideTable() {
        return pepTable;
    }

    /**
     * Trigger when a peptide is selected
     */
    @SuppressWarnings("unchecked")
    private class PeptideSelectionListener implements ListSelectionListener {
        private final JTable table;
        private int previousSelectedRow;

        private PeptideSelectionListener(JTable table) {
            this.table = table;
            this.previousSelectedRow = -1;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {

            if (!e.getValueIsAdjusting()) {
                int rowNum = table.getSelectedRow();
                if (rowNum >= 0 && (rowNum != previousSelectedRow)) {
                    previousSelectedRow = rowNum;
                    logger.debug("Peptide table has been clicked, row number: {}", rowNum);
                    // get table model
                    ListTableModel tableModel = (ListTableModel) pepTable.getModel();


                    // get spectrum reference column
                    int peptideSpeciesColumnIndex = tableModel.getColumnIndex(PeptideSpeciesTableModel.TableHeader.PEPTIDE_SPECIES_COLUMN.getHeader());
                    PeptideSpecies peptideSpecies = (PeptideSpecies) tableModel.getValueAt(table.convertRowIndexToModel(rowNum), peptideSpeciesColumnIndex);

                    // fire a background task to retrieve peptide
                    if (peptideSpecies != null) {
                        // publish the event to local event bus
                        EventService eventBus = ContainerEventServiceFinder.getEventService(PeptideDescriptionPane.this);
                        eventBus.publish(new PeptideSpeciesEvent(PeptideDescriptionPane.this, peptideSpecies));
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
                    table.changeSelection(0, 0, true, false);
                }
            }
        }
    }
}
