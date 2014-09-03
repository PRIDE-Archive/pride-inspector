package uk.ac.ebi.pride.toolsuite.gui.component.metadata;

import uk.ac.ebi.pride.utilities.data.core.ExperimentProtocol;
import uk.ac.ebi.pride.utilities.data.core.Parameter;
import uk.ac.ebi.pride.utilities.data.core.Sample;
import uk.ac.ebi.pride.toolsuite.gui.access.GeneralMetaDataGroup;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
/*
 * Created by JFormDesigner on Sat Jul 23 10:52:41 BST 2011
 */


/**
 * @author User #2
 */
public class SampleProtocolMetadataPanel extends JPanel {
    public SampleProtocolMetadataPanel(GeneralMetaDataGroup metaData) {
        populateComponents(metaData);
        initComponents();
    }

    private void populateComponents(GeneralMetaDataGroup metaData) {
        // add samples
        sampleTabbedPane = new JTabbedPane();
        sampleTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
        // add each sample
        List<Sample> samples = metaData.getSampleList();
        if (!samples.isEmpty()) {
            for (Sample sample : samples) {
                String sampleName = sample.getName();
                JTable sampleTable = TableFactory.createParamTable(sample);
                JScrollPane scrollPane = new JScrollPane(sampleTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPane.setBorder(BorderFactory.createEmptyBorder());
                sampleTabbedPane.addTab(sampleName, scrollPane);
            }
        } else {
            // todo: what if no samples are available, display a message?
        }

        // protocol
        protocolNameField = new JLabel();
        if ((metaData.getProtocol() != null) && (!metaData.getProtocol().getProtocolSteps().isEmpty())) {
            // get protocol
            ExperimentProtocol protocol = metaData.getProtocol();
            // protocol name
            String protName = protocol.getName();
            protocolNameField.setText(protName);
            protocolNameField.setToolTipText(protName);
            // protocol table
            protocolTable = TableFactory.createParamTable(protocol.getProtocolSteps());
        } else {
            protocolTable = TableFactory.createParamTable(new ArrayList<Parameter>());
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        sampleLabel = new JLabel();
        protocolLabel = new JLabel();
        scrollPane2 = new JScrollPane();

        //======== this ========

        //---- sampleLabel ----
        sampleLabel.setText("Samples");
        sampleLabel.setFont(sampleLabel.getFont().deriveFont(sampleLabel.getFont().getStyle() | Font.BOLD));

        //---- protocolLabel ----
        protocolLabel.setText("Protocol");
        protocolLabel.setFont(protocolLabel.getFont().deriveFont(protocolLabel.getFont().getStyle() | Font.BOLD));

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(protocolTable);
        }

        //---- protocolNameField ----

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(scrollPane2, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                                        .addComponent(sampleTabbedPane, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE)
                                        .addGroup(GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                .addComponent(protocolLabel, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(protocolNameField, GroupLayout.PREFERRED_SIZE, 400, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(sampleLabel, GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(sampleLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sampleTabbedPane, GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(protocolLabel)
                                        .addComponent(protocolNameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
                                .addContainerGap(31, Short.MAX_VALUE))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel sampleLabel;
    private JLabel protocolLabel;
    private JScrollPane scrollPane2;
    private JTable protocolTable;
    private JLabel protocolNameField;
    private JTabbedPane sampleTabbedPane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
