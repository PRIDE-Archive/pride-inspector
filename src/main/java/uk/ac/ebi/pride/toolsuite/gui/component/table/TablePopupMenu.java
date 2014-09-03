package uk.ac.ebi.pride.toolsuite.gui.component.table;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.search.SearchFactory;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.desktop.DesktopContext;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;

/**
 * @author Rui Wang
 * @version $Id$
 */
public class TablePopupMenu extends JPopupMenu implements ActionListener{

    /**
     * Action command for copy cell
     */
    private static final String COPY_CELL_ACTION = "Copy Cell";

    /**
     * Action command for copy row
     */
    private static final String COPY_ROW_ACTION = "Copy Row";
    /**
     * Action command for select all
     */
    private static final String SELECT_ALL_ACTION = "Select All";

    /**
     * Action command for deselect all
     */
    private static final String DESELECT_ALL_ACTION = "Deselect All";

    /**
     * Action command for search table
     */
    private static final String FIND = "find";

    private final JXTable table;

    /**
     * System clipboard
     */
    private Clipboard clipboard;

    /**
     * row where mouse clicked
     */
    private int rowByMouse;

    /**
     * column where mouse clicked
     */
    private int columnByMouse;

    public TablePopupMenu(JXTable table) throws HeadlessException {
        this.table = table;
        this.clipboard =  Toolkit.getDefaultToolkit().getSystemClipboard();
        initMenuItem();
    }

    private void initMenuItem() {
        // pride inspector context
        DesktopContext context = uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();

        // search table
        JMenuItem findItem = new JMenuItem(context.getProperty("search.table.title"),
                GUIUtilities.loadIcon(context.getProperty("search.table.small.icon")));
        findItem.setActionCommand(FIND);
        findItem.addActionListener(this);
        this.add(findItem);

        // separator
        this.add(new JSeparator());

        // select all rows
        JMenuItem selectAllItem = new JMenuItem(context.getProperty("select.all.title"));
        selectAllItem.setActionCommand(SELECT_ALL_ACTION);
        selectAllItem.addActionListener(this);
        this.add(selectAllItem);

        // deselect all rows
        JMenuItem deselectAllItem = new JMenuItem(context.getProperty("deselect.all.title"));
        deselectAllItem.setActionCommand(DESELECT_ALL_ACTION);
        deselectAllItem.addActionListener(this);
        this.add(deselectAllItem);

        // add copy cell menu item
        JMenuItem copyCellItem = new JMenuItem(context.getProperty("copy.cell.title"),
                GUIUtilities.loadIcon(context.getProperty("copy.cell.small.icon")));
        copyCellItem.setActionCommand(COPY_CELL_ACTION);
        copyCellItem.addActionListener(this);
        this.add(copyCellItem);

        // add copy row menu item
        JMenuItem copyRowItem = new JMenuItem(context.getProperty("copy.row.title"),
                GUIUtilities.loadIcon(context.getProperty("copy.row.small.icon")));
        copyRowItem.setActionCommand(COPY_ROW_ACTION);
        copyRowItem.addActionListener(this);
        this.add(copyRowItem);

        // add popup menu listener
        PopupListener listener = new PopupListener();
        table.addMouseListener(listener);

        // listener to ctrl + c from keyboard
        registerKeyboardStroke();
    }

    /**
     * Register keyboard action, listens to ctrl+C to copy rows
     */
    private void registerKeyboardStroke() {
        // copy key stroke
        KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);

        // register key stroke
        this.registerKeyboardAction(this, COPY_ROW_ACTION, copy, JComponent.WHEN_FOCUSED);

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String evtCmd = e.getActionCommand();

        if (COPY_CELL_ACTION.equals(evtCmd)) {
            copyCellToClipboard(rowByMouse, columnByMouse);
        } else if (COPY_ROW_ACTION.equals(evtCmd)) {
            copyRowsToClipboard(table.getSelectedRows(), getAllColumns());
        } else if (SELECT_ALL_ACTION.equals(evtCmd)) {
            selectAllRows();
        } else if (DESELECT_ALL_ACTION.equals(evtCmd)) {
            deselectAllRows();
        } else if (FIND.equals(evtCmd)) {
            SearchFactory.getInstance().showFindInput(table, table.getSearchable());
        }
    }

    private void selectAllRows() {
        int rowCnt = table.getRowCount();
        // todo: EDT ???
        table.getSelectionModel().setSelectionInterval(0, rowCnt);
    }

    private void deselectAllRows() {
        table.clearSelection();
    }


    /**
     * Copy cell value to clipboard
     *
     * @param row row number
     * @param col column number
     */
    private void copyCellToClipboard(int row, int col) {
        // get the value selected by mouse
        String str = getCellStringValue(row, col);

        // add to clipboard
        StringSelection strSelection = new StringSelection(str);
        clipboard.setContents(strSelection, strSelection);
    }

    /**
     * Copy specified rows to clipboard
     *
     * @param rows a array of row numbers
     * @param cols a array of column numbers
     */
    private void copyRowsToClipboard(int[] rows, int[] cols) {
        // string builder to store the values
        StringBuilder strBuilder = new StringBuilder();

        if (rows != null && cols != null) {
            // row count
            int rowCnt = rows.length;

            // column count
            int colCnt = cols.length;

            // iterate over all the selected rows, append all the values
            for (int i = 0; i < rowCnt; i++) {
                if (i < table.getRowCount()) {
                    for (int j = 0; j < colCnt; j++) {
                        if (j < table.getColumnCount()) {
                            // cell string value
                            String str = getCellStringValue(rows[i], cols[j]);

                            // append the value to string builder
                            strBuilder.append(str);
                            if (j < colCnt - 1) {
                                strBuilder.append(Constants.TAB);
                            }
                        }
                    }
                    strBuilder.append(Constants.LINE_SEPARATOR);
                }
            }
        }

        // add to clipboard
        StringSelection strSelection = new StringSelection(strBuilder.toString());
        clipboard.setContents(strSelection, strSelection);
    }

    /**
     * Get cell string value using row number and column number
     *
     * @param row row number
     * @param col column number
     * @return String   cell string value
     */
    private String getCellStringValue(int row, int col) {
        // get the value selected by mouse
        Object value = table.getValueAt(row, col);

        // get output string
        String str = "";
        if (value != null) {
            str = value.toString();
        }

        return str;
    }

    /**
     * Get a list of indexes for all columns
     *
     * @return int[]    a list of column indexes
     */
    private int[] getAllColumns() {
        int colCnt = table.getColumnCount();
        int[] cols = new int[colCnt];
        for (int i = 0; i < colCnt; i++) {
            cols[i] = i;
        }
        return cols;
    }

    /**
     * Listen to mouse click and show popup menu
     */
    private class PopupListener extends MouseAdapter {

        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                TablePopupMenu.this.show(e.getComponent(),
                        e.getX(), e.getY());
                Point clickPoint = new Point(e.getX(), e.getY());
                rowByMouse = table.rowAtPoint(clickPoint);
                columnByMouse = table.columnAtPoint(clickPoint);
            }
        }
    }
}
