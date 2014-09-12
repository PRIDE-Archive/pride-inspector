package uk.ac.ebi.pride.toolsuite.gui.component.startup;

import org.bushe.swing.event.annotation.AnnotationProcessor;
import org.bushe.swing.event.annotation.EventSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.toolsuite.gui.EDTUtils;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.mzidentml.SimpleMsDialog;
import uk.ac.ebi.pride.toolsuite.gui.component.table.listener.TableCellMouseMotionListener;
import uk.ac.ebi.pride.toolsuite.gui.event.AddDataSourceEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.ForegroundDataSourceEvent;
import uk.ac.ebi.pride.toolsuite.gui.event.ProcessingDataSourceEvent;


import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.ImageObserver;
import java.util.*;
import java.util.List;

/**
 * DataSourceViewer should be monitor the DataAccessControllers in
 * DataAccessMonitor.
 * <p/>
 * @author ypriverol
 * @author rwang
 * Date: 26-Feb-2010
 * Time: 10:42:08
 */
public class DataSourceViewer extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceViewer.class);

    /**
     * table to display the data access controllers
     */
    private JTable sourceTable = null;

    /**
     * table model for table which displays the data access controllers.
     */
    private DataAccessTableModel sourceTableModel = null;

    /**
     * a reference to PrideInspectorContext
     */
    private PrideInspectorContext context = null;

    /**
     * Constructor
     */
    public DataSourceViewer() {
        // enable annotation
        AnnotationProcessor.process(this);

        // setup main panel
        setupMainPane();

        // set up the rest of components
        addComponents();
    }

    private void setupMainPane() {
        // pride inspector context
        context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();

        // set up the main pane
        this.setLayout(new BorderLayout());
    }

    private void addComponents() {

        JPanel panel = new JPanel();
        panel.setBackground(Color.white);
        panel.setLayout(new BorderLayout());

        // create data source table with data access model
        sourceTableModel = new DataAccessTableModel();
        sourceTable = new DataAccessTable(sourceTableModel);
        sourceTable.setBorder(BorderFactory.createEmptyBorder());
        sourceTable.addMouseMotionListener(new TableCellMouseMotionListener(sourceTable, TableHeader.DATA_SOURCE_COLUMN.getHeader()));

        // set renderer for data source column
        TableColumn sourceCol = sourceTable.getColumn(TableHeader.DATA_SOURCE_COLUMN.getHeader());
        sourceCol.setCellRenderer(new DataAccessTableCellRenderer());

        // set renderer for mzidentml column
        TableColumn mzidentmlCol = sourceTable.getColumn(TableHeader.DATA_MZIDENT_COLUMN.getHeader());
        mzidentmlCol.setCellRenderer(new MzidentMLMSCellRenderer());

        mzidentmlCol.setMaxWidth(20);

        // set renderer for close data source column
        TableColumn closeCol = sourceTable.getColumn(TableHeader.CLOSE_COLUMN.getHeader());
        closeCol.setCellRenderer(new CloseDataSourceCellRenderer());

        // set the max width for the close data source column
        closeCol.setMaxWidth(20);

        // listen to any close data source event
        sourceTable.addMouseListener(new CloseDataSourceMouseListener());

        // listen to any Open mzidentml MS files
        sourceTable.addMouseListener(new MzidentMLOpenMSMouseListener());


        // listen to any row selection
        sourceTable.getSelectionModel().addListSelectionListener(new DataAccessSelectionListener());

        // setup table visual
        sourceTable.setRowSelectionAllowed(true);
        sourceTable.setRowHeight(20);
        sourceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sourceTable.setFillsViewportHeight(true);
        sourceTable.setTableHeader(null);
        sourceTable.setGridColor(Color.white);
        sourceTable.setBackground(Color.white);

        panel.add(sourceTable);

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * This is triggered when data access controller is added/removed
     *
     * @param evt foreground data source event
     */
    @EventSubscriber(eventClass = ForegroundDataSourceEvent.class)
    public void onForegroundDataSourceEvent(ForegroundDataSourceEvent evt) {
        Runnable code;

        if (ForegroundDataSourceEvent.Status.EMPTY.equals(evt.getStatus())) {
            // clear all selection
            code = new Runnable() {
                @Override
                public void run() {
                    sourceTable.clearSelection();
                }
            };
        } else {
            // get the new foreground data access controller
            DataAccessController controller = (DataAccessController) evt.getNewForegroundDataSource();

            // highlight the selected foreground data source
            final int rowNum = sourceTableModel.getRowIndex(controller);

            code = new Runnable() {

                @Override
                public void run() {
                    sourceTable.changeSelection(rowNum, sourceTableModel.getColumnIndex(TableHeader.DATA_SOURCE_COLUMN), false, false);
                }
            };
        }

        // run the code on EDT
        EDTUtils.invokeLater(code);

        // update the table with the new entries
        sourceTable.revalidate();
        sourceTable.repaint();
    }

    @EventSubscriber(eventClass = ProcessingDataSourceEvent.class)
    public void onProcessingDataSourceEvent(ProcessingDataSourceEvent evt){
        DataAccessController controller = (DataAccessController) evt.getDataSource();
        if(context.getDataAccessMonitor().containStatusController(controller, evt.getStatus()))
            context.getDataAccessMonitor().removeStatusController(controller, evt.getStatus());
        else
            context.getDataAccessMonitor().addStatusController(controller,evt.getStatus());

        sourceTable.revalidate();
        sourceTable.repaint();
    }

    @EventSubscriber(eventClass = AddDataSourceEvent.class)
    public void onAddDataSourceEvent(AddDataSourceEvent evt) {
        sourceTable.revalidate();
        sourceTable.repaint();
    }

    /**
     * table column title
     */
    public enum TableHeader {
        DATA_SOURCE_COLUMN("Data Source", "Data Source"),
        DATA_MZIDENT_COLUMN("Open MS Files", "Open Ms Files for mzIdentMl"),
        CLOSE_COLUMN("Close", "Close Data Source");

        private final String header;
        private final String toolTip;

        private TableHeader(String header, String tooltip) {
            this.header = header;
            this.toolTip = tooltip;
        }

        public String getHeader() {
            return header;
        }

        public String getToolTip() {
            return toolTip;
        }
    }

    /**
     * Data access table to show experiment name as tooltip
     */
    private static class DataAccessTable extends JTable {

        private DataAccessTable(TableModel dm) {
            super(dm);
        }

        @Override
        public String getToolTipText(MouseEvent event) {
            String tooltip = null;

            Point p = event.getPoint();
            int rowIndex = rowAtPoint(p);
            int colIndex = columnAtPoint(p);
            int realColIndex = convertColumnIndexToModel(colIndex);
            TableModel model = getModel();
            if (realColIndex == 0 && model != null) {
                Object value = model.getValueAt(rowIndex, 0);
                if (value != null) {
                    tooltip = value.toString();
                }
            } else {
                tooltip = super.getToolTipText(event);
            }

            return tooltip;
        }
    }

    /**
     * DataAccessTableModel tracks data sources stored in DataAccessMonitor
     * <p/>
     * It uses DataAccessMonitor as a background data model, and it also use
     * TableHeader to define the table headers.
     */
    private class DataAccessTableModel extends AbstractTableModel {

        public String getColumnName(int column) {
            return TableHeader.values()[column].getHeader();
        }

        public int getColumnIndex(TableHeader header) {
            int index = -1;
            TableHeader[] headers = TableHeader.values();
            for (int i = 0; i < headers.length; i++) {
                TableHeader tableHeader = headers[i];
                if (tableHeader.equals(header)) {
                    index = i;
                }
            }
            return index;
        }

        @Override
        public int getRowCount() {
            java.util.List<DataAccessController> controllers = context.getControllers();
            return controllers.size();
        }

        @Override
        public int getColumnCount() {
            return TableHeader.values().length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            // get all data access controllers
            java.util.List<DataAccessController> controllers = context.getControllers();

            if (rowIndex >= 0) {
                // return data access controller if the column is data source column
                return TableHeader.values()[columnIndex].equals(TableHeader.DATA_SOURCE_COLUMN) ? controllers.get(rowIndex).getName() : null;
            }
            return null;
        }

        public int getRowIndex(Object controller) {
            // get all data access controllers
            java.util.List<DataAccessController> controllers = context.getControllers();
            return controllers.indexOf(controller);
        }
    }

    /**
     * DataAccessTableCellRender draw a icon in front of each data access controller
     * Depending on the type of the data access controller:
     * <p/>
     * For database based controller, it is a database icon
     * <p/>
     * For file based controller, it is a file icon
     */
    private class DataAccessTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            // get the original component
            final JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // get the current data access controller
            DataAccessController controller = context.getControllers().get(row);
            // get its content categories
            Collection<DataAccessController.ContentCategory> categories = controller.getContentCategories();

            List<ProcessingDataSourceEvent.Status> status = context.getDataAccessMonitor().getStatusController(controller);

            // get the icon depending on the type of the data access controller
            ImageIcon icon = null;
            DataAccessController.Type type = controller.getType();
            if (DataAccessController.Type.XML_FILE.equals(type) || DataAccessController.Type.MZIDENTML.equals(type) || DataAccessController.Type.MZTAB.equals(type)) {
                icon = GUIUtilities.loadImageIcon(context.getProperty(categories.isEmpty() || status.size()>0 ? "file.source.loading.small.icon" : "file.source.small.icon"));
            } else if (DataAccessController.Type.DATABASE.equals(type)) {
                icon = GUIUtilities.loadImageIcon(context.getProperty(categories.isEmpty() || status.size()>0 ? "database.source.loading.small.icon" : "database.source.small.icon"));
            }

            // set the icon
            cell.setIcon(icon);

            if (icon != null && icon.getImageObserver() == null) {
                icon.setImageObserver(new CellImageObserver(table));
            }

            return cell;
        }
    }

    private static class CellImageObserver implements ImageObserver {
        private JTable table;

        private CellImageObserver(JTable table) {
            this.table = table;
        }

        @Override
        public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
            if ((infoflags & (FRAMEBITS | ALLBITS)) != 0) {
                //Rectangle rect = table.getCellRect(row, col, false);
                table.revalidate();
                table.repaint();
            }
            return (infoflags & (ABORT | ALLBITS)) == 0;
        }
    }

    /**
     * Draw a red cross for close the data access controller
     */
    private class CloseDataSourceCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            // get the original component
            JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // get the icon
            Icon icon = GUIUtilities.loadIcon(context.getProperty("close.individual.source.enable.icon.small"));

            // set the icon
            cell.setIcon(icon);

            // overwrite the background changing behavior when selected
            cell.setBackground(Color.white);

            // set the component to none focusable
            cell.setFocusable(false);

            return cell;
        }
    }

    /**
     * Draw a red cross for close the data access controller
     * */
    private class MzidentMLMSCellRenderer extends DefaultTableCellRenderer {


        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            // get the original component
            JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            DataAccessController controller = context.getControllers().get(row);

            Collection<DataAccessController.ContentCategory> categories = controller.getContentCategories();

            // get the icon depending on the type of the data access controller

            DataAccessController.Type type = controller.getType();


            if ((DataAccessController.Type.MZIDENTML.equals(type) || DataAccessController.Type.MZTAB.equals(type)) && !categories.isEmpty()) {
                // get the icon

                Icon icon = GUIUtilities.loadImageIcon(context.getProperty("open.mzidentml.ms.icon.small"));

                cell.setIcon(icon);
                // overwrite the background changing behavior when selected
                cell.setBackground(Color.white);
                // set the component to none focusable
                cell.setFocusable(false);

            }else{
                cell.setBackground(Color.white);
                cell.setIcon(null);
            }
            return cell;
        }

    }


    /**
     * DataAccessSelectionListener is triggered when a selection has been made on a data source
     */
    private class DataAccessSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            // get row number and column number
            int row = sourceTable.getSelectedRow();
            if (row >= 0) {
                int col = sourceTable.getSelectedColumn();

                // get column name
                String colName = sourceTable.getColumnName(col);
                DataAccessController controller = context.getControllers().get(row);
                if (colName.equals(TableHeader.DATA_SOURCE_COLUMN.getHeader()) && !context.isForegroundDataAccessController(controller)) {
                    // close foreground data access controller
                    logger.debug("Set foreground data access controller: {}", controller.getName());
                    context.setForegroundDataAccessController(controller);
                }
            }
        }
    }

    /**
     * CloseDataSourceMouseListener  is triggered when a close action has been made on a data source
     */
    private class CloseDataSourceMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            // get row number and column number
            int row = sourceTable.rowAtPoint(new Point(e.getX(), e.getY()));
            int col = sourceTable.columnAtPoint(new Point(e.getX(), e.getY()));

            // get column name
            String colName = sourceTable.getColumnName(col);
            if (colName.equals(TableHeader.CLOSE_COLUMN.getHeader())) {
                // remove the data access controller from data access monitor
                MzidentMLMSCellRenderer cellRender = (MzidentMLMSCellRenderer) sourceTable.getCellRenderer(row,col-1);

                java.util.List<DataAccessController> controllers = context.getControllers();
                if (row >= 0 && row < controllers.size()) {
                    DataAccessController controller = controllers.get(row);
                    context.removeDataAccessController(controller, true);
                }

            }
        }
    }

    /**
     * CloseDataSourceMouseListener  is triggered when a close action has been made on a data source
     **/
    private class MzidentMLOpenMSMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            // get row number and column number
            int row = sourceTable.rowAtPoint(new Point(e.getX(), e.getY()));
            int col = sourceTable.columnAtPoint(new Point(e.getX(), e.getY()));
            // get column name
            String colName = sourceTable.getColumnName(col);
            if (colName.equals(TableHeader.DATA_MZIDENT_COLUMN.getHeader())) {
                // remove the data access controller from data access monitor
                java.util.List<DataAccessController> controllers = context.getControllers();
                if (row >= 0 && row < controllers.size()) {
                    DataAccessController controller = controllers.get(row);
                    if(controller.getType().equals(DataAccessController.Type.MZIDENTML) || controller.getType().equals(DataAccessController.Type.MZTAB)){
                        Dialog dialog = new SimpleMsDialog(uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getMainComponent(),controller);
                        dialog.setVisible(true);
                    }

                }
            }
        }
    }
}
