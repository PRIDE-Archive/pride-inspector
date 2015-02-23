package uk.ac.ebi.pride.toolsuite.gui.component.decoy;

import uk.ac.ebi.pride.toolsuite.gui.GUIUtilities;
import uk.ac.ebi.pride.toolsuite.gui.PrideInspectorContext;
import uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop;
import uk.ac.ebi.pride.utilities.data.filter.DecoyAccessionFilter;

import javax.help.CSH;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/*
 * Created by JFormDesigner on Wed Aug 31 11:14:10 BST 2011
 */


/**
 * @author ypriverol, rwwang
 */
public class DecoyFilterDialog extends JDialog {
    /**
     * Property change event when a new filter is created
     */
    public static final String NEW_FILTER = "New Filter";

    private static final String FILTER_STRING_LABEL = "Filter String";
    private static final String PREFIX_MESSAGE = "Hide decoy protein accessions start with";
    private static final String POST_MESSAGE = "Hide decoy protein accessions end with";
    private static final String CONTAIN_MESSAGE = "Hide decoy protein accessions contain";

    /**
     * Pride Inspector desktop context
     */
    private PrideInspectorContext appContext;

    public DecoyFilterDialog(Frame owner) {
        super(owner, uk.ac.ebi.pride.toolsuite.gui.desktop.Desktop.getInstance().getDesktopContext().getProperty("decoy.filter.title"));
        this.appContext = (PrideInspectorContext) Desktop.getInstance().getDesktopContext();
        initComponents();
        populateComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        panel1 = new JPanel();
        prefixRadioButton = new JRadioButton();
        postRadioButton = new JRadioButton();
        containRadioButton = new JRadioButton();
        descriptionLabel = new JLabel();
        descriptionContentLabel = new JLabel();
        criteriaLabel = new JLabel();
        criteriaTextField = new JTextField();
        buttonBar = new JPanel();
        helpButton = new JButton();
        Icon helpIcon = GUIUtilities.loadIcon(appContext.getProperty("help.icon.small"));
        JButton helpButton = GUIUtilities.createLabelLikeButton(helpIcon, null);
        helpButton.setToolTipText("Help");
        helpButton.setForeground(Color.blue);
        CSH.setHelpIDString(helpButton, "help.protein.decoy");
        helpButton.addActionListener(new CSH.DisplayHelpFromSource(appContext.getMainHelpBroker()));
        cancelButton = new JButton();
        okButton = new JButton();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //======== panel1 ========
                {
                    panel1.setBorder(new TitledBorder(null, "Filter Action", TitledBorder.LEADING, TitledBorder.ABOVE_TOP));

                    //---- prefixRadioButton ----
                    prefixRadioButton.setText("Prefix");
                    prefixRadioButton.setSelected(true);

                    //---- postRadioButton ----
                    postRadioButton.setText("Postfix");

                    //---- containRadioButton ----
                    containRadioButton.setText("Contain");

                    //---- descriptionLabel ----
                    descriptionLabel.setText("Description: ");

                    GroupLayout panel1Layout = new GroupLayout(panel1);
                    panel1.setLayout(panel1Layout);
                    panel1Layout.setHorizontalGroup(
                            panel1Layout.createParallelGroup()
                                    .addGroup(panel1Layout.createSequentialGroup()
                                            .addGroup(panel1Layout.createParallelGroup()
                                                    .addGroup(panel1Layout.createSequentialGroup()
                                                            .addComponent(prefixRadioButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addGap(70, 70, 70)
                                                            .addComponent(postRadioButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                            .addGap(55, 55, 55)
                                                            .addComponent(containRadioButton))
                                                    .addGroup(panel1Layout.createSequentialGroup()
                                                            .addGap(9, 9, 9)
                                                            .addComponent(descriptionLabel)
                                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                            .addComponent(descriptionContentLabel, GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)))
                                            .addContainerGap())
                    );
                    panel1Layout.setVerticalGroup(
                            panel1Layout.createParallelGroup()
                                    .addGroup(panel1Layout.createSequentialGroup()
                                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(prefixRadioButton)
                                                    .addComponent(postRadioButton)
                                                    .addComponent(containRadioButton))
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                                            .addGroup(panel1Layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(descriptionLabel)
                                                    .addComponent(descriptionContentLabel)))
                    );
                }

                //---- criteriaLabel ----
                criteriaLabel.setText("Filter String");

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                        contentPanelLayout.createParallelGroup()
                                .addComponent(panel1, GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                        .addGap(8, 8, 8)
                                        .addComponent(criteriaLabel, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                        .addContainerGap(262, Short.MAX_VALUE))
                                .addComponent(criteriaTextField, GroupLayout.DEFAULT_SIZE, 374, Short.MAX_VALUE)
                );
                contentPanelLayout.setVerticalGroup(
                        contentPanelLayout.createParallelGroup()
                                .addGroup(GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                                        .addComponent(panel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(criteriaLabel)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(criteriaTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                );
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[]{0, 85, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[]{1.0, 0.0, 0.0, 0.0};

                //---- helpLabel ----
                buttonBar.add(helpButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 5), 0, 0));

                //---- okButton ----
                okButton.setText("OK");
                buttonBar.add(okButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());

        //---- buttonGroup1 ----
        ButtonGroup buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(prefixRadioButton);
        buttonGroup1.add(postRadioButton);
        buttonGroup1.add(containRadioButton);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    /**
     * Populate the components with actions
     */
    private void populateComponents() {
        // cancel button
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DecoyFilterDialog.this.setVisible(false);
            }
        });

        // ok button
        okButton.addActionListener(new FilterActionListener());

        // set the default text of description
        descriptionContentLabel.setText(PREFIX_MESSAGE);

        // action listener to radio buttons
        prefixRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                descriptionContentLabel.setText(PREFIX_MESSAGE);
            }
        });

        postRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                descriptionContentLabel.setText(POST_MESSAGE);
            }
        });

        containRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                descriptionContentLabel.setText(CONTAIN_MESSAGE);
            }
        });
    }


    /**
     * Action triggered when the ok button is clicked
     */
    private class FilterActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            // get criteria
            String criteria = criteriaTextField.getText();
            if (criteria != null && !"".equals(criteria.trim())) {
                // get action
                DecoyAccessionFilter.Type type;
                if (prefixRadioButton.isSelected()) {
                    type = DecoyAccessionFilter.Type.PREFIX;
                } else if (postRadioButton.isSelected()) {
                    type = DecoyAccessionFilter.Type.POSTFIX;
                } else {
                    type = DecoyAccessionFilter.Type.CONTAIN;
                }

                DecoyFilterDialog.this.setVisible(false);
                DecoyFilterDialog.this.firePropertyChange(NEW_FILTER, null, new DecoyAccessionFilter(type, criteria.toLowerCase()));

                // reset label
                criteriaLabel.setText(FILTER_STRING_LABEL);
            } else {
                // set error message
                criteriaLabel.setText("<html><div> " + FILTER_STRING_LABEL + " <b style=\"color:#FF0000\"> (Empty String)</b></div></html>");
            }
        }
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel panel1;
    private JRadioButton prefixRadioButton;
    private JRadioButton postRadioButton;
    private JRadioButton containRadioButton;
    private JLabel criteriaLabel;
    private JTextField criteriaTextField;
    private JPanel buttonBar;
    private JButton helpButton;
    private JButton cancelButton;
    private JButton okButton;
    private JLabel descriptionLabel;
    private JLabel descriptionContentLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}