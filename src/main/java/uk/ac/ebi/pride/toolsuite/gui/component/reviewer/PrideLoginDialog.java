/*
 * Created by JFormDesigner on Tue Apr 23 09:13:40 BST 2013
 */

package uk.ac.ebi.pride.toolsuite.gui.component.reviewer;

import net.java.balloontip.BalloonTip;
import uk.ac.ebi.pride.archive.web.service.model.project.ProjectDetailList;
import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspector;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.task.Task;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskEvent;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskListener;
import uk.ac.ebi.pride.toolsuite.gui.task.TaskUtil;
import uk.ac.ebi.pride.toolsuite.gui.task.impl.GetMyProjectsMetadataTask;
import uk.ac.ebi.pride.toolsuite.gui.utils.BalloonTipUtil;
import uk.ac.ebi.pride.toolsuite.gui.utils.ColourUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 *
 * @author Rui Wang
 */
public class PrideLoginDialog extends JDialog implements TaskListener<ProjectDetailList, String> {
    private static final String REVIEWER = "review";
    private static final String EMAIL_SIGN = "@";
    private static final String EBI_EMAIL_DOMAIN = "ebi.ac.uk";

    private BalloonTip warningBalloonTip;

    public PrideLoginDialog(Frame owner) {
        super(owner);
        initComponents();
        postInitComponents();
    }

    public PrideLoginDialog(Dialog owner) {
        super(owner);
        initComponents();
        postInitComponents();
    }

    public void hideWarnings() {
        hideWarningBallooTip();

        userNameField.setBackground(ColourUtil.TEXT_FIELD_NORMAL_COLOUR);
        passwordField.setBackground(ColourUtil.TEXT_FIELD_NORMAL_COLOUR);
    }

    private boolean doFieldValidation() {
        boolean invalid = false;

        hideWarningBallooTip();

        // user name
        if (!validateUserName(getUserName())) {
            warningBalloonTip = BalloonTipUtil.createErrorBalloonTip(userNameField, PrideInspector.getInstance().getDesktopContext().getProperty("pride.login.username.error.message"));
            warningBalloonTip.setVisible(true);
            userNameField.setBackground(ColourUtil.TEXT_FIELD_WARNING_COLOUR);
            invalid = true;
        } else {
            userNameField.setBackground(ColourUtil.TEXT_FIELD_NORMAL_COLOUR);
        }

        // password
        if (!validatePassword(getPassword())) {
            if (!invalid) {
                warningBalloonTip = BalloonTipUtil.createErrorBalloonTip(passwordField, PrideInspector.getInstance().getDesktopContext().getProperty("pride.login.password.error.message"));
                warningBalloonTip.setVisible(true);
            }
            passwordField.setBackground(ColourUtil.TEXT_FIELD_WARNING_COLOUR);
            invalid = true;
        } else {
            passwordField.setBackground(ColourUtil.TEXT_FIELD_NORMAL_COLOUR);
        }

        return invalid;
    }

    private void hideWarningBallooTip() {
        if (warningBalloonTip != null && warningBalloonTip.isVisible()) {
            warningBalloonTip.closeBalloon();
        }
    }

    private boolean validateUserName(String userName) {
        return userName != null && userName.trim().length() > 0;
    }

    private boolean validatePassword(char[] password) {
        return password != null && password.length > 0;
    }

    private void postInitComponents() {
        this.setTitle(PrideInspector.getInstance().getDesktopContext().getProperty("pride.login.dialog.title"));

        setLoginButtonAction();

        setCancelButtonAction();

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        });
    }

    private void setLoginButtonAction() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean invalid = doFieldValidation();
                if (!invalid) {
                    loginAction();

                }
            }
        });
    }

    protected void loginAction() {
        // launch a new task for login
        Task task = new GetMyProjectsMetadataTask(getUserName(), getPassword());
        task.addTaskListener(this);
        TaskUtil.startBackgroundTask(task);
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public String getUserName() {
        String username = userNameField.getText();

        if (username != null && username.toLowerCase().startsWith(REVIEWER) && !username.contains(EMAIL_SIGN)) {
            username += EMAIL_SIGN + EBI_EMAIL_DOMAIN;
        }

        return username;
    }

    private void setCancelButtonAction() {
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelAction();
            }
        });
    }

    protected void cancelAction() {
        this.dispose();
    }

    @Override
    public void started(TaskEvent<Void> event) {
        hideWarnings();
        loginButton.setEnabled(false);
        loginButton.setIcon(GUIUtilities.loadIcon(PrideInspector.getInstance().getDesktopContext().getProperty("pride.login.button.loading.small.icon")));
    }

    @Override
    public void process(TaskEvent<java.util.List<String>> listTaskEvent) {
        java.util.List<String> msgs = listTaskEvent.getValue();
        hideWarningBallooTip();

        // show the last warning message
        // please note: this must happen before change the icon of the login button
        warningBalloonTip = BalloonTipUtil.createErrorBalloonTip(loginButton, msgs.get(msgs.size() - 1));
        warningBalloonTip.setVisible(true);

        // login label
        loginButton.setEnabled(true);
        loginButton.setIcon(null);

    }

    @Override
    public void succeed(TaskEvent<ProjectDetailList> mapTaskEvent) {
        ProjectDetailList projectDetailList = mapTaskEvent.getValue();

        if (projectDetailList != null) {
            // set my project list into application context
            LoginRecord loginRecord = new LoginRecord(getUserName(), getPassword(), projectDetailList);
            ((PrideInspectorContext)PrideInspector.getInstance().getDesktopContext()).setLoginRecord(loginRecord);

            // close the current panel
            this.dispose();

            // open project summary dialog
            MyProjectSummaryDialog myProjectSummaryDialog = new MyProjectSummaryDialog(PrideInspector.getInstance().getMainComponent());
            myProjectSummaryDialog.setVisible(true);
        }
    }

    @Override
    public void finished(TaskEvent<Void> event) {
    }

    @Override
    public void failed(TaskEvent<Throwable> event) {
    }

    @Override
    public void cancelled(TaskEvent<Void> event) {
    }

    @Override
    public void interrupted(TaskEvent<InterruptedException> iex) {
    }

    @Override
    public void progress(TaskEvent<Integer> progress) {
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        userNameField = new JTextField();
        passwordLabel = new JLabel();
        passwordField = new JPasswordField();
        controlBar = new JPanel();
        cancelButton = new JButton();
        loginButton = new JButton();
        userNameLabel = new JLabel();

        //======== this ========
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //---- passwordLabel ----
                passwordLabel.setText("Password");
                passwordLabel.setFont(passwordLabel.getFont().deriveFont(passwordLabel.getFont().getSize() + 2f));

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addComponent(userNameField, GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                        .addComponent(passwordField, GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addComponent(passwordLabel, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 304, Short.MAX_VALUE))
                );
                contentPanelLayout.setVerticalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addComponent(userNameField, GroupLayout.PREFERRED_SIZE, 42, GroupLayout.PREFERRED_SIZE)
                            .addGap(26, 26, 26)
                            .addComponent(passwordLabel)
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(passwordField, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
                            .addGap(0, 17, Short.MAX_VALUE))
                );
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== controlBar ========
            {
                controlBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                controlBar.setLayout(new GridBagLayout());
                ((GridBagLayout)controlBar.getLayout()).columnWidths = new int[] {0, 85, 85, 80};
                ((GridBagLayout)controlBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 0.0};

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                cancelButton.setFont(cancelButton.getFont().deriveFont(cancelButton.getFont().getSize() + 2f));
                controlBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- loginButton ----
                loginButton.setText("Login");
                loginButton.setFont(loginButton.getFont().deriveFont(loginButton.getFont().getSize() + 2f));
                controlBar.add(loginButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(controlBar, BorderLayout.SOUTH);

            //---- userNameLabel ----
            userNameLabel.setText("User name");
            userNameLabel.setFont(userNameLabel.getFont().deriveFont(userNameLabel.getFont().getSize() + 2f));
            dialogPane.add(userNameLabel, BorderLayout.NORTH);
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
    private JTextField userNameField;
    private JLabel passwordLabel;
    private JPasswordField passwordField;
    private JPanel controlBar;
    private JButton cancelButton;
    private JButton loginButton;
    private JLabel userNameLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
