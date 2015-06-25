/*
 * Created by JFormDesigner on Tue Apr 23 10:08:22 BST 2013
 */

package uk.ac.ebi.pride.toolsuite.gui.component.reviewer;

import uk.ac.ebi.pride.archive.web.service.model.file.FileDetail;
import uk.ac.ebi.pride.archive.web.service.model.file.FileDetailList;
import uk.ac.ebi.pride.archive.web.service.model.project.ProjectDetail;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.GetMyProjectFilesMetadataTask;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Rui Wang
 */
public class MyProjectSummaryDialog extends JDialog implements TaskListener<FileDetailList, String> {
    public static final String PRIVATE_PROJECT = "Private";

    private LoginRecord myLoginRecord;

    public MyProjectSummaryDialog(Frame owner) {
        super(owner);
        preInitComponents();
        initComponents();
        postInitComponents();
    }

    public MyProjectSummaryDialog(Dialog owner) {
        super(owner);
        preInitComponents();
        initComponents();
        postInitComponents();
    }

    private void preInitComponents() {
        this.setTitle(PrideInspector.getInstance().getDesktopContext().getProperty("pride.my.project.summary.dialog.title"));
        this.setPreferredSize(new Dimension(637, 450));
        this.myLoginRecord = ((PrideInspectorContext) PrideInspector.getInstance().getDesktopContext()).getLoginRecord();
    }

    private void postInitComponents() {

        initProjectAccessionList();

        setProjectListAction();

        setCancelButtonAction();

        setLogoutButtonAction();

        setViewFilesButtonAction();
    }

    private void initProjectAccessionList() {
        DefaultListModel model = new DefaultListModel();
        for (ProjectDetail projectDetail : myLoginRecord.getProjectDetailList().getList()) {
            model.addElement(projectDetail.getAccession());
        }
        projectAccessionList.setModel(model);
    }

    private void setProjectListAction() {
        projectAccessionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int index = projectAccessionList.locationToIndex(e.getPoint());
                    String projectAccession = (String) projectAccessionList.getModel().getElementAt(index);
                    if (projectAccession != null) {
                        ProjectDetail projectDetail = myLoginRecord.getProjectDetailList().getProjectByAccession(projectAccession);
                        updateProjectDetails(projectDetail);
                    }
                }
            }


        });

        if (projectAccessionList.getModel().getSize() > 0) {
            projectAccessionList.setSelectedIndex(0);
            updateProjectDetails(myLoginRecord.getProjectDetailList().getList().get(0));
        }
    }

    private void updateProjectDetails(ProjectDetail projectDetail) {
        projectTitleArea.setText(projectDetail.getTitle());
        projectDescriptionTextArea.setText(projectDetail.getProjectDescription());

        Date publicationDate = projectDetail.getPublicationDate();
        if (publicationDate != null) {
            publcationDateTextField.setText(DateFormat.getDateInstance().format(publicationDate));
        } else {
            publcationDateTextField.setText(PRIVATE_PROJECT);
        }

        submissionTypeTextField.setText(projectDetail.getSubmissionType());
    }

    private void setLogoutButtonAction() {
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                MyProjectSummaryDialog.this.dispose();
                // empty my project list
                ((PrideInspectorContext) PrideInspector.getInstance().getDesktopContext()).setLoginRecord(null);
            }
        });
    }

    private void setCancelButtonAction() {
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MyProjectSummaryDialog.this.setVisible(false);
            }
        });
    }

    private void setViewFilesButtonAction() {
        viewFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // launch a new task for login
                String projectAccession = (String) projectAccessionList.getSelectedValue();
                Task task = new GetMyProjectFilesMetadataTask(myLoginRecord.getUserName(), myLoginRecord.getPassword(), projectAccession);
                task.addTaskListener(MyProjectSummaryDialog.this);
                TaskUtil.startBackgroundTask(task);
            }
        });
    }

    @Override
    public void started(TaskEvent<Void> event) {
        viewFilesButton.setEnabled(false);
        viewFilesButton.setIcon(GUIUtilities.loadIcon(PrideInspector.getInstance().getDesktopContext().getProperty("pride.login.button.loading.small.icon")));
    }

    @Override
    public void process(TaskEvent<List<String>> event) {
        //todo:  display error message

        viewFilesButton.setEnabled(true);
        viewFilesButton.setIcon(null);
    }

    @Override
    public void succeed(TaskEvent<FileDetailList> event) {
        FileDetailList fileDetailList = event.getValue();

        if (fileDetailList != null) {
            // close the current panel
            MyProjectSummaryDialog.this.setVisible(false);

            List<SubmissionFileDetail> submissionFileDetails = new ArrayList<SubmissionFileDetail>();
            for (FileDetail fileDetail : fileDetailList.getList()) {
                submissionFileDetails.add(new SubmissionFileDetail(fileDetail));
            }

            // open project summary dialog
            MyProjectDownloadDialog myProjectDownloadDialog = new MyProjectDownloadDialog(PrideInspector.getInstance().getMainComponent(), myLoginRecord.getUserName(), myLoginRecord.getPassword(), submissionFileDetails);
            myProjectDownloadDialog.setVisible(true);
        }
    }

    @Override
    public void finished(TaskEvent<Void> event) {
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
    }

    @Override
    public void progress(TaskEvent<Integer> progress) {
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        projectLabel = new JLabel();
        projectAccessionScrollPane = new JScrollPane();
        projectAccessionList = new JList();
        panel1 = new JPanel();
        projectTitleLabel = new JLabel();
        projectDescriptionLabel = new JLabel();
        projectDescriptionScrollPane = new JScrollPane();
        projectDescriptionTextArea = new JTextArea();
        projectTitleScrollPane = new JScrollPane();
        projectTitleArea = new JTextArea();
        publicationDateLabel = new JLabel();
        publcationDateTextField = new JTextField();
        submissionTypeLabel = new JLabel();
        submissionTypeTextField = new JTextField();
        controlBar = new JPanel();
        logoutButton = new JButton();
        cancelButton = new JButton();
        viewFilesButton = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //---- projectLabel ----
                projectLabel.setText("Projects");

                //======== projectAccessionScrollPane ========
                {
                    projectAccessionScrollPane.setViewportView(projectAccessionList);
                }

                //======== panel1 ========
                {
                    panel1.setBorder(new EtchedBorder());

                    //---- projectTitleLabel ----
                    projectTitleLabel.setText("Project title");

                    //---- projectDescriptionLabel ----
                    projectDescriptionLabel.setText("Project description");

                    //======== projectDescriptionScrollPane ========
                    {
                        projectDescriptionScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

                        //---- projectDescriptionTextArea ----
                        projectDescriptionTextArea.setLineWrap(true);
                        projectDescriptionScrollPane.setViewportView(projectDescriptionTextArea);
                    }

                    //======== projectTitleScrollPane ========
                    {
                        projectTitleScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

                        //---- projectTitleArea ----
                        projectTitleArea.setLineWrap(true);
                        projectTitleScrollPane.setViewportView(projectTitleArea);
                    }

                    //---- publicationDateLabel ----
                    publicationDateLabel.setText("Publication date");

                    //---- submissionTypeLabel ----
                    submissionTypeLabel.setText("Submission type");

                    GroupLayout panel1Layout = new GroupLayout(panel1);
                    panel1.setLayout(panel1Layout);
                    panel1Layout.setHorizontalGroup(
                        panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(panel1Layout.createParallelGroup()
                                    .addComponent(projectTitleScrollPane)
                                    .addComponent(projectDescriptionScrollPane)
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addComponent(projectTitleLabel)
                                            .addComponent(projectDescriptionLabel)
                                            .addComponent(publicationDateLabel)
                                            .addComponent(publcationDateTextField, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(panel1Layout.createParallelGroup()
                                            .addGroup(panel1Layout.createSequentialGroup()
                                                .addComponent(submissionTypeLabel)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                            .addComponent(submissionTypeTextField))))
                                .addContainerGap())
                    );
                    panel1Layout.setVerticalGroup(
                        panel1Layout.createParallelGroup()
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(projectTitleLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(projectTitleScrollPane, GroupLayout.PREFERRED_SIZE, 50, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(projectDescriptionLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(projectDescriptionScrollPane, GroupLayout.PREFERRED_SIZE, 176, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(publicationDateLabel)
                                    .addComponent(submissionTypeLabel))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(submissionTypeTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(publcationDateTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(14, Short.MAX_VALUE))
                    );
                }

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addComponent(projectLabel)
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addComponent(projectAccessionScrollPane, GroupLayout.PREFERRED_SIZE, 127, GroupLayout.PREFERRED_SIZE)
                                    .addGap(12, 12, 12)
                                    .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addContainerGap(12, GroupLayout.PREFERRED_SIZE))
                );
                contentPanelLayout.setVerticalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addComponent(projectLabel)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(projectAccessionScrollPane, GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)))
                );
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== controlBar ========
            {
                controlBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                controlBar.setLayout(new GridBagLayout());
                ((GridBagLayout)controlBar.getLayout()).columnWidths = new int[] {0, 0, 85, 85, 80};
                ((GridBagLayout)controlBar.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0};

                //---- logoutButton ----
                logoutButton.setText("Logout");
                controlBar.add(logoutButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                controlBar.add(cancelButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- viewFilesButton ----
                viewFilesButton.setText("View files");
                controlBar.add(viewFilesButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(controlBar, BorderLayout.SOUTH);
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
    private JLabel projectLabel;
    private JScrollPane projectAccessionScrollPane;
    private JList projectAccessionList;
    private JPanel panel1;
    private JLabel projectTitleLabel;
    private JLabel projectDescriptionLabel;
    private JScrollPane projectDescriptionScrollPane;
    private JTextArea projectDescriptionTextArea;
    private JScrollPane projectTitleScrollPane;
    private JTextArea projectTitleArea;
    private JLabel publicationDateLabel;
    private JTextField publcationDateTextField;
    private JLabel submissionTypeLabel;
    private JTextField submissionTypeTextField;
    private JPanel controlBar;
    private JButton logoutButton;
    private JButton cancelButton;
    private JButton viewFilesButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
