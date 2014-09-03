package uk.ac.ebi.pride.toolsuite.gui.component.metadata;

import uk.ac.ebi.pride.utilities.data.core.DataProcessing;
import uk.ac.ebi.pride.utilities.data.core.InstrumentConfiguration;
import uk.ac.ebi.pride.utilities.data.core.ProcessingMethod;
import uk.ac.ebi.pride.toolsuite.gui.access.GeneralMetaDataGroup;

import javax.swing.*;
import java.awt.*;
import java.util.List;
/*
 * Created by JFormDesigner on Sun Jul 24 22:11:06 BST 2011
 */


/**
 * @author User #2
 */
public class InstrumentProcessingMetadataPanel extends JPanel {


    public InstrumentProcessingMetadataPanel(GeneralMetaDataGroup metaData) {
        populateComponents(metaData);
        initComponents();
    }

    private void populateComponents(GeneralMetaDataGroup metaData) {
        // instrument configurations
        instrumentTabbedPane = new JTabbedPane();
        instrumentTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
        List<InstrumentConfiguration> instrumentConfigurationList = metaData.getInstrumentConfigurations();
        if (instrumentConfigurationList.size() > 0) {
            for (InstrumentConfiguration instrumentConfiguration : instrumentConfigurationList) {
                String name = instrumentConfiguration.getId();
                InstrumentCompMetadataPanel comps = new InstrumentCompMetadataPanel(instrumentConfiguration);
                instrumentTabbedPane.addTab(name, comps);
            }
        }

        // data processings
        dataProcTabbedPane = new JTabbedPane();
        dataProcTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
        List<DataProcessing> dataProcessingList = metaData.getDataProcessings();
        if (!dataProcessingList.isEmpty()) {
            int cnt = 1;
            for (DataProcessing dataProcessing : dataProcessingList) {
                List<ProcessingMethod> methods = dataProcessing.getProcessingMethods();
                if (methods.size() > 0) {
                    for (ProcessingMethod method : methods) {
                        DataProcessingMetadataPanel dataProc = new DataProcessingMetadataPanel(method);
                        dataProcTabbedPane.addTab("Method " + cnt, dataProc);
                        cnt++;
                    }
                }
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        instrumentLabel = new JLabel();
        dataProcLabel = new JLabel();

        //======== this ========

        //---- instrumentLabel ----
        instrumentLabel.setText("Instrument Configurations");
        instrumentLabel.setFont(instrumentLabel.getFont().deriveFont(instrumentLabel.getFont().getStyle() | Font.BOLD));

        //---- dataProcLabel ----
        dataProcLabel.setText("Data Processings");
        dataProcLabel.setFont(dataProcLabel.getFont().deriveFont(dataProcLabel.getFont().getStyle() | Font.BOLD));

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(dataProcTabbedPane, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
                                        .addComponent(instrumentTabbedPane, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
                                        .addComponent(instrumentLabel, GroupLayout.Alignment.LEADING)
                                        .addComponent(dataProcLabel, GroupLayout.Alignment.LEADING))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(instrumentLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(instrumentTabbedPane, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(dataProcLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dataProcTabbedPane, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                                .addGap(28, 28, 28))
        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel instrumentLabel;
    private JTabbedPane instrumentTabbedPane;
    private JLabel dataProcLabel;
    private JTabbedPane dataProcTabbedPane;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
