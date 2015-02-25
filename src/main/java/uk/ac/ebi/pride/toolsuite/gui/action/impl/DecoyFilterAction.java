package uk.ac.ebi.pride.toolsuite.gui.action.impl;

import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.action.PrideAction;
import uk.ac.ebi.pride.toolsuite.gui.component.chart.ChartTabPane;
import uk.ac.ebi.pride.toolsuite.gui.component.decoy.DecoyFilterDialog;
import uk.ac.ebi.pride.toolsuite.gui.component.startup.ControllerContentPane;
import uk.ac.ebi.pride.toolsuite.gui.component.table.filter.DecoyAccessionTableFilter;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProteinTableHeader;
import uk.ac.ebi.pride.toolsuite.gui.component.table.sorttreetable.ProteinSortableTreeTable;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.DecoyFilterTask;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.DecoyRatioTask;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.filter.DecoyAccessionFilter;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Action to show a decoy filter dialog
 * <p/>
 * User: rwang, ypriverol
 * Date: 31/08/2011
 * Time: 11:34
 */
public class DecoyFilterAction extends PrideAction implements PropertyChangeListener {
    private static final String FILTER_ACTION_NAME = Desktop.getInstance().getDesktopContext().getProperty("decoy.filter.title");
    private static final String NONE_FILTER_ACTION_NAME = Desktop.getInstance().getDesktopContext().getProperty("none.decoy.filter.title");
    private static final String FILTER_ACTION_ICON_PATH = Desktop.getInstance().getDesktopContext().getProperty("decoy.filter.small.icon");
    private static final String NONE_FILTER_ACTION_ICON_PATH = Desktop.getInstance().getDesktopContext().getProperty("none.decoy.filter.small.icon");

    /**
     * DataAccessController
     */
    private DataAccessController controller;

    /**
     * Decoy filter dialog
     */
    private JDialog decoyFilterDialog;

    /**
     * Boolean that whether this is an existing filter
     */
    private boolean filterApplied = false;

    /**
     * Pride Inspector context
     */
    private PrideInspectorContext appContext;

    /**
     * Current decoy accession filter
     */
    private DecoyAccessionFilter currentFilter;

    /**
     * Constructor
     *
     * @param controller data access controller
     */
    public DecoyFilterAction(DataAccessController controller) {
        super(FILTER_ACTION_NAME, GUIUtilities.loadIcon(FILTER_ACTION_ICON_PATH));
        this.controller = controller;
        this.appContext = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (filterApplied) {
            undoFilter();
        } else {
            // show dialog
            if (decoyFilterDialog == null) {
                decoyFilterDialog = new DecoyFilterDialog(Desktop.getInstance().getMainComponent());
                decoyFilterDialog.addPropertyChangeListener(DecoyFilterAction.this);
            }
            decoyFilterDialog.setVisible(true);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(DecoyFilterDialog.NEW_FILTER)) {

           currentFilter = (DecoyAccessionFilter) evt.getNewValue();

            applyFilter(currentFilter);

            // change icon and name of the action
            putValue(Action.NAME, NONE_FILTER_ACTION_NAME);
            putValue(Action.SMALL_ICON, GUIUtilities.loadIcon(NONE_FILTER_ACTION_ICON_PATH));

            // record filter
            filterApplied = true;
        }
    }

    public void applyFilter() {
        if (currentFilter != null) {
            applyFilter(currentFilter);
        }
    }

    private void applyFilter(DecoyAccessionFilter filter) {
        ControllerContentPane contentPane = (ControllerContentPane) appContext.getDataContentPane(controller);

        //Update the Summary Charts Tab
        ChartTabPane chartContentPane = contentPane.getChartTabPane();
        chartContentPane.populateWithDecoyFilter(filter);

        // decoy task
        Task decoyRatioTask = new DecoyRatioTask(controller, filter);
        TaskUtil.startBackgroundTask(decoyRatioTask, controller);

        Task decoyFilterTask = new DecoyFilterTask(controller, filter);
        TaskUtil.startBackgroundTask(decoyFilterTask, controller);
    }


    private void undoFilter() {
        removeFilter();


        // change icon and name of the action
        putValue(Action.NAME, FILTER_ACTION_NAME);
        putValue(Action.SMALL_ICON, GUIUtilities.loadIcon(FILTER_ACTION_ICON_PATH));

        // record filter
        filterApplied = false;
    }

    public void removeFilter() {
        // remove the decoy filter
        ControllerContentPane contentPane = (ControllerContentPane) appContext.getDataContentPane(controller);

        //Update the Summary Charts Tab
        ChartTabPane chartContentPane = contentPane.getChartTabPane();
        chartContentPane.populate();

        // protein tab
        clearFilter(contentPane.getProteinTabPane().getIdentificationPane().getIdentificationTable());

        // peptide tab
        clearFilter(contentPane.getPeptideTabPane().getPeptidePane().getPeptideTable());

        // quantitative tab
        if (contentPane.isQuantTabEnabled()) {
            clearFilter(contentPane.getQuantTabPane().getQuantProteinSelectionPane().getQuantProteinTable());
        }
    }

    private void clearFilter(JTable table) {
        if (table instanceof ProteinSortableTreeTable) {
            String protAccColName = ProteinTableHeader.PROTEIN_ACCESSION.getHeader();
            int index = getAccessionColumnIndex(table.getModel(), protAccColName);
            ((ProteinSortableTreeTable) table).setRowFilter(new DecoyAccessionTableFilter(new NoneAccessionTableFilter(), index));
        } else {
            TableRowSorter rowSorter = (TableRowSorter) table.getRowSorter();
            rowSorter.setRowFilter(null);
        }
    }

    private int getAccessionColumnIndex(TableModel tableModel, String protAccColName) {
        int colCnt = tableModel.getColumnCount();
        for (int i = 0; i < colCnt; i++) {
            if (tableModel.getColumnName(i).equals(protAccColName)) {
                return i;
            }
        }
        return -1;
    }

    private static class NoneAccessionTableFilter extends DecoyAccessionFilter {

        public NoneAccessionTableFilter() {
            super(null, null);
        }

        @Override
        public boolean apply(String proteinAccession) {
            return true;
        }
    }
}
