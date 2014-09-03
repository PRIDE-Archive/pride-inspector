package uk.ac.ebi.pride.toolsuite.gui.component.metadata;

import uk.ac.ebi.pride.utilities.data.core.ProcessingMethod;
import uk.ac.ebi.pride.utilities.data.core.Software;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;

import javax.swing.*;
import java.awt.*;
/*
 * Created by JFormDesigner on Sun Jul 24 21:57:43 BST 2011
 */


/**
 * @author User #2
 */
public class DataProcessingMetadataPanel extends JPanel {
    public DataProcessingMetadataPanel(ProcessingMethod method) {
        populateComponents(method);
        initComponents();
    }

    private void populateComponents(ProcessingMethod method) {
        // software name
        softwareNameField = new JTextField();
        softwareNameField.setEditable(false);

        // software version
        softwareVersionField = new JTextField();
        softwareVersionField.setEditable(false);

        // software
        Software software = method.getSoftware();
        if (software != null) {
            String name = (software.getName() != null)?software.getName():null;
            if (name != null) {
                softwareNameField.setText(name);
            }
            String version = software.getVersion();
            if (version != null) {
                softwareVersionField.setText(version);
            }
        }

        // processing methods
        dataProcTable = TableFactory.createParamTable(method);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        softwareLabel = new JLabel();
        softwareVersionLabel = new JLabel();
        scrollPane1 = new JScrollPane();
        scrollPane1.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.gray));

        //======== this ========

        //---- softwareLabel ----
        softwareLabel.setText("Software:");

        //---- softwareVersionLabel ----
        softwareVersionLabel.setText("Version:");

        //======== scrollPane1 ========
        {
            scrollPane1.setViewportView(dataProcTable);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(12, 12, 12)
                    .addComponent(softwareLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(softwareNameField, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
                    .addGap(39, 39, 39)
                    .addComponent(softwareVersionLabel)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(softwareVersionField, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(55, Short.MAX_VALUE))
                .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 525, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                    .addGap(10, 10, 10)
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(softwareLabel)
                        .addComponent(softwareNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(softwareVersionField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(softwareVersionLabel))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel softwareLabel;
    private JTextField softwareNameField;
    private JLabel softwareVersionLabel;
    private JTextField softwareVersionField;
    private JScrollPane scrollPane1;
    private JTable dataProcTable;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
