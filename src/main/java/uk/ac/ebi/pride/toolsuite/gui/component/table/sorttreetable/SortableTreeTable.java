package uk.ac.ebi.pride.toolsuite.gui.component.table.sorttreetable;


import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.event.TableColumnModelExtListener;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.DefaultTableColumnModelExt;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.TreeTableModel;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.PeptideTreeTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProteinTableHeader;
import uk.ac.ebi.pride.toolsuite.gui.component.table.renderer.IconRenderer;
import uk.ac.ebi.pride.toolsuite.gui.component.table.renderer.ProteinAccessionHyperLinkCellRenderer;
import uk.ac.ebi.pride.toolsuite.gui.component.table.renderer.ProteinGroupCellRenderer;
import uk.ac.ebi.pride.toolsuite.gui.component.table.renderer.SequenceCoverageRenderer;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;


import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.TreePath;
import java.beans.PropertyChangeEvent;
import java.util.*;

/**
 * A specialized {@link org.jdesktop.swingx.JXTreeTable tree table} containing checkboxes and
 * hyperlinks in its hierarchical column.
 * 
 * @author ypriverol
 */
public class SortableTreeTable extends JXTreeTable {


    /**
     * Constructs a sortable tree table with checkboxes from a tree table model.
     * @param treeModel The tree model to be used. Must be an instance of
     * {@link SortableTreeTableModel}
     */
    public SortableTreeTable(TreeTableModel treeModel, boolean hasProteinGroups) {
        super(treeModel);
        // Check whether the provided tree model is sortable
        if (!(treeModel instanceof SortableTreeTableModel)) {
            throw new IllegalArgumentException(
                    "Model must be a SortableTreeTableModel");
        }

       // Install column factory to cache and restore visuals after sorting/filtering
        this.setColumnFactory(new SortableColumnFactory(this,hasProteinGroups));

        this.setTreeTableModel(treeModel);
    }

    @Override
    public void setTreeTableModel(TreeTableModel treeModel) {
        // forward model change to column factory
        ((SortableColumnFactory) this.getColumnFactory()).setColumnCount(treeModel.getColumnCount());

        super.setTreeTableModel(treeModel);
    }

    /**
     * Something happen with invisible columns in JTreeTable and add a new row, then we need to remove it.
     * @return
     */
    @Override
    public int getRowCount() {
        return super.getRowCount();
    }

    /* Overrides of sorting-related methods forwarding to JXTreeTable's hooks. */
    @Override
    public void setSortable(boolean sortable) {
        superSetSortable(sortable);
    }

    @Override
    public void setAutoCreateRowSorter(boolean autoCreateRowSorter) {
        superSetAutoCreateRowSorter(autoCreateRowSorter);
    }

    @Override
    public void setRowSorter(RowSorter<? extends TableModel> sorter) {
        superSetRowSorter(sorter);
    }

    public void setRowSorter(){
        SortableTreeTableRowSorter sorter = new SortableTreeTableRowSorter(this);
        setRowSorter(sorter);
    }

    @Override
    public RowFilter<?, ?> getRowFilter() {
        if(getRowSorter() != null)
           return ((TreeTableRowSorter<?>) getRowSorter()).getRowFilter();
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends TableModel> void setRowFilter(RowFilter<? super R, ? super Integer> filter) {
        // all fine, because R extends TableModel
        ((TreeTableRowSorter<R>) getRowSorter()).setRowFilter(filter);
    }

    @Override
    protected TableColumnModel createDefaultColumnModel() {
        return new DefaultTableColumnModelExt() {
            @Override
            public void removeColumn(TableColumn column) {
                super.removeColumn(column);
                this.fireColumnPropertyChange(new PropertyChangeEvent(
                        column, "visible", true, ((TableColumnExt) column).isVisible()));
            }
        };
    }

    @Override
    protected void postprocessModelChange(TableModelEvent e) {
        super.postprocessModelChange(e);
        ColumnFactory factory = this.getColumnFactory();
        if (factory instanceof SortableColumnFactory) {
            ((SortableColumnFactory) factory).reorderColumns();
        }
    }


    /**
     * Custom row sorter implementation.
     * @author ypriverol
     */
    public class SortableTreeTableRowSorter extends TreeTableRowSorter<TableModel> {

        private Enumeration<TreePath> expPaths;

        /**
         * Constructs a {@link RowSorter} for the provided {@link JXTreeTable}.
         * @param treeTable The tree table to which the sorter shall be attached.
         */
        public SortableTreeTableRowSorter(JXTreeTable treeTable) {
            super(treeTable);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void preCollapse() {
            this.expPaths = (Enumeration<TreePath>) this.treeTable.getExpandedDescendants(
                    new TreePath(this.treeModel.getRoot()));
        }

        @Override
        protected void reExpand() {
            if (this.expPaths != null) {
                while (this.expPaths.hasMoreElements()) {
                    TreePath expPath = this.expPaths.nextElement();
                    this.treeTable.expandPath(expPath);
                }
            }
        }

    }

    /**
     * Column factory extension to automatically cache and restore column properties.
     * @author ypriverol
     */
    private class SortableColumnFactory extends ColumnFactory {

        /**
         * The parent tree table reference.
         */
        private JXTreeTable treeTbl;

        /**
         * The array of column prototypes.
         */
        private TableColumnExt[] prototypes;

        /**
         * The array of column view coordinates.
         */
        private int[] viewToModel;


        private boolean hasProteinGroups;

        /**
         * Constructs a column factory for the specified tree table.
         *
         * @param treeTbl the tree table reference
         */
        public SortableColumnFactory(JXTreeTable treeTbl, boolean hasProteinGroups) {
            super();
            this.treeTbl = treeTbl;
            this.hasProteinGroups = hasProteinGroups;
            this.setColumnCount(treeTbl.getColumnCount());

            treeTbl.getColumnModel().addColumnModelListener(new TableColumnModelExtListener() {
                @Override
                public void columnMoved(TableColumnModelEvent evt) {
                    // check whether columns have been reordered (or are in the process of being reordered)
                    if (evt.getFromIndex() != evt.getToIndex()) {
                        DefaultTableColumnModelExt model = (DefaultTableColumnModelExt) evt.getSource();
                        // only update cache when visible columns are reordered
                        if (!model.isAddedFromInvisibleEvent(0)) {
                            SortableColumnFactory factory = SortableColumnFactory.this;
                            JXTreeTable treeTbl = factory.treeTbl;
                            // cache view coordinates
                            for (int i = 0; i < treeTbl.getColumnModel().getColumnCount(); i++) {
                                int modelIndex = treeTbl.convertColumnIndexToModel(i);
                                factory.viewToModel[i] = modelIndex;
                            }
                            // FIXME: reordering while columns are hidden messes up coordinates (low priority)
                        }
                    }
                }
                @Override
                public void columnPropertyChange(PropertyChangeEvent evt) {
                    if (evt.getNewValue().equals(evt.getOldValue())) {
                        return;
                    }
                    TableColumnExt column = (TableColumnExt) evt.getSource();
                    int modelIndex = column.getModelIndex();
                    // update prototype cache
                    SortableColumnFactory.this.prototypes[modelIndex] = new TableColumnExt(column);
                }
                /* we don't need these */
                public void columnSelectionChanged(ListSelectionEvent evt) {}
                public void columnMarginChanged(ChangeEvent evt) {}
                public void columnAdded(TableColumnModelEvent evt) {}
                public void columnRemoved(TableColumnModelEvent evt) {}
            });
        }

        /**
         * Sets the number of columns to the specified value.
         * @param columnCount the number of columns
         */
        public void setColumnCount(int columnCount) {
            this.prototypes = new TableColumnExt[columnCount];
            this.viewToModel = new int[columnCount];

            // init view coordinates
            for (int i = 0; i < this.viewToModel.length; i++) {
                this.viewToModel[i] = i;
            }
        }

        /**
         * Reorders the table's columns using the cached view-to-model coordinate mapping.
         */
        public void reorderColumns() {
            boolean[] hidden = this.unhideAll();

            // cache coordinates locally
            int[] targetIndexes = Arrays.copyOf(this.viewToModel, this.viewToModel.length);
            // initialize current positions
            Vector<Integer> currentIndexes = new Vector<Integer>();
            for (int i = 0; i < targetIndexes.length; i++) {
                currentIndexes.add(i);
            }
            // iterate columns
            for (int i = 0; i < targetIndexes.length; i++) {
                int targetIndex = targetIndexes[i];
                int currentIndex = currentIndexes.indexOf(targetIndex);
                // only move colors to the left
                if (currentIndex > i) {
                    this.treeTbl.moveColumn(currentIndex, i);
                    // update current positions in a fashion similar to what happens to the column model
                    currentIndexes.remove(currentIndex);
                    currentIndexes.insertElementAt(targetIndex, i);
                }
            }
//			factory.viewToModel = viewToModel;
			this.hideAll(hidden);

		}

		/** Convenience method to unhide all columns and return an array of their visibility states. */
        private boolean[] unhideAll() {
            List<TableColumn> columns =
                    ((DefaultTableColumnModelExt) this.treeTbl.getColumnModel()).getColumns(true);
            boolean[] hidden = new boolean[columns.size()];
            int i = 0;
            for (TableColumn column : columns) {
                TableColumnExt columnExt = (TableColumnExt) column;
                hidden[i++] = columnExt.isVisible();
                columnExt.setVisible(true);
            }
            return hidden;
        }

		/** Convenience method to set the visibility states of all columns. */
        private void hideAll(boolean[] hidden) {
            List<TableColumn> columns =
                    ((DefaultTableColumnModelExt) this.treeTbl.getColumnModel()).getColumns(true);
            int i = 0;
            for (TableColumn column : columns) {
                TableColumnExt columnExt = (TableColumnExt) column;
                columnExt.setVisible(hidden[i++]);
            }
        }


        @Override
        public void configureColumnWidths(JXTable table,
                                          TableColumnExt columnExt) {
            // get cached values and apply them
            int modelIndex = columnExt.getModelIndex();
            TableColumnExt prototype = this.prototypes[modelIndex];

            int prefWidth = prototype.getPreferredWidth();
            if (prefWidth > 0) {
                columnExt.setPreferredWidth(prefWidth);
            }

            int minWidth = prototype.getMinWidth();
            columnExt.setMinWidth(minWidth);

            int maxWidth = prototype.getMaxWidth();
            if (maxWidth > 0) {
                columnExt.setMaxWidth(maxWidth);
            }

        }

        @Override
        public TableColumnExt createAndConfigureTableColumn(TableModel model, int modelIndex) {
            if (modelIndex < this.prototypes.length) {
                // get prototype column
                TableColumnExt prototype = this.prototypes[modelIndex];
                // if prototype has not been initialized...
                if (prototype == null) {
                    // lazily instantiate default column
                    prototype = super.createAndConfigureTableColumn(model, modelIndex);
                    this.prototypes[modelIndex] = prototype;
                }
                // create column using prototype properties
                TableColumnExt column = new TableColumnExt(prototype);

                // configure header renderer
                column.setHeaderRenderer(this.getHeaderRenderer(this.treeTbl, column));

                return column;
            }
            return null;
        }

        @Override
        public void configureTableColumn(TableModel model, TableColumnExt columnExt) {

            super.configureTableColumn(model, columnExt);

            // peptide sequence column renderer
            String columnTitle = columnExt.getTitle();
            // set column visibility
            if (ProteinTableHeader.COMPARE.getHeader().equals(columnTitle) ||
                    //                  ProteinTableHeader.PROTEIN_GROUP_ID.getHeader().equals(columnTitle) ||
                    ProteinTableHeader.ADDITIONAL.getHeader().equals(columnTitle) ||
                    ProteinTableHeader.PROTEIN_ID.getHeader().equals(columnTitle) ||
                    ProteinTableHeader.PROTEIN_NAME.getHeader().equals(columnTitle) ||
                    ProteinTableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader().equals(columnTitle) ||
                    ProteinTableHeader.PROTEIN_STATUS.getHeader().equals(columnTitle) ||
                    ProteinTableHeader.THEORETICAL_ISOELECTRIC_POINT.getHeader().equals(columnTitle) ||
                    ProteinTableHeader.IDENTIFICATION_THRESHOLD.getHeader().equals(columnTitle)) {
                columnExt.setVisible(false);
            }

            // set protein name column width
            if (ProteinTableHeader.PROTEIN_NAME.getHeader().equals(columnTitle)) {
                columnExt.setPreferredWidth(200);
            }

            // sequence coverage column
            if (ProteinTableHeader.PROTEIN_SEQUENCE_COVERAGE.getHeader().equals(columnTitle)) {
                columnExt.setCellRenderer(new SequenceCoverageRenderer());
            }

            // ptm accession hyperlink
            if (ProteinTableHeader.PROTEIN_ACCESSION.getHeader().equals(columnTitle)) {
                columnExt.setCellRenderer(new ProteinAccessionHyperLinkCellRenderer());
            }


            // set additional column
            if (PeptideTreeTableModel.TableHeader.ADDITIONAL.getHeader().equals(columnTitle)) {
                Icon icon = GUIUtilities.loadIcon(Desktop.getInstance().getDesktopContext().getProperty("view.detail.small.icon"));
                columnExt.setCellRenderer(new IconRenderer(icon));
                columnExt.setMaxWidth(50);
            }

        }
    }

	
}
