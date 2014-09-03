/*
 * Created by JFormDesigner on Tue Apr 23 10:34:27 BST 2013
 */

package uk.ac.ebi.pride.toolsuite.gui.component.reviewer;

import org.jdesktop.swingx.JXTreeTable;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.component.dialog.SimpleFileDialog;
import uk.ac.ebi.pride.toolsuite.gui.component.dialog.TaskDialog;
import uk.ac.ebi.pride.toolsuite.gui.component.table.model.SubmissionFileDownloadTableModel;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.GetPrideFileTask;
import uk.ac.ebi.pride.toolsuite.gui.utils.BorderUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * @author Rui Wang
 */
public class MyProjectDownloadDialog extends JDialog implements TreeModelListener {
    private String userName;
    private char[] password;
    private java.util.List<SubmissionFileDetail> submissionFileDetails;
    private SubmissionFileDownloadTableModel fileSelectionTableModel;

    public MyProjectDownloadDialog(Frame owner,
                                   String userName,
                                   char[] password,
                                   Collection<SubmissionFileDetail> submissionFileDetails) {
        super(owner);
        this.userName = userName;
        this.password = password;
        this.submissionFileDetails = new ArrayList<SubmissionFileDetail>(submissionFileDetails);
        initComponents();
        postInitComponents();
    }

    public MyProjectDownloadDialog(Dialog owner, Collection<SubmissionFileDetail> submissionFileDetails) {
        super(owner);
        this.submissionFileDetails = new ArrayList<SubmissionFileDetail>(submissionFileDetails);
        initComponents();
        postInitComponents();
    }

    private void createUIComponents() {
        createSummaryPanel();

        createFileSelectionTable();
    }

    private void createFileSelectionTable() {
        fileSelectionTable = new JXTreeTable();
        ((JXTreeTable) fileSelectionTable).setClosedIcon(null);
        ((JXTreeTable) fileSelectionTable).setLeafIcon(null);
        ((JXTreeTable) fileSelectionTable).setOpenIcon(null);

        fileSelectionTable.getTableHeader().setReorderingAllowed(false);
        fileSelectionTableModel = new SubmissionFileDownloadTableModel();
        fileSelectionTableModel.addTreeModelListener(this);
        ((JXTreeTable) fileSelectionTable).setTreeTableModel(fileSelectionTableModel);
        ((JXTreeTable) fileSelectionTable).setColumnControlVisible(false);
        fileSelectionTable.setFillsViewportHeight(true);
        fileSelectionTable.setCellEditor(new DefaultCellEditor(new JCheckBox()));
        fileSelectionTable.getColumnModel().getColumn(0).setPreferredWidth(400);
    }

    private void createSummaryPanel() {
        summaryPanel = new JPanel();
        summaryPanel.setBorder(BorderUtil.createLoweredBorder());
    }

    private void postInitComponents() {
        initSummaryItemPanel();

        downloadButton.setEnabled(false);

        fileSelectionTableModel.addSubmissionFileDetails(submissionFileDetails);
        ((JXTreeTable) fileSelectionTable).packAll();

        setSelectAllButtonAction();

        setDeselectAllButtonAction();

        setCancelButtonAction();

        setDownloadButtonAction();
    }

    private void initSummaryItemPanel() {
        SummaryItemPanel summaryItemPanel = new SummaryItemPanel(submissionFileDetails);
        summaryPanel.add(summaryItemPanel, BorderLayout.CENTER);
    }

    private void setDownloadButtonAction() {
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PrideInspectorContext context = (PrideInspectorContext) PrideInspector.getInstance().getDesktopContext();
                SimpleFileDialog ofd = new SimpleFileDialog(context.getOpenFilePath(), "Select Path Save To", true, null, false);
                ofd.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                ofd.setMultiSelectionEnabled(false);
                int result = ofd.showOpenDialog(MyProjectDownloadDialog.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = ofd.getSelectedFile();
                    String folderPath = selectedFile.getPath();
                    context.setOpenFilePath(folderPath.replace(selectedFile.getName(), ""));
                    downloadFiles(folderPath);
                    MyProjectDownloadDialog.this.dispose();
                }
            }
        });
    }

    private void downloadFiles(String folderPath) {
        File path = new File(folderPath);

        java.util.List<SubmissionFileDetail> submissionFileDetails = new ArrayList<SubmissionFileDetail>();

        // find all the selected px submission files
        java.util.Set<Object> leaves = fileSelectionTableModel.getNoneRootNodes();
        boolean toDownload = true;
        for (Object leaf : leaves) {
            if (((SubmissionFileDetail) leaf).isDownload()) {
                SubmissionFileDetail entry = (SubmissionFileDetail) leaf;
                submissionFileDetails.add(entry);
                if (entry.getFileType().equalsIgnoreCase("RESULT")) {
                    toDownload = false;
                }
            }
        }

        if (!toDownload) {
            int confirmed = JOptionPane.showConfirmDialog(this, "Would you like Inspector try to open the files after downloading?", "Download PRIDE submission", JOptionPane.YES_NO_OPTION);
            if (confirmed != 0) {
                return;
            }
        }

        // create a dialog to show progress
        TaskDialog dialog = new TaskDialog(uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getMainComponent(), "Download project files from PRIDE", "Downloading...Please be aware that this may take a few minutes");
        dialog.setVisible(true);

        GetPrideFileTask downloadTask = new GetPrideFileTask(submissionFileDetails, path, userName, password == null ? null : new String(password), true);
        downloadTask.addTaskListener(dialog);
        TaskUtil.startBackgroundTask(downloadTask);
    }

    private void setSelectAllButtonAction() {
        selectAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectionButtonPressed(true);
            }
        });
    }

    private void setDeselectAllButtonAction() {
        deselectAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectionButtonPressed(false);
            }
        });
    }

    private void selectionButtonPressed(boolean downloadable) {
        int downloadColumnIndex = fileSelectionTableModel.getColumnIndex(SubmissionFileDownloadTableModel.TableHeader.DOWNLOAD_COLUMN.getHeader());
        Object root = fileSelectionTableModel.getRoot();
        int cnt = fileSelectionTableModel.getChildCount(root);
        if (cnt > 0) {
            for (int i = 0; i < cnt; i++) {
                Object parent = fileSelectionTableModel.getChild(root, i);
                fileSelectionTableModel.setValueAt(downloadable, parent, downloadColumnIndex);
                int childCnt = fileSelectionTableModel.getChildCount(parent);
                for (int j = 0; j < childCnt; j++) {
                    Object child = fileSelectionTableModel.getChild(parent, j);
                    fileSelectionTableModel.setValueAt(downloadable, child, downloadColumnIndex);
                    int nestedChildCnt = fileSelectionTableModel.getChildCount(child);
                    for (int k = 0; k < nestedChildCnt; k++) {
                        fileSelectionTableModel.setValueAt(downloadable, fileSelectionTableModel.getChild(child, k), downloadColumnIndex);
                    }
                }
            }
        }
    }

    private void setCancelButtonAction() {
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyProjectDownloadDialog.this.dispose();
            }
        });
    }

    @Override
    public void treeStructureChanged(TreeModelEvent e) {
        boolean newEntries = fileSelectionTableModel.getChildCount(fileSelectionTableModel.getRoot()) > 0;

        selectAllButton.setEnabled(newEntries);
        deselectAllButton.setEnabled(newEntries);
        downloadButton.setEnabled(false);
    }

    @Override
    public void treeNodesChanged(TreeModelEvent e) {
        boolean downloadable = false;

        // check whether any file has been selected, enable/disable the download button
        Set<Object> nodes = fileSelectionTableModel.getNoneRootNodes();
        for (Object node : nodes) {
            if (((SubmissionFileDetail) node).isDownload()) {
                downloadable = true;
                break;
            }
        }

        downloadButton.setEnabled(downloadable);

        fileSelectionTable.revalidate();
        fileSelectionTable.repaint();
    }

    @Override
    public void treeNodesInserted(TreeModelEvent e) {
    }

    @Override
    public void treeNodesRemoved(TreeModelEvent e) {
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        createUIComponents();

        dialogPane = new JPanel();
        contentPanel = new JPanel();
        fileSelectionLabel = new JLabel();
        fileSelectionScrollPane = new JScrollPane();
        selectAllButton = new JButton();
        deselectAllButton = new JButton();
        buttonBar = new JPanel();
        cancelButton = new JButton();
        downloadButton = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //======== summaryPanel ========
                {
                    summaryPanel.setLayout(new BorderLayout());
                }

                //---- fileSelectionLabel ----
                fileSelectionLabel.setText("Select files to download");

                //======== fileSelectionScrollPane ========
                {
                    fileSelectionScrollPane.setViewportView(fileSelectionTable);
                }

                //---- selectAllButton ----
                selectAllButton.setText("Select All");

                //---- deselectAllButton ----
                deselectAllButton.setText("Deselect All");

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                        contentPanelLayout.createParallelGroup()
                                .addComponent(summaryPanel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(fileSelectionScrollPane, GroupLayout.DEFAULT_SIZE, 719, Short.MAX_VALUE)
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addGroup(contentPanelLayout.createParallelGroup()
                                                .addComponent(fileSelectionLabel)
                                                .addGroup(contentPanelLayout.createSequentialGroup()
                                                        .addComponent(selectAllButton, GroupLayout.PREFERRED_SIZE, 84, GroupLayout.PREFERRED_SIZE)
                                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                        .addComponent(deselectAllButton)))
                                        .addGap(0, 540, Short.MAX_VALUE))
                );
                contentPanelLayout.setVerticalGroup(
                        contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addComponent(summaryPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(fileSelectionLabel)
                                        .addGap(4, 4, 4)
                                        .addComponent(fileSelectionScrollPane, GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                .addComponent(selectAllButton)
                                                .addComponent(deselectAllButton)))
                );
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[]{0, 85, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[]{1.0, 0.0, 0.0, 0.0};

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- downloadButton ----
                downloadButton.setText("Download");
                buttonBar.add(downloadButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel summaryPanel;
    private JLabel fileSelectionLabel;
    private JScrollPane fileSelectionScrollPane;
    private JTable fileSelectionTable;
    private JButton selectAllButton;
    private JButton deselectAllButton;
    private JPanel buttonBar;
    private JButton cancelButton;
    private JButton downloadButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
