package uk.ac.ebi.pride.toolsuite.gui.component.protein;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.jdesktop.swingx.JXTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.engine.SearchEngineType;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.ExtraProteinDetailAction;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProteinTableHeader;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProteinTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.SortableProteinNode;
import uk.ac.ebi.pride.toolsuite.gui.component.table.sorttreetable.ProteinSortableTreeTable;
import uk.ac.ebi.pride.toolsuite.gui.event.container.ExpandPanelEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.ProteinIdentificationEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.SortProteinTableEvent;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

/**
 * IdentificationSelectionPane displays identification related details in a table.
 * <p/>
 * @author ypriverol
 * @author rwang
 */

public class ProteinSelectionPane extends DataAccessControllerPane{

    private static final Logger logger = LoggerFactory.getLogger(ProteinTabPane.class.getName());

    /**
     * table for display the identifications
     */
    private JXTable identTable;

    //private SortProteinTableEventSubscriber sortProteinTableEventSubscriber;

    public ProteinSelectionPane(DataAccessController controller) {
        super(controller);
        // enable annotation
        AnnotationProcessor.process(this);
    }

    @Override
    protected void setupMainPane() {
        // set layout
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    }

    @EventSubscriber(eventClass = SortProteinTableEvent.class)
    public void onSortProteinTableEvent(SortProteinTableEvent evt){
        if(evt.getType() == SortProteinTableEvent.Type.ENABLE_SORT && identTable instanceof ProteinSortableTreeTable){
            identTable.setSortable(true);
            identTable.setAutoCreateRowSorter(true);
            ((ProteinSortableTreeTable)identTable).setRowSorter();

        }else if(evt.getType() == SortProteinTableEvent.Type.DISABLE_SORT && identTable instanceof ProteinSortableTreeTable){
            identTable.setSortable(false);
            identTable.setAutoCreateRowSorter(false);
            identTable.setRowSorter(null);
        }
       // ((TreeTableRowSorter) identTable.getRowSorter()).setSortOrder(0, SortOrder.ASCENDING);
       /*Remove last line of the table I need to know why is inserting this line*/

    }

    @Override
    protected void addComponents() {
        // create identification table
        identTable = TableFactory.createProteinTable(controller.getAvailableProteinLevelScores(), controller.hasProteinAmbiguityGroup(),controller);

        // createAttributedSequence header panel
        JPanel headerPanel = buildHeaderPane();
        this.add(headerPanel, BorderLayout.NORTH);


        // add row selection listener
        if(controller.hasProteinAmbiguityGroup()){
            TreeSelectionModel selectionModel = ((ProteinSortableTreeTable) identTable).getTreeSelectionModel();
            selectionModel.addTreeSelectionListener(new IdentificationTreeSelectionListener(identTable));
            identTable.getModel().addTableModelListener(new ProteinInsertListener(identTable));
        }else{
            ListSelectionModel selectionModel =  identTable.getSelectionModel();
            selectionModel.addListSelectionListener(new IdentificationSelectionListener(identTable));
            identTable.getModel().addTableModelListener(new ProteinInsertListener(identTable));
        }

        // add identification table to scroll pane
        JScrollPane scrollPane = new JScrollPane(identTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel buildHeaderPane() {
        // add meta data panel
        JPanel metaDataPanel = buildMetaDataPane();
        JToolBar buttonPanel = buildButtonPane();
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(metaDataPanel, BorderLayout.WEST);
        titlePanel.add(buttonPanel, BorderLayout.EAST);

        return titlePanel;
    }

    private JPanel buildMetaDataPane() {
        // add descriptive panel
        JPanel metaDataPanel = new JPanel();
        metaDataPanel.setOpaque(false);
        metaDataPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        try {
            // protein table label
            JLabel tableLabel = new JLabel("Protein");
            tableLabel.setFont(tableLabel.getFont().deriveFont(Font.BOLD));

            metaDataPanel.add(tableLabel);
            metaDataPanel.add(Box.createRigidArea(new Dimension(5, 0)));

            // identification type
//            Collection<Comparable> identIds = controller.getProteinIds();
//            Comparable identId = CollectionUtils.getElement(identIds, 0);

//            // search engine
//            Object engine = identId == null ? "Unknown" : getSearchEngineName(controller.getSearchEngineTypes());
//            engine = engine == null ? "Unknown" : engine;
//            JLabel dbLabel = new JLabel("<html><b>Search Engine</b>: " + engine + "</htlm>");
//            dbLabel.setToolTipText(engine.toString());
//            metaDataPanel.add(dbLabel);
//            metaDataPanel.add(Box.createRigidArea(new Dimension(5, 0)));
//
//            // search database
//            Object database = ((identId == null) || (controller.getSearchDatabase(identId).getName() == null)) ? "Unknown" : controller.getSearchDatabase(identId).getName();
//            database = database == null ? "Unknown" : database;
//            JLabel engineLabel = new JLabel("<html><b>Search Database</b>: </html>");
//            JLabel engineValLabel = new JLabel(database.toString());
//            engineValLabel.setPreferredSize(new Dimension(200, 15));
//            engineValLabel.setToolTipText(database.toString());
//            metaDataPanel.add(engineLabel);
//            metaDataPanel.add(engineValLabel);
        } catch (DataAccessException e) {
            String msg = "Failed to createAttributedSequence meta data pane for identifications";
            logger.error(msg, e);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
        }
        return metaDataPanel;
    }

    private String getSearchEngineName(Collection<SearchEngineType> searchEngineTypes) {
        StringBuilder builder = new StringBuilder();

        for (SearchEngineType searchEngineType : searchEngineTypes) {
            builder.append(searchEngineType.toString()).append(",");
        }

        String searchEngineName = builder.toString();
        if (searchEngineName.length() > 1) {
            searchEngineName.substring(0, searchEngineName.length() - 1);
        }

        return searchEngineName;
    }

    /**
     * Build toolbar which contains all the buttons.
     *
     * @return JToolbar    tool bar
     */
    private JToolBar buildButtonPane() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.setOpaque(false);

        // load protein names
        JButton loadAllProteinNameButton = GUIUtilities.createLabelLikeButton(null, null);
        loadAllProteinNameButton.setForeground(Color.blue);

        loadAllProteinNameButton.setAction(new ExtraProteinDetailAction(controller));
        toolBar.add(loadAllProteinNameButton);

        // add gap
        toolBar.add(Box.createRigidArea(new Dimension(10, 10)));

        // decoy filter
//        JButton decoyFilterButton = GUIUtilities.createLabelLikeButton(null, null);
//        decoyFilterButton.setForeground(Color.blue);
//        PrideAction action = appContext.getPrideAction(controller, DecoyFilterAction.class);
//        if (action == null) {
//            action = new DecoyFilterAction(controller);
//            appContext.addPrideAction(controller, action);
//        }
//        decoyFilterButton.setAction(action);
//        toolBar.add(decoyFilterButton);

        // add gap
        toolBar.add(Box.createRigidArea(new Dimension(10, 10)));

        // expand button
        Icon expandIcon = GUIUtilities.loadIcon(appContext.getProperty("expand.table.icon.small"));
        JButton expandButton = GUIUtilities.createLabelLikeButton(expandIcon, null);
        expandButton.setToolTipText("Expand");
        expandButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventService eventBus = ContainerEventServiceFinder.getEventService(ProteinSelectionPane.this);
                eventBus.publish(new ExpandPanelEvent(ProteinSelectionPane.this, ProteinSelectionPane.this));
            }
        });

        toolBar.add(expandButton);

        // Help button
        // load icon
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, null);
        helpButton.setToolTipText("Help");
        helpButton.setForeground(Color.blue);
        CSH.setHelpIDString(helpButton, "help.browse.protein");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));
        toolBar.add(helpButton);

        return toolBar;
    }

    /**
     * Return the identification table
     *
     * @return JTable  identification details table.
     */
    public JTable getIdentificationTable() {
        return identTable;
    }


    /**
     * This selection listener listens to Identification table for any selection on the row.
     * It will then fire a property change event with the selected identification id.
     */
    private class IdentificationTreeSelectionListener implements TreeSelectionListener {
        private final JXTable table;

        private IdentificationTreeSelectionListener(JXTable table) {
            this.table = table;
        }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            int rowNum = table.getSelectedRow();
            int rowCnt = table.getRowCount();
            if (rowCnt > 0 && rowNum >= 0) {
                // get table model
                // fire a property change event with selected identification id
                    TreeSelectionModel treeSelectionModel = ((ProteinSortableTreeTable)table).getTreeSelectionModel();
                    TreePath selectionPath = treeSelectionModel.getSelectionPath();
                    Object node = selectionPath.getLastPathComponent();

                    Comparable identId = ((SortableProteinNode) node).getProteinId();

                    // publish the event to local event bus
                    EventService eventBus = ContainerEventServiceFinder.getEventService(ProteinSelectionPane.this);
                    eventBus.publish(new ProteinIdentificationEvent(ProteinSelectionPane.this, controller, identId));
            }
        }
    }

    /**
     *  * This selection listener listens to Identification table for any selection on the row.
         * It will then fire a property change event with the selected identification id.
         */
    private class IdentificationSelectionListener implements ListSelectionListener {

        private final JXTable table;

            private IdentificationSelectionListener(JXTable table) {
                this.table = table;
            }

            @Override
            public void valueChanged(ListSelectionEvent e) {
                int rowNum = table.getSelectedRow();
                int rowCnt = table.getRowCount();
                if (rowCnt > 0 && rowNum >= 0) {
                    ProteinTableModel proteinTableModel = (ProteinTableModel) table.getModel();
                    // get Protein ID
                    int identColNum = proteinTableModel.getColumnIndex(ProteinTableHeader.PROTEIN_ID.getHeader());
                    int modelRowIndex = table.convertRowIndexToModel(rowNum);
                    Comparable identId = (Comparable) proteinTableModel.getValueAt(modelRowIndex, identColNum);

                    // publish the event to local event bus
                    EventService eventBus = ContainerEventServiceFinder.getEventService(ProteinSelectionPane.this);
                    eventBus.publish(new ProteinIdentificationEvent(ProteinSelectionPane.this, controller, identId));
                }
            }
    }

    /**
     * Trigger when a protein is inserted on the table,
     * a new background task will be started to retrieve the peptide.
     */
    @SuppressWarnings("unchecked")
    private class ProteinInsertListener implements TableModelListener {

        private final JTable table;

        private ProteinInsertListener(JTable table) {
            this.table = table;
        }

        @Override
        public void tableChanged(TableModelEvent e) {
            if (e.getType() == TableModelEvent.INSERT || e.getType() == TableModelEvent.UPDATE) {
                if (table.getRowCount() > 0 && table.getSelectedRow() < 0) {
                    table.changeSelection(0, 0, false, false);
                }
            }
        }
    }
}
