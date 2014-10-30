package uk.ac.ebi.pride.toolsuite.gui.component.dialog;

import uk.ac.ebi.pride.archive.web.service.model.file.FileDetail;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.AssayFileDownloadTableModel;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.AsperaDownloadTask;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.GetAssayFileMetadataTask;
import uk.ac.ebi.pride.utilities.util.Tuple;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog for download files belongs to assay
 *
 * @author Rui Wang
 * @version $Id$
 */
public class AssayFileDownloadDialog extends JDialog implements ActionListener {
    private static final String CANCEL_ACTION_COMMAND = "cancelAction";
    private static final String DOWNLOAD_ACTION_COMMAND = "downloadAction";

    /**
     * File mapping table
     */
    private JTable fileDownloadSelectionTable;

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
//    private FileMappingTableModel fileMappingTableModel;
    public AssayFileDownloadDialog(Frame owner, String assayAccession) {
        super(owner);
        this.assayAccession = assayAccession;
        this.prideInspectorContext = (PrideInspectorContext) PrideInspector.getInstance().getDesktopContext();
        initComponents();
        postComponents();
    }

    public AssayFileDownloadDialog(Dialog owner, String assayAccession) {
        super(owner);
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

        GetAssayFileMetadataTask task = new GetAssayFileMetadataTask(assayAccession);
        task.addTaskListener(model);
        TaskUtil.startBackgroundTask(task);
    }

    /**
     * Initialize table panel
     */
    private void initTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // create table title label
        JLabel label = new JLabel("File Download (assay: " + assayAccession + ")");
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

        // control pane
        JPanel ctrlPane = new JPanel(new FlowLayout(FlowLayout.RIGHT));

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

                int result = ofd.showOpenDialog(AssayFileDownloadDialog.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = ofd.getSelectedFile();
                    String folderPath = selectedFile.getPath();
                    prideInspectorContext.setOpenFilePath(folderPath.replace(selectedFile.getName(), ""));
                    downloadFiles(folderPath, filesToDownload);
                    this.dispose();
                }
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

        int confirmed = JOptionPane.showConfirmDialog(this, "Would you like Inspector try to open the files after downloading?", "Download assay files", JOptionPane.YES_NO_OPTION);
        if (confirmed != 0) {
            return;
        }

        // create a dialog to show progress
        TaskDialog dialog = new TaskDialog(PrideInspector.getInstance().getMainComponent(), "Download assay files from PRIDE", "Downloading in progress...please wait");
        dialog.setVisible(true);

        AsperaDownloadTask downloadTask = new AsperaDownloadTask(filesToDownload, path, true);
        downloadTask.addTaskListener(dialog);
        TaskUtil.startBackgroundTask(downloadTask);
    }
}
