/*
 * Created by JFormDesigner on Tue Sep 11 14:32:16 BST 2012
 */

package uk.ac.ebi.pride.toolsuite.gui.component.mzidentml;

import java.awt.event.*;

import org.jdesktop.swingx.border.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.*;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.dialog.SimpleFileDialog;
import uk.ac.ebi.pride.toolsuite.gui.component.table.listener.TableCellMouseMotionListener;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import javax.help.CSH;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

/**
 * Dialog to add Ms files to the mzIdentMl files
 * Note: this task doesn't check whether file has been loaded before
 * <p/>
 * <p/>
 * User: ypriverol
 * Date: 13-Sep-2012
 * Time: 10:37:49
 */
public class MzIdMsDialog extends JDialog {

    private static final Logger logger = LoggerFactory.getLogger(MzIdMsDialog.class);

    private static final String TITLE_FILES = "mzIdentMl Files";

    private static final String TITLE_FILES_TOOLTIP = "Files for the Analysis";

    private static final String COLUMN_HEADER_TITLE = "File Name";

    private static final String COLUMN_HEADER_SIZE = "Size (M)";

    private static final String COLUMN_HEADER_TYPE = "Type";

    private static final String COLUMN_HEADER_REMOVE = "Remove";

    private static final String DIALOG_TITLE = "Load spectrum files";

    private static final String MZML_FILE = "MZML";

    private static final String MZXML_FILE = "MZXML";

    private static final String MZDATA_FILE = "MZDATA";

    private static final String MGF_FILE = "MGF";

    private static final String PKL_FILE = "PKL";


    /**
     * table to display the mzidentml Files
     */

    private JTable sourceTable = null;

    /**
     * table model for table which displays the data access controllers.
     */

    private DataAccessTableModel sourceTableModel = null;

    private PrideInspectorContext context;

    private java.util.List<File> fileList = null;

    private File currentFile = null;

    private Map<File, List<File>> mzidentmlFiles = null;


    public MzIdMsDialog(Frame owner, List<File> mzIdFiles) {
        super(owner);
        this.setTitle(DIALOG_TITLE);
        this.setPreferredSize(new Dimension(600, 445));
        fileList = mzIdFiles;
        initMzIdList(fileList);
        context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
        initComponents();
    }

    private void initMzIdList(List<File> files) {
        mzidentmlFiles = new HashMap<File, List<File>>(fileList.size());
        for (File file : files) {
            mzidentmlFiles.put(file, null);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        splitPane1 = new JSplitPane();
        menuPanel = new JPanel();
        msFilesPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        msFileTable = new JTable();
        globalAddPanel = new JPanel();
        panel2 = new JPanel();
        separator1 = new JSeparator();
        headerPanel = new JPanel();
        setButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        Container contentPane = getContentPane();

        //======== splitPane1 ========
        {
            splitPane1.setDividerLocation(146);
            splitPane1.setDividerSize(4);

            //======== menuPanel ========
            {
                menuPanel.setLayout(new BorderLayout(8, 8));
            }
            splitPane1.setLeftComponent(menuPanel);

            //======== msFilesPanel ========
            {
                msFilesPanel.setLayout(new BorderLayout());

                //======== scrollPane1 ========
                {
                    scrollPane1.setPreferredSize(new Dimension(450, 419));
                    scrollPane1.setForeground(Color.white);

                    //---- msFileTable ----
                    msFileTable.setModel(new DefaultTableModel(
                            new Object[][]{
                            },
                            new String[]{
                                    COLUMN_HEADER_TITLE, COLUMN_HEADER_SIZE, COLUMN_HEADER_TYPE, COLUMN_HEADER_REMOVE
                            }
                    ) {
                        Class<?>[] columnTypes = new Class<?>[]{
                                String.class, String.class, String.class, ImageIcon.class
                        };
                        boolean[] columnEditable = new boolean[]{
                                false, false, false, false
                        };

                        @Override
                        public Class<?> getColumnClass(int columnIndex) {
                            return columnTypes[columnIndex];
                        }

                        @Override
                        public boolean isCellEditable(int rowIndex, int columnIndex) {
                            return columnEditable[columnIndex];
                        }
                    });
                    {
                        TableColumnModel cm = msFileTable.getColumnModel();
                        cm.getColumn(0).setPreferredWidth(160);
                        cm.getColumn(1).setPreferredWidth(55);
                        cm.getColumn(2).setPreferredWidth(40);
                        cm.getColumn(3).setPreferredWidth(30);
                        cm.getColumn(3).setWidth(30);
                    }
                    msFileTable.setBorder(null);
                    msFileTable.setGridColor(Color.white);
                    msFileTable.setFocusTraversalPolicyProvider(true);
                    scrollPane1.getViewport().setBackground(Color.white);
                    scrollPane1.setViewportView(msFileTable);
                }
                msFilesPanel.add(scrollPane1, BorderLayout.CENTER);

                //======== globalAddPanel ========
                {
                    globalAddPanel.setLayout(new BorderLayout(12, 12));
                }
                msFilesPanel.add(globalAddPanel, BorderLayout.SOUTH);

                //======== panel2 ========
                {
                    panel2.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 3));
                }
                msFilesPanel.add(panel2, BorderLayout.WEST);
            }
            splitPane1.setRightComponent(msFilesPanel);
        }

        //======== headerPanel ========
        {
            headerPanel.setLayout(new BorderLayout());
        }

        //---- setButton ----
        setButton.setText("Set");
        setButton.setEnabled(false);
        setButton.setMaximumSize(new Dimension(75, 25));
        setButton.setMinimumSize(new Dimension(75, 25));

        //---- cancelButton ----
        cancelButton.setText("Cancel");

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addComponent(separator1, GroupLayout.Alignment.TRAILING)
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(headerPanel, GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                                        .addComponent(splitPane1, GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                                .addGap(0, 360, Short.MAX_VALUE)
                                                .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(setButton, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(splitPane1, GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(separator1, GroupLayout.PREFERRED_SIZE, 5, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(setButton, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE))
                                .addGap(11, 11, 11))
        );
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
        addSubComponents();
    }

    private void addSubComponents() {

        //headerPanel.setBorder(BorderFactory.createEtchedBorder());

        JPanel addButtonPane = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton addButton = new JButton("Add spectra files");
        addButton.setPreferredSize(new Dimension(140, 25));
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addButtonActionPerformed(e);
            }
        });

        setButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setDialog();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                closeDialog();
            }
        });


        addButtonPane.add(addButton);
        globalAddPanel.add(addButtonPane, BorderLayout.CENTER);

        // get display related details
        Icon dataSourceViewerIcon = GUIUtilities.loadIcon(context.getProperty("data.source.viewer.small.icon"));
        JLabel titleLabel = new JLabel(TITLE_FILES);
        titleLabel.setIcon(dataSourceViewerIcon);
        titleLabel.setToolTipText(TITLE_FILES_TOOLTIP);

        headerPanel.add(titleLabel, BorderLayout.WEST);

        Icon helpIcon = GUIUtilities.loadIcon(context.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, "Help");
        helpButton.setForeground(Color.blue);
        CSH.setHelpIDString(helpButton, "help.ms.information");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        helpButton.setBorder(BorderFactory.createEtchedBorder());

        headerPanel.add(helpButton, BorderLayout.EAST);

        // create data source table with data access model
        sourceTableModel = new DataAccessTableModel();

        sourceTable = new DataAccessTable(sourceTableModel);
        sourceTable.addMouseMotionListener(new TableCellMouseMotionListener(sourceTable, TableHeader.DATA_SOURCE_COLUMN.getHeader()));

        // set renderer for data source column
        TableColumn sourceCol = sourceTable.getColumn(TableHeader.DATA_SOURCE_COLUMN.getHeader());
        sourceCol.setCellRenderer(new DataAccessTableCellRenderer());

        // listen to any row selection
        sourceTable.getSelectionModel().addListSelectionListener(new DataAccessSelectionListener());
        // setup table visual

        sourceTable.setRowSelectionAllowed(true);
        sourceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sourceTable.changeSelection(0, sourceTableModel.getColumnIndex(TableHeader.DATA_SOURCE_COLUMN), false, false);
        sourceTable.setRowHeight(20);
        sourceTable.setFillsViewportHeight(true);
        sourceTable.setTableHeader(null);
        sourceTable.setGridColor(Color.white);
        sourceTable.setBackground(Color.white);

        JPanel mzIdMlPanel = new JPanel();
        mzIdMlPanel.setBackground(Color.white);
        mzIdMlPanel.setLayout(new BorderLayout());
        mzIdMlPanel.add(sourceTable);

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(mzIdMlPanel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        this.add(scrollPane, BorderLayout.CENTER);

        JPanel mzIdMlPanelLevel = new JPanel();
        mzIdMlPanelLevel.setLayout(new BorderLayout());
        mzIdMlPanelLevel.setBorder(BorderFactory.createLineBorder(Color.gray));
        mzIdMlPanelLevel.add(scrollPane, BorderLayout.CENTER);

        JScrollPane dataSourceScrollPane = new JScrollPane(mzIdMlPanelLevel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        dataSourceScrollPane.setBorder(new DropShadowBorder(Color.DARK_GRAY, 5));
        menuPanel.add(dataSourceScrollPane, BorderLayout.CENTER);

        msFileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                msFileTableMouseReleased(e);
            }
        });
    }

    private void setDialog() {
        this.setVisible(false);
    }

    private void closeDialog() {
        this.setVisible(false);
        this.dispose();
    }

    /**
     * table column title
     */
    public enum TableHeader {
        DATA_SOURCE_COLUMN("Data Source", "Data Source");
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
            return fileList.size();
        }

        @Override
        public int getColumnCount() {
            return TableHeader.values().length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            // get all data access files
            java.util.List<File> files = fileList;
            if (rowIndex >= 0) {
                // return data access file if the column is data source column
                return TableHeader.values()[columnIndex].equals(TableHeader.DATA_SOURCE_COLUMN) ? files.get(rowIndex).getName() : null;
            }
            return null;
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

            // get the icon depending on the type of the data access controller

            Icon icon = GUIUtilities.loadIcon(context.getProperty("file.source.small.icon"));

            // set the icon
            cell.setIcon(icon);

            return cell;
        }
    }

    /**
     * DataAccessSelectionListener is triggered when a selection has been made on a data source
     */
    private class DataAccessSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            int row = sourceTable.getSelectedRow();
            if (row >= 0) {
                int col = sourceTable.getSelectedColumn();
                // get column name
                String colName = sourceTable.getColumnName(col);
                File file = fileList.get(row);
                if (colName.equals(TableHeader.DATA_SOURCE_COLUMN.getHeader()) && file != currentFile) {
                    currentFile = file;
                    removeTableMSFiles();
                    addTableMSFiles(mzidentmlFiles.get(file));
                    logger.debug("Set foreground data access controller: {}", file.getName());
                }
            }
        }
    }


    private void msFileTableMouseReleased(MouseEvent e) {
        int row = msFileTable.rowAtPoint(e.getPoint());
        int col = msFileTable.columnAtPoint(e.getPoint());
        String colName = msFileTable.getColumnName(col);
        if (colName.equals(COLUMN_HEADER_REMOVE) && row >= 0) {
            DefaultTableModel model = (DefaultTableModel) msFileTable.getModel();
            model.removeRow(row);
            int rowSource = sourceTable.getSelectedRow();
            File file = fileList.get(rowSource);
            List<File> files = mzidentmlFiles.get(file);
            files.remove(row);
            mzidentmlFiles.put(file, files);
        }
        boolean enableSet = false;
        for (File file : mzidentmlFiles.keySet())
            if (mzidentmlFiles.get(file) != null && mzidentmlFiles.get(file).size() > 0) enableSet = true;
        setButton.setEnabled(enableSet);
    }

    private void addButtonActionPerformed(ActionEvent e) {
        SimpleFileDialog simpleFileDialog = new SimpleFileDialog(context.getOpenFilePath(), "Open MS Files", true, null, true,
                Constants.MZML_FILE,
                Constants.MZXML_FILE,
                Constants.MGF_FILE,
                Constants.MS2_FILE,
                Constants.PKL_FILE,
                Constants.DTA_FILE,
                Constants.GZIPPED_FILE);
        simpleFileDialog.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        // check the selection results from open file dialog

        int result = simpleFileDialog.showDialog(this, null);
        List<File> files = new ArrayList<File>();
        if (result == JFileChooser.APPROVE_OPTION) {
            for (File file : simpleFileDialog.getSelectedFiles()) {
                files = recursiveFileFolder(file, files);
            }
            Set<File> nonRedundantFiles = new HashSet<File>(files);
            files = new ArrayList<File>(nonRedundantFiles);
            int selectedRow = sourceTable.getSelectedRow();
            List<File> currentFiles = mzidentmlFiles.get(fileList.get(selectedRow));
            currentFiles = (currentFiles != null) ? currentFiles : new ArrayList<File>();
            currentFiles.addAll(files);
            mzidentmlFiles.put(fileList.get(sourceTable.getSelectedRow()), currentFiles);
            addTableMSFiles(currentFiles);
            if (mzidentmlFiles.size() > 0) setButton.setEnabled(true);
            //System.out.println(mzidentmlFiles.size());
        }


    }

    private void removeTableMSFiles() {
        DefaultTableModel dm = (DefaultTableModel) msFileTable.getModel();
        dm.getDataVector().removeAllElements();
        msFileTable.repaint();
    }

    private void addTableMSFiles(List<File> files) {
        if (files != null) {
            for (File msFile : files) {
                Object[] data = new Object[4];
                data[0] = msFile.getName();
                DecimalFormat decimalFormat = new DecimalFormat("#.####");
                data[1] = decimalFormat.format(msFile.length() / (Math.pow(1024, 2)));
                data[2] = getFileType(msFile.getName());
                data[3] = GUIUtilities.loadImageIcon(context.getProperty("delete.mzidentml.ms.icon.small"));
                ((DefaultTableModel) msFileTable.getModel()).addRow(data);
            }
        }
    }

    private List<File> recursiveFileFolder(File file, List<File> files) {
        if (!file.isDirectory()) {
            files.add(file);
        } else {
            if(file != null && file.listFiles() != null && file.listFiles().length >0){
                for (File fileOnDir : file.listFiles()) {
                    String name = fileOnDir.getName();
                    if (getFileType(name) != null || fileOnDir.isDirectory()) {
                        files = (recursiveFileFolder(fileOnDir, files));
                    }
                }
            }

        }
        return files;
    }

    private String getFileType(String name) {
        if (name.toLowerCase().endsWith(Constants.MGF_FILE)) {
            return MGF_FILE;
        } else if (name.toLowerCase().endsWith(Constants.MZML_FILE)) {
            return MZML_FILE;
        } else if (name.toLowerCase().endsWith(Constants.PKL_FILE)) {
            return PKL_FILE;
        }
        return null;
    }

    public Map<File, List<File>> getMzIdentMlMap() {
        return mzidentmlFiles;
    }

    private String getFileType(File file) throws IOException {
        String classType = null;
        // check file type
        if (MzMLControllerImpl.isValidFormat(file)) {
            classType = MZML_FILE;
        } else if (MzXmlControllerImpl.isValidFormat(file)) {
            classType = MZXML_FILE;
        } else if (MzDataControllerImpl.isValidFormat(file)) {
            classType = MZDATA_FILE;
        } else if (PeakControllerImpl.isValidFormat(file) != null) {
            classType = PKL_FILE;
        }

        return classType;
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JSplitPane splitPane1;
    private JPanel menuPanel;
    private JPanel msFilesPanel;
    private JScrollPane scrollPane1;
    private JTable msFileTable;
    private JPanel globalAddPanel;
    private JPanel panel2;
    private JSeparator separator1;
    private JPanel headerPanel;
    private JButton setButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
