/*
 * Created by JFormDesigner on Wed Aug 15 13:55:01 BST 2012
 */

package uk.ac.ebi.pride.toolsuite.gui.component.mzidentml;

import org.bushe.swing.event.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.utilities.data.controller.DataAccessController;
import uk.ac.ebi.pride.utilities.data.controller.impl.ControllerImpl.ReferencedIdentificationController;
import uk.ac.ebi.pride.utilities.data.core.SpectraData;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.dialog.SimpleFileDialog;
import uk.ac.ebi.pride.toolsuite.gui.component.report.RemovalReportMessage;
import uk.ac.ebi.pride.toolsuite.gui.component.report.RoundCornerLabel;
import uk.ac.ebi.pride.toolsuite.gui.component.report.SummaryReportMessage;
import uk.ac.ebi.pride.toolsuite.gui.event.SummaryReportEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.AddMsDataAccessControllersTask;
import uk.ac.ebi.pride.toolsuite.gui.utils.Constants;


import javax.help.CSH;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Simple Dialog to add the Spectrum.
 * @author ypriverol
 */
public class SimpleMsDialog extends JDialog {

    private static final Logger logger = LoggerFactory.getLogger(SimpleMsDialog.class);

    private static final String OPEN_FILE = "Load related spectrum files";

    private PrideInspectorContext context = null;

    DataAccessController controller = null;

    Map<SpectraData, File> msFileMap = null;

    private static final int DEFAULT_HEIGHT = 25;
    private static final int START_ALPHA = 100;
    private static final int STOP_ALPHA = 150;
    private static final String ERROR_MESSAGE = "No Supported Spectra Data Files for this mzIdentml";
    private static final String WARNING = "spectra missing";
    private static final String TOTAL_SPECTRUMS = "All Spectrum Found";
    private static final String COLUMN_HEADER_REMOVE = "Add/Remove";
    private static final String COLUMN_HEADER_SOURCE = "Spectra File Source";
    private static final String COLUMN_HEADER_NUMBER = "No. Spectra";
    private static final String COLUMN_HEADER_MSFILE = "MS File";
    private static final String COLUMN_HEADER_MSTYPE = "MS Type";
    private static final String COLUMN_HEADER_ID = "File ID";
    private static final String NO_SUPPORTED = "No Supported";

    private String message;
    private SummaryReportMessage.Type type;


    public SimpleMsDialog(Frame owner, DataAccessController controller) {
        super(owner);
        context = (PrideInspectorContext) uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext();
        this.controller = controller;
        initComponents();
    }

    /**
     * Init Form Components
     */
    private void initComponents() {

        scrollPane1 = new JScrollPane();
        addButton = new JButton();
        separator1 = new JSeparator();
        setButton = new JButton();
        cancelButton = new JButton();
        panelMessage = new JPanel();
        headerPanel = new JPanel();

        //======== this ========
        setTitle("Open Ms files for");
        Container contentPane = getContentPane();

        //===== Create Custom Table ====

        setTitle(OPEN_FILE);

        String[] columns = new String[]{
                COLUMN_HEADER_ID, COLUMN_HEADER_SOURCE, COLUMN_HEADER_NUMBER, COLUMN_HEADER_MSFILE, COLUMN_HEADER_MSTYPE, COLUMN_HEADER_REMOVE
        };

        msFileMap = ((ReferencedIdentificationController) controller).getSpectraDataMSFiles();

        Object[][] data = new Object[msFileMap.size()][columns.length];

        int i = 0;
        JComboBox[] comboBoxes = new JComboBox[msFileMap.size()];

        for (SpectraData spectraData : msFileMap.keySet()) {
            String msFileName = (msFileMap.get(spectraData) == null) ? "" : msFileMap.get(spectraData).getAbsolutePath();
            data[i][0] = spectraData.getId();
            data[i][1] = (spectraData.getName() != null) ? spectraData.getName() : "";
            data[i][2] = ((ReferencedIdentificationController) controller).getNumberOfSpectrabySpectraData(spectraData);
            data[i][3] = msFileName;
            List<uk.ac.ebi.pride.utilities.data.utils.Constants.SpecFileFormat> fileformats = uk.ac.ebi.pride.utilities.data.utils.Constants.getFileTypeSupported(spectraData);
            List<String> fileStrFormats = new ArrayList<>(fileformats.size());
            for (uk.ac.ebi.pride.utilities.data.utils.Constants.SpecFileFormat specFileFormat : fileformats)
                fileStrFormats.add(specFileFormat.toString());
            data[i][4] = (fileStrFormats.size() == 0) ? NO_SUPPORTED : fileformats.get(0);
            if (fileStrFormats.size() == 0) fileStrFormats.add(NO_SUPPORTED);
            JComboBox comboBox = new JComboBox(fileStrFormats.toArray());
            comboBoxes[i] = comboBox;
            i++;
        }

        DefaultTableModel dm = new DefaultTableModel(data, columns) {
            boolean[] columnEditable = new boolean[]{
                    false, false, false, false, true, false
            };

            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        };
        for (i = 0; i < comboBoxes.length; i++) {
            editors.add(new DefaultCellEditor(comboBoxes[i]));
        }

        msFileTable = new JTable(dm) {
            //  Determine editor to be used by row
            public TableCellEditor getCellEditor(int row, int column) {
                int modelColumn = convertColumnIndexToModel(column);

                if (modelColumn == 4 && row < msFileMap.size())
                    return editors.get(row);
                else
                    return super.getCellEditor(row, column);
            }
        };

        //msFileTable.setModel(dm);
        //rowEditor = new RowEditor(msFileTable);

        //msFileTable.getColumn(COLUMN_HEADER_MSFILE).setCellEditor(rowEditor);

        TableColumnModel cm = msFileTable.getColumnModel();
        cm.getColumn(0).setPreferredWidth(60);
        cm.getColumn(1).setPreferredWidth(80);
        cm.getColumn(2).setPreferredWidth(50);
        cm.getColumn(3).setPreferredWidth(175);
        cm.getColumn(4).setPreferredWidth(60);
        cm.getColumn(5).setPreferredWidth(35);

        // create combo box to select file type
        TableColumn fileTypeColumn = msFileTable.getColumn(COLUMN_HEADER_MSTYPE);
        // fileTypeColumn.setCellRenderer(new ComboBoxCellRenderer(uk.ac.ebi.pride.utilities.data.utils.Constants.SpecFileFormat.values()));
        // fileTypeColumn.setCellEditor(new ComboBoxCellEditor(fileTypeGroups));
        fileTypeColumn.setMinWidth(90);
        fileTypeColumn.setMaxWidth(90);

        // removal column
        String removalColHeader = COLUMN_HEADER_REMOVE;
        final TableColumn removalColumn = msFileTable.getColumn(removalColHeader);
        removalColumn.setCellRenderer(new AddButoonCellRenderer());

        msFileTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                msFileTableMouseReleased(e);
            }
        });

        Icon helpIcon = GUIUtilities.loadIcon(context.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, "Help");
        helpButton.setForeground(Color.blue);
        CSH.setHelpIDString(helpButton, "help.ms.information");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(context.getMainHelpBroker()));
        helpButton.setBorder(BorderFactory.createEtchedBorder());

        headerPanel.add(helpButton, BorderLayout.EAST);

        msFileTable.setRowHeight(DEFAULT_HEIGHT);

        scrollPane1.setViewportView(msFileTable);
        scrollPane1.getViewport().setBackground(Color.white);


        //---- addButton ----
        addButton.setText("Add spectra files");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMultipleMsFiles(e);
            }
        });

        //---- setButton ----
        setButton.setText("Set");
        setButton.setEnabled(false);
        setButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setMSFilesActionPerformed(e);
            }
        });

        //---- cancelButton ----
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelbuttonActionPerformed(e);
            }
        });

        //======== panelMessage ========
        {
            panelMessage.setLayout(new BorderLayout());
        }

        GroupLayout contentPaneLayout = new GroupLayout(contentPane);
        contentPane.setLayout(contentPaneLayout);
        contentPaneLayout.setHorizontalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE)
                                        .addGroup(contentPaneLayout.createSequentialGroup()
                                                .addComponent(addButton)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(panelMessage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                                .addComponent(helpButton)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addGap(0, 644, Short.MAX_VALUE)
                                                .addComponent(cancelButton)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(setButton, GroupLayout.PREFERRED_SIZE, 67, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(separator1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE))
                                .addContainerGap())
        );
        contentPaneLayout.setVerticalGroup(
                contentPaneLayout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 230, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup()
                                        .addComponent(addButton)
                                        .addComponent(panelMessage, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(separator1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(helpButton)
                                        .addComponent(setButton)
                                        .addComponent(cancelButton))
                                .addGap(13, 13, 13))
        );
        pack();
        setLocationRelativeTo(getOwner());
        updateFormStatus(); // Update Status of all the Components
    }

    private void updateFormStatus() {
        int totalSpectra = 0;
        int missSpectrum = 0;
        int noSupported = 0;
        for (int i = 0; i < msFileTable.getRowCount(); i++) {
            totalSpectra += (Integer) msFileTable.getValueAt(i, 2);
            missSpectrum += (((String) msFileTable.getValueAt(i, 3)).length() == 0) ? (Integer) msFileTable.getValueAt(i, 2) : 0;
            noSupported += (!((ReferencedIdentificationController) controller).getSupportedSpectraData().contains(msFileTable.getValueAt(i, 0))) ? (Integer) msFileTable.getValueAt(i, 2) : 0;
        }
        message = getMessage(missSpectrum, noSupported, totalSpectra);
        type = getMessageType(missSpectrum, noSupported, totalSpectra);
        updateMessage(type, message);
    }

    /**
     * This method initialize the custom components in the MsDialog
     */

    private void msFileTableMouseReleased(MouseEvent e) {
        int row = msFileTable.rowAtPoint(e.getPoint());
        int col = msFileTable.columnAtPoint(e.getPoint());
        String colName = msFileTable.getColumnName(col);
        DefaultTableModel model = (DefaultTableModel) msFileTable.getModel();
        String fileName = (String) model.getValueAt(row, 3);
        Comparable idSpectra = (Comparable) model.getValueAt(row, 0);
        boolean supported = ((ReferencedIdentificationController) controller).getSupportedSpectraData().contains(idSpectra);
        if (colName.equals(COLUMN_HEADER_REMOVE) && row >= 0 && fileName.length() > 0) {
            msFileTable.getModel().setValueAt("", row, 3);
            setButton.setEnabled(true);
            updateFormStatus();
        } else if (supported && (colName.equals(COLUMN_HEADER_REMOVE) && row >= 0 && ((fileName == null) || (fileName.length() == 0)))) {
            SimpleFileDialog ofd = new SimpleFileDialog(context.getOpenFilePath(), "Select mzML/mzXML/mzData/Peak Files ",
                    false, null, true,
                    Constants.MGF_FILE,
                    Constants.MZXML_FILE,
                    Constants.MZML_FILE,
                    Constants.DTA_FILE,
                    Constants.XML_FILE,
                    Constants.MS2_FILE,
                    Constants.GZIPPED_FILE);
            int result = ofd.showDialog(this, null);

            java.util.List<File> filesToOpen = new ArrayList<>();

            // check the selection results from open fiel dialog
            if (result == JFileChooser.APPROVE_OPTION) {
                filesToOpen.addAll(Arrays.asList(ofd.getSelectedFiles()));
                File selectedFile = ofd.getSelectedFile();
                String filePath = selectedFile.getPath();
                // remember the path has visited
                context.setOpenFilePath(filePath.replace(selectedFile.getName(), ""));
                if (filePath.length() > 0) {
                    msFileTable.getModel().setValueAt(filePath, row, 3);
                    setButton.setEnabled(true);
                    updateFormStatus();
                }
            }
        }
        repaint();
    }

    private void addMultipleMsFiles(ActionEvent e) {

        SimpleFileDialog ofd = new SimpleFileDialog(context.getOpenFilePath(), "Select mzML/mzXML/mzData/Peak Files ",
                true, null, true,
                Constants.MGF_FILE,
                Constants.MZXML_FILE,
                Constants.MZML_FILE,
                Constants.DTA_FILE,
                Constants.XML_FILE,
                Constants.MS2_FILE,
                Constants.GZIPPED_FILE);

        int result = ofd.showDialog(this, null);

        java.util.List<File> filesToOpen = new ArrayList<>();

        // check the selection results from open fiel dialog
        if (result == JFileChooser.APPROVE_OPTION) {
            filesToOpen.addAll(Arrays.asList(ofd.getSelectedFiles()));
            File selectedFile = ofd.getSelectedFile();
            String filePath = selectedFile.getPath();
            // remember the path has visited
            context.setOpenFilePath(filePath.replace(selectedFile.getName(), ""));
        }
        addMultipleMStoSpectraData(filesToOpen);

    }

    private void addMultipleMStoSpectraData(List<File> files) {
        Map<SpectraData, File> tempMapFile = ((ReferencedIdentificationController) controller).checkMScontrollers(files);
        for (SpectraData spectraData : tempMapFile.keySet()) {
            for (int i = 0; i < msFileTable.getRowCount(); i++) {
                Comparable idSpectra = (Comparable) msFileTable.getValueAt(i, 0);
                if (spectraData.getId().equals(idSpectra) && tempMapFile.get(spectraData).getAbsolutePath().length() > 0) {
                    msFileTable.setValueAt(tempMapFile.get(spectraData).getAbsolutePath(), i, 3);
                    setButton.setEnabled(true);
                }
            }
        }
        updateFormStatus();
        repaint();
    }

    private void updateMessage(SummaryReportMessage.Type type, String message) {
        if (panelMessage.getComponentCount() > 0) panelMessage.removeAll();
        RoundCornerLabel label = new RoundCornerLabel(getIcon(type), message, getBackgroundPaint(type), getBorderPaint(type));
        label.setPreferredSize(new Dimension(50, DEFAULT_HEIGHT));
        panelMessage.add(label);
        panelMessage.revalidate();
    }


    private void cancelbuttonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void setMSFilesActionPerformed(ActionEvent e) {
        Map<Comparable, File> files = new HashMap<>();
        Map<Comparable, String> fileTypes = new HashMap<>();
        for (int i = 0; i < msFileTable.getRowCount(); i++) {
            Comparable idSpectra = (Comparable) msFileTable.getValueAt(i, 0);
            String filePath = (String) msFileTable.getValueAt(i, 3);
            String fileType = (String) ((JComboBox) editors.get(i).getTableCellEditorComponent(msFileTable, msFileTable.getValueAt(i, 4), false, i, 4)).getSelectedItem();
            if (filePath.length() > 0) {
                files.put(idSpectra, new File(filePath));
                fileTypes.put(idSpectra, fileType);
            } else {
                files.put(idSpectra, null);
            }
        }
        AddMsDataAccessControllersTask task = new AddMsDataAccessControllersTask(controller, files, fileTypes, msFileMap);
        TaskUtil.startBackgroundTask(task);
        //context.replaceDataAccessController(controller,controller,true);
        EventBus.publish(new SummaryReportEvent(this, controller, new RemovalReportMessage(Pattern.compile("Spectra not found.*"))));
        EventBus.publish(new SummaryReportEvent(this, controller, new RemovalReportMessage(Pattern.compile("Missing spectra.*"))));
        EventBus.publish(new SummaryReportEvent(this, controller, new RemovalReportMessage(Pattern.compile("Spectra found.*"))));
        EventBus.publish(new SummaryReportEvent(this, controller, new RemovalReportMessage(Pattern.compile("All Spectrum Found.*"))));
        EventBus.publish(new SummaryReportEvent(this, controller, new RemovalReportMessage(Pattern.compile(".*spectra missing.*"))));

        EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(type, message, message)));
        EventBus.publish(new SummaryReportEvent(this, controller, new SummaryReportMessage(SummaryReportMessage.Type.SUCCESS, "Spectra found", "This data source contains spectra")));
        dispose();

    }

    /**
     * Get the icon of the message according to the type
     *
     * @param type message type
     * @return Icon    message icon
     */
    private Icon getIcon(SummaryReportMessage.Type type) {
        switch (type) {
            case SUCCESS:
                return GUIUtilities.loadIcon(context.getProperty("report.item.success.icon.small"));
            case ERROR:
                return GUIUtilities.loadIcon(context.getProperty("report.item.error.icon.small"));
            case WARNING:
                return GUIUtilities.loadIcon(context.getProperty("report.item.warning.icon.small"));
            case INFO:
                return GUIUtilities.loadIcon(context.getProperty("report.item.plain.icon.small"));
            default:
                return GUIUtilities.loadIcon(context.getProperty("report.item.plain.icon.small"));
        }
    }

    /**
     * Get the paint for the message background
     *
     * @param type message type
     * @return Paint   background
     */
    private Paint getBackgroundPaint(SummaryReportMessage.Type type) {
        switch (type) {
            case SUCCESS:
                return new GradientPaint(0, 0, new Color(40, 175, 99, START_ALPHA), 0, DEFAULT_HEIGHT, new Color(40, 175, 99, STOP_ALPHA), true);
            case ERROR:
                return new GradientPaint(0, 0, new Color(215, 39, 41, START_ALPHA), 0, DEFAULT_HEIGHT, new Color(215, 39, 41, STOP_ALPHA), true);
            case WARNING:
                return new GradientPaint(0, 0, new Color(251, 182, 1, START_ALPHA), 0, DEFAULT_HEIGHT, new Color(251, 182, 1, STOP_ALPHA), true);
            case INFO:
                return new GradientPaint(0, 0, new Color(27, 106, 165, START_ALPHA), 0, DEFAULT_HEIGHT, new Color(27, 106, 165, STOP_ALPHA), true);
            default:
                return new GradientPaint(0, 0, new Color(27, 106, 165, START_ALPHA), 0, DEFAULT_HEIGHT, new Color(27, 106, 165, STOP_ALPHA), true);
        }
    }

    /**
     * Get the paint for the message border
     *
     * @param type message type
     * @return Paint   border color
     */
    private Paint getBorderPaint(SummaryReportMessage.Type type) {
        switch (type) {
            case SUCCESS:
                return new Color(40, 175, 99);
            case ERROR:
                return new Color(215, 39, 41);
            case WARNING:
                return new Color(251, 182, 1);
            case INFO:
                return new Color(27, 106, 165);
            default:
                return new Color(27, 106, 165);
        }
    }

    private SummaryReportMessage.Type getMessageType(Integer missSpectrum, Integer noSupported, Integer totalSpectra) {
        if (noSupported.intValue() == totalSpectra.intValue()) {
            return SummaryReportMessage.Type.ERROR;
        } else if (missSpectrum > 0) {
            return SummaryReportMessage.Type.WARNING;
        } else if (missSpectrum == 0) {
            return SummaryReportMessage.Type.SUCCESS;
        }
        return SummaryReportMessage.Type.INFO;
    }

    private String getMessage(Integer missSpectrum, Integer noSupported, Integer totalSpectra) {
        if (noSupported.intValue() == totalSpectra.intValue()) {
            return SimpleMsDialog.ERROR_MESSAGE;
        } else if (missSpectrum == 0) {
            return SimpleMsDialog.TOTAL_SPECTRUMS;
        } else if (missSpectrum > 0) {
            return "[" + missSpectrum + "] " + SimpleMsDialog.WARNING;
        }
        return SimpleMsDialog.ERROR_MESSAGE;

    }

    /**
     * Draw a red cross for close the data access controller
     */
    private class AddButoonCellRenderer extends DefaultTableCellRenderer {


        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            // get the original component
            JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            Comparable idSpectra = (Comparable) msFileTable.getValueAt(row, 0);

            boolean supported = ((ReferencedIdentificationController) controller).getSupportedSpectraData().contains(idSpectra.toString());

            String fileName = (String) msFileTable.getValueAt(row, 3);

            Icon icon;
            if (supported) {
                if ((fileName == null || fileName.length() == 0)) {
                    icon = GUIUtilities.loadImageIcon(context.getProperty("open.file.icon.small"));
                } else {
                    icon = GUIUtilities.loadImageIcon(context.getProperty("delete.mzidentml.ms.icon.small"));
                }
            } else {
                icon = GUIUtilities.loadImageIcon(context.getProperty("delete.mzidentml.ms.icon.small.disable"));
            }

            cell.setIcon(icon);
            // overwrite the background changing behavior when selected
            cell.setBackground(Color.white);
            // set the component to none focusable
            cell.setFocusable(false);

            cell.setHorizontalAlignment(CENTER);

            //paintComponent((ImageIcon) icon, cell.getGraphics());

            return cell;
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JScrollPane scrollPane1;
    private JTable msFileTable;
    private JButton addButton;
    private JSeparator separator1;
    private JButton setButton;
    private JButton cancelButton;
    private JPanel panelMessage;
    List<TableCellEditor> editors = new ArrayList<>();
    private JPanel headerPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
