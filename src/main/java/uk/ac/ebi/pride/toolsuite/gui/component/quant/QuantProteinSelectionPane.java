package uk.ac.ebi.pride.toolsuite.gui.component.quant;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.bushe.swing.event.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.ExportQuantitativeDataAction;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.ExtraProteinDetailAction;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.OpenHelpAction;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.SetRefSampleAction;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;
import uk.ac.ebi.pride.toolsuite.gui.component.protein.ProteinTabPane;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProgressiveListTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProteinTableHeader;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.QuantProteinTableModel;
import uk.ac.ebi.pride.toolsuite.gui.event.QuantSelectionEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.ReferenceSampleChangeEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.ProteinIdentificationEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.RetrieveQuantProteinTableTask;
import uk.ac.ebi.pride.utilities.term.QuantCvTermReference;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

/**
 * @author rwang
 * Date: 15/08/2011
 * Time: 11:33
 */
public class QuantProteinSelectionPane extends DataAccessControllerPane implements EventBusSubscribable {

    private static final Logger logger = LoggerFactory.getLogger(ProteinTabPane.class.getName());

    private static final int MAX_NUMBER_OF_PROTEINS = 10;

    /**
     * table for display the identifications
     */
    private JTable proteinTable;

    /**
     * button to set reference sample
     */
    private JButton refSampleButton;

    /**
     * Button to open a popup menu
     */
    private JButton moreButton;

    /**
     * Menu contains more options
     */
    private JPopupMenu moreMenu;

    /**
     * The current reference sample index
     */
    private int referenceSampleIndex = -1;

    /**
     * The number of selected proteins
     */
    private int numOfSelectedProteins = 0;

    /**
     * Subscriber to reference sample change event
     */
    private ReferenceSampleSubscriber referenceSampleSubscriber;

    /**
     * Constructor
     *
     * @param controller data access controller
     * @param parentComp parent component
     */
    public QuantProteinSelectionPane(DataAccessController controller, JComponent parentComp) {
        super(controller, parentComp);
        try {
            this.referenceSampleIndex = controller.getReferenceSubSampleIndex();
        } catch (DataAccessException e) {
            logger.error("Failed to get the default reference sub sample index");
        }
    }

    /**
     * Setup the main pane
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
            proteinTable = TableFactory.createQuantProteinTable(controller, controller.getAvailableProteinLevelScores());
        } catch (DataAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // createAttributedSequence header panel
        JPanel headerPanel = buildHeaderPane();
        this.add(headerPanel, BorderLayout.NORTH);

        // add row selection listener
        ListSelectionModel selectionModel = proteinTable.getSelectionModel();
        selectionModel.addListSelectionListener(new IdentificationSelectionListener(proteinTable));

        // add table model listener to listen to checkbox selection event
        TableModel tableModel = proteinTable.getModel();
        tableModel.addTableModelListener(new CheckBoxSelectionListener());

        // add identification table to scroll pane
        JScrollPane scrollPane = new JScrollPane(proteinTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * This builds the top panel to display, it includes
     *
     * @return JPanel  header panel
     */
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
        try {
            // protein table label
            JLabel tableLabel = new JLabel("<html><b>Protein</b></html>");
            metaDataPanel.add(tableLabel);
            metaDataPanel.add(Box.createRigidArea(new Dimension(5, 0)));

            // qauntitation methods
            String method = "";
            Collection<QuantCvTermReference> labelFreeMethods = controller.getProteinLabelFreeQuantMethods();
            for (QuantCvTermReference labelFreeMethod : labelFreeMethods) {
                method += labelFreeMethod.getName() + ",";
            }

            Collection<QuantCvTermReference> isotopeLabellingMethods = controller.getProteinIsotopeLabellingQuantMethods();
            for (QuantCvTermReference isotopeLabellingMethod : isotopeLabellingMethods) {
                method += isotopeLabellingMethod.getName() + ",";
            }

            if (method.length() > 1) {
                method = method.substring(0, method.length() - 1);
            }

            JLabel methodLabel = new JLabel("<html><b>Method</b>: " + method + "</html>");
            methodLabel.setToolTipText(method);
            metaDataPanel.add(methodLabel);
        } catch (DataAccessException e) {
            String msg = "Failed to createAttributedSequence meta data pane for identifications";
            logger.error(msg, e);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
        }
        return metaDataPanel;
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

        // set reference sample button
        refSampleButton = GUIUtilities.createLabelLikeButton(null, null);
        refSampleButton.setForeground(Color.blue);

        refSampleButton.setAction(new SetRefSampleAction(controller));
        refSampleButton.getAction().setEnabled(false);
        toolBar.add(refSampleButton);
        // add gaps
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
//        toolBar.add(Box.createRigidArea(new Dimension(20, 10)));

        moreButton = GUIUtilities.createLabelLikeButton(null, "More...");
        moreButton.setForeground(Color.blue);
        moreButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton button = (JButton) e.getSource();
                if (moreMenu == null) {
                    // create new dialog
                    moreMenu = createPopupMenu();
                }
                Point location = button.getLocation();
                moreMenu.show(button, (int) location.getX() - 250, (int) location.getY() + button.getHeight());
            }
        });
        toolBar.add(moreButton);

        return toolBar;
    }

    /**
     * Create an popup menu for the decoy filter
     *
     * @return JPopupMenu  popup menu
     */
    private JPopupMenu createPopupMenu() {
        JPopupMenu menu = new JPopupMenu();

        //protein details
        JMenuItem protMenuItem = new JMenuItem();
        protMenuItem.setAction(new ExtraProteinDetailAction(controller));
        menu.add(protMenuItem);

        // export
        JMenuItem exportMenuItem = new JMenuItem();
        exportMenuItem.setAction(new ExportQuantitativeDataAction(proteinTable, controller));
        menu.add(exportMenuItem);

        // Disclaimer
        JMenuItem disclaimerItem = new JMenuItem();
        Icon sharedPeptideIcon = GUIUtilities.loadIcon(appContext.getProperty("shared.peptide.icon.small"));
        String sharedPeptideTitle = appContext.getProperty("shared.peptide.title");
        disclaimerItem.setAction(new OpenHelpAction(sharedPeptideTitle, sharedPeptideIcon, "help.faq.shared.peptide"));
        menu.add(disclaimerItem);

        return menu;
    }

    /**
     * Return the identification table
     *
     * @return JTable  identification details table.
     */
    public JTable getQuantProteinTable() {
        return proteinTable;
    }

    /**
     * Return the button to set reference sample
     *
     * @return JButton reference sample button
     */
    public JButton getRefSampleButton() {
        return refSampleButton;
    }

    @Override
    public void subscribeToEventBus(EventService eventBus) {
        // get local event bus
        if (eventBus == null) {
            eventBus = ContainerEventServiceFinder.getEventService(this);
        }

        // subscriber
        referenceSampleSubscriber = new ReferenceSampleSubscriber();

        // subscribeToEventBus
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
                referenceSampleIndex = newIndex;
                QuantProteinTableModel tableModel = (QuantProteinTableModel) proteinTable.getModel();

                cancelOngoingTableUpdates(tableModel);

                RetrieveQuantProteinTableTask retrieveTask = new RetrieveQuantProteinTableTask(controller, newIndex);

                // register quant tab pane as a task listener
                if (parentComponent instanceof TaskListener) {
                    retrieveTask.addTaskListener((TaskListener) parentComponent);
                }
                // register protein tab as a task listener
                retrieveTask.addTaskListener((ProgressiveListTableModel) tableModel);

                TaskUtil.startBackgroundTask(retrieveTask, controller);
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
    }

    /**
     * This selection listener listens to Identification table for any selection on the row.
     * It will then fire a property change event with the selected identification id.
     */
    private class IdentificationSelectionListener implements ListSelectionListener {
        private final JTable table;

        private IdentificationSelectionListener(JTable table) {
            this.table = table;
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                int colTableNum = table.convertColumnIndexToModel(table.getSelectedColumn());
                int checkBoxTableColumn = ((QuantProteinTableModel) table.getModel()).getColumnIndex(ProteinTableHeader.COMPARE.getHeader());
                if (checkBoxTableColumn != colTableNum) {
                    int rowTableNum = table.getSelectedRow();
                    int rowCnt = table.getRowCount();
                    if (rowCnt > 0 && rowTableNum >= 0) {
                        // get table model
                        QuantProteinTableModel tableModel = (QuantProteinTableModel) proteinTable.getModel();

                        // fire a property change event with selected identification id
                        int identColNum = tableModel.getColumnIndex(ProteinTableHeader.PROTEIN_ID.getHeader());
                        int rowModelNum = table.convertRowIndexToModel(rowTableNum);
                        Comparable identId = (Comparable) tableModel.getValueAt(rowModelNum, identColNum);

                        // publish the event to local event bus
                        EventService eventBus = ContainerEventServiceFinder.getEventService(QuantProteinSelectionPane.this);
                        eventBus.publish(new ProteinIdentificationEvent(QuantProteinSelectionPane.this, controller, identId));
                    }
                } else {
                    table.getSelectionModel().clearSelection();
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
            QuantProteinTableModel tableModel = (QuantProteinTableModel) proteinTable.getModel();

            int checkBoxColumnIndex = tableModel.getColumnIndex(ProteinTableHeader.COMPARE.getHeader());
            if (!ignore && firstRowIndex == lastRowIndex && columnIndex == checkBoxColumnIndex && TableModelEvent.UPDATE == type) {
                // get protein identification id
                int identColNum = tableModel.getColumnIndex(ProteinTableHeader.PROTEIN_ID.getHeader());
                Comparable identId = (Comparable) tableModel.getValueAt(firstRowIndex, identColNum);

                // notify protein selection
                notifyProteinSelection(tableModel, identId, firstRowIndex, columnIndex);
            }
            ignore = false;
        }

        private void notifyProteinSelection(TableModel tableModel, Comparable identId, int rowModelIndex, int colModelIndex) {
            Boolean selected = (Boolean) tableModel.getValueAt(rowModelIndex, colModelIndex);

            // check whether reached the maximum number
            if (selected && numOfSelectedProteins >= MAX_NUMBER_OF_PROTEINS) {
                // show warnings
                GUIUtilities.warn(uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getMainComponent(),
                        "Protein Selection limited is reached: " + MAX_NUMBER_OF_PROTEINS + " maximum",
                        "Selection Limit Reached");
                ignore = true;
                tableModel.setValueAt(false, rowModelIndex, colModelIndex);
            } else {
                EventService eventBus = ContainerEventServiceFinder.getEventService(QuantProteinSelectionPane.this);
                eventBus.publish(new QuantSelectionEvent(proteinTable, identId, referenceSampleIndex, QuantSelectionEvent.Type.PROTIEN, selected));
                if (selected) {
                    numOfSelectedProteins++;
                } else {
                    numOfSelectedProteins--;
                }
            }
        }
    }
}
