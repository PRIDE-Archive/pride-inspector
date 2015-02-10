package uk.ac.ebi.pride.toolsuite.gui.component.dialog;

import uk.ac.ebi.pride.archive.web.service.model.file.FileDetail;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.AssayFileDownloadTableModel;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.*;
import uk.ac.ebi.pride.toolsuite.gui.utils.DataTransferConfiguration;
import uk.ac.ebi.pride.toolsuite.gui.utils.DataTransferPort;
import uk.ac.ebi.pride.toolsuite.gui.utils.DataTransferProtocol;
import uk.ac.ebi.pride.utilities.util.Tuple;

import javax.jnlp.ServiceManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Dialog for download files belongs to assay
 *
 * @author Rui Wang
 * @version $Id$
 */
public class ProjectFileDownloadDialog extends JDialog implements ActionListener, TaskListener<List<DataTransferProtocol>, Void> {
    private static final String SELECTION_ACTION_COMMAND = "selectionAction";
    private static final String CANCEL_ACTION_COMMAND = "cancelAction";
    private static final String DOWNLOAD_ACTION_COMMAND = "downloadAction";

    /**
     * File mapping table
     */
    private JTable fileDownloadSelectionTable;

    /**
     * Open file after download
     */
    private JCheckBox openFileOptionCheckbox;

    /**
     * Button for select all / deselect all
     */
    private JButton selectionButton;

    /**
     * Accession of the project to download
     */
    private final String projectAccession;

    /**
     * Accession for the assay to download
     */
    private final String assayAccession;

    /**
     * Application context
     */
    private final PrideInspectorContext prideInspectorContext;

    /**
     * File mapping table model
     */
    public ProjectFileDownloadDialog(Frame owner, String projectAccession, String assayAccession) {
        super(owner);
        this.projectAccession = projectAccession;
        this.assayAccession = assayAccession;
        this.prideInspectorContext = (PrideInspectorContext) PrideInspector.getInstance().getDesktopContext();
        initComponents();
        postComponents();
    }

    public ProjectFileDownloadDialog(Dialog owner, String projectAccession, String assayAccession) {
        super(owner);
        this.projectAccession = projectAccession;
        this.assayAccession = assayAccession;
        this.prideInspectorContext = (PrideInspectorContext) PrideInspector.getInstance().getDesktopContext();
        initComponents();
        postComponents();
    }

    /**
     * Create GUI components
     */
    private void initComponents() {
        this.setSize(new Dimension(600, 300));

        JPanel contentPanel = new JPanel(new BorderLayout());
        this.setContentPane(contentPanel);

        // create table panel
        initTablePanel();

        // create button panel
        initControlPanel();

        this.setContentPane(contentPanel);
    }

    /**
     * Post component creation, populate the table with content
     */
    private void postComponents() {
        AssayFileDownloadTableModel model = (AssayFileDownloadTableModel) fileDownloadSelectionTable.getModel();

        if (projectAccession != null) {
            GetProjectFileMetadataTask task = new GetProjectFileMetadataTask(projectAccession);
            task.addTaskListener(model);
            TaskUtil.startBackgroundTask(task);
        } else if (assayAccession != null) {
            GetAssayFileMetadataTask task = new GetAssayFileMetadataTask(assayAccession);
            task.addTaskListener(model);
            TaskUtil.startBackgroundTask(task);
        }
    }

    /**
     * Initialize table panel
     */
    private void initTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // create table title label
        String title = "Files ";
        if (projectAccession != null) {
            title += "[Project: " + projectAccession + "]";
        } else if (assayAccession != null) {
            title += "[Assay: " + assayAccession + "]";
        }

        JLabel label = new JLabel(title);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        tablePanel.add(label, BorderLayout.NORTH);

        // create table
        fileDownloadSelectionTable = TableFactory.createAssayFileDownloadTable();

        // scroll pane
        JScrollPane scrollPane = new JScrollPane(fileDownloadSelectionTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        tablePanel.add(scrollPane, BorderLayout.CENTER);

        this.getContentPane().add(tablePanel, BorderLayout.CENTER);
    }

    /**
     * Initialize control panel
     */
    private void initControlPanel() {
        // setup main pane
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());

        // open file after download checkbox
        JPanel openFileOptionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        openFileOptionCheckbox = new JCheckBox("Open files after download");
        openFileOptionCheckbox.setSelected(true);
        openFileOptionPanel.add(openFileOptionCheckbox);
        controlPanel.add(openFileOptionPanel, BorderLayout.WEST);

        // control pane
        JPanel ctrlPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        // selection button
        selectionButton = new JButton("Deselect all");
        selectionButton.setPreferredSize(new Dimension(120, 30));
        selectionButton.setActionCommand(SELECTION_ACTION_COMMAND);
        selectionButton.addActionListener(this);
        ctrlPane.add(selectionButton);

        // cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(90, 30));
        cancelButton.setActionCommand(CANCEL_ACTION_COMMAND);
        cancelButton.addActionListener(this);
        ctrlPane.add(cancelButton);

        // next button
        JButton addButton = new JButton("Download");
        addButton.setPreferredSize(new Dimension(90, 30));
        addButton.setActionCommand(DOWNLOAD_ACTION_COMMAND);
        addButton.addActionListener(this);
        ctrlPane.add(addButton);

        controlPanel.add(ctrlPane, BorderLayout.EAST);

        this.getContentPane().add(controlPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String evtName = e.getActionCommand();

        if (CANCEL_ACTION_COMMAND.equals(evtName)) {
            this.dispose();
        } else if (DOWNLOAD_ACTION_COMMAND.equals(evtName)) {
            List<FileDetail> filesToDownload = getFilesToDownload();

            if (filesToDownload.size() > 0) {
                SimpleFileDialog ofd = new SimpleFileDialog(prideInspectorContext.getOpenFilePath(), "Select Path Save To", true, null, false);
                ofd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                ofd.setMultiSelectionEnabled(false);

                int result = ofd.showOpenDialog(ProjectFileDownloadDialog.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = ofd.getSelectedFile();
                    String folderPath = selectedFile.getPath();
                    prideInspectorContext.setOpenFilePath(folderPath);
                    downloadFiles(folderPath, filesToDownload);
                    this.dispose();
                }
            }
        } else if (SELECTION_ACTION_COMMAND.equals(evtName)) {
            AssayFileDownloadTableModel assayFileDownloadTableModel = (AssayFileDownloadTableModel) fileDownloadSelectionTable.getModel();
            int columnIndex = assayFileDownloadTableModel.getColumnIndex(AssayFileDownloadTableModel.TableHeader.SELECTION.getHeader());
            boolean toDeselect = selectionButton.getText().equals("Deselect all");

            for (int i = 0; i < assayFileDownloadTableModel.getRowCount(); i++) {
                assayFileDownloadTableModel.setValueAt(!toDeselect, i, columnIndex);
            }
            assayFileDownloadTableModel.fireTableDataChanged();

            if (toDeselect) {
                selectionButton.setText("Select all");
            } else {
                selectionButton.setText("Deselect all");
            }
        }
    }

    private java.util.List<FileDetail> getFilesToDownload() {
        AssayFileDownloadTableModel assayFileDownloadTableModel = (AssayFileDownloadTableModel) fileDownloadSelectionTable.getModel();
        java.util.List<Tuple<FileDetail, Boolean>> data = assayFileDownloadTableModel.getData();

        java.util.List<FileDetail> fileDetails = new ArrayList<FileDetail>();

        for (Tuple<FileDetail, Boolean> fileDetailBooleanTuple : data) {
            if (fileDetailBooleanTuple.getValue()) {
                fileDetails.add(fileDetailBooleanTuple.getKey());
            }
        }

        return fileDetails;
    }

    private void downloadFiles(String folderPath, java.util.List<FileDetail> filesToDownload) {
        File path = new File(folderPath);

        DataTransferProtocol dataTransferProtocol = prideInspectorContext.getDataTransferProtocol();

        // open file after download
        boolean selected = openFileOptionCheckbox.isSelected();
        TaskDialog dialog = new TaskDialog(PrideInspector.getInstance().getMainComponent(), "Download assay files from PRIDE", "Downloading in progress...please wait");

        switch (dataTransferProtocol) {
            case ASPERA:
                // create a dialog to show progress
                dialog.setVisible(true);

                AsperaDownloadTask asperaDownloadTask = new AsperaDownloadTask(filesToDownload, path, selected);
                asperaDownloadTask.addTaskListener(dialog);
                TaskUtil.startBackgroundTask(asperaDownloadTask);
                break;
            case FTP:
                // create a dialog to show progress
                dialog.setVisible(true);

                FTPDownloadTask ftpDownloadTask = new FTPDownloadTask(filesToDownload, path, selected);
                ftpDownloadTask.addTaskListener(dialog);
                TaskUtil.startBackgroundTask(ftpDownloadTask);
                break;
            case NONE:
                selectDataTransferProtocol();
                break;

        }
    }

    /**
     * Select the best data transfer protocol for download database
     * <p/>
     * Should be called only once at the beginning of establish the panel
     */
    private void selectDataTransferProtocol() {
        // ftp
        String ftpHost = prideInspectorContext.getProperty("ftp.EBI.host");
        int ftpPort = Integer.parseInt(prideInspectorContext.getProperty("ftp.EBI.port"));
        DataTransferConfiguration ftpProtocolConfiguration = new DataTransferConfiguration(DataTransferProtocol.FTP, ftpHost,
                new DataTransferPort(DataTransferPort.Type.TCP, ftpPort));

        // aspera
        String asperaHost = prideInspectorContext.getProperty("aspera.EBI.host");
        int asperaTcpPort = Integer.parseInt(prideInspectorContext.getProperty("aspera.xfer.tcpPort"));
        int asperaUdpPort = Integer.parseInt(prideInspectorContext.getProperty("aspera.xfer.udpPort"));

        DataTransferConfiguration asperaProtocolConfiguration = new DataTransferConfiguration(DataTransferProtocol.ASPERA, asperaHost,
                new DataTransferPort(DataTransferPort.Type.TCP, asperaTcpPort), new DataTransferPort(DataTransferPort.Type.UDP, asperaUdpPort));

        DataTransferProtocolTask task = new DataTransferProtocolTask(ftpProtocolConfiguration, asperaProtocolConfiguration);
        task.addTaskListener(this);
        TaskUtil.startBackgroundTask(task);
    }

    @Override
    public void started(TaskEvent<Void> event) {

    }

    @Override
    public void process(TaskEvent<List<Void>> event) {

    }

    @Override
    public void finished(TaskEvent<Void> event) {

    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
        prideInspectorContext.setDataTransferProtocol(DataTransferProtocol.NONE);
    }

    @Override
    public void succeed(TaskEvent<List<DataTransferProtocol>> event) {
        List<DataTransferProtocol> value = event.getValue();

        if (value.size() == 0) {
            // show warning
            JOptionPane.showMessageDialog(PrideInspector.getInstance().getMainComponent(),
                    "<html>FTP or ASPERA are required for file download. <br/> Please ensure " +
                    "that either port 21 is opened for FTP or ports 22, 33001 are opened for ASPERA</html>",
                    "File Download", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if(isRunningJavaWebStart()) {
            // when running in web start using FTP by default
            prideInspectorContext.setDataTransferProtocol(DataTransferProtocol.FTP);
        } else {
            Collections.sort(value, new DataTransferProtocol.PriorityComparator());
            prideInspectorContext.setDataTransferProtocol(value.get(0));
        }

        String filePath = prideInspectorContext.getOpenFilePath();
        List<FileDetail> filesToDownload = getFilesToDownload();
        downloadFiles(filePath, filesToDownload);
    }

    private boolean isRunningJavaWebStart() {
        try {
            String[] serviceNames = ServiceManager.getServiceNames();
            return serviceNames != null;
        } catch (NoClassDefFoundError e) {
            return false;
        }
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
        prideInspectorContext.setDataTransferProtocol(DataTransferProtocol.NONE);
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
        prideInspectorContext.setDataTransferProtocol(DataTransferProtocol.NONE);
    }

    @Override
    public void progress(TaskEvent<Integer> progress) {
    }
}
