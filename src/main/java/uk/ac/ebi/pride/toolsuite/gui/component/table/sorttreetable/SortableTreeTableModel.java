package uk.ac.ebi.pride.toolsuite.gui.component.table.sorttreetable;

import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableNode;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.ProteinTableHeader;
import uk.ac.ebi.pride.utilities.term.CvTermReference;

import javax.swing.*;
import javax.swing.RowSorter.SortKey;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;
import java.util.*;

/**
 * Extended tree table model for use with {@link} and
 * {@link}.<br><br>
 * Based on <a href="http://java.net/projects/jdnc-incubator/
 * lists/cvs/archive/2008-01/message/82">Ray Turnbull's implementation</a>.
 * 
 * @author ypriverol
 */
public class SortableTreeTableModel extends DefaultTreeTableModel {

	/**
	 * The list of sort keys containing column indices and their corresponding 
	 * sort orders
	 */
	private List<SortKey> sortKeys = new ArrayList<SortKey>();
	
	/**
	 * The row filter object specifying whether certain children of a node shall
	 * be hidden.
	 */
	private RowFilter<? super TableModel,? super Integer> rowFilter;

	/**
	 * Flag denoting whether non-leaf nodes with no visible children shall be hidden.
	 */
	private boolean hideEmpty = true;

    public final Map<String, String> columnNames;

    public final Collection<CvTermReference> proteinScores;

	/**
	 * Constructs a sortable tree table model using the specified root node.
	 * @param root The tree table node to be used as root.
	 */
	public SortableTreeTableModel(TreeTableNode root, final Collection<CvTermReference> listPeptideScores) {
		super(root);
        this.proteinScores = listPeptideScores;
        columnNames = new LinkedHashMap<String, String>();
        addAdditionalColumns(this.proteinScores);
        setColumnIdentifiers(new ArrayList<Object>(columnNames.keySet()));
	}
	
	/**
	 * Returns the sort keys.
	 * @return The sort keys.
	 */
	public List<? extends SortKey> getSortKeys() {
		return sortKeys;
	}

	/**
	 * Sets the sort keys. Causes the model to re-sort if the provided keys 
	 * differ from the stored state.
	 * @param sortKeys The new sort keys to set.
	 */
	public void setSortKeys(List<? extends SortKey> sortKeys) {
//		if (!sortKeys.equals(this.sortKeys)) {
			this.sortKeys = new ArrayList<SortKey>(sortKeys);
			this.sort();
//		}
	}
	
	/**
	 * Returns the row filter.
	 * @return The row filter.
	 */
	public RowFilter<? super TableModel,? super Integer> getRowFilter() {
		return rowFilter;
	}

	/**
	 * Sets the row filter. Causes the model to re-sort if the provided filter
	 * differs from the stored state.
	 * 
	 * @param rowFilter The new row filter to set.
	 */
	public void setRowFilter(RowFilter<? super TableModel,? super Integer> rowFilter) {
//		if (rowFilter != this.rowFilter) {
			this.rowFilter = rowFilter;
			this.sort();
//		}
	}
	
	/**
	 * Sets the flag denoting whether non-leaf nodes with no visible children
	 * shall be hidden.
	 * @param hideEmpty <code>true</code> if empty non-leaves shall be hidden,
	 *  <code>false</code> otherwise
	 */
	public void setHideEmpty(boolean hideEmpty) {
		this.hideEmpty = hideEmpty;
	}
	
	/**
	 * Returns whether the current sort keys necessitate re-sorting the model.
	 * @return <code>true</code> if sorting is necessary, <code>false</code> 
	 * otherwise 
	 */
	private boolean shouldSort() {
		for (SortKey sortKey : getSortKeys()) {
			if (sortKey.getSortOrder() != SortOrder.UNSORTED) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns whether the current sort keys indicate that the model's sort 
	 * order should be reset.
	 * @return <code>true</code> if resetting is in order, <code>false</code> 
	 * otherwise 
	 */
	private boolean shouldReset() {
		if (rowFilter != null) {
			return false;
		}
		for (SortKey sortKey : getSortKeys()) {
			if (sortKey.getSortOrder() != SortOrder.UNSORTED) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Sorts the whole model w.r.t. the stored sort keys.
	 */
	public void sort() {
        this.sort(this.getRoot());
	}
	
	/**
	 * Sorts the part of the model below the specified parent node.
	 * @param parent The node below which the model will be sorted.
	 */
	public void sort(TreeTableNode parent) {
		this.doSort(parent, shouldReset());
		this.modelSupport.fireTreeStructureChanged(new TreePath(getPathToRoot(parent)));
	}

	/**
	 * Recursively filters, sorts or resets the provided parent node's children.
	 * @param parent The parent node whose children shall be sorted/reset.
	 * @param reset <code>true</code> if the original sort order shall be 
	 * restored, <code>false</code> otherwise
	 */
	private void doSort(TreeTableNode parent, boolean reset) {
		if (parent instanceof SortableTreeTableNode) {
			SortableTreeTableNode node = (SortableTreeTableNode) parent;

			// depth-first sorting, start with leaves by recursing downwards
			Enumeration<? extends TreeTableNode> childEnum = parent.children();
			while (childEnum.hasMoreElements()) {
				TreeTableNode child = childEnum.nextElement();
				this.doSort(child, reset);
			}
			
			// sort node itself, if possible
			if (node.canSort()) {
				if (reset) {
					// reset only if node was sorted before
					if (node.isSorted()) {
						node.reset();
					}
				} else {
					if (node.canSort(this.getSortKeys())) {
						node.sort(this.getSortKeys(), this.getRowFilter(), this.hideEmpty);
					}
				}
			}

		}
	}
	
	/**
	 * {@inheritDoc} <p>
	 * 
	 * Overridden to sort newly inserted nodes if necessary.
	 */
	@Override
	public void insertNodeInto(MutableTreeTableNode newChild, MutableTreeTableNode parent, int index) {
		super.insertNodeInto(newChild, parent, index);
		
		if (shouldSort()) {
			sort(parent);
		}
	}

    private void addAdditionalColumns(Collection<CvTermReference> proteinScores) {
        columnNames.clear();

        // add columns for search engine scores
        ProteinTableHeader[] headers = ProteinTableHeader.values();
        for (ProteinTableHeader header : headers) {
            columnNames.put(header.getHeader(), header.getToolTip());
            if (proteinScores != null && ProteinTableHeader.PROTEIN_ID.getHeader().equals(header.getHeader())) {
                for (uk.ac.ebi.pride.utilities.term.CvTermReference scoreCvTerm : proteinScores) {
                    String name = scoreCvTerm.getName();
                    columnNames.put(name, name);
                }
            }
        }
    }

    public List<String> getColumns(){
        return new ArrayList<String>(columnNames.keySet());
    }

    public List<String> getColumnTooltips(){
        return new ArrayList<String>(columnNames.values());
    }

}
