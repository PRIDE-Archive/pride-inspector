package uk.ac.ebi.pride.toolsuite.gui.component.mzdata;

import org.bushe.swing.event.ContainerEventServiceFinder;
import org.bushe.swing.event.EventService;
import org.bushe.swing.event.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessException;
import uk.ac.ebi.pride.utilities.data.core.Chromatogram;
import uk.ac.ebi.pride.utilities.data.core.MzGraph;
import uk.ac.ebi.pride.utilities.data.core.Spectrum;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.action.impl.ExportSpectrumAction;
import uk.ac.ebi.pride.toolsuite.gui.component.DataAccessControllerPane;
import uk.ac.ebi.pride.toolsuite.gui.component.EventBusSubscribable;
import uk.ac.ebi.pride.toolsuite.gui.component.exception.ThrowableEntry;
import uk.ac.ebi.pride.toolsuite.gui.component.message.MessageType;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ChromatogramTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProgressiveListTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.SpectrumTableModel;
import uk.ac.ebi.pride.toolsuite.gui.event.container.ChromatogramEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.ExportSpectrumDetailEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.LoadBatchEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.container.SpectrumEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.RetrieveChromatogramTableTask;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.RetrieveSpectrumTableTask;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * MzDataSelectionPane contains two tabs: one for spectra and one for chromatogram.
 * <p/>
 * <p/>
 * 1. It listens the PropertyChangeEvent from the background DataAccessController.
 * <p/>
 * 2. It notifies all parties (components) listening on the changes with appropriate data.
 * <p/>
 * <p/>
 * User: rwang
 * Date: 03-Mar-2010
 * Time: 14:55:11
 */
public class MzDataSelectionPane extends DataAccessControllerPane<MzGraph, Void> implements EventBusSubscribable {

    private static final Logger logger = LoggerFactory.getLogger(MzDataTabPane.class.getName());
    /**
     * Tab index for spectrum table
     */
    private static final int SPECTRUM_TAB_INDEX = 0;
    /**
     * Tab index for chromatogram table
     */
    private static final int CHROMATOGRAM_TAB_INDEX = 1;
    /**
     * Label to display the number loaded spectra and chromatogram
     */
    private MzDataCountLabel countLabel;
    /**
     * Tab pane contains spectrum table and chromatogram table
     */
    private JTabbedPane tabPane;
    /**
     * Spectrum table
     */
    private JTable spectrumTable;
    /**
     * Chromatogram table
     */
    private JTable chromaTable;
    /**
     * the number of entries to read for each iteration of the paging
     */
    private int defaultOffset;
    /**
     * start index for spectrum, this is for paging
     */
    private int startForSpec;

    /**
     * start index for chromatogram
     */
    private int startForChroma;

    /**
     * Load next batch button
     */
    private JButton loadNextButton;
    /**
     * Load all button
     */
    private JButton loadAllButton;
    /**
     * Export button
     */
    private JButton exportButton;
    /**
     * Subscribe to export spectrum detail event
     */
    private ExportSpectrumDetailEventSubscriber exportEventSubscriber;

    private LoadBatchEventSubscriber loadBatchSubscriber;

    /**
     * Constructor
     *
     * @param controller      data access controller
     * @param parentComponent parent component
     */
    public MzDataSelectionPane(DataAccessController controller, JComponent parentComponent) {
        super(controller, parentComponent);
    }

    /**
     * initialize the main display pane
     */
    @Override
    protected void setupMainPane() {
        defaultOffset = Integer.parseInt(appContext.getProperty("mzdata.batch.load.size"));
        this.setLayout(new BorderLayout());
        this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    }

    /**
     * add the rest components
     */
    @Override
    protected void addComponents() {
        // add the top panel which display the spectra and chromatogram count
        JPanel topPane = initializeTopPane();
        this.add(topPane, BorderLayout.NORTH);

        // create selection pane
        tabPane = new JTabbedPane();

        // add tab change listener
        tabPane.addChangeListener(new MzDataTabChangeListener());

        // init spectra selection pane
        spectrumTable = TableFactory.createSpectrumTable();

        // add selection listener
        spectrumTable.getSelectionModel().addListSelectionListener(new MzDataListSelectionListener(spectrumTable));
        spectrumTable.getModel().addTableModelListener(new SpectrumTableInsertListener(spectrumTable));

        // add to scroll pane
        JScrollPane spectrumScrollPane = new JScrollPane(spectrumTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        spectrumScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tabPane.addTab(DataAccessController.SPECTRUM_TYPE, spectrumScrollPane);

        // init chromatogram selection pane
        chromaTable = TableFactory.createChromatogramTable();

        // add selection listener
        chromaTable.getSelectionModel().addListSelectionListener(new MzDataListSelectionListener(chromaTable));
        chromaTable.getModel().addTableModelListener(new SpectrumTableInsertListener(chromaTable));

        // add to scroll pane
        JScrollPane chromaScrollPane = new JScrollPane(chromaTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        chromaScrollPane.setBorder(BorderFactory.createEmptyBorder());
        tabPane.addTab(DataAccessController.CHROMATOGRAM_TYPE, chromaScrollPane);

        // initialize the visibility and the content of the tabs
        initializeTabPane();

        // add tabPane to the main display pane
        this.add(tabPane, BorderLayout.CENTER);
    }

    /**
     * Create the top panel
     *
     * @return JPanel top panel
     */
    private JPanel initializeTopPane() {
        // top panel
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BorderLayout());
        labelPanel.setOpaque(false);

        // count label
        try {
            countLabel = new MzDataCountLabel(controller.getNumberOfSpectra(), controller.getNumberOfChromatograms());
        } catch (DataAccessException e) {
            String msg = "Error while creating mzData count label";
            logger.error(msg);
            appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, e));
        }
        labelPanel.add(countLabel, BorderLayout.WEST);

        // button panel
        JToolBar toolBar = new JToolBar();
        toolBar.setOpaque(false);
        toolBar.setFloatable(false);
        toolBar.setLayout(new FlowLayout(FlowLayout.RIGHT));

        // load next button
        // load icon
        Icon loadNextIcon = GUIUtilities.loadIcon(appContext.getProperty("load.next.mzdata.small.icon"));
        loadNextButton = GUIUtilities.createLabelLikeButton(loadNextIcon, appContext.getProperty("load.next.mzdata.title"));
        loadNextButton.setToolTipText(appContext.getProperty("load.next.mzdata.tooltip"));
        loadNextButton.setForeground(Color.blue);
        // set action command
        loadNextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventService eventBus = ContainerEventServiceFinder.getEventService(MzDataSelectionPane.this);
                eventBus.publish(new LoadBatchEvent(this, LoadBatchEvent.Type.NEXT));
            }
        });

        toolBar.add(loadNextButton);

        // add gap
        toolBar.add(Box.createRigidArea(new Dimension(10, 10)));

        // load all button
        // load icon
        Icon loadAllIcon = GUIUtilities.loadIcon(appContext.getProperty("load.all.mzdata.small.icon"));
        loadAllButton = GUIUtilities.createLabelLikeButton(loadAllIcon, appContext.getProperty("load.all.mzdata.title"));
        loadAllButton.setToolTipText(appContext.getProperty("load.all.mzdata.tooltip"));
        loadAllButton.setForeground(Color.blue);

        // set action command
        loadAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EventService eventBus = ContainerEventServiceFinder.getEventService(MzDataSelectionPane.this);
                eventBus.publish(new LoadBatchEvent(this, LoadBatchEvent.Type.ALL));
            }
        });

        toolBar.add(loadAllButton);
        // add gap
        toolBar.add(Box.createRigidArea(new Dimension(10, 10)));

        // export button
        Icon exportIcon = GUIUtilities.loadIcon(appContext.getProperty("export.enabled.small.icon"));
        Icon disabledExportIcon = GUIUtilities.loadIcon(appContext.getProperty("export.disabled.small.icon"));

        exportButton = GUIUtilities.createLabelLikeButton(exportIcon, "Export");
        exportButton.setDisabledIcon(disabledExportIcon);
        exportButton.setToolTipText(appContext.getProperty("export.tooltip"));
        exportButton.setForeground(Color.blue);

        // set action command
        exportButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                EventService eventBus = ContainerEventServiceFinder.getEventService(MzDataSelectionPane.this);
                eventBus.publish(new ExportSpectrumDetailEvent(this));
            }
        });

        toolBar.add(exportButton);
        // add gap
        toolBar.add(Box.createRigidArea(new Dimension(10, 10)));

        // Help button
        // load icon
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, null);
        helpButton.setToolTipText("Help");
        helpButton.setForeground(Color.blue);
        CSH.setHelpIDString(helpButton, "help.browse.mzgraph");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));
        toolBar.add(helpButton);

        labelPanel.add(toolBar, BorderLayout.EAST);

        return labelPanel;
    }

    /**
     * Update all the tabs
     */
    private void initializeTabPane() {
        // set the start index for paging
        startForSpec = 0;
        startForChroma = 0;

        // check whether there is spectra
        boolean spectrumTabVisibility = false;
        // check whether there is chromatogram
        boolean chromaTabVisibility = false;
        try {
            spectrumTabVisibility = controller.hasSpectrum();
            chromaTabVisibility = controller.hasChromatogram();
        } catch (DataAccessException e) {
            logger.error("Failed to check the number of mzgraph", e);
        }

        // set the visibility of the tab
        tabPane.setEnabledAt(SPECTRUM_TAB_INDEX, spectrumTabVisibility);
        tabPane.setEnabledAt(CHROMATOGRAM_TAB_INDEX, chromaTabVisibility);

        // start retrieving data for spectrum table
        if (spectrumTabVisibility) {
            updateTable((SpectrumTableModel) spectrumTable.getModel(), Spectrum.class, defaultOffset);
        }

        // start retrieving data for chromatogram table
        if (chromaTabVisibility) {
            updateTable((ChromatogramTableModel) chromaTable.getModel(), Chromatogram.class, defaultOffset);
        }

        // set the tab selection, if spectrum tab is enabled, this should be used as default
        if (spectrumTabVisibility) {
            tabPane.setSelectedIndex(SPECTRUM_TAB_INDEX);
        } else if (chromaTabVisibility) {
            tabPane.setSelectedIndex(CHROMATOGRAM_TAB_INDEX);
        }
    }

    /**
     * This method is responsible for fire up a new background task to retrieve mzgraph data.
     *
     * @param tableModel table model to insert the result to
     * @param classType  indicates the type of mzgraph
     * @param offset
     */
    @SuppressWarnings("unchecked")
    private <T extends MzGraph> void updateTable(ProgressiveListTableModel tableModel, Class<T> classType, int offset) {
        // create a new task
        Task retrieveTask = null;
        if (Spectrum.class.equals(classType)) {
            retrieveTask = new RetrieveSpectrumTableTask(controller, startForSpec, offset);
            startForSpec += defaultOffset;
        } else if (Chromatogram.class.equals(classType)) {
            retrieveTask = new RetrieveChromatogramTableTask(controller, startForChroma, offset);
            startForChroma += defaultOffset;
        }

        if (retrieveTask != null) {
            // add parent component as a task listener
            if (parentComponent instanceof TaskListener) {
                retrieveTask.addTaskListener((TaskListener) parentComponent);
            }

            // add table model as a task listener
            retrieveTask.addTaskListener(tableModel);

            // add count label as a task listener
            retrieveTask.addTaskListener(countLabel);

            TaskUtil.startBackgroundTask(retrieveTask, controller);
        }
    }

    @Override
    public void subscribeToEventBus(EventService eventBus) {
        // get local event bus
        if (eventBus == null) {
            eventBus = ContainerEventServiceFinder.getEventService(this);
        }

        // subscriber
        exportEventSubscriber = new ExportSpectrumDetailEventSubscriber();
        loadBatchSubscriber = new LoadBatchEventSubscriber();

        // subscribeToEventBus
        eventBus.subscribe(ExportSpectrumDetailEvent.class, exportEventSubscriber);
        eventBus.subscribe(LoadBatchEvent.class, loadBatchSubscriber);
    }

    /**
     * Capture export spectrum details event
     */
    private static class ExportSpectrumDetailEventSubscriber implements EventSubscriber<ExportSpectrumDetailEvent> {

        @Override
        public void onEvent(ExportSpectrumDetailEvent event) {
            ExportSpectrumAction exportAction = new ExportSpectrumAction(null, null);
            exportAction.actionPerformed(null);
        }
    }

    /**
     * Capture load next/all batch event
     */
    private class LoadBatchEventSubscriber implements EventSubscriber<LoadBatchEvent> {

        @Override
        public void onEvent(LoadBatchEvent event) {
            int offset = defaultOffset;

            //
            int index = tabPane.getSelectedIndex();
            try {
                if (index == SPECTRUM_TAB_INDEX) {
                    int numOfSpectra = controller.getNumberOfSpectra();
                    // set offset
                    if (LoadBatchEvent.Type.ALL.equals(event.getType())) {
                        offset = numOfSpectra - startForSpec;
                    }
                    // get table model
                    if (startForSpec < numOfSpectra && numOfSpectra > spectrumTable.getRowCount()) {
                        updateTable((SpectrumTableModel) spectrumTable.getModel(), Spectrum.class, offset);
                    }
                } else {
                    int numOfChromas = controller.getNumberOfChromatograms();

                    // set offset
                    if (LoadBatchEvent.Type.ALL.equals(event.getType())) {
                        offset = numOfChromas - startForChroma;
                    }

                    if (startForChroma < numOfChromas && numOfChromas > chromaTable.getRowCount()) {
                        updateTable((ChromatogramTableModel) chromaTable.getModel(), Chromatogram.class, offset);
                    }
                }
            } catch (DataAccessException ex) {
                String msg = "Failed to get the number of spectra";
                logger.error(msg, ex);
                appContext.addThrowableEntry(new ThrowableEntry(MessageType.ERROR, msg, ex));
            }
        }
    }

    /**
     * Listens to row selection.
     */
    private class MzDataListSelectionListener implements ListSelectionListener {

        private JTable table = null;

        public MzDataListSelectionListener(JTable table) {
            this.table = table;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {

                int rowNum = table.getSelectedRow();
                int rowCnt = table.getRowCount();

                if (rowCnt > 0 && rowNum >= 0) {
                    // get table model
                    TableModel tableModel = table.getModel();

                    // local event bus
                    EventService eventBus = ContainerEventServiceFinder.getEventService(MzDataSelectionPane.this);

                    // get the column number for mzgraph id
                    int columnNum;
                    if (tableModel instanceof SpectrumTableModel) {
                        columnNum = ((SpectrumTableModel) tableModel).getColumnIndex(SpectrumTableModel.TableHeader.SPECTRUM_ID_COLUMN.getHeader());
                        Comparable id = (Comparable) table.getValueAt(rowNum, columnNum);
                        eventBus.publish(new SpectrumEvent(this, controller, id));
                    } else if (tableModel instanceof ChromatogramTableModel) {
                        columnNum = ((ChromatogramTableModel) tableModel).getColumnIndex(ChromatogramTableModel.TableHeader.CHROMATOGRAM_ID_COLUMN.getHeader());
                        Comparable id = (Comparable) table.getValueAt(rowNum, columnNum);
                        eventBus.publish(new ChromatogramEvent(this, controller, id));
                    }
                }
            }
        }
    }

    /**
     * Listens to tab selection among spectrum tab and chromatogram tab.
     */
    private class MzDataTabChangeListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent e) {
            JTabbedPane pane = (JTabbedPane) e.getSource();

            // local event bus
            EventService eventBus = ContainerEventServiceFinder.getEventService(MzDataSelectionPane.this);

            int columnNum;
            int rowNum;
            int rowCnt;

            switch (pane.getSelectedIndex()) {
                case 0:
                    columnNum = ((SpectrumTableModel) spectrumTable.getModel()).getColumnIndex(SpectrumTableModel.TableHeader.SPECTRUM_ID_COLUMN.getHeader());
                    rowNum = spectrumTable.getSelectedRow();
                    rowCnt = spectrumTable.getRowCount();
                    Comparable spectrumId = (rowCnt > 0 && rowNum >= 0) ? (Comparable) spectrumTable.getValueAt(rowNum, columnNum) : null;
                    eventBus.publish(new SpectrumEvent(this, controller, spectrumId));
                    exportButton.setEnabled(true);
                    exportButton.setForeground(Color.blue);
                    break;
                case 1:
                    columnNum = ((ChromatogramTableModel) chromaTable.getModel()).getColumnIndex(ChromatogramTableModel.TableHeader.CHROMATOGRAM_ID_COLUMN.getHeader());
                    rowNum = chromaTable.getSelectedRow();
                    rowCnt = chromaTable.getRowCount();
                    Comparable chromaId = (rowCnt > 0 && rowNum >= 0) ? (Comparable) chromaTable.getValueAt(rowNum, columnNum) : null;
                    eventBus.publish(new ChromatogramEvent(this, controller, chromaId));
                    exportButton.setEnabled(false);
                    exportButton.setForeground(Color.gray);
                    break;
            }
        }
    }

    public Comparable getFirstSpectrum() {
        TableModel tableModel = spectrumTable.getModel();
        int columnNum = ((SpectrumTableModel) tableModel).getColumnIndex(SpectrumTableModel.TableHeader.SPECTRUM_ID_COLUMN.getHeader());
        Comparable id = (Comparable) spectrumTable.getValueAt(0, columnNum);
        return id;
    }

    /**
     * Trigger when a protein is inserted on the table,
     * a new background task will be started to retrieve the peptide.
     */
    @SuppressWarnings("unchecked")
    private class SpectrumTableInsertListener implements TableModelListener {

        private final JTable table;

        private SpectrumTableInsertListener(JTable table) {
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