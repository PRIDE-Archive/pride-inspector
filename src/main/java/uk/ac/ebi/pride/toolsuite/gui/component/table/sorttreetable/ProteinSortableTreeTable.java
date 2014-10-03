package uk.ac.ebi.pride.toolsuite.gui.component.table.sorttreetable;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.table.ColumnControlButton;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.TreeTableModel;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProteinTableHeader;

import javax.swing.table.TableColumn;

/**
 * @author ypriverol
 * @author rwang
 */
public class ProteinSortableTreeTable extends SortableTreeTable {

    /**
     * Constructs a sortable tree table with checkboxes from a tree table model.
     *
     * @param treeModel        The tree model to be used. Must be an instance of
     *                         {@link uk.ac.ebi.pride.toolsuite.gui.component.table.sorttreetable.SortableTreeTableModel}
     * @param hasProteinGroups
     */
    public ProteinSortableTreeTable(TreeTableModel treeModel, boolean hasProteinGroups) {
        super(treeModel, hasProteinGroups);
        this.setColumnControl(new CustomColumnControlButton(this));
    }


    class CustomColumnControlButton extends ColumnControlButton {


        public CustomColumnControlButton(JXTable table) {
            super(table);
        }

        protected ColumnVisibilityAction createColumnVisibilityAction(TableColumn column)
        {
            if (column instanceof TableColumnExt &&
                    (ProteinTableHeader.PROTEIN_GROUP_ID.getHeader().equals(((TableColumnExt) column).getTitle()) ||
                            ProteinTableHeader.PROTEIN_ACCESSION.getHeader().equals(((TableColumnExt) column).getTitle())))
                return null;
            return super.createColumnVisibilityAction(column);
        }
    }


}
