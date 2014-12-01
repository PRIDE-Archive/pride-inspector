package uk.ac.ebi.pride.toolsuite.gui.component.table.sorttreetable;

import org.jdesktop.swingx.treetable.DefaultMutableTreeTableNode;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;
import org.jdesktop.swingx.treetable.TreeTableNode;
import uk.ac.ebi.pride.toolsuite.gui.utils.AlphanumComparator;
import uk.ac.ebi.pride.toolsuite.gui.utils.ProteinAccession;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.net.URI;
import java.util.*;

public class SortableTreeTableNode extends DefaultMutableTreeTableNode implements SortableTreeNode {

    private boolean sorted;
    private int[] modelToView;
    private Row[] viewToModel;

    /**
     * The node's user objects.
     */
    protected transient Object[] userObjects;

    /**
     * The flag determining whether the selection state may be changed.
     * Defaults to <code>false</code>.
     */
    private boolean fixed = false;

    /**
     * URI reference for hyperlink interactivity in tree views.
     */
    private URI uri;

    /**
     *
     */
    public SortableTreeTableNode() {
        this(new Object());
    }

    /**
     * Constructs a tree table node bearing properties
     * accepting one or more objects corresponding to the node's columns.
     * @param userObjects The objects to store.
     */
    public SortableTreeTableNode(Object... userObjects) {
        super(userObjects[0]);
        int length = userObjects.length;
        this.userObjects = new Object[length];
        System.arraycopy(userObjects, 0, this.userObjects, 0, length);
    }

    /**
     * Constructs a tree table node bearing checkbox-related properties
     * accepting a single user object and setting the node's <code>fixed</code> state flag.
     * @param userObject The object to store.
     * @param fixed <code>true</code> if this node's checkbox selection state
     * cannot be changed, <code>false</code> otherwise.
     */
    public SortableTreeTableNode(Object userObject, boolean fixed) {
        this(userObject);
        this.fixed = fixed;
    }



    /**
     * Gets the underlying values for this node that correspond to a particular tabular column.<br><br>
     * Sub-classes need to override this method if more than the singular column value returned by
     * <code>getValueAt()</code> wrapped in a list is to be returned.
     * @param column the column index
     * @return a collection of underlying values
     */
    public Collection<?> getValuesAt(int column) {
        List<Object> res = new ArrayList<Object>();
        res.add(this.getValueAt(column));
        return res;
    }

    @Override
    public int getChildCount() {
        return (sorted) ? viewToModel.length : super.getChildCount();
    }

   @Override
    public TreeTableNode getChildAt(int childIndex) {
        if ((sorted) && (childIndex > viewToModel.length)) {
            return new SortableTreeTableNode("I SHOULD BE INVISIBLE");
        }
        return super.getChildAt(convertRowIndexToModel(childIndex));
    }

    @Override
    public int getIndex(TreeNode node) {
        return convertRowIndexToView(children.indexOf(node));
    }

    @Override
    public void setParent(MutableTreeTableNode newParent) {
        super.setParent(newParent);
    }

    /**
     * Provides a child node with the ability to be sorted and/or filtered.
     */
    protected class Row<M, I> extends RowFilter.Entry<M, I> implements Comparable<Row> {
        /**
         * The tree table node containing the row's cell values.
         */
        protected TreeTableNode node;
        /**
         * The row's model index.
         */
        protected int modelIndex;
        /**
         * The row's list of column indices to be sorted and their respective
         * sort orders.
         */
        protected List<? extends RowSorter.SortKey> sortKeys;

        /**
         * Constructs a row object.
         * @param node The node upon which comparisons are evaluated.
         * @param modelIndex The row index.
         * @param sortKeys The list of sort keys.
         */
        public Row(TreeTableNode node, int modelIndex,
                   List<? extends RowSorter.SortKey> sortKeys) {
            this.node = node;
            this.modelIndex = modelIndex;
            this.sortKeys = sortKeys;
        }

        @Override
        public String toString() {
            return ("" + this.modelIndex + " " + node.getUserObject()
                    .toString());
        }

        @Override
        public M getModel() {
            return null; // we don't need this
        }

        @Override
        public int getValueCount() {
            return children.get(modelIndex).getColumnCount();
        }

        @Override
        public Object getValue(int index) {
            // special case to return underlying node
            if (index == -1) {
                return this.node;
            }
            // return column value
            return children.get(modelIndex).getValueAt(index);
        }

        @Override
        public String getStringValue(int index) {
            Object value = children.get(modelIndex).getValueAt(index);
            return (value == null) ? "" : value.toString();
        }

        @Override
        public I getIdentifier() {
            return null; // we don't need this
        }

        @SuppressWarnings("unchecked")
        @Override
        public int compareTo(Row that) {
            // trivial case
            if (that == null) {
                return 1;
            }
            // initialize result with fall-back value
            int result = this.modelIndex - that.modelIndex;
            for (RowSorter.SortKey sortKey : this.sortKeys) {
                if (sortKey.getSortOrder() != SortOrder.UNSORTED) {
//                    // sort leaf nodes to always appear below non-leaves
//                    if (this.node.isLeaf() != that.node.isLeaf()) {
//                        // either one node might be a leaf
//                        result = that.node.getChildCount() - this.node.getChildCount();
//                    } else {
                        int column = sortKey.getColumn();
                        Object this_value = this.node.getValueAt(column);
                        Object that_value = that.node.getValueAt(column);
                        // define null as less than not-null
                        if (this_value == null) {
                            result = (that_value == null) ? 0 : -1;
                        } else if (that_value == null) {
                            result = 1;
                        } else {
                            // both value objects are not null, invoke comparison
                            if (this_value instanceof String) {
                                // special case for strings to get a more natural sorting
                                result = AlphanumComparator.getInstance().compare(this_value, that_value);
                            } else if(this_value instanceof ProteinAccession){
                                String this_mapped = ((ProteinAccession)this_value).getMappedAccession();
                                String that_mapped = ((ProteinAccession)that_value).getMappedAccession();
                                result = AlphanumComparator.getInstance().compare(this_mapped, that_mapped);
                            } else {
                                result = ((Comparable<Object>) this_value).compareTo(that_value);
                            }
                        }
                        // correct result w.r.t. sort order
                        if (sortKey.getSortOrder() == SortOrder.DESCENDING) {
                            result *= -1;
                        }
//                    }
                }
                if (result != 0) {
                    break;
                }
            }
            return result;
        }

    }
	
	@Override
	public Object getUserObject() {
		return this.getUserObject(0);
	}

	/**
	 * Returns this node's i-th user object. 
	 * @param i The index of the user object to retrieve.
	 * @return The i-th object stored in this node.
	 */
	private Object getUserObject(int i) {
		return this.userObjects[i];
	}

	/**
	 * Sets the user objects stored in this node.
	 * @param userObjects The objects to store.
	 */
	public void setUserObjects(Object... userObjects) {
		this.userObjects = userObjects;
	}
	
	@Override
	public int getColumnCount() {
		return this.userObjects.length;
	}
	
	@Override
	public Object getValueAt(int column) {
		return (column >= this.userObjects.length) ? null : this.userObjects[column];
	}
	
	@Override
	public void setValueAt(Object aValue, int column) {
		try {
			this.userObjects[column] = aValue;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @Override
    public boolean isLeaf() {
        return (children.size() == 0);
    }
	
	/**
	 * Returns this node's <code>fixed</code> flag.
	 * @return
	 */
	public boolean isFixed() {
		return fixed;
	}
	
	/**
	 * Sets this node's <code>fixed</code> flag.
	 * @param fixed <code>true</code> if this node's checkbox selection state 
	 * cannot be changed, <code>false</code> otherwise.
	 */
	public void setFixed(boolean fixed) {
		this.fixed = fixed;
	}
	
	/**
	 * Returns the {@link javax.swing.tree.TreePath} leading from the tree's root to this node.
	 * @return The path to this node.
	 */
	public TreePath getPath() {
		SortableTreeTableNode node = this;
	    List<SortableTreeTableNode> list = new ArrayList<SortableTreeTableNode>();

	    // Add all nodes to list
	    while (node != null) {
	        list.add(node);
	        node = (SortableTreeTableNode) node.getParent();
	    }
	    Collections.reverse(list);

	    // Convert array of nodes to TreePath
	    return new TreePath(list.toArray());
	}

	/**
	 * Returns whether this node contains a valid URI reference.
	 * @return <code>true</code> if the URI is valid, <code>false</code> otherwise.
	 */
	public boolean hasURI() {
		return (uri != null);
	}

	/**
	 * Returns this node's URI reference.
	 * @return This node's URI.
	 */
	public URI getURI() {
		return uri;
	}

	/**
	 * Sets this node's URI reference.
	 * @param uri The URI to set.
	 */
	public void setURI(URI uri) {
		this.uri = uri;
	}

	/* starting here everything was copied from DefaultMutableTreeNode */

	/**
	 * Removes all child nodes from this node.
	 */
	public void removeAllChildren() {
		for (int i = getChildCount() - 1; i >= 0; i--) {
			remove(i);
		}
	}

    /**
     * Returns true if <code>aNode</code> is a child of this node.  If
     * <code>aNode</code> is null, this method returns false.
     *
     * @return	true if <code>aNode</code> is a child of this node; false if
     *  		<code>aNode</code> is null
     */
	public boolean isNodeChild(TreeNode aNode) {
		boolean retval;

		if (aNode == null) {
			retval = false;
		} else {
			if (getChildCount() == 0) {
				retval = false;
			} else {
				retval = (aNode.getParent() == this);
			}
		}

		return retval;
	}

    /**
     * Returns this node's first child.  If this node has no children,
     * throws NoSuchElementException.
     *
     * @return	the first child of this node
     * @exception	java.util.NoSuchElementException	if this node has no children
     */
	public TreeNode getFirstChild() {
		if (getChildCount() == 0) {
			throw new NoSuchElementException("node has no children");
		}
		return getChildAt(0);
	}

    /**
     * Returns the child in this node's child array that immediately
     * follows <code>aChild</code>, which must be a child of this node.  If
     * <code>aChild</code> is the last child, returns null.  This method
     * performs a linear search of this node's children for
     * <code>aChild</code> and is O(n) where n is the number of children; to
     * traverse the entire array of children, use an enumeration instead.
     *
     * @see		#children
     * @exception	IllegalArgumentException if <code>aChild</code> is
     *					null or is not a child of this node
     * @return	the child of this node that immediately follows
     *		<code>aChild</code>
     */
	public TreeNode getChildAfter(TreeNode aChild) {
		if (aChild == null) {
			throw new IllegalArgumentException("argument is null");
		}

		int index = getIndex(aChild); // linear search

		if (index == -1) {
			throw new IllegalArgumentException("node is not a child");
		}

		if (index < getChildCount() - 1) {
			return getChildAt(index + 1);
		} else {
			return null;
		}
	}
	
	/**
     * Returns true if <code>anotherNode</code> is a sibling of (has the
     * same parent as) this node.  A node is its own sibling.  If
     * <code>anotherNode</code> is null, returns false.
     *
     * @param	anotherNode	node to test as sibling of this node
     * @return	true if <code>anotherNode</code> is a sibling of this node
     */
	public boolean isNodeSibling(TreeNode anotherNode) {
		boolean retval;

		if (anotherNode == null) {
			retval = false;
		} else if (anotherNode == this) {
			retval = true;
		} else {
			TreeNode myParent = getParent();
			retval = (myParent != null && myParent == anotherNode.getParent());

			if (retval
					&& !((SortableTreeTableNode) getParent())
							.isNodeChild(anotherNode)) {
				throw new Error("sibling has different parent");
			}
		}

		return retval;
	}

	/**
     * Returns the next sibling of this node in the parent's children array.
     * Returns null if this node has no parent or is the parent's last child.
     * This method performs a linear search that is O(n) where n is the number
     * of children; to traverse the entire array, use the parent's child
     * enumeration instead.
     *
     * @see     #children
     * @return  the sibling of this node that immediately follows this node
     */
	public SortableTreeTableNode getNextSibling() {
		SortableTreeTableNode retval;

		SortableTreeTableNode myParent = (SortableTreeTableNode) getParent();

		if (myParent == null) {
			retval = null;
		} else {
			retval = (SortableTreeTableNode) myParent.getChildAfter(this);	// linear search
		}

		if (retval != null && !isNodeSibling(retval)) {
			throw new Error("child of parent is not a sibling");
		}

		return retval;
	}
    
	/**
     * Finds and returns the first leaf that is a descendant of this node --
     * either this node or its first child's first leaf.
     * Returns this node if it is a leaf.
     *
     */
	public SortableTreeTableNode getFirstLeaf() {
		SortableTreeTableNode node = this;

		while (!node.isLeaf()) {
			node = (SortableTreeTableNode) node.getFirstChild();
		}

		return node;
	}
	
	public boolean isRoot() {
		return (getParent() == null);
	}

	/**
     * Returns the leaf after this node or null if this node is the
     * last leaf in the tree.
     * <p>
     * In this implementation of the <code>MutableNode</code> interface,
     * this operation is very inefficient. In order to determine the
     * next node, this method first performs a linear search in the 
     * parent's child-list in order to find the current node. 
     * <p>
     * That implementation makes the operation suitable for short
     * traversals from a known position. But to traverse all of the 
     * leaves in the tree, you should use <code>depthFirstEnumeration</code>
     * to enumerate the nodes in the tree and use <code>isLeaf</code>
     * on each node to determine which are leaves.
     *
     * @see	#depthFirstEnumeration
     * @see	#isLeaf
     * @return	returns the next leaf past this node
     */
	public SortableTreeTableNode getNextLeaf() {
		SortableTreeTableNode myParent = (SortableTreeTableNode) this.getParent();
		
		if (myParent == null) {
			return null;
		}

		SortableTreeTableNode nextSibling = this.getNextSibling();	// linear search

		if (nextSibling != null) {
			return nextSibling.getFirstLeaf();
		}

		return myParent.getNextLeaf();	// tail recursion
	}
	
	/**
     * Creates and returns an enumeration that traverses the subtree rooted at
     * this node in depth-first order.  The first node returned by the
     * enumeration's <code>nextElement()</code> method is the leftmost leaf.
     * This is the same as a postorder traversal.<P>
     *
     * Modifying the tree by inserting, removing, or moving a node invalidates
     * any enumerations created before the modification.
     *
     * @return  an enumeration for traversing the tree in depth-first order
     */
    public Enumeration<TreeNode> depthFirstEnumeration() {
        return new PostorderEnumeration(this);
    }
    
    /** 
     * Provides an enumeration of a tree traversing it preordered.
     *
     * @see	#preOrderEnumeration
     * @return an enumeration for preordered traversal.
    */
    public Enumeration<TreeNode> preOrderEnumeration() {
    	return new PreorderEnumeration(this);
    }

    @Override
    public void sort(List<? extends RowSorter.SortKey> sortKeys, RowFilter<? super TableModel, ? super Integer> filter, boolean hideEmpty) {
        int childCount = children.size();
        int excludedCount = 0;

        // build view-to-model mapping
        modelToView = new int[childCount];
        List<Row> viewToModelList = new ArrayList<Row>(childCount);

        for (int i = 0; i < childCount; i++) {
            MutableTreeTableNode child = children.get(i);
            Row<TableModel, Integer> row = new Row<TableModel, Integer>(child, i, sortKeys);

            // check whether the child node is eligible for filtering (only
            // leaves can be actively filtered, parent nodes will automatically
            // become invisible when they have no visible children)
            if (child.isLeaf()) {
                // check whether the filter permits this child (if any filter is
                // configured at all)
                if ((filter == null) || (filter.include(row))) {
                    viewToModelList.add(row);
                } else {
                    excludedCount++;
                }
            } else {
                // check whether the child node has any visible children of its
                // own, treat as excluded if the corresponding flag is set
                if ((child.getChildCount() == 0) && hideEmpty) {
                    excludedCount++;
                } else {
                    viewToModelList.add(row);
                }
            }

//			if ((filter == null) || (filter.include(row))) {
//				viewToModelList.add(row);
//			} else {
//				excludedCount++;
//			}

            // initialize model-to-view mapping while we're at it
            modelToView[i] = -1;
        }
        viewToModel = viewToModelList.toArray(new Row[childCount - excludedCount]);

        // sort view-to-model mapping
        if (sortKeys != null) {
            Arrays.sort(viewToModel);
        }

        // build model-to-view mapping
        for (int i = 0; i < viewToModel.length; i++) {
            modelToView[viewToModel[i].modelIndex] = i;
        }

        sorted = true;

    }

    @Override
    public int convertRowIndexToModel(int viewIndex) {
        return (sorted) ? viewToModel[viewIndex].modelIndex : viewIndex;
    }

    @Override
    public int convertRowIndexToView(int modelIndex) {
        return (!sorted || (modelIndex < 0) || (modelIndex >= modelToView.length)) ? modelIndex
                : modelToView[modelIndex];
    }

    @Override
    public boolean canSort() {
        return !isLeaf();
    }

    @Override
    public boolean canSort(List<? extends RowSorter.SortKey> sortKeys) {
        return true;
    }

    @Override
    public boolean isSorted() {
        return false;
    }

    @Override
    public void reset() {
        sorted = false;
    }

    final class PostorderEnumeration implements Enumeration<TreeNode> {
        protected TreeNode root;
        protected Enumeration<TreeNode> children;
        protected Enumeration<TreeNode> subtree;

        @SuppressWarnings("unchecked")
		public PostorderEnumeration(TreeNode rootNode) {
            super();
            root = rootNode;
            children = root.children();
            subtree = DefaultMutableTreeNode.EMPTY_ENUMERATION;
        }

        public boolean hasMoreElements() {
            return root != null;
        }

        public TreeNode nextElement() {
            TreeNode retval;

            if (subtree.hasMoreElements()) {
                retval = subtree.nextElement();
            } else if (children.hasMoreElements()) {
                subtree = new PostorderEnumeration(
                                (TreeNode)children.nextElement());
                retval = subtree.nextElement();
            } else {
                retval = root;
                root = null;
            }

            return retval;
        }
    }  // End of class PostorderEnumeration
	
	/**
	 * PreorderEnumeration class - taken from original DefaultMutableTreeNode class - slightly modified with generic code.
	 */
	final class PreorderEnumeration implements Enumeration<TreeNode> {
		TreeNode next;

		Stack<Enumeration<TreeNode>> childrenEnums = new Stack<Enumeration<TreeNode>>();

		@SuppressWarnings("unchecked")
		public PreorderEnumeration(TreeNode node) {
			next = node;
			childrenEnums.push(node.children());
		}

		public boolean hasMoreElements() {
			return next != null;
		}

		public TreeNode nextElement() {
			if (next == null)
				throw new NoSuchElementException("No more elements left.");
			TreeNode current = next;
			Enumeration children = (Enumeration) childrenEnums.peek();

			next = traverse(children);
			return current;
		}

		@SuppressWarnings("unchecked")
		private TreeNode traverse(Enumeration children) {
			if (children.hasMoreElements()) {
				TreeNode child = (TreeNode) children.nextElement();
				childrenEnums.push(child.children());
				return child;
			}
			childrenEnums.pop();

			if (childrenEnums.isEmpty())
				return null;
			else {
				return traverse((Enumeration) childrenEnums.peek());
			}
		}

        @Override
        public String toString() {
            return "lolo";
        }
    }
}
