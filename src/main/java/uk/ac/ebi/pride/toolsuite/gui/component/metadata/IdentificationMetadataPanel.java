/*
 * Created by JFormDesigner on Wed Feb 01 08:45:57 GMT 2012
 */

package uk.ac.ebi.pride.toolsuite.gui.component.metadata;

import uk.ac.ebi.pride.utilities.data.core.Parameter;
import uk.ac.ebi.pride.utilities.data.core.Protocol;
import uk.ac.ebi.pride.utilities.data.core.SearchDataBase;
import uk.ac.ebi.pride.utilities.data.core.SpectrumIdentificationProtocol;
import uk.ac.ebi.pride.toolsuite.gui.access.GeneralMetaDataGroup;
import uk.ac.ebi.pride.toolsuite.gui.component.table.TableFactory;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author User #2
 */
public class IdentificationMetadataPanel extends JPanel {

    public IdentificationMetadataPanel(GeneralMetaDataGroup metaData) {
        populateComponents(metaData);
        initComponents();

    }

    private void populateComponents(GeneralMetaDataGroup metaData) {

        // database parameters
        dataBaseTabbedPane = new JTabbedPane();
        dataBaseTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
        List<SearchDataBase> searchDataBaseList = metaData.getSearchDatabases();
        if (!searchDataBaseList.isEmpty()) {
            for (SearchDataBase searchDataBase : searchDataBaseList) {
                String name = searchDataBase.getName();
                SearchDatabaseMetadataPanel databaseMetadataPanel = new SearchDatabaseMetadataPanel(searchDataBase);
                dataBaseTabbedPane.add(name, databaseMetadataPanel);
            }
        }

        //Protein Protocol Parameters
        proteinProtocolLabel = new JLabel();

        Protocol proteinProtocol = metaData.getProteinDetectionProtocol();

        if ((proteinProtocol != null) && (!proteinProtocol.getAnalysisParam().isEmpty())) {

            String nameProtocol = proteinProtocol.getName();
            if (nameProtocol == null) nameProtocol = "Dafault";
            String softwareProtocol = (proteinProtocol != null && proteinProtocol.getAnalysisSoftware() != null)?proteinProtocol.getAnalysisSoftware().getName():"";
            proteinProtocolLabel.setText("Protein Identification Protocol: " + nameProtocol + ", Software: " + softwareProtocol);
            proteinProtocolLabel.setToolTipText(nameProtocol);
            // protocol table
            proteinProtocolTable = TableFactory.createParamTable(proteinProtocol.getAnalysisParam());
        } else {
            proteinProtocolTable = TableFactory.createParamTable(new ArrayList<Parameter>());
        }


        // peptide Protocol
        peptideProtocolTabbedPane = new JTabbedPane();
        peptideProtocolTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);

        List<SpectrumIdentificationProtocol> spectrumIdentificationProtocolList = metaData.getSpectrumIdentificationProtocol();
        if (!spectrumIdentificationProtocolList.isEmpty()) {
            for (SpectrumIdentificationProtocol spectrumIdentificationProtocol : spectrumIdentificationProtocolList) {
                String name = spectrumIdentificationProtocol.getName();
                if (name == null) name = "Default";
                String software = spectrumIdentificationProtocol.getAnalysisSoftware().getName();
                PeptideIdentificationMetadataPanel peptideComp = new PeptideIdentificationMetadataPanel(spectrumIdentificationProtocol);
                peptideProtocolTabbedPane.addTab("Protocol " + name + ", Software: " + software, peptideComp);
            }
        } else {
            PeptideIdentificationMetadataPanel peptideComp = new PeptideIdentificationMetadataPanel(null);
            peptideProtocolTabbedPane.addTab("Default Peptide Protocol: ", peptideComp);
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner non-commercial license
        databaseLabel = new JLabel();
        peptideProtocol = new JLabel();
        scrollPane2 = new JScrollPane();

        //======== this ========

        //---- databaseLabel ----
        databaseLabel.setText("DataBase Properties");
        databaseLabel.setFont(databaseLabel.getFont().deriveFont(databaseLabel.getFont().getStyle() | Font.BOLD));

        //---- peptideProtocol ----
        peptideProtocol.setText("Peptide Identification Protocol");
        peptideProtocol.setFont(peptideProtocol.getFont().deriveFont(peptideProtocol.getFont().getStyle() | Font.BOLD));

        proteinProtocolLabel.setFont(peptideProtocol.getFont().deriveFont(peptideProtocol.getFont().getStyle() | Font.BOLD));

        //======== scrollPane2 ========
        {
            scrollPane2.setViewportView(proteinProtocolTable);
        }

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                        .addComponent(peptideProtocolTabbedPane, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
                                        .addComponent(dataBaseTabbedPane, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
                                        .addComponent(databaseLabel, GroupLayout.Alignment.LEADING)
                                        .addComponent(peptideProtocol, GroupLayout.Alignment.LEADING)
                                        .addComponent(proteinProtocolLabel, GroupLayout.Alignment.LEADING)
                                        .addComponent(scrollPane2, GroupLayout.Alignment.LEADING))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(databaseLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dataBaseTabbedPane, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(peptideProtocol)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(peptideProtocolTabbedPane, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(proteinProtocolLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE)
                                .addGap(28, 28, 28))

        );
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner non-commercial license
    private JLabel databaseLabel;
    private JTabbedPane dataBaseTabbedPane;
    private JLabel peptideProtocol;
    private JTabbedPane peptideProtocolTabbedPane;
    private JLabel proteinProtocolLabel;
    private JTable proteinProtocolTable;
    private JScrollPane scrollPane2;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
